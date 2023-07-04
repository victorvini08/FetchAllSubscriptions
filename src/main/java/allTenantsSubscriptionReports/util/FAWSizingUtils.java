package allTenantsSubscriptionReports.util;


import allTenantsSubscriptionReports.dao.api.service.*;
import allTenantsSubscriptionReports.dao.config.FAWPodApiConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.faw.cp.common.auth.VaultReader;
import com.oracle.faw.cp.common.exception.RestCallException;
import com.oracle.faw.cp.common.exception.TranslatableException;
import com.oracle.faw.cp.common.exception.TranslatableExceptionCode;
import com.oracle.faw.cp.common.pojo.cp.SizingConfigCSVNames;
import com.oracle.faw.cp.common.pojo.cp.SizingFile;
import com.oracle.faw.cp.common.pojo.cp.SizingFileRecord;
import com.oracle.faw.cp.common.utils.RESTUtils;
import allTenantsSubscriptionReports.dao.config.ControlPlaneInfraUserConfiguration;
import allTenantsSubscriptionReports.dao.config.SizingConfiguration;
import allTenantsSubscriptionReports.dao.utils.OCIAuthDetailsProvider;
//import lombok.extern.slf4j.Slf4j;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.time.Instant;
import java.util.stream.Collectors;

//@Slf4j
@Log4j2
public class FAWSizingUtils {

    //private static final Logger log = LoggerFactory.getLogger(FAWSizingUtils.class);
    private static VaultReader vaultReader;
    private static final String DEV_INTENDED_USE = "dev";
    private static final String TEST_INTENDED_USE = "test";
    private static final String LAB_INTENDED_USE = "lab";
    private static final String PROD_INTENDED_USE = "prod";

    private static final String SIZING_SHEET_APP_TYPE_COLUMN_NAME = "AppType";
    private static final String SIZING_SHEET_NUM_USERS_COLUMN_NAME = "NumUsers";
    private static final String SIZING_SHEET_ADW_OCPU_COUNT_COLUMN_NAME = "adw-ocpu-count";
    private static final String SIZING_SHEET_ADW_TOTAL_STORAGE_COLUMN_NAME = "adw-total-storage-tb";
    private static final String SIZING_SHEET_OAC_OCPU_COUNT_COLUMN_NAME = "ee-ocpu-count";

    private static final String EXPIRATION_STATUS_ACTIVE = "Active";
    private static final String EXPIRATION_STATUS_EXPIRED = "Expired";

    public static TenantSubAndSizingTO buildTenantSubAndSizingTO(String tenantName, String tenantOcid,
                                                                 List<FAWEntitlementTO> accountsEntitlementList,
                                                                 List<TenantSizingDetailsTO> tenantSizing) {

        TenantSubAndSizingTO tenantSubAndSizingTO;
        tenantSubAndSizingTO = TenantSubAndSizingTO.builder()
                .tenantName(tenantName)
                .tenantOcid(tenantOcid)
                .lastUpdatedAt(Instant.now().toEpochMilli())
                .listOfEntitlements(accountsEntitlementList)
                .tenantSizingDetails(tenantSizing)
                .build();
        return tenantSubAndSizingTO;

    }

    private enum IntendedUse {

        DEV_INTENDED_USE("dev"), TEST_INTENDED_USE("test"), LAB_INTENDED_USE("lab"), PROD_INTENDED_USE("prod"),
        ADDITIONAL_ENVIRONMENT_INTENDED_USE("add_env");
        private final String intendedUse;
        IntendedUse(String intendedUse) {
            this.intendedUse = intendedUse;
        }
    }

    public static void setVaultReader(VaultReader vaultReaderClient) {
        vaultReader = vaultReaderClient;
    }

