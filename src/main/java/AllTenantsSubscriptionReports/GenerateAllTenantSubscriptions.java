package AllTenantsSubscriptionReports;

import AllTenantsSubscriptionReports.DataLoader.GlobalStoreHandler;
import AllTenantsSubscriptionReports.DataLoader.config.GlobalStoreConfig;
import AllTenantsSubscriptionReports.dao.config.ControlPlaneInfraUserConfiguration;
import AllTenantsSubscriptionReports.dao.config.FAWPodApiConfig;
import AllTenantsSubscriptionReports.dao.config.SizingConfiguration;
import AllTenantsSubscriptionReports.dao.core.FAWDao;
import AllTenantsSubscriptionReports.dao.core.TenantDAO;
import AllTenantsSubscriptionReports.dao.impl.GlobalResourceInfoUtils;
import AllTenantsSubscriptionReports.resource.TenantDetails;
import AllTenantsSubscriptionReports.resource.TenantResource;
import AllTenantsSubscriptionReports.service.TenantService;
import com.oracle.faw.cp.common.auth.VaultReader;
import com.oracle.faw.cp.common.utils.InstancePrincipalCertificateSupplierImpl;
import com.oracle.pic.account.api.AccountServiceClient;
import com.oracle.pic.commons.client.authentication.ClientAuthenticationConfig;
import com.oracle.pic.commons.client.authentication.ServiceAuthClientFilter;
import com.oracle.pic.commons.client.http.OracleHttpClientConfig;
import com.oracle.pic.commons.client.http.auth.TlsConfig;
import com.oracle.pic.commons.crypto.JCEProviders;
import com.oracle.pic.commons.ssl.DynamicSslContextProviderConfig;
import com.oracle.pic.identity.authentication.AuthServiceAuthenticationClient;
import com.oracle.pic.identity.authentication.entities.X509FederationRequest;
import com.oracle.pic.vault.model.GetSecretResponse;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.ws.rs.client.Client;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.security.Security;
import java.sql.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

@Log4j2
@Component
public class GenerateAllTenantSubscriptions {


    private static GlobalStoreHandler globalStoreHandler;

    private static GlobalResourceInfoUtils globalResourceInfo;

    private static AccountServiceClient accountServiceClient;

    private static TenantResource tenantResource;
    private static TenantService tenantService;
    private static FAWPodApiConfig fawPodApiConfig;

    private static TenantDAO tenantDAO;

    private static FAWDao fawDao;

    static VaultReader testVaultReader = new VaultReader() {
        @Override
        public GetSecretResponse getSecret(String secretPath) {
            return null;
        }

        @Override
        public byte[] getSecretAsBytes(String secretPath) {
//            return secretPath.getBytes();
            try {
                InputStream initialStream = Files.newInputStream(new File(secretPath).toPath());
                byte[] buffer = new byte[initialStream.available()];
                initialStream.read(buffer);
                return buffer;
            } catch (Throwable t){
                return new byte[0];
            }
        }

        @Override
        public char[] getSecretAsUtf8Chars(String secretPath) {
            return secretPath.toCharArray();
        }
    };


    @Scheduled(fixedDelay = 60000)
    public static void generate_all_subscriptions() throws SQLException {

        log.info("Current time is :: {}", LocalTime.now());
        globalStoreHandler = createGlobalStoreHandler();
        globalResourceInfo = new GlobalResourceInfoUtils();

        tenantDAO = new TenantDAO(globalStoreHandler,globalResourceInfo);

        fawDao = new FAWDao(globalStoreHandler,globalResourceInfo);

        accountServiceClient = createAccountServiceClient();

        tenantService = new TenantService(tenantDAO,testVaultReader,accountServiceClient,fawDao);

        fawPodApiConfig = createFAWPodApiConfig();

        tenantResource = new TenantResource(tenantService,fawPodApiConfig);

        globalStoreHandler.buildDataSource();

        DataSource dataSource = globalStoreHandler.getDataSource();

        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM TENANT_INFO");
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int countOfColumns = resultSetMetaData.getColumnCount();

        int num_tenants = 0;
        while (resultSet.next()) {
            num_tenants++;
            if(num_tenants>5) {
                break;
            }
            String data_tenantName = null;
            String data_tenantID = null;
            for (int i = 1; i <= countOfColumns; i++) {
                if (Objects.equals(resultSetMetaData.getColumnName(i), "TENANT_NAME")) {
                    data_tenantName = resultSet.getString(i);
                } else if (Objects.equals(resultSetMetaData.getColumnName(i), "TENANT_OCID")) {
                    data_tenantID = resultSet.getString(i);
                }
            }
                TenantDetails tenantDetails = new TenantDetails(data_tenantName,data_tenantID);
                tenantResource.refreshTenantSubAndSizing(tenantDetails);

        }
        //connection.close();
    }

    private static void loadBCProviderWithoutFipsMode() {
        if (Security.getProvider(BouncyCastleFipsProvider.PROVIDER_NAME) == null) {
            final SecureRandom entropySource = JCEProviders.getSecureRandom();
            Security.addProvider(new BouncyCastleFipsProvider(null, entropySource));
        }
    }

