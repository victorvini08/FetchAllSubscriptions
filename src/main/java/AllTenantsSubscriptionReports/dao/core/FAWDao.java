package AllTenantsSubscriptionReports.dao.core;


import AllTenantsSubscriptionReports.DataLoader.GlobalStoreHandler;
import AllTenantsSubscriptionReports.dao.api.service.FAWApprovedTrialTO;
import AllTenantsSubscriptionReports.dao.api.service.GlobalResourceInfo;
import AllTenantsSubscriptionReports.dao.exceptions.JavaSqlException;
import AllTenantsSubscriptionReports.dao.exceptions.FAWDaoException;;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.log4j.Log4j2;


@Singleton
@Log4j2
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
