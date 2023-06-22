package allTenantsSubscriptionReports.dao.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class S2SConfig {
    @NotNull
    private String tenantId;

    @NotNull
    private String metadataServiceBaseUrl;

    @NotNull
    private String federationEndpoint;
}
