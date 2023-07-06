package AllTenantsSubscriptionReports.DataLoader.config;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GlobalStoreConfig {
    private String globalStoreId;
    private String passwordPath;
    private String region;
    private String walletPath;
    private String walletPassword;
    private boolean repairFlyway;
    private int maximumPoolSize;
    private int minimumIdle;
    private int connectionTimeout;
    private int idleTimeout;
    private int maxLifetime;
    private int keepAliveTime;
    private int driverReadTimeout;
    private int driverConnectTimeout;
}