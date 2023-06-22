package allTenantsSubscriptionReports.dao.config;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class FawDeploymentCompartmentConfig {
    String compartmentName;
    @NotNull
    String compartmentId;
    String environmentType;
}
