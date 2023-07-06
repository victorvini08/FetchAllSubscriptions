package AllTenantsSubscriptionReports.DataLoader;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import com.google.inject.Singleton;
import com.oracle.faw.cp.common.auth.VaultReader;
import com.oracle.faw.cp.common.pojo.globalstore.GlobalStoreATPCred;
import AllTenantsSubscriptionReports.DataLoader.config.GlobalStoreConfig;
import AllTenantsSubscriptionReports.DataLoader.config.GlobalStoreConnectionHolder;
import AllTenantsSubscriptionReports.DataLoader.exceptions.FAWDaoException;
import AllTenantsSubscriptionReports.DataLoader.utils.GlobalStoreDatabaseUtils;
import io.dropwizard.lifecycle.Managed;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import oracle.jdbc.pool.OracleDataSource;
import com.zaxxer.hikari.HikariDataSource;

import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
@Singleton
@Getter
public class GlobalStoreHandler implements Managed {

    private final String GLOBAL_STORE_USERNAME = "ADMIN";
    private final String GLOBAL_STORE_SERVICENAME = "oaxglobalstore";
    private final String JDBC_URL = "jdbc:oracle:thin:@";
    private final int CONN_TIMEOUT_SECONDS = 5;
    private final int MAX_RETRIES = 3;
    private static String JDBC_DRIVER_READ_TIMEOUT = "oracle.jdbc.ReadTimeout";
    private static String JDBC_DRIVER_CONNECT_TIMEOUT = "oracle.net.CONNECT_TIMEOUT";

    private final GlobalStoreConfig globalStoreConfig;
    private DataSource dataSource;
    private VaultReader vaultReader;


    @Inject
    public GlobalStoreHandler(GlobalStoreConfig globalStoreConfig,
                              VaultReader vaultReader) {
        this.globalStoreConfig = globalStoreConfig;
        this.vaultReader = vaultReader;
        log.info("Building global store datasource");
        buildDataSource();
        log.info("Global store data source created successfully");
    }

    public void buildDataSource() {
        try {
            if (this.dataSource == null) {
                initializeDataSource();
            }
            Connection testConn = this.dataSource.getConnection();
            if (!testConn.isValid(CONN_TIMEOUT_SECONDS)) {
                initializeDataSource();
            }
        } catch (Exception e) {
            try {
                initializeDataSource();
            } catch (Exception e1) {
                log.error("Error while connecting to Global Store :: {}",e1);
                throw new FAWDaoException(e1);
            }
        }
    }

    private void initializeDataSource() throws Exception {

        log.info("Initializing global store data source....");
        GlobalStoreDatabaseUtils globalStoreDbUtils =
                new GlobalStoreDatabaseUtils(globalStoreConfig.getWalletPath(), globalStoreConfig.getWalletPassword(),
                        vaultReader);
        GlobalStoreConnectionHolder globalStoreConnectionHolder = globalStoreDbUtils.buildGlobalStoreConnectionHolder();

        String url = JDBC_URL + globalStoreConnectionHolder.getServiceUrl();
        OracleDataSource oracleDataSource = new OracleDataSource();
        //System.out.println(new String(vaultReader.getSecretAsUtf8Chars(globalStoreConfig.getPasswordPath())));
        oracleDataSource.setUser(GLOBAL_STORE_USERNAME);
        oracleDataSource.setPassword(new String(vaultReader.getSecretAsUtf8Chars(globalStoreConfig.getPasswordPath())));
        oracleDataSource.setURL(url);

        if (globalStoreConnectionHolder.getSslContext() != null) {
            oracleDataSource.setSSLContext(globalStoreConnectionHolder.getSslContext());
        }
        log.info("Common Oracle Datasource created....");

        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setMaximumPoolSize(globalStoreConfig.getMaximumPoolSize());
        hikariDataSource.setMinimumIdle(globalStoreConfig.getMinimumIdle());
        hikariDataSource.setConnectionTimeout(SECONDS.toMillis(globalStoreConfig.getConnectionTimeout()));
        hikariDataSource.setIdleTimeout(SECONDS.toMillis(globalStoreConfig.getIdleTimeout()));
        hikariDataSource.setMaxLifetime(SECONDS.toMillis(globalStoreConfig.getMaxLifetime()));
        hikariDataSource.setKeepaliveTime(SECONDS.toMillis(globalStoreConfig.getKeepAliveTime()));
        hikariDataSource.addDataSourceProperty(JDBC_DRIVER_READ_TIMEOUT,
                SECONDS.toMillis(globalStoreConfig.getDriverReadTimeout()));

        hikariDataSource.addDataSourceProperty(JDBC_DRIVER_CONNECT_TIMEOUT,
                SECONDS.toMillis(globalStoreConfig.getDriverConnectTimeout()));

        hikariDataSource.setDataSource(oracleDataSource);
        this.dataSource = hikariDataSource;
        log.info("Hikari Datasource created....");

        log.info("Global Store Connection test ::" + this.dataSource.getConnection().isValid(CONN_TIMEOUT_SECONDS));
    }

    public GlobalStoreATPCred getStoreCred() {
        GlobalStoreATPCred globalStoreATPCred = GlobalStoreATPCred.builder()
                .atpUserName(GLOBAL_STORE_USERNAME)
                .atpServiceName(GLOBAL_STORE_SERVICENAME)
                .atpPassword(new String(vaultReader.getSecretAsUtf8Chars(globalStoreConfig.getPasswordPath())))
                .build();

        return globalStoreATPCred;
    }

    @Override
    public void start() throws Exception {
        log.info("Datasource should already be created by now");
    }

    @Override
    public void stop() throws Exception {
        log.info("Shutting down Global Store Handler.");
    }
}
