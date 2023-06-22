package allTenantsSubscriptionReports.dao.api.service;


import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class TenantSizingDetailsTO {

    private int adwCpuCount;
    private int adwStorage;
    private int oacOlpuCount;
    private String instanceType;
}