package allTenantsSubscriptionReports.DataLoader.utils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import allTenantsSubscriptionReports.DataLoader.GlobalStoreHandler;
import allTenantsSubscriptionReports.DataLoader.config.GlobalStoreConnectionHolder;
import com.oracle.faw.cp.common.auth.VaultReader;
//import lombok.extern.slf4j.Slf4j;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.math.NumberUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


//@Slf4j
@Log4j2
public class GlobalStoreDatabaseUtils  {

   // private static final Logger log = LoggerFactory.getLogger(GlobalStoreDatabaseUtils.class);
    private VaultReader vaultReader;

    private static final Integer BUFFER_SIZE = 16384;
    private static final String KEYSTORE_JSK_FILE_NAME = "keystore.jks";
    private static final String TRUSTSTORE_JSK_FILE_NAME = "truststore.jks";
    private static final String TNSNAMES_FILE_NAME = "tnsnames.ora";
    private static final String TLS = "TLSv1.2";

    private String walletPath;
    private String walletPassword;

    public GlobalStoreDatabaseUtils(String walletPath,String walletPassword,VaultReader vaultReader) {
        this.walletPassword=walletPassword;
        this.walletPath=walletPath;
        this.vaultReader=vaultReader;
    }

    public GlobalStoreConnectionHolder buildGlobalStoreConnectionHolder() throws Exception {
        GlobalStoreConnectionHolder globalStoreConnectionHolder=new GlobalStoreConnectionHolder();
        globalStoreConnectionHolder.setWalletPassword(new String(vaultReader.getSecretAsUtf8Chars(walletPassword)));

        byte[] bytes = vaultReader.getSecretAsBytes(walletPath);

        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null && !entry.isDirectory()) {
                switch (entry.getName()) {
                    case TNSNAMES_FILE_NAME:
                        List<String> tnsNames = getTnsNames(zipInputStream);
                        buildServiceHolder(tnsNames,globalStoreConnectionHolder);
                        break;
                    case KEYSTORE_JSK_FILE_NAME:
                        globalStoreConnectionHolder.setKeyStore(getInputStream(zipInputStream));
                        break;
                    case TRUSTSTORE_JSK_FILE_NAME:
                        globalStoreConnectionHolder.setTrustStore(getInputStream(zipInputStream));
                        break;
                }
            }
            zipInputStream.closeEntry();
            globalStoreConnectionHolder.setSslContext(buildSSLContextForJks(globalStoreConnectionHolder.getWalletPassword(), globalStoreConnectionHolder.getKeyStore(), globalStoreConnectionHolder.getTrustStore()));

            return globalStoreConnectionHolder;
        } catch (Exception ex) {
            log.error("Error while constructing GlobalStoreConnectionHolder ", ex);
            throw new Exception("Failed to construct GlobalStoreConnectionHolder ", ex);
        }
    }

    public static InputStream getInputStream(ZipInputStream zipInputStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] tempBuffer = new byte[BUFFER_SIZE];

        int bytesRead;
        while((bytesRead = zipInputStream.read(tempBuffer)) != NumberUtils.INTEGER_MINUS_ONE) {
            outputStream.write(tempBuffer, NumberUtils.INTEGER_ZERO, bytesRead);
        }

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public SSLContext buildSSLContextForJks(String walletPassword, InputStream keyStoreStream, InputStream trustStoreStream) throws Exception {
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keyStoreStream, walletPassword.toCharArray());
            keyManagerFactory.init(keyStore, walletPassword.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(trustStoreStream, walletPassword.toCharArray());
            trustManagerFactory.init(trustStore);
            SSLContext sslContext = SSLContext.getInstance(TLS);
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            keyStoreStream.close();
            trustStoreStream.close();
            return sslContext;
        } catch (Exception var9) {
            log.error("Exception occurred while constructing SSLContext factory for jsk with error " + var9.getMessage(), var9);
            throw var9;
        }
    }

    private void buildServiceHolder(List<String> tnsNames,GlobalStoreConnectionHolder globalStoreConnectionHolder) {
        tnsNames.forEach(s -> {
            String[] arr = s.split("=", NumberUtils.INTEGER_TWO);
            if (arr.length > NumberUtils.INTEGER_ZERO && arr[NumberUtils.INTEGER_ZERO].contains("_low")) {
                globalStoreConnectionHolder.setServiceUrl(arr[NumberUtils.INTEGER_ONE]);
            }
        });
    }

    private List<String> getTnsNames(ZipInputStream zipInputStream) throws Exception {
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream));
        String line = bufferedReader.readLine();
        while (line != null) {
            builder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        String tnsnames = builder.toString();
        String[] arr = tnsnames.split("\\r?\\n");
        return Arrays.stream(arr).filter(s -> (s != null && s.length() > NumberUtils.INTEGER_ZERO)).collect(Collectors.toList());
    }

}