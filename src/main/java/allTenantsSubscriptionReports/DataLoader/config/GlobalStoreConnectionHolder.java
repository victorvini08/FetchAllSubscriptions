package allTenantsSubscriptionReports.DataLoader.config;


import javax.net.ssl.SSLContext;
import java.io.InputStream;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GlobalStoreConnectionHolder {

    private String walletPassword;
    private InputStream keyStore;
    private InputStream trustStore;
    private SSLContext sslContext;
    private String serviceUrl;

}