    private static FAWPodApiConfig createFAWPodApiConfig() {

        ControlPlaneInfraUserConfiguration  cp = new ControlPlaneInfraUserConfiguration();
        cp.setControlPlaneInfraUserOcid("/secret/oax-infra-saasprovdevfaw-iad/cmn-cpadminuserocid/latest");
        cp.setControlPlaneInfraUserFingerPrint( "/secret/oax-infra-saasprovdevfaw-iad/cmn-cpadminfingerprint/latest");
        cp.setControlPlaneInfraUserInternalAPIKey( "ocid1.tenancy.oc1..aaaaaaaax2mwvpdolnhtsqz3t5sbunrgsxzqd7gjtb54sd7uo34mdeqxspiq/ocid1.user.oc1..aaaaaaaa6s3xp37usrqukdn64ogtgqxje7ujf5qcpn6hwph3xfs6nvmjorca/52:e3:7d:32:22:ab:1e:3d:89:f8:84:0c:29:b2:e5:84");
        cp.setControlPlaneInfraUserTenantOcid("ocid1.tenancy.oc1..aaaaaaaax2mwvpdolnhtsqz3t5sbunrgsxzqd7gjtb54sd7uo34mdeqxspiq");
        cp.setControlPlaneInfraUserPrivateKeyFilePath("/secret/oax-infra-saasprovdevfaw-iad/cmn-cpadminprivatekeyssh/latest");
        SizingConfiguration sc = new SizingConfiguration() ;
        sc.setBucketName("service-sizing-config");
        sc.setRegion("us-ashburn-1");
        sc.setBucketNameSpace("saasprovdevfaw");
        sc.setSizingConfigIntendedUseToCSVNameMap("sizing-config-intended-use-to-csv-name.json");
        FAWPodApiConfig fp = new FAWPodApiConfig();
        fp.setSizingConfiguration(sc);
        fp.setControlPlaneInfraUserConfiguration(cp);
        return fp;
    }

    private static class LimitsClientConfig implements ClientAuthenticationConfig {

        String CA_BUNDLE_PATH ="/Users/arymehta/Downloads/tls-ca-bundle.pem";
        @Override
        public void configure(Client client) {

            loadBCProviderWithoutFipsMode();

            DynamicSslContextProviderConfig trustRootConfig =
                    new DynamicSslContextProviderConfig(null, null, null,
                            CA_BUNDLE_PATH,
                            Duration.ofMinutes(10), "TLS");
            AuthServiceAuthenticationClient serviceAuthenticationClient = AuthServiceAuthenticationClient.builder()
                    .authServiceEndpoint("https://auth.us-ashburn-1.oraclecloud.com")
                    .certificateSupplier(
                            new InstancePrincipalCertificateSupplierImpl("http://localhost:8000/opc/v1/"))
                    .trustRootConfig(trustRootConfig)
                    .purpose(X509FederationRequest.Purpose.SERVICE_PRINCIPAL)
                    .expirationBufferInSeconds(5)
                    .globalBusinessUnit("ProdDev-TK-Core")
                    .teamName("Oracle Analytics for Applications")
                    .applicationName("Control Plane Service API")
                    .build();
            client.register(new ServiceAuthClientFilter(serviceAuthenticationClient, null));
        }
    }

    private static AccountServiceClient createAccountServiceClient() {

        String CA_BUNDLE_PATH = "/Users/arymehta/Downloads/tls-ca-bundle.pem";
        String accountsServiceEndpoint = "https://accounts.us-ashburn-1.oracleiaas.com/v1";

        TlsConfig tlsConfig = TlsConfig.builder()
                .caBundle(CA_BUNDLE_PATH)
                .build();


        ClientAuthenticationConfig clientAuthenticationConfig = new LimitsClientConfig();

        OracleHttpClientConfig oracleHttpClientConfig = OracleHttpClientConfig.builder()
                .authenticationConfig(clientAuthenticationConfig)
                .endpoint(accountsServiceEndpoint)
                .asyncThreadPoolSize(5)
                .tlsConfig(tlsConfig)
                .build();
        accountServiceClient  = new AccountServiceClient(oracleHttpClientConfig);
        return accountServiceClient;
    }

    private static GlobalStoreHandler createGlobalStoreHandler() {

        GlobalStoreConfig globalStoreConfig = new GlobalStoreConfig();
        globalStoreConfig.setPasswordPath("Aanya@20012006");
        globalStoreConfig.setRegion("us-ashburn-1");
        globalStoreConfig.setGlobalStoreId("ocid1.autonomousdatabase.oc1.iad.anuwcljt3nl7vkiagpigxjdlny23ij3pkuet7s2x7gzpkrnnwexuuiej5hqa");
        globalStoreConfig.setWalletPath("/Users/arymehta/Desktop/Oracle_files/Wallet_aryanTestATP.zip");
        globalStoreConfig.setWalletPassword("Aanya@20012006");
        globalStoreConfig.setRepairFlyway(true);
        globalStoreConfig.setMaximumPoolSize(30);
        globalStoreConfig.setMinimumIdle(10);
        globalStoreConfig.setConnectionTimeout(60);
        globalStoreConfig.setIdleTimeout(300);
        globalStoreConfig.setMaxLifetime(600);
        globalStoreConfig.setKeepAliveTime(300);
        globalStoreConfig.setDriverReadTimeout(600);
        globalStoreConfig.setDriverConnectTimeout(600);



        GlobalStoreHandler gs =  new GlobalStoreHandler(globalStoreConfig, testVaultReader);
        return gs;
    }
}
