package AllTenantsSubscriptionReports.dao.config;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SizingConfiguration {
    @NotNull
    private String sizingConfigIntendedUseToCSVNameMap;

    @NotNull
    private String bucketName;

    @NotNull
    private String bucketNameSpace;

    @NotNull
    private String region;
}
