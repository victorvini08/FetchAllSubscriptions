package allTenantsSubscriptionReports.dao.core;


import allTenantsSubscriptionReports.DataLoader.GlobalStoreHandler;
import allTenantsSubscriptionReports.dao.api.service.GlobalResourceInfo;
import allTenantsSubscriptionReports.dao.api.service.TenantSubAndSizingTO;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.log4j.Log4j2;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

//import lombok.extern.slf4j.Slf4j;

@Singleton
//@Slf4j
@Log4j2
public class TenantDAO {


    //private static final Logger log = LoggerFactory.getLogger(TenantDAO.class);
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