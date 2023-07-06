package AllTenantsSubscriptionReports.dao.config;


import com.oracle.pic.commons.configuration.location.Location;
import com.oracle.pic.commons.configuration.location.LocationOverride;
import com.oracle.pic.commons.service.configuration.ServiceConfiguration;
import com.oracle.pic.commons.util.Realm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class FAWPodApiConfig extends ServiceConfiguration {

    @NotNull
    private String logdir;

    @NotNull
    private ControlPlaneInfraUserConfiguration controlPlaneInfraUserConfiguration;

    @NotNull
    private GlobalStoreConfig globalStoreConfig;


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




    private SizingConfiguration sizingConfiguration;

}
