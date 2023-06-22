package allTenantsSubscriptionReports.dao.api.service;
import allTenantsSubscriptionReports.dao.exceptions.FAWDaoException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;

@Slf4j
@Data
@Getter
@Builder(builderClassName = "Builder", toBuilder = true)
public class FAWEntitlementTO {

    private FAWApplicationType applicationType;
    private int numberOfUsersForApplication;
    private Date startDate;
    private Date endDate;
    private String expirationStatus;

    public FAWEntitlementTO(FAWApplicationType applicationType, int numberOfUsersForApplication, Date startDate,
                            Date endDate, String expirationStatus) {
        this.applicationType = applicationType;
        this.numberOfUsersForApplication = numberOfUsersForApplication;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expirationStatus = expirationStatus;
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder{
    }

    public FAWEntitlementTO(){

    }

    public static FAWEntitlementTO fromJsonString(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        FAWEntitlementTO fawEntitlementTO = null;

        try {
            fawEntitlementTO = objectMapper.readValue(jsonString, FAWEntitlementTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not Parse Json String to Faw Application", e);
        }
        return fawEntitlementTO;
    }

    public static String toJsonString(FAWEntitlementTO fawApplication) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(fawApplication);
        } catch (JsonProcessingException e) {
            throw new FAWDaoException(e);
        }

        return jsonString;
    }


}
