//package AllTenantsSubscriptionReports;
//
//import AllTenantsSubscriptionReports.DataLoader.GlobalStoreHandler;
//import AllTenantsSubscriptionReports.DataLoader.config.GlobalStoreConfig;
//import com.oracle.faw.cp.common.auth.VaultReader;
//import com.oracle.pic.vault.model.GetSecretResponse;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.io.File;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.sql.*;
//import java.util.Objects;
//
//@Component
//public class TestScheduler {
//
//    public TestScheduler() { }
//
//    private static GlobalStoreHandler globalStoreHandler;
//
//    static VaultReader testVaultReader = new VaultReader() {
//        @Override
//        public GetSecretResponse getSecret(String secretPath) {
//            return null;
//        }
//
//        @Override
//        public byte[] getSecretAsBytes(String secretPath) {
////            return secretPath.getBytes();
//            try {
//                InputStream initialStream = Files.newInputStream(new File(secretPath).toPath());
//                byte[] buffer = new byte[initialStream.available()];
//                initialStream.read(buffer);
//                return buffer;
//            } catch (Throwable t){
//                return new byte[0];
//            }
//        }
//
//        @Override
//        public char[] getSecretAsUtf8Chars(String secretPath) {
//            return secretPath.toCharArray();
//        }
//    };
//
//       // @Scheduled(cron = "*/10 * * * * *")
//       // @Qualifier("scheduler_test")
//        public void test_scheduler() {
//            System.out.println(java.time.LocalTime.now());
//        }
//
//        //@Scheduled(fixedDelay = 60000)
//        //@Qualifier("scheduler_globalstore_test")
//        public void test_scheduler_global_store() throws SQLException {
//
//            System.out.println("Start time: "+java.time.LocalTime.now());
//
//            GlobalStoreConfig globalStoreConfig = new GlobalStoreConfig();
//            globalStoreConfig.setPasswordPath("Aanya@20012006");
//            globalStoreConfig.setRegion("us-ashburn-1");
//            globalStoreConfig.setGlobalStoreId("ocid1.autonomousdatabase.oc1.iad.anuwcljt3nl7vkiagpigxjdlny23ij3pkuet7s2x7gzpkrnnwexuuiej5hqa");
//            globalStoreConfig.setWalletPath("/Users/arymehta/Desktop/Oracle_files/Wallet_aryanTestATP.zip");
//            globalStoreConfig.setWalletPassword("Aanya@20012006");
//            globalStoreConfig.setRepairFlyway(true);
//            globalStoreConfig.setMaximumPoolSize(30);
//            globalStoreConfig.setMinimumIdle(10);
//            globalStoreConfig.setConnectionTimeout(60);
//            globalStoreConfig.setIdleTimeout(300);
//            globalStoreConfig.setMaxLifetime(600);
//            globalStoreConfig.setKeepAliveTime(300);
//            globalStoreConfig.setDriverReadTimeout(600);
//            globalStoreConfig.setDriverConnectTimeout(600);
//
//
//            globalStoreHandler = new GlobalStoreHandler(globalStoreConfig, testVaultReader);
//
//
//            globalStoreHandler.buildDataSource();
//
//            DataSource dataSource = globalStoreHandler.getDataSource();
//
//            Connection connection = dataSource.getConnection();
//
//            Statement statement = connection.createStatement();
//
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM TENANT_INFO");
//            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
//            int countOfColumns = resultSetMetaData.getColumnCount();
//
//
//            while (resultSet.next()) {
//                String data_tenantName = null;
//                String data_tenantID = null;
//                for (int i = 1; i <= countOfColumns; i++) {
//                    if (Objects.equals(resultSetMetaData.getColumnName(i), "TENANT_NAME")) {
//                        data_tenantName = resultSet.getString(i);
//                    } else if (Objects.equals(resultSetMetaData.getColumnName(i), "TENANT_OCID")) {
//                        data_tenantID = resultSet.getString(i);
//                    }
//                }
//                System.out.println("Tenant name: " + data_tenantName + " and Tenant OCID: " + data_tenantID);
//            }
//            System.out.println("End time: "+java.time.LocalTime.now());
//        }
//
//
//    }
