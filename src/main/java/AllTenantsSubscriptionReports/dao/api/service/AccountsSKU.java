package AllTenantsSubscriptionReports.dao.api.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = AccountsSKU.Builder.class)
public class AccountsSKU {

    private String skuName;
    private Integer quantity;
    private Date startDate;
    private Date endDate;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {
    }
}
