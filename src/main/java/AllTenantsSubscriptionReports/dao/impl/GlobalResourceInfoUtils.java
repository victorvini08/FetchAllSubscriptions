package AllTenantsSubscriptionReports.dao.impl;

import AllTenantsSubscriptionReports.DataLoader.GlobalStoreHandler;
import AllTenantsSubscriptionReports.dao.api.service.*;
import AllTenantsSubscriptionReports.dao.exceptions.JavaSqlException;
import com.google.inject.Inject;
import com.oracle.faw.security.DataSecurityInterpreter;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class GlobalResourceInfoUtils implements GlobalResourceInfo {
    @Inject
    private DataSecurityInterpreter dataSecurityInterpreter;

    private static final FAWApprovedTrialTO NO_TRIAL_ENTITLEMENTS = null;




    public FAWApprovedTrialTO getApprovedTrialForTenant(String tenantName, GlobalStoreHandler globalStoreHandler) {

        System.out.println("REACHED APPROVED TRIAL METHOD");
        try (Connection connection = getConnection(globalStoreHandler)) {
            String trialDetailsForTenantSql = "SELECT SKU.VALUE AS SKU_VALUE, SKU_MAPPING.UC_CLOUD_ACCOUNT " +
                    "AS TENANT_NAME, TRIAL.NUMBER_OF_USERS, TRIAL.TRIAL_START_DATE, TRIAL.TRIAL_END_DATE " +
                    "FROM FAW_TRIALS TRIAL INNER JOIN FAW_SKU_MAPPING SKU_MAPPING " +
                    "ON TRIAL.SKU_TENANT_MAPPING_ID = SKU_MAPPING.ID INNER JOIN FAW_SKU SKU " +
                    "ON SKU.ID = SKU_MAPPING.SKU_ID WHERE LOWER(SKU_MAPPING.UC_CLOUD_ACCOUNT) = ? AND TRIAL.STATUS = ?";

            System.out.println("DEBUG 2");
            FAWApprovedTrialTO oaxApprovedTrialTO = null;
            PreparedStatement preparedStatement = connection.prepareStatement(trialDetailsForTenantSql);
            System.out.println("DEBUG 3");
            preparedStatement.setString(1, tenantName.toLowerCase());
            preparedStatement.setString(2, "APPROVED");
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Retrieving approved trial details for tenant {}" + tenantName);

            while (resultSet.next()) {

                if (oaxApprovedTrialTO == null) {
                    oaxApprovedTrialTO = FAWApprovedTrialTO.builder()
                            .tenantName(tenantName)
                            .trialStartDate(resultSet.getTimestamp("TRIAL_START_DATE").getTime())
                            .trialEndDate(resultSet.getTimestamp("TRIAL_END_DATE").getTime())
                            .build();
                }
                System.out.println(resultSet.getString("SKU_VALUE"));
                switch (resultSet.getString("SKU_VALUE")) {
                    case "ERP":
                        oaxApprovedTrialTO.setErpEnabled(resultSet.getInt("NUMBER_OF_USERS") > 0);
                        oaxApprovedTrialTO.setNumberOfErpUsers(resultSet.getInt("NUMBER_OF_USERS"));
                        break;
                    case "SCM":
                        oaxApprovedTrialTO.setScmEnabled(resultSet.getInt("NUMBER_OF_USERS") > 0);
                        oaxApprovedTrialTO.setNumberOfScmUsers(resultSet.getInt("NUMBER_OF_USERS"));
                        break;
                    case "HCM":
                        oaxApprovedTrialTO.setHcmEnabled(resultSet.getInt("NUMBER_OF_USERS") > 0);
                        oaxApprovedTrialTO.setNumberOfHcmUsers(resultSet.getInt("NUMBER_OF_USERS"));
                        break;
                    case "CX":
                        oaxApprovedTrialTO.setCxEnabled(resultSet.getInt("NUMBER_OF_USERS") > 0);
                        oaxApprovedTrialTO.setNumberOfCxUsers(resultSet.getInt("NUMBER_OF_USERS"));
                        break;
                    default:
                        throw new JavaSqlException("Unknown SKU value found");
                }
            }

            if (oaxApprovedTrialTO == null) {
                log.info("No approved trial data for tenant {}", tenantName);
                return NO_TRIAL_ENTITLEMENTS;
            } else {
                return oaxApprovedTrialTO;
            }
        } catch (SQLException sqlEx) {
            throw new JavaSqlException(sqlEx);
        }
    }

    private Connection getConnection(GlobalStoreHandler globalStoreHandler) {
        Connection connection;
        try {
            connection = globalStoreHandler.getDataSource().getConnection();
        } catch (SQLException e) {
            globalStoreHandler.buildDataSource();
            try {
                connection = globalStoreHandler.getDataSource().getConnection();
            } catch (SQLException e1) {
                throw new JavaSqlException(e1);
            }
        }

        return connection;
    }

    @Override
    public boolean isMockSpectreEnabledForTenant(GlobalStoreHandler globalStoreHandler, String tenantId) {

        String getServiceCreationFailureCleanupFlagSql =  "SELECT FLAG_VALUE FROM TENANT_FEATURE_FLAGS " +
                "where FEATURE_NAME = 'MOCK_SPECTRE' AND TENANT_OCID = ?";
        boolean mockSpectreEnabled;
        try (Connection connection = getConnection(globalStoreHandler)) {
            PreparedStatement preparedStatement = connection.prepareStatement(getServiceCreationFailureCleanupFlagSql);
            preparedStatement.setString(1, tenantId);
            ResultSet resultSet = preparedStatement.executeQuery();
            mockSpectreEnabled = resultSet.next() ? Boolean.parseBoolean(resultSet.getString("FLAG_VALUE"))
                    : Boolean.FALSE;
            System.out.println("Mock Spectre enabled:" +  mockSpectreEnabled);
            return mockSpectreEnabled;
        } catch (SQLException sqlEx) {
            throw new JavaSqlException(sqlEx);
        }
    }

    @Override
    public void updateTenantSubAndSizing(GlobalStoreHandler globalStoreHandler, TenantSubAndSizingTO tenantSubAndSizingTO) {

        int updatedValues = 0, updatedValues2 =0, count =0;
        log.info("Updating Tenant Subscription & Sizing with fetched values : {}", tenantSubAndSizingTO.toString());
        String checkSql1 = "DELETE FROM TENANT_SKU_DATA WHERE TENANT_OCID = ?";
        String checkSql2 = "INSERT INTO TENANT_SKU_DATA(TENANT_OCID, TENANT_NAME, SKU_NAME, QUANTITY, START_DATE, " +
                "END_DATE, CREATED_AT, UPDATED_AT) VALUES (?,?,?,?,?,?,?,?)";

        String checkSql3 = "DELETE FROM TENANT_SIZING WHERE TENANT_OCID = ?";
        String checkSql4 = "INSERT INTO TENANT_SIZING(TENANT_OCID, TENANT_NAME, INSTANCE_TYPE, ADW_CPU, ADW_STORAGE, " +
                "OAC_OLPU, CREATED_AT, UPDATED_AT) VALUES (?,?,?,?,?,?,?,?)";

        try (Connection connection = getConnection(globalStoreHandler);
             PreparedStatement preparedStatement1 = connection.prepareStatement(checkSql1);
             PreparedStatement preparedStatement2 = connection.prepareStatement(checkSql2);
             PreparedStatement preparedStatement3 = connection.prepareStatement(checkSql3);
             PreparedStatement preparedStatement4 = connection.prepareStatement(checkSql4)
        ){
            connection.setAutoCommit(false);
            log.info("Deleting older data from TENANT_SKU_DATA for tenant: {}", tenantSubAndSizingTO.getTenantOcid());
            preparedStatement1.setString(1, tenantSubAndSizingTO.getTenantOcid());
            preparedStatement1.execute();

            log.info("Deleting older data from TENANT_SIZING for tenant: {}", tenantSubAndSizingTO.getTenantOcid());
            preparedStatement3.setString(1, tenantSubAndSizingTO.getTenantOcid());
            preparedStatement3.execute();

            List<FAWEntitlementTO> entitlementList = tenantSubAndSizingTO.getListOfEntitlements();
            for(FAWEntitlementTO entitlement : entitlementList)
            {
                int index = 1;
                log.info("Entitlements fetched for updating to GlobalStore: {}", entitlement.toString());

                preparedStatement2.setString(index++,tenantSubAndSizingTO.getTenantOcid());
                preparedStatement2.setString(index++, tenantSubAndSizingTO.getTenantName());
                preparedStatement2.setString(index++,entitlement.getApplicationType().toString());
                preparedStatement2.setInt(index++,entitlement.getNumberOfUsersForApplication());
                preparedStatement2.setDate(index++,new Date(entitlement.getStartDate().getTime()));
                preparedStatement2.setDate(index++,new Date(entitlement.getEndDate().getTime()));
                preparedStatement2.setTimestamp(index++,new Timestamp(System.currentTimeMillis()));
                preparedStatement2.setTimestamp(index++,new Timestamp(System.currentTimeMillis()));

                preparedStatement2.addBatch();
            }

            List<TenantSizingDetailsTO> tenantSizingList = tenantSubAndSizingTO.getTenantSizingDetails();
            for( TenantSizingDetailsTO tenantSizing : tenantSizingList)
            {
                int index = 1;
                preparedStatement4.setString(index++,tenantSubAndSizingTO.getTenantOcid());
                preparedStatement4.setString(index++,tenantSubAndSizingTO.getTenantName());
                preparedStatement4.setString(index++,tenantSizing.getInstanceType());
                preparedStatement4.setInt(index++, tenantSizing.getAdwCpuCount());
                preparedStatement4.setInt(index++, tenantSizing.getAdwStorage());
                preparedStatement4.setInt(index++, tenantSizing.getOacOlpuCount());
                preparedStatement4.setTimestamp(index++,new Timestamp(System.currentTimeMillis()));
                preparedStatement4.setTimestamp(index++,new Timestamp(System.currentTimeMillis()));

                preparedStatement4.addBatch();
            }
            int[] updates = preparedStatement2.executeBatch();
            updatedValues = Arrays.stream(updates).sum();
            int[] updates2 = preparedStatement4.executeBatch();
            updatedValues2 = Arrays.stream(updates2).sum();
            connection.commit();
            connection.setAutoCommit(true);
            count = updatedValues + updatedValues2;
            log.info("Total number of overall records added are {}", count);

        } catch (SQLException e) {
            throw new JavaSqlException(e);
        }
    }


}