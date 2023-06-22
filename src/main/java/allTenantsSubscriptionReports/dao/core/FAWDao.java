package allTenantsSubscriptionReports.dao.core;


import allTenantsSubscriptionReports.DataLoader.GlobalStoreHandler;
import allTenantsSubscriptionReports.dao.api.service.FAWApprovedTrialTO;
import allTenantsSubscriptionReports.dao.api.service.GlobalResourceInfo;
import allTenantsSubscriptionReports.dao.exceptions.JavaSqlException;
import allTenantsSubscriptionReports.dao.exceptions.FAWDaoException;;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class FAWDao {

    private GlobalStoreHandler globalStoreHandler;
    private GlobalResourceInfo globalResourceInfo;

    @Inject
    public FAWDao(GlobalStoreHandler globalStoreHandler, GlobalResourceInfo globalResourceInfo) {

        this.globalStoreHandler = globalStoreHandler;
        this.globalResourceInfo = globalResourceInfo;

    }

    public FAWApprovedTrialTO getApprovedTrialEntitlements(String tenantName) {

        try {
            return globalResourceInfo.getApprovedTrialForTenant(tenantName, globalStoreHandler);
        } catch (JavaSqlException sqlException) {
            log.error("Error retrieving trial entitlements");
            throw new FAWDaoException(sqlException);
        }
    }
}
