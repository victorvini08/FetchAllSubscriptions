package allTenantsSubscriptionReports.dao.api.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class FAWApprovedTrialTO {

    private String tenantName;
    private boolean isErpEnabled;
    private boolean isHcmEnabled;
    private boolean isScmEnabled;
    private boolean isCxEnabled;
    private int numberOfErpUsers;
    private int numberOfHcmUsers;
    private int numberOfScmUsers;
    private int numberOfCxUsers;
    private long trialStartDate;
    private long trialEndDate;

    public static class Builder{
    }

}
