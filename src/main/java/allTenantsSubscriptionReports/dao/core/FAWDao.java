package allTenantsSubscriptionReports.dao.core;


import allTenantsSubscriptionReports.DataLoader.GlobalStoreHandler;
import allTenantsSubscriptionReports.dao.api.service.FAWApprovedTrialTO;
import allTenantsSubscriptionReports.dao.api.service.GlobalResourceInfo;
import allTenantsSubscriptionReports.dao.exceptions.JavaSqlException;
import allTenantsSubscriptionReports.dao.exceptions.FAWDaoException;;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.log4j.Log4j2;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import lombok.extern.slf4j.Slf4j;

@Singleton
//@Slf4j
@Log4j2
public class FAWDao {

    //private static final Logger log = LoggerFactory.getLogger(FAWDao.class);
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
