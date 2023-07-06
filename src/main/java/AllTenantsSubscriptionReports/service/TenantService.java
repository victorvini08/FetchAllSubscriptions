package AllTenantsSubscriptionReports.service;


import AllTenantsSubscriptionReports.dao.api.service.*;
import com.google.inject.Inject;
import com.oracle.faw.cp.common.auth.VaultReader;
import AllTenantsSubscriptionReports.dao.core.FAWDao;
import AllTenantsSubscriptionReports.dao.core.TenantDAO;
import AllTenantsSubscriptionReports.dao.utils.ServiceEntitlementUtils;
import AllTenantsSubscriptionReports.dao.config.FAWPodApiConfig;
import AllTenantsSubscriptionReports.util.FAWSizingUtils;
import AllTenantsSubscriptionReports.resource.TenantDetails;
import com.oracle.pic.account.api.AccountServiceClient;
import com.oracle.pic.account.model.AccountObject;
import com.oracle.pic.account.model.Tenant;
//import lombok.extern.slf4j.Slf4j;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


//@Slf4j
@Log4j2
public class TenantService {

   // private static final Logger log = LoggerFactory.getLogger(TenantService.class);
    private TenantDAO tenantDAO;
    private AccountServiceClient accountServiceClient;
    private FAWDao fawDao;


    @Inject
    public TenantService(TenantDAO tenantDAO, VaultReader vaultReader,
                         AccountServiceClient accountServiceClient, FAWDao fawDao) {
        this.tenantDAO = tenantDAO;
        FAWSizingUtils.setVaultReader(vaultReader);
        this.accountServiceClient = accountServiceClient;
        this.fawDao = fawDao;
    }



    public void refreshTenantSubAndSizing(FAWPodApiConfig fawPodApiConfig, TenantDetails tenantDetails)
            throws Exception {

        String tenantName = tenantDetails.getTenantName();
        String tenantOcid = tenantDetails.getTenantId();

        if (StringUtils.isBlank(tenantName)) {
            Tenant tenant = accountServiceClient.getTenant(tenantOcid, null);
            tenantName = tenant.getAccountName();
        } else {
            List<AccountObject> tenants = accountServiceClient.getAccounts(null, null, null,
                    null, null, tenantName, null, null, null);
            tenantOcid = tenants.get(0).getOcid();
        }
        log.info("Fetching subscriptions and sizing for tenant {}", tenantOcid);
        List<FAWEntitlementTO> accountsEntitlementList;

        ImmutablePair<Map<String, AccountsSKU>, Map<String, AccountsSKU>> skuDataFromAccounts =
                    ServiceEntitlementUtils.getSKUDataFromAccounts(tenantOcid, accountServiceClient);
            accountsEntitlementList = FAWSizingUtils.buildAccountsEntitlementList(skuDataFromAccounts);
        log.info("Fetched Entire FawApplicationList for tenant {} ", accountsEntitlementList);
        List<FAWEntitlementTO> activeAccountsEntitlementList = accountsEntitlementList.stream()
                .filter(s -> s.getExpirationStatus().equalsIgnoreCase("Active"))
                .collect(Collectors.toList());
        List<String> activeFawApplicationList = activeAccountsEntitlementList.stream().map(FAWEntitlementTO::toJsonString).collect(Collectors.toList());
        log.info("Fetched Active FawApplicationList for tenant {} ", activeFawApplicationList);
        List<TenantSizingDetailsTO> tenantSizing = FAWSizingUtils.getTenantSizing(fawPodApiConfig,
                activeFawApplicationList);
        TenantSubAndSizingTO tenantSubAndSizingTO = FAWSizingUtils.buildTenantSubAndSizingTO(tenantName,
                tenantOcid, accountsEntitlementList , tenantSizing);
        log.info("Fetched Subscription & Sizing for tenant {}", tenantSubAndSizingTO);
        tenantDAO.persistTenantSubAndSizing(tenantSubAndSizingTO);
    }

}