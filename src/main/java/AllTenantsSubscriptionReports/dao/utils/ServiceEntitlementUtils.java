package AllTenantsSubscriptionReports.dao.utils;


import AllTenantsSubscriptionReports.dao.api.service.AccountsSKU;
import AllTenantsSubscriptionReports.dao.config.ControlPlaneInfraUserConfiguration;
import AllTenantsSubscriptionReports.dao.config.MockSpectreConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.faw.cp.common.auth.VaultReader;
import com.oracle.faw.cp.common.utils.RESTUtils;
import com.oracle.pic.account.api.AccountServiceClient;
import com.oracle.pic.account.model.ServiceEntitlementFull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.ImmutablePair;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class ServiceEntitlementUtils {

    private static final String SERVICE_NAME = "ANALYTICSAPP";
    private static final String SERVICE_ENTITLEMENT_NAME_ERP = "ANALYTICSAPP_SAAS_OCI_ERP_UPM";
    private static final String SERVICE_ENTITLEMENT_NAME_HCM = "ANALYTICSAPP_SAAS_OCI_HCM_EPM";
    private static final String SERVICE_ENTITLEMENT_NAME_SCM = "ANALYTICSAPP_SAAS_OCI_SCM_UPM";
    private static final String SERVICE_ENTITLEMENT_NAME_CX = "ANALYTICSAPP_SAAS_OCI_CX_UPM";
    private static final String SERVICE_ENTITLEMENT_NAME_ERP_EMPLOYEES = "ANALYTICSAPP_SAAS_OCI_ERP_EPM";
    private static final String SERVICE_ENTITLEMENT_NAME_SCM_EMPLOYEES = "ANALYTICSAPP_SAAS_OCI_SCM_EPM";
    private static final String SERVICE_ENTITLEMENT_NAME_ADDITIONAL_ENVIRONMENT = "ANALYTICSAPP_SAAS_OCI_ADDITIONAL_ENVIRONMENT";


    private static final String MOCK_API_CHOICE_ACCOUNTS = "Accounts";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSSS");

    private static VaultReader vaultReader;

    private static ControlPlaneInfraUserConfiguration controlPlaneInfraUserConfiguration;

    private static MockSpectreConfiguration mockSpectreConfiguration;

    public static void setVaultReader(VaultReader vaultReaderClient) {
        vaultReader = vaultReaderClient;
    }

    public static void setControlPlaneInfraUserConfiguration(ControlPlaneInfraUserConfiguration
                                                                     infraUserConfig) {
        controlPlaneInfraUserConfiguration = infraUserConfig;
    }

    public static void setMockSpectreConfiguration(MockSpectreConfiguration mockSpectreConfig) {
        mockSpectreConfiguration = mockSpectreConfig;
    }

    public static String readEntitlementsJsonFromOSSBucket(String tenantName, String mockApiChoice) throws IOException {

        ObjectStorageClient objectStorageClient = new ObjectStorageClient(
                new OCIAuthDetailsProvider(controlPlaneInfraUserConfiguration, mockSpectreConfiguration.getRegion(),
                        vaultReader));
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucketName(mockSpectreConfiguration.getBucketName())
                .objectName(tenantName + "_" + mockApiChoice + "_entitlements.json")
                .namespaceName(mockSpectreConfiguration.getBucketNameSpace())
                .retryConfiguration(RESTUtils.getRetryConfiguration())
                .build();

        InputStream inputStream = null;
        GetObjectResponse getObjectResponse = objectStorageClient.getObject(getObjectRequest);
        inputStream = getObjectResponse.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    public static String getServiceEntitlementNameFromAccountsResourceName(String accountsResourceName) {

        String entitlementName;
        switch (accountsResourceName) {
            case SERVICE_ENTITLEMENT_NAME_ERP:
                entitlementName = "FUSION_ERP";
                break;
            case SERVICE_ENTITLEMENT_NAME_HCM:
                entitlementName = "FUSION_HCM";
                break;
            case SERVICE_ENTITLEMENT_NAME_SCM:
                entitlementName = "FUSION_SCM";
                break;
            case SERVICE_ENTITLEMENT_NAME_CX:
                entitlementName = "FUSION_CX";
                break;
            case SERVICE_ENTITLEMENT_NAME_ERP_EMPLOYEES:
                entitlementName = "FUSION_ERP_EMPLOYEES";
                break;
            case SERVICE_ENTITLEMENT_NAME_SCM_EMPLOYEES:
                entitlementName = "FUSION_SCM_EMPLOYEES";
                break;
            case SERVICE_ENTITLEMENT_NAME_ADDITIONAL_ENVIRONMENT:
                entitlementName = "ADDITIONAL_ENVIRONMENTS";
                break;
            default:
                entitlementName = "OTHER_SKU";
                break;
        }

        return entitlementName;
    }
    private static void updateAccountsEntitlementDataMap(Map<String, AccountsSKU> accountsEntitlementData,
                                                         String entitlementName,
                                                         int entitlementQuantity,
                                                         Date startDate,
                                                         Date endDate) {

        if (accountsEntitlementData.containsKey(entitlementName)) {
            accountsEntitlementData.put(entitlementName, AccountsSKU.builder()
                    .skuName(entitlementName)
                    .quantity(entitlementQuantity + accountsEntitlementData
                            .get(entitlementName).getQuantity())
                    .startDate(startDate.before(accountsEntitlementData.get(entitlementName).getStartDate()) ?
                            startDate : accountsEntitlementData.get(entitlementName).getStartDate())
                    .endDate(endDate.after(accountsEntitlementData.get(entitlementName).getEndDate()) ?
                            endDate : accountsEntitlementData.get(entitlementName).getEndDate())
                    .build());

        } else {
            accountsEntitlementData.put(entitlementName, AccountsSKU.builder()
                    .skuName(entitlementName)
                    .quantity(entitlementQuantity)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build());
        }
    }

    public static ImmutablePair<Map<String, AccountsSKU>, Map<String, AccountsSKU>> getSKUDataFromMockAccounts(
            String tenantName) throws Exception {


        Map<String, AccountsSKU> unexpiredMockAccountsEntitlementData = new HashMap<>();
        Map<String, AccountsSKU> expiredMockAccountsEntitlementData = new HashMap<>();
        String entitlementJson = readEntitlementsJsonFromOSSBucket(tenantName, MOCK_API_CHOICE_ACCOUNTS);


        JsonNode decodedDataJson = new ObjectMapper().readTree(entitlementJson);
        if (decodedDataJson.isArray()) {
            for (JsonNode resourceObjNode : decodedDataJson) {
                try {
                    String entitlementName = getServiceEntitlementNameFromAccountsResourceName(resourceObjNode
                            .get("name").textValue());
                    if (!entitlementName.equals("OTHER_SKU")) {
                        Date startDate = DATE_FORMAT.parse(resourceObjNode.get("startDate").textValue());
                        Date endDate = DATE_FORMAT.parse(resourceObjNode.get("endDate").textValue());
                        Date currentDate = new Date(System.currentTimeMillis());
                        int entitlementQuantity = resourceObjNode.get("value").intValue();
                        if (startDate.before(currentDate) && endDate.after(currentDate)) {
                            updateAccountsEntitlementDataMap(unexpiredMockAccountsEntitlementData, entitlementName,
                                    entitlementQuantity, startDate, endDate);
                        } else if (startDate.before(currentDate) && endDate.before(currentDate)){
                            updateAccountsEntitlementDataMap(expiredMockAccountsEntitlementData, entitlementName,
                                    entitlementQuantity, startDate, endDate);
                        }
                    }
                } catch (Throwable e) {
                    log.error("Error in parsing Json from mock accounts {}", e.getMessage());
                    throw new Exception("Error in parsing Json from mock accounts {}" + e.getMessage());
                }
            }
        } else {
            log.error("Json mock accounts not in the correct format");
            throw new Exception("Json mock accounts not in the correct format");
        }

        log.info("Unexpired SKU data from mock accounts {}", unexpiredMockAccountsEntitlementData);
        log.info("Expired SKU data from mock accounts {}", expiredMockAccountsEntitlementData);
        return new ImmutablePair<>(unexpiredMockAccountsEntitlementData, expiredMockAccountsEntitlementData);

    }


    public static ImmutablePair<Map<String, AccountsSKU>, Map<String, AccountsSKU>> getSKUDataFromAccounts(
            String tenantId,
            AccountServiceClient accountServiceClient) throws IOException {

        List<ServiceEntitlementFull> serviceEntitlementFullList =
                accountServiceClient.getEntitlements(tenantId, SERVICE_NAME);
        Map<String, AccountsSKU> unexpiredAccountsEntitlementData = new HashMap<>();
        Map<String, AccountsSKU> expiredAccountsEntitlementData = new HashMap<>();


        for (ServiceEntitlementFull serviceEntitlement : serviceEntitlementFullList) {
            String data = serviceEntitlement.getData();
            String decodedData = new String(Base64.decodeBase64(data), StandardCharsets.UTF_8);
            JsonNode decodedDataJson = new ObjectMapper().readTree(decodedData);
            JsonNode resourceArray = decodedDataJson.get("resources");
            if (resourceArray.isArray()) {
                for (JsonNode resourceObjNode : resourceArray) {
                    try {
                        String entitlementName = getServiceEntitlementNameFromAccountsResourceName(resourceObjNode
                                .get("name").textValue());
                        if (!entitlementName.equals("OTHER_SKU")) {
                            Date startDate = DATE_FORMAT.parse(resourceObjNode.get("startDate").textValue());
                            Date endDate = DATE_FORMAT.parse(resourceObjNode.get("endDate").textValue());
                            Date currentDate = new Date(System.currentTimeMillis());
                            int entitlementQuantity = resourceObjNode.get("value").intValue();
                            if (startDate.before(currentDate) && endDate.after(currentDate)) {
                                updateAccountsEntitlementDataMap(unexpiredAccountsEntitlementData, entitlementName,
                                        entitlementQuantity, startDate, endDate);
                            } else if (startDate.before(currentDate) && endDate.before(currentDate)){
                                updateAccountsEntitlementDataMap(expiredAccountsEntitlementData, entitlementName,
                                        entitlementQuantity, startDate, endDate);
                            }
                        }
                    } catch (Throwable e) {
                        log.error("Error in parsing {}", e.getMessage());
                    }
                }
            }
        }

        log.info("Unexpired SKU data from accounts {}", unexpiredAccountsEntitlementData);
        log.info("Expired SKU data from accounts {}", expiredAccountsEntitlementData);
        return new ImmutablePair<>(unexpiredAccountsEntitlementData, expiredAccountsEntitlementData);

    }


}