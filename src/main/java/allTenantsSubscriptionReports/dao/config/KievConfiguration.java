package allTenantsSubscriptionReports.dao.config;

import com.oracle.pic.kiev.DataStoreConfig;
import com.oracle.pic.kiev.KaasStoreConfig;
import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;

@Data
@ToString
public class KievConfiguration {
    private Boolean useKievInMemory = false;
    private String compartmentId;

    @Valid
    private DataStoreConfig kaasStoreConfig;

}