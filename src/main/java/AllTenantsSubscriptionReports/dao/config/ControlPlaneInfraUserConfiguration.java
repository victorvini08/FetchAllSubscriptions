package AllTenantsSubscriptionReports.dao.config;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ControlPlaneInfraUserConfiguration {
    @NotNull
    private String controlPlaneInfraUserTenantOcid;

    @NotNull
    private String controlPlaneInfraUserOcid;

    @NotNull
    private String controlPlaneInfraUserFingerPrint;

    @NotNull
    private String controlPlaneInfraUserInternalAPIKey;

    @NotNull
    private String controlPlaneInfraUserPrivateKeyFilePath;
}