    public static List<TenantSizingDetailsTO> getTenantSizing(FAWPodApiConfig fawPodApiConfig,
                                                              List<String> fawApplicationList) throws Exception {
        List<TenantSizingDetailsTO> tenantSizingDetailsList = new ArrayList<>();
        TenantSizingDetailsTO devSizing = calcTenantSizing(fawPodApiConfig, fawApplicationList,
                IntendedUse.DEV_INTENDED_USE.intendedUse);
        TenantSizingDetailsTO prodSizing = calcTenantSizing(fawPodApiConfig, fawApplicationList,
                IntendedUse.PROD_INTENDED_USE.intendedUse);

        List<String> intendedUses =  Arrays.asList(DEV_INTENDED_USE,TEST_INTENDED_USE,LAB_INTENDED_USE,
                PROD_INTENDED_USE);

        for (String use : intendedUses) {
            boolean isProdUse = use.equalsIgnoreCase(PROD_INTENDED_USE);
            TenantSizingDetailsTO tenantSizingDetails =
                    TenantSizingDetailsTO.builder()
                            .instanceType(use)
                            .adwCpuCount(isProdUse ? prodSizing.getAdwCpuCount() : devSizing.getAdwCpuCount())
                            .adwStorage(isProdUse ? prodSizing.getAdwStorage() : devSizing.getAdwStorage())
                            .oacOlpuCount(isProdUse ? prodSizing.getOacOlpuCount() : devSizing.getOacOlpuCount())
                            .build();
            tenantSizingDetailsList.add(tenantSizingDetails);
        }
        System.out.println("Fetched TenantSizingDetailsList {} " + tenantSizingDetailsList);
        //log.info("Fetched TenantSizingDetailsList {} " , tenantSizingDetailsList);
        return tenantSizingDetailsList;
    }

    private static TenantSizingDetailsTO calcTenantSizing(FAWPodApiConfig fawPodApiConfig,
                                                          List<String> fawApplicationList,
                                                          String intendedUse) throws Exception {

        SizingFile sizingFile = loadSizingConfig(fawPodApiConfig, intendedUse);
        Map<String, Integer> sizingAggregate =  getSizingAggregate(fawApplicationList, sizingFile);
        return TenantSizingDetailsTO.builder()
                .adwStorage(sizingAggregate.get("adwDataStorageSizeinTBs"))
                .oacOlpuCount(sizingAggregate.get("oacOlpuCount"))
                .adwCpuCount(sizingAggregate.get("adwExtCpuCoreCount"))
                .build();
    }

    private static SizingFile loadSizingConfig(FAWPodApiConfig fawPodApiConfig, String intendedUse) throws Exception {
        log.info("Loading Control Plane User Config" + fawPodApiConfig.getControlPlaneInfraUserConfiguration()
                .getControlPlaneInfraUserTenantOcid());
        String sizing_config_dev = "/Users/arymehta/Downloads/sizing-config-v51-dev.csv";
//        String sizingConfigCSVDownloadedFilePath = getSizingCSVNameBasedOnIntendedUse(
//                fawPodApiConfig.getControlPlaneInfraUserConfiguration(),
//                fawPodApiConfig.getSizingConfiguration(), intendedUse);
        return readSizingFile(sizing_config_dev);
    }

