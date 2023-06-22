package allTenantsSubscriptionReports.dao.config;


import javax.validation.constraints.NotNull;
import com.oracle.faw.cp.common.pojo.dp.EmailInfo;
import com.oracle.faw.cp.common.pojo.dp.KmsInfo;
import com.oracle.pic.commons.configuration.location.Location;
import com.oracle.pic.commons.configuration.location.LocationOverride;
import com.oracle.pic.commons.service.configuration.ServiceConfiguration;
import com.oracle.pic.commons.service.metrics.jersey.MetricsConfiguration;
import com.oracle.pic.commons.util.AvailabilityDomain;
import com.oracle.pic.commons.util.Realm;
import com.oracle.pic.commons.util.Region;
import com.oracle.pic.sherlock.collector.AuditConfig;
import com.oracle.pic.vault.SecretServiceConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.Validate;

@Getter
@Setter
@ToString
public class FAWPodApiConfig extends ServiceConfiguration {

    @NotNull
    private String logdir;

    @NotNull
    private MetricsConfiguration metricsConfig;

    private boolean adMappingDisabled;

    private AuditConfig auditConfig;

    @NotNull
    private AuthConfig authConfig;

    @NotNull
    private S2SConfig s2SConfig;

    @NotNull
    private LimitsConfig limitsConfig;

    @NotNull
    private KievConfiguration kievConfiguration;

    @NotNull
    private KmsInfo kmsInfo;

    @NotNull
    private ControlPlaneInfraUserConfiguration controlPlaneInfraUserConfiguration;

    @NotNull
    private GlobalStoreConfig globalStoreConfig;

    public void validateAdAndRegionConfiguration() {
        Validate.isTrue(getLocation().isValid());
    }

    @NotNull
    private Realm realm;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Location location;

    @Getter(AccessLevel.NONE)
    private LocationOverride locationOverride;

    /**
     * Allow config to override the region and AD so we don't look them up from /etc/region and
     * /etc/availability-domain.
     *
     * @return Location
     */
    private Location resolveLocation() {
        return (locationOverride == null) ? Location.fromEnvironmentFiles() : Location
                .fromLocationOverride(locationOverride);
    }

    private Location getLocation() {
        if (location == null) {
            location = resolveLocation();
        }
        return location;
    }

    public Region getRegion() {
        return getLocation().getRegion();
    }

    public AvailabilityDomain getAvailabilityDomain() {
        return getLocation().getAvailabilityDomain();
    }

    @NotNull
    private SecretServiceConfig secretServiceConfig;

    @NotNull
    private EmailInfo emailInfo;

    @NotNull
    private String dataPlaneAuthSecretPath;

    @NotNull
    private FawDeploymentCompartmentConfig fawDeploymentCompartmentConfig;

    @NotNull
    private String dynamicCoreRegionsImportPath;

    private SizingConfiguration sizingConfiguration;

}
