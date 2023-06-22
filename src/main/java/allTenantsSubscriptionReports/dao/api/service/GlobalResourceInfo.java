package allTenantsSubscriptionReports.dao.api.service;

import allTenantsSubscriptionReports.DataLoader.GlobalStoreHandler;

public interface GlobalResourceInfo {

    FAWApprovedTrialTO getApprovedTrialForTenant(String tenantName, GlobalStoreHandler globalStoreHandler);

    boolean isMockSpectreEnabledForTenant(GlobalStoreHandler globalStoreHandler, String tenantId);

    void updateTenantSubAndSizing(GlobalStoreHandler globalStoreHandler, TenantSubAndSizingTO tenantSubAndSizingTO);

}
