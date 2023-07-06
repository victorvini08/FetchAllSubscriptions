package AllTenantsSubscriptionReports.resource;

public class TenantDetails {
    private String tenantName;
    private String tenantId;

    public TenantDetails(String tenantName, String tenantId) {
        this.tenantName = tenantName;
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getTenantId() {
        return tenantId;
    }
}
