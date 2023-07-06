package AllTenantsSubscriptionReports.DataLoader;
import AllTenantsSubscriptionReports.DataLoader.config.GlobalStoreConfig;
import com.oracle.faw.cp.common.auth.VaultReader;
import com.oracle.pic.vault.model.GetSecretResponse;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.*;

public class TestGlobalStoreHandler {
    private static GlobalStoreConfig globalStoreConfig;

    private static VaultReader testVaultReader = new VaultReader() {
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
    public static void main(String[] args) throws SQLException {


        globalStoreConfig = new GlobalStoreConfig();
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

        gs.buildDataSource();

        DataSource dataSource = gs.getDataSource();

        Connection connection = dataSource.getConnection();

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM TENANT_INFO");
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int countOfColumns = resultSetMetaData.getColumnCount();

        while (resultSet.next()) {
            for (int i = 1; i <= countOfColumns; i++) {
                System.out.println(resultSetMetaData.getColumnName(i) + " " + resultSet.getString(i));
            }
        }

    }
}
