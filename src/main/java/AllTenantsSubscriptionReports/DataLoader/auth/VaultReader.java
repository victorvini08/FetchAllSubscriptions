package AllTenantsSubscriptionReports.DataLoader.auth;

import com.oracle.pic.vault.model.GetSecretResponse;

public interface VaultReader {

    GetSecretResponse getSecret(String secretPath);

    byte[] getSecretAsBytes(String secretPath);

    char[] getSecretAsUtf8Chars(String secretPath);
}
