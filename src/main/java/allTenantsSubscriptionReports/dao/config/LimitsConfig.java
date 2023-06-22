package allTenantsSubscriptionReports.dao.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class LimitsConfig {

    @NotNull
    private String limitsServiceEndpoint;
    @NotNull
    private String accountsServiceEndpoint;
    @NotNull
    private String accountsTlsCaBundlePath;
    @NotNull
    private Integer connectTimeoutMilliSeconds;
    @NotNull
    private Integer readTimeoutMilliSeconds;
    @NotNull
    private Integer asyncThreadPoolSize;

}