    private static String getSizingCSVNameBasedOnIntendedUse(
            ControlPlaneInfraUserConfiguration controlPlaneInfraUserConfiguration,
            SizingConfiguration sizingConfiguration, String intendedUse) throws Exception {

        SizingConfigCSVNames sizingConfigCSVNames = null;
        BufferedReader br = null;
        String downloadFilePath;
        String sizingConfigIntendedUseToCSVNameMap = sizingConfiguration.getSizingConfigIntendedUseToCSVNameMap();
        try {
            checkIfFileExistsInObjectStore(controlPlaneInfraUserConfiguration, sizingConfiguration,
                    sizingConfigIntendedUseToCSVNameMap);
        } catch (Exception e) {
            log.error("File not found in the object store", e);
            if (TranslatableException.isTranslatable(e.getMessage())) {
                throw new Exception(e.getMessage(), e);
            }
            throw new Exception(TranslatableException.JsonStringify(
                    TranslatableExceptionCode.WrkExceptionCode.NO_FILE_FOUND.getCode()), e);
        }
        try {
            downloadFilePath = downloadSizingLocationFileFromObjectStore(controlPlaneInfraUserConfiguration,
                    sizingConfiguration, sizingConfigIntendedUseToCSVNameMap);
            log.info("Download File Path Name: {}" , downloadFilePath);
            log.info("Reading the sizing-config-to-intended-use json file");
            br = new BufferedReader(new FileReader(downloadFilePath));
            String line;
            StringBuilder sizingConfigIntendedUseToCSVNameJson = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sizingConfigIntendedUseToCSVNameJson.append(line);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            sizingConfigCSVNames = objectMapper.readValue(sizingConfigIntendedUseToCSVNameJson.toString(),
                    SizingConfigCSVNames.class);
            log.info("sizing Config CSVNames is {}" , sizingConfigCSVNames);
        } catch (Exception e) {
            log.error("Error getting or processing the sizing config CSV names map from the object store ", e);
            if (TranslatableException.isTranslatable(e.getMessage())) {
                throw new Exception(e.getMessage(), e);
            }
            throw new Exception(TranslatableException.JsonStringify(
                    TranslatableExceptionCode.WrkExceptionCode.ERR_SZNG_CSV.getCode()), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    if (TranslatableException.isTranslatable(e.getMessage())) {
                        throw new RestCallException(e.getMessage(), e);
                    }
                    throw new RestCallException(TranslatableException.JsonStringify(
                            TranslatableExceptionCode.WrkExceptionCode.SZNG_CSV_REST_CALL.getCode()), e);
                }
            }
        }
        String csvFileName;

        if (intendedUse.equalsIgnoreCase("dev") || intendedUse.equalsIgnoreCase("test") ||
                intendedUse.equalsIgnoreCase("add_env") || intendedUse.equalsIgnoreCase("lab")) {
            if (sizingConfigCSVNames != null && StringUtils.isNotBlank(sizingConfigCSVNames
                    .getDevSizingConfigCSVName())) {
                log.info("DevSizingConfigCSVName : {}", sizingConfigCSVNames.getDevSizingConfigCSVName());
                csvFileName = sizingConfigCSVNames.getDevSizingConfigCSVName();
            } else {
                log.error("The sizing config csv name is null");
                throw new IOException(TranslatableException.JsonStringify(
                        TranslatableExceptionCode.WrkExceptionCode.SZNG_CSV_NULL.getCode()));
            }
        } else if (intendedUse.equalsIgnoreCase("prod")) {
            if (sizingConfigCSVNames != null && StringUtils.isNotBlank(sizingConfigCSVNames
                    .getProdSizingConfigCSVName())) {
                csvFileName = sizingConfigCSVNames.getProdSizingConfigCSVName();
            } else {
                log.error("The sizing config csv name is null");
                throw new IOException(TranslatableException.JsonStringify(
                        TranslatableExceptionCode.WrkExceptionCode.SZNG_CSV_NULL.getCode()));
            }
        } else {
            throw new Exception(TranslatableException.JsonStringify(
                    TranslatableExceptionCode.WrkExceptionCode.INVALID_INTEND_TYPE.getCode()));
        }

