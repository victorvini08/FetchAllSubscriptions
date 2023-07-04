package allTenantsSubscriptionReports.resource;


import allTenantsSubscriptionReports.dao.config.FAWPodApiConfig;
import allTenantsSubscriptionReports.service.TenantService;
import allTenantsSubscriptionReports.util.RenderableExceptionsGenerator;
import com.google.inject.Inject;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

//@Slf4j
@Log4j2
@Path("/20181105")
@Produces({"application/json"})
public class TenantResource {

    //private static final Logger log = LoggerFactory.getLogger(TenantResource.class);
    private final TenantService tenantService;
    private final FAWPodApiConfig fawPodApiConfig;

    @Inject
    public TenantResource(TenantService tenantService,
                          FAWPodApiConfig fawPodApiConfig) {
        this.tenantService = tenantService;
        this.fawPodApiConfig = fawPodApiConfig;
    }
    @POST
    @Path("/analyticsWarehousePods/report")
    @Produces(MediaType.APPLICATION_JSON)
    public void refreshTenantSubAndSizing(TenantDetails tenantDetails) {

        try {
            System.out.println("Refreshing Entitlement Data for tenantName {} and tenantOcid {} " +
                    tenantDetails.getTenantName() + tenantDetails.getTenantId());
//            log.info("Refreshing Entitlement Data for tenantName {} and tenantOcid {} ",
//                    tenantDetails.getTenantName(), tenantDetails.getTenantId());
            tenantService.refreshTenantSubAndSizing(fawPodApiConfig, tenantDetails);
        } catch (Exception ex) {

            log.error("Tenant with tenantName {} and tenantOcid {} does not exist. {}",
                    tenantDetails.getTenantName(), tenantDetails.getTenantId(), ex);
            throw RenderableExceptionsGenerator.generateValidationException(ex.getMessage());
        } catch (Throwable throwable) {

            log.error("Unexpected exception while generating tenant entitlement report: {}", throwable);
            throw RenderableExceptionsGenerator.generateInternalServerErrorException(throwable.getMessage());
        }
    }

}