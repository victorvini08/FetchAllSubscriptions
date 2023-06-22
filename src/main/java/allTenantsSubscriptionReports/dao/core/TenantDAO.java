package allTenantsSubscriptionReports.dao.core;


import allTenantsSubscriptionReports.DataLoader.GlobalStoreHandler;
import allTenantsSubscriptionReports.dao.api.service.GlobalResourceInfo;
import allTenantsSubscriptionReports.dao.api.service.TenantSubAndSizingTO;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class TenantDAO {


    private GlobalStoreHandler globalStoreHandler;
    private GlobalResourceInfo globalResourceInfo;



    @Inject
    public TenantDAO(GlobalStoreHandler globalStoreHandler,
                     GlobalResourceInfo globalResourceInfo) {

        this.globalStoreHandler = globalStoreHandler;
        this.globalResourceInfo = globalResourceInfo;

    }

    public boolean checkIfMockSpectreEnabled(String tenantOcid) {
        boolean isMockSpectreEnabled;
        isMockSpectreEnabled = globalResourceInfo.isMockSpectreEnabledForTenant(globalStoreHandler, tenantOcid);
        return isMockSpectreEnabled;

    }

    public void persistTenantSubAndSizing(TenantSubAndSizingTO tenantSubAndSizingTO) {
        globalResourceInfo.updateTenantSubAndSizing(globalStoreHandler, tenantSubAndSizingTO);
    }
}