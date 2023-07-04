package allTenantsSubscriptionReports.dao.utils;


import allTenantsSubscriptionReports.dao.config.ControlPlaneInfraUserConfiguration;
import com.google.inject.Inject;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.CustomerAuthenticationDetailsProvider;
import com.oracle.bmc.auth.RegionProvider;
import com.oracle.faw.cp.common.auth.VaultReader;
import lombok.extern.log4j.Log4j2;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
//import lombok.extern.slf4j.Slf4j;

//@Slf4j
@Log4j2
public class OCIAuthDetailsProvider extends CustomerAuthenticationDetailsProvider
        implements AuthenticationDetailsProvider, RegionProvider {

    //private static final Logger log = LoggerFactory.getLogger(OCIAuthDetailsProvider.class);
    private ControlPlaneInfraUserConfiguration controlPlaneInfraUserConfiguration;
    private String region;
    private VaultReader vaultReader;

    @Inject
    public OCIAuthDetailsProvider(ControlPlaneInfraUserConfiguration credentialConfig, String region, VaultReader vaultReader) {
        log.info("Control Plane Infra Configuration {}", credentialConfig);
        log.info("Region Configuration {}", region);
        this.controlPlaneInfraUserConfiguration = credentialConfig;
        this.region = region;
        this.vaultReader = vaultReader;
    }


    public String getFingerprint() {
        return new String(vaultReader.getSecretAsUtf8Chars(
                controlPlaneInfraUserConfiguration.getControlPlaneInfraUserFingerPrint()));
    }


    public String getTenantId() {
        return controlPlaneInfraUserConfiguration.getControlPlaneInfraUserTenantOcid();
    }


    public String getUserId() {
        return new String(vaultReader.getSecretAsUtf8Chars(
                controlPlaneInfraUserConfiguration.getControlPlaneInfraUserOcid()));
    }


    public InputStream getPrivateKey() {
        InputStream returnValue = null;
        try {
            log.info("OCICredentialConfig: Private Key file '{}'", controlPlaneInfraUserConfiguration
                    .getControlPlaneInfraUserPrivateKeyFilePath());
            returnValue = new ByteArrayInputStream(vaultReader.getSecretAsBytes(controlPlaneInfraUserConfiguration
                    .getControlPlaneInfraUserPrivateKeyFilePath()));
        } catch (Throwable e) {
            log.error("Cannot read OCI Credential private key file", e);
        }
        return returnValue;
    }

    @Deprecated
    public String getPassPhrase() {
        return "";
    }


    public char[] getPassphraseCharacters() {
        return new char[0];
    }


    public Region getRegion() {
        return Region.fromRegionId(this.region);
    }
}