        String downloadFilePathCSV = downloadSizingLocationFileFromObjectStore(controlPlaneInfraUserConfiguration,
                sizingConfiguration, csvFileName);
        log.info("downloadFilePathCSV Name is : {}", downloadFilePathCSV);
        return downloadFilePathCSV;

    }

    private static void checkIfFileExistsInObjectStore(ControlPlaneInfraUserConfiguration cpInfraUserConfig,
                                                       SizingConfiguration sizingConfiguration,
                                                       String objectName) {
        log.info("Checking if file exists in object store {}, for vaultReader {} ", objectName, vaultReader);
        ObjectStorageClient objectStorageClient = new ObjectStorageClient(
                new OCIAuthDetailsProvider(cpInfraUserConfig, sizingConfiguration.getRegion(), vaultReader));

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucketName(sizingConfiguration.getBucketName())
                .objectName(objectName)
                .namespaceName(sizingConfiguration.getBucketNameSpace())
                .retryConfiguration(RESTUtils.getRetryConfiguration())
                .build();

        objectStorageClient.getObject(getObjectRequest);

    }

    private static String downloadSizingLocationFileFromObjectStore(ControlPlaneInfraUserConfiguration
                                                                            cpInfraUserConfig, SizingConfiguration
                                                                            sizingConfiguration, String objectName) throws Exception {

        ObjectStorageClient objectStorageClient = new ObjectStorageClient(
                new OCIAuthDetailsProvider(cpInfraUserConfig, sizingConfiguration.getRegion(), vaultReader));

        String tempDirName = System.getProperty("java.io.tmpdir") + File.separator + "sizingConfig";
        String sizingConfigFilePath = tempDirName + File.separator + objectName;
        File sizingConfigFile = new File(sizingConfigFilePath);
        FileOutputStream out = null;
        InputStream inputStream = null;

        if (sizingConfigFile.exists()) {
            try {
                Files.deleteIfExists(Paths.get(sizingConfigFilePath));
            } catch (Exception e) {
                log.error("Could not delete the existing file");
                if (TranslatableException.isTranslatable(e.getMessage())) {
                    throw new Exception(e.getMessage());
                }
                throw new Exception(TranslatableException.JsonStringify(
                        TranslatableExceptionCode.WrkExceptionCode.DEL_FILE_ERR.getCode()), e);
            }
        }
        try {
            if (!sizingConfigFile.exists()) {
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucketName(sizingConfiguration.getBucketName())
                        .objectName(objectName)
                        .namespaceName(sizingConfiguration.getBucketNameSpace())
                        .retryConfiguration(RESTUtils.getRetryConfiguration())
                        .build();

                GetObjectResponse getObjectResponse = objectStorageClient.getObject(getObjectRequest);

                Path tempDirPath = Paths.get(tempDirName);
                if (!Files.exists(tempDirPath)) {
                    Files.createDirectories(tempDirPath);
                }

                out = new FileOutputStream(sizingConfigFilePath);
                inputStream = getObjectResponse.getInputStream();
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                inputStream.close();
                out.close();
            }
        } catch (Exception e) {
            if (TranslatableException.isTranslatable(e.getMessage())) {
                throw new RestCallException(e.getMessage());
            }
            throw new RestCallException(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                log.error("Couldn't Close Stream.");
            }
        }
        return sizingConfigFilePath;
    }

    private static SizingFile readSizingFile(String sizingConfigFilePath) throws RestCallException {

        log.info("Inside readSizingFile sizingConfigFilePath: {}", sizingConfigFilePath);
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean isFirstRow = true;
        Map<String, Integer> columnNameMap = new HashMap<>();
        List<SizingFileRecord> sizingFileRecords = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(sizingConfigFilePath));
            while ((line = br.readLine()) != null) {
                if (isFirstRow) {
                    String[] columnNamesList = line.split(cvsSplitBy);
                    for (int i = 0; i < columnNamesList.length; i++) {
                        columnNameMap.put(columnNamesList[i], i);
                    }
                    isFirstRow = false;
                    continue;
                }
                String[] sizingDetails = line.split(cvsSplitBy);
                SizingFileRecord sizingFileRecord = SizingFileRecord.builder()
                        .appType(sizingDetails[columnNameMap.get(SIZING_SHEET_APP_TYPE_COLUMN_NAME)])
                        .numUsers(Integer.valueOf(sizingDetails[columnNameMap
                                .get(SIZING_SHEET_NUM_USERS_COLUMN_NAME)]))
                        .adwExtOCPUCount(Integer.valueOf(sizingDetails[columnNameMap
                                .get(SIZING_SHEET_ADW_OCPU_COUNT_COLUMN_NAME)]))
                        .adwStorageInTB(Integer.valueOf(sizingDetails[columnNameMap
                                .get(SIZING_SHEET_ADW_TOTAL_STORAGE_COLUMN_NAME)]))
                        .oacOLPUCount(Integer.valueOf(sizingDetails[columnNameMap
                                .get(SIZING_SHEET_OAC_OCPU_COUNT_COLUMN_NAME)]))
                        .build();
                sizingFileRecords.add(sizingFileRecord);
            }
        } catch (IOException e) {
            log.error("Exception while adding the sizing file object", e);
            if (TranslatableException.isTranslatable(e.getMessage())) {
                throw new RestCallException(e.getMessage(), e);
            }
            throw new RestCallException(TranslatableException.JsonStringify(
                    TranslatableExceptionCode.WrkExceptionCode.EXC_ADD_SZNG_OBJ.getCode()), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    if (TranslatableException.isTranslatable(e.getMessage())) {
                        throw new RestCallException(e.getMessage(), e);
                    }
                    throw new RestCallException(TranslatableException.JsonStringify(
                            TranslatableExceptionCode.WrkExceptionCode.SZNG_CSV_REST_CALL.getCode()), e);
                }
            }
        }

        return SizingFile.builder()
                .sizingFileRecords(sizingFileRecords)
                .build();

    }

    public static Map<String, Integer> getSizingAggregate(List<String> fawApplicationList,
                                                          SizingFile sizingFile)
            throws Exception {

        Integer extCpuCount = 0;
        Integer dataStorage = 0;
        Integer oacOlpuCount = 0;


        int searchIndex;
        int userRangeSize;

        int inputNumberOfUsers;
        log.info("Faw Application List {} ", fawApplicationList);
        try {
            for (String fawApplicationStr : fawApplicationList) {
                FAWEntitlementTO fawApplication = FAWEntitlementTO.fromJsonString(fawApplicationStr);
                log.info("Fetched FAWApplication Name {}", fawApplication);
                String applicationName = fawApplication.getApplicationType().name();
                inputNumberOfUsers = fawApplication.getNumberOfUsersForApplication();
                log.info("Fetching sizing information for application: {}, userRange: {}", applicationName,
                        inputNumberOfUsers);

                if (inputNumberOfUsers < 1) {
                    log.info("Number of users cannot be < 1");
                    throw new Exception(TranslatableException.JsonStringify(
                            TranslatableExceptionCode.WrkExceptionCode.NUM_USER_ERR.getCode()));
                }

                List<SizingFileRecord> sizingFileRecords = sizingFile.getSizingFileRecords().stream()
                        .filter(record -> record.getAppType()
                                .equalsIgnoreCase(applicationName.replaceFirst("^TRIAL_","")))
                        .sorted(Comparator.comparing(SizingFileRecord::getNumUsers))
                        .collect(Collectors.toList());

                userRangeSize = sizingFileRecords.size();
                searchIndex = userRangeSize;
                for (int i = 1; i < sizingFileRecords.size(); i++) {
                    if (inputNumberOfUsers < sizingFileRecords.get(i).getNumUsers()) {
                        searchIndex = i - 1;
                        break;
                    }
                }

                searchIndex = (searchIndex == userRangeSize) ? (searchIndex - 1) : searchIndex;
                extCpuCount += sizingFileRecords.get(searchIndex).getAdwExtOCPUCount();
                dataStorage += sizingFileRecords.get(searchIndex).getAdwStorageInTB();
                oacOlpuCount += sizingFileRecords.get(searchIndex).getOacOLPUCount();
            }
        } catch (Exception e) {
            if (TranslatableException.isTranslatable(e.getMessage())) {
                throw new RestCallException(e.getMessage());
            }
            throw new RestCallException(e);
        }

        log.info("External ADW Aggregate CPU Core Count: {}", extCpuCount);
        log.info("ADW Storage Aggregate TB Count: {}", dataStorage);
        log.info("OAC Olpu Count: {}", oacOlpuCount);

        Map<String, Integer> aggMetricValues = new HashMap<>();

        aggMetricValues.put("adwExtCpuCoreCount", extCpuCount);
        aggMetricValues.put("adwDataStorageSizeinTBs", dataStorage);
        aggMetricValues.put("oacOlpuCount", oacOlpuCount);

        return aggMetricValues;
    }

    public static List<FAWEntitlementTO> buildAccountsEntitlementList(ImmutablePair<Map<String, AccountsSKU>,
            Map<String, AccountsSKU>> accountsEntitlementData) {

        Map<String, AccountsSKU> unexpiredAccountsEntitlementData = accountsEntitlementData.getLeft();
        Map<String, AccountsSKU> expiredAccountsEntitlementData = accountsEntitlementData.getRight();
        List<FAWEntitlementTO> fawEntitlementTOList = new ArrayList<>();

        for (String entitlementName : expiredAccountsEntitlementData.keySet()) {
            if (!entitlementName.equals("ADDITIONAL_ENVIRONMENTS")) {
                if (!unexpiredAccountsEntitlementData.containsKey(entitlementName)) {
                    fawEntitlementTOList.add(FAWEntitlementTO.builder()
                            .applicationType(FAWApplicationType.valueOf(entitlementName))
                            .numberOfUsersForApplication(expiredAccountsEntitlementData.get(entitlementName)
                                    .getQuantity())
                            .startDate(expiredAccountsEntitlementData.get(entitlementName).getStartDate())
                            .endDate(expiredAccountsEntitlementData.get(entitlementName).getEndDate())
                            .expirationStatus(EXPIRATION_STATUS_EXPIRED)
                            .build());
                }
            }

        }

        for (String entitlementName : unexpiredAccountsEntitlementData.keySet()) {
            if (!entitlementName.equals("ADDITIONAL_ENVIRONMENTS")) {
                fawEntitlementTOList.add(FAWEntitlementTO.builder()
                        .applicationType(FAWApplicationType.valueOf(entitlementName))
                        .numberOfUsersForApplication(unexpiredAccountsEntitlementData.get(entitlementName)
                                .getQuantity())
                        .startDate(unexpiredAccountsEntitlementData.get(entitlementName).getStartDate())
                        .endDate(unexpiredAccountsEntitlementData.get(entitlementName).getEndDate())
                        .expirationStatus(EXPIRATION_STATUS_ACTIVE)
                        .build());
            }

        }

        return fawEntitlementTOList;
    }

    public static List<FAWEntitlementTO> buildEntitlementListForTrials( FAWApprovedTrialTO approvedTrialEntitlements) {
        List<FAWEntitlementTO> fawEntitlementTOList = new ArrayList<>();
        Map<FAWApplicationType, Integer> applicationInfos = getApplicationInfo(approvedTrialEntitlements);
        for (FAWApplicationType applicationName : applicationInfos.keySet()) {
            fawEntitlementTOList.add(FAWEntitlementTO.builder()
                    .applicationType(applicationName)
                    .numberOfUsersForApplication(applicationInfos.get(applicationName))
                    .startDate(new java.util.Date(approvedTrialEntitlements.getTrialStartDate()))
                    .endDate(new java.util.Date(approvedTrialEntitlements.getTrialEndDate()))
                    .expirationStatus((approvedTrialEntitlements.getTrialEndDate() > System.currentTimeMillis()) ?
                            EXPIRATION_STATUS_ACTIVE : EXPIRATION_STATUS_EXPIRED)
                    .build());
        }
        return fawEntitlementTOList;
    }

    public static Map<FAWApplicationType, Integer> getApplicationInfo(FAWApprovedTrialTO approvedTrialEntitlements) {
        Map<FAWApplicationType, Integer> applicationInfoMap = new HashMap<>();
        if (approvedTrialEntitlements.isErpEnabled()) {
            applicationInfoMap.put(FAWApplicationType.TRIAL_FUSION_ERP,
                    approvedTrialEntitlements.getNumberOfErpUsers());
        }
        if (approvedTrialEntitlements.isHcmEnabled()) {
            applicationInfoMap.put(FAWApplicationType.TRIAL_FUSION_HCM,
                    approvedTrialEntitlements.getNumberOfHcmUsers());
        }
        if (approvedTrialEntitlements.isScmEnabled()) {
            applicationInfoMap.put(FAWApplicationType.TRIAL_FUSION_SCM,
                    approvedTrialEntitlements.getNumberOfScmUsers());
        }
        if (approvedTrialEntitlements.isCxEnabled()) {
            applicationInfoMap.put(FAWApplicationType.TRIAL_FUSION_CX,
                    approvedTrialEntitlements.getNumberOfCxUsers());
        }
        return applicationInfoMap;
    }

}
