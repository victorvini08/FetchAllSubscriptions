package AllTenantsSubscriptionReports.dao.core;


import AllTenantsSubscriptionReports.DataLoader.GlobalStoreHandler;
import AllTenantsSubscriptionReports.dao.api.service.GlobalResourceInfo;
import AllTenantsSubscriptionReports.dao.api.service.TenantSubAndSizingTO;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.log4j.Log4j2;


@Singleton
@Log4j2
public class TenantDAO {


    private GlobalStoreHandler globalStoreHandler;
    private GlobalResourceInfo globalResourceInfo;



    @Inject
    public TenantDAO(GlobalStoreHandler globalStoreHandler,
                     GlobalResourceInfo globalResourceInfo) {

        this.globalStoreHandler = globalStoreHandler;
        this.globalResourceInfo = globalResourceInfo;

    }

    public void persistTenantSubAndSizing(TenantSubAndSizingTO tenantSubAndSizingTO) {
        globalResourceInfo.updateTenantSubAndSizing(globalStoreHandler, tenantSubAndSizingTO);
    }
}