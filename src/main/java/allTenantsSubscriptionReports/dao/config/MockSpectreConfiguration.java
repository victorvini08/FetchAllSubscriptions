package allTenantsSubscriptionReports.dao.config;


import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MockSpectreConfiguration {
    @NotNull
    private String bucketName;

    @NotNull
    private String bucketNameSpace;

    @NotNull
    private String region;
}
