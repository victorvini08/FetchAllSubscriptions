package AllTenantsSubscriptionReports.dao.api.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = TenantSubAndSizingTO.Builder.class)
@Value
public class TenantSubAndSizingTO {

    private String tenantName;
    private String tenantOcid;
    private long lastUpdatedAt;
    private List<FAWEntitlementTO> listOfEntitlements;
    private List<TenantSizingDetailsTO> tenantSizingDetails;
}
