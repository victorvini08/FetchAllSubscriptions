package allTenantsSubscriptionReports.util;

public interface MetricsConstants {
    /**
     * API response categories
     */
    String API_REQUEST_GET = "api.request.GET";
    String API_REQUEST_POST = "api.request.POST";
    String API_REQUEST_PUT = "api.request.PUT";
    String API_REQUEST_DELETE = "api.request.DELETE";

    /**
     * API response codes
     */
    String API_RESPONSE_2XX = "api.response.2XX";
    String API_RESPONSE_4XX = "api.response.4XX";
    String API_RESPONSE_5XX = "api.response.5XX";

    /**
     * API service metrics
     */
    String SERVICE_LIMITS_PRECHECK_FAILURE = "Precheck.ServiceLimitsFailure.Count";
    String POD_CAPACITY_PRECHECK_FAILURE = "Precheck.PodCapacityFailure.Count";


    /**
     * Service Metrics
     */


    String ROTATE_ADW_CREDENTIALS_SUCCESS_COUNT = "rotateWarehouseAdwCredentials.Success.Count";
    String ROTATE_ADW_CREDENTIALS_SUCCESSFUL_COMPLETION_TIME = "worker.rotateWarehouseAdwCredentials.successTime";
    String ROTATE_ADW_CREDENTIALS_FAILURE_COMPLETION_TIME = "worker.rotateWarehouseAdwCredentials.failureTime";

    String CREATE_WAREHOUSE_SUCCESS_COUNT = "CreateAnalyticsWarehouse.Success.Count";
    String CREATE_WAREHOUSE_FAILURE_COUNT = "CreateAnalyticsWarehouse.Failure.Count";
    String DELETE_WAREHOUSE_SUCCESS_COUNT = "DeleteAnalyticsWarehouse.Success.Count";
    String DELETE_WAREHOUSE_FAILURE_COUNT = "DeleteAnalyticsWarehouse.Failure.Count";
    String UPDATE_WAREHOUSE_SUCCESS_COUNT = "UpdateAnalyticsWarehouse.Success.Count";

    String CREATE_ATTACHMENT_SUCCESS_COUNT = "CreateAttachment.Success.Count";
    String CREATE_ATTACHMENT_FAILURE_COUNT = "CreateAttachment.Failure.Count";
    String DELETE_ATTACHMENT_SUCCESS_COUNT = "DeleteAttachment.Success.Count";
    String DELETE_ATTACHMENT_FAILURE_COUNT = "DeleteAttachment.Failure.Count";
    String UPDATE_ATTACHMENT_SUCCESS_COUNT = "UpdateAttachment.Success.Count";
    String UPDATE_ATTACHMENT_FAILURE_COUNT = "UpdateAttachment.Failure.Count";
    String CREATE_ATTACHMENT_SUCCESSFUL_COMPLETION_TIME = "worker.createAttachment.successTime";
    String CREATE_ATTACHMENT_FAILURE_COMPLETION_TIME = "worker.createAttachment.failureTime";
    String DELETE_ATTACHMENT_SUCCESSFUL_COMPLETION_TIME = "worker.deleteAttachment.successTime";
    String DELETE_ATTACHMENT_FAILURE_COMPLETION_TIME = "worker.deleteAttachment.failureTime";
    String UPDATE_ATTACHMENT_SUCCESSFUL_COMPLETION_TIME = "worker.updateAttachment.successTime";
    String UPDATE_ATTACHMENT_FAILURE_COMPLETION_TIME = "worker.updateAttachment.failureTime";

    String CREATE_WAREHOUSE_TIME_LIMIT_EXCEEDED = "worker.createAnalyticsWarehouse.timeLimitExceeded";
    String CREATE_WAREHOUSE_ARTIFACTS_OSS_UPLOAD_FAIL
            = "worker.createAnalyticsWarehouse.uploadServiceArtifactsToStorageFailed";
    String DELETE_WAREHOUSE_TIME_LIMIT_EXCEEDED = "worker.deleteAnalyticsWarehouse.timeLimitExceeded";
    String UPDATE_OFFERING_TIME_LIMIT_EXCEEDED = "worker.updateAnalyticsWarehouse.timeLimitExceeded";
    String CREATE_WAREHOUSE_SUCCESSFUL_COMPLETION_TIME = "worker.createAnalyticsWarehouse.successTime";
    String CREATE_WAREHOUSE_FAILURE_COMPLETION_TIME = "worker.createAnalyticsWarehouse.failureTime";
    String DELETE_WAREHOUSE_SUCCESSFUL_COMPLETION_TIME = "worker.deleteAnalyticsWarehouse.successTime";
    String DELETE_WAREHOUSE_FAILURE_COMPLETION_TIME = "worker.deleteAnalyticsWarehouse.failureTime";
    String UPDATE_WAREHOUSE_SUCCESSFUL_COMPLETION_TIME = "worker.updateAnalyticsWarehouse.successTime";
    String UPDATE_WAREHOUSE_FAILURE_COMPLETION_TIME = "worker.updateAnalyticsWarehouse.failureTime";
    String RESTORE_WAREHOUSE_SUCCESSFUL_COMPLETION_TIME = "worker.restoreAnalyticsWarehouse.successTime";
    String ROTATE_ADW_WALLETS_SUCCESS_COUNT = "rotateWarehouseAdwWallets.Success.Count";
    String ROTATE_ADW_WALLETS_FAILURE_COUNT = "rotateWarehouseAdwWallets.Failure.Count";
    String ROTATE_ADW_WALLETS_SUCCESSFUL_COMPLETION_TIME = "worker.rotateWarehouseAdwWallets.successTime";
    String ROTATE_ADW_WALLETS_FAILURE_COMPLETION_TIME = "worker.rotateWarehouseAdwWallets.failureTime";
    String CHANGE_FAW_COMPARTMENT_SUCCESS_COUNT = "changeFawCompartment.Success.Count";
    String CHANGE_FAW_COMPARTMENT_FAILURE_COUNT = "changeFawCompartment.Failure.Count";
    String CHANGE_FAW_COMPARTMENT_SUCCESSFUL_COMPLETION_TIME = "worker.changeFawCompartment.successTime";
    String CHANGE_FAW_COMPARTMENT_FAILURE_COMPLETION_TIME = "worker.changeFawCompartment.failureTime";
    String CONFIGURE_WAREHOUSE_TIME_LIMIT_EXCEEDED = "worker.configureWarehouse.timeLimitExceeded";
    String CONFIGURE_NEXUS_INSTANCE_SUCCESS_COUNT = "configureNexusInstance.Success.Count";
    String CONFIGURE_NEXUS_INSTANCE_FAILURE_COUNT = "configureNexusInstance.Failure.Count";
    String CONFIGURE_NEXUS_INSTANCE_SUCCESSFUL_COMPLETION_TIME = "worker.configureNexusInstance.successTime";
    String CONFIGURE_NEXUS_INSTANCE_FAILURE_COMPLETION_TIME = "worker.configureNexusInstance.failureTime";

    /**
     * Pod Metrics
     */

    String CREATE_WAREHOUSE_POD_SUCCESS_COUNT = "CreateAnalyticsWarehousePod.Success.Count";
    String CREATE_WAREHOUSE_POD_FAILURE_COUNT = "CreateAnalyticsWarehousePod.Failure.Count";
    String SCALEOUT_WAREHOUSE_POD_SUCCESS_COUNT = "ScaleoutAnalyticsWarehousePod.Success.Count";
    String SCALEOUT_WAREHOUSE_POD_FAILURE_COUNT = "ScaleoutAnalyticsWarehousePod.Failure.Count";
    String PATCH_WAREHOUSE_POD_SUCCESS_COUNT = "PatchAnalyticsWarehousePod.Success.Count";
    String PATCH_WAREHOUSE_POD_FAILURE_COUNT = "PatchAnalyticsWarehousePod.Failure.Count";
    String OUT_OF_PLACE_PATCH_WAREHOUSE_POD_SUCCESS_COUNT =
            "ScaleoutOutOfPlacePatchAnalyticsWarehousePod.Success.Count";
    String OUT_OF_PLACE_PATCH_WAREHOUSE_POD_FAILURE_COUNT =
            "ScaleoutOutOfPlacePatchAnalyticsWarehousePod.Failure.Count";

    String CREATE_WAREHOUSE_POD_SUCCESSFUL_COMPLETION_TIME = "worker.createAnalyticsWarehousePod.successTime";
    String CREATE_WAREHOUSE_POD_FAILURE_COMPLETION_TIME = "worker.createAnalyticsWarehousePod.failureTime";
    String SCALEOUT_WAREHOUSE_POD_SUCCESSFUL_COMPLETION_TIME = "worker.scaleoutAnalyticsWarehousePod.successTime";
    String SCALEOUT_WAREHOUSE_POD_FAILURE_COMPLETION_TIME = "worker.scaleoutAnalyticsWarehousePod.failureTime";
    String PATCH_WAREHOUSE_POD_SUCCESSFUL_COMPLETION_TIME = "worker.patchAnalyticsWarehousePod.successTime";
    String PATCH_WAREHOUSE_POD_FAILURE_COMPLETION_TIME = "worker.patchAnalyticsWarehousePod.failureTime";
    String OUT_OF_PLACE_PATCH_WAREHOUSE_POD_SUCCESSFUL_COMPLETION_TIME =
            "worker.ScaleOutoutOfPlacePatchAnalyticsWarehousePod.successTime";
    String OUT_OF_PLACE_PATCH_WAREHOUSE_POD_FAILURE_COMPLETION_TIME =
            "worker.ScaleOutoutOfPlacePatchAnalyticsWarehousePod.failureTime";

    String PUSH_ARTIFACTS_SUCCESS_COUNT = "PushArtifacts.Success.Count";
    String PUSH_ARTIFACTS_FAILURE_COUNT = "PushArtifacts.Failure.Count";
    String PUSH_ARTIFACTS_SUCCESSFUL_COMPLETION_TIME = "worker.PushArtifacts.successTime";
    String PUSH_ARTIFACTS_FAILURE_COMPLETION_TIME = "worker.PushArtifacts.failureTime";

    String KMS_MIGRATE_SUCCESS_COUNT = "KmsMigrate.Success.Count";
    String KMS_MIGRATE_FAILURE_COUNT = "KmsMigrate.Failure.Count";
    String KMS_MIGRATE_SUCCESSFUL_COMPLETION_TIME = "worker.KmsMigrate.successTime";
    String KMS_MIGRATE_FAILURE_COMPLETION_TIME = "worker.KmsMigrate.failureTime";

    String SCALE_OUT_REDIS_CLUSTER_SUCCESS_COUNT = "scaleOutOOPPatch.scaleOutRedisCluster.Success.Count";
    String SCALE_OUT_REDIS_CLUSTER_FAILURE_COUNT = "scaleOutOOPPatch.scaleOutRedisCluster.Failure.Count";
    String SCALE_OUT_REDIS_CLUSTER_SUCCESSFUL_COMPLETION_TIME =
            "worker.scaleOutOOPPatch.scaleOutRedisCluster.successTime";
    String SCALE_OUT_REDIS_CLUSTER_FAILURE_COMPLETION_TIME =
            "worker.scaleOutRedisCluster.scaleOutRedisCluster.failureTime";
    String SCALE_IN_REDIS_CLUSTER_SUCCESS_COUNT = "scaleOutOOPPatch.scaleInRedisCluster.Success.Count";
    String SCALE_IN_REDIS_CLUSTER_FAILURE_COUNT = "scaleOutOOPPatch.scaleInRedisCluster.Failure.Count";
    String SCALE_IN_REDIS_CLUSTER_SUCCESSFUL_COMPLETION_TIME =
            "worker.scaleOutOOPPatch.scaleInRedisCluster.successTime";
    String SCALE_IN_REDIS_CLUSTER_FAILURE_COMPLETION_TIME =
            "worker.scaleOutRedisCluster.scaleInRedisCluster.failureTime";

    String ZDT_PATCH_SUCCESS_COUNT = "ZdtPatch.Success.Count";
    String ZDT_PATCH_FAILURE_COUNT = "ZdtPatch.Failure.Count";
    String ZDT_PATCH_SUCCESSFUL_COMPLETION_TIME = "worker.ZdtPatch.successTime";
    String ZDT_PATCH_FAILURE_COMPLETION_TIME = "worker.ZdtPatch.failureTime";

    String ZDT_RU_PATCH_SUCCESS_COUNT = "ZdtRUPatch.Success.Count";
    String ZDT_RU_PATCH_FAILURE_COUNT = "ZdtRUPatch.Failure.Count";
    String ZDT_RU_PATCH_SUCCESSFUL_COMPLETION_TIME = "worker.ZdtRUPatch.successTime";
    String ZDT_RU_PATCH_FAILURE_COMPLETION_TIME = "worker.ZdtRUPatch.failureTime";

    String UPDATE_DATABAG_ENTITY_SUCCESS_COUNT = "UpdateDatbagEntity.Success.Count";
    String UPDATE_DATABAG_ENTITY_FAILURE_COUNT = "UpdateDatbagEntity.Failure.Count";
    String UPDATE_DATABAG_ENTITY_SUCCESSFUL_COMPLETION_TIME = "worker.UpdateDatbagEntity.successTime";
    String UPDATE_DATABAG_ENTITY_FAILURE_COMPLETION_TIME = "worker.UpdateDatbagEntity.failureTime";

    String IDCS_APP_FOR_WTSS_OUTOFSYNC_COUNT = "worker.idcsApp.outOfSync.Count";

    String KIEV_BACKUP_SUCCESS_COUNT = "kievBackup.Success.Count";
    String KIEV_BACKUP_SUCCESSFUL_COMPLETION_TIME = "worker.kievBackup.successTime";
    String KIEV_BACKUP_FAILURE_COMPLETION_TIME = "worker.kievBackup.failureTime";

    String POD_DATABASE_BACKUP_SUCCESS_COUNT = "podDatabaseBackup.Success.Count";
    String POD_DATABASE_BACKUP_SUCCESSFUL_COMPLETION_TIME = "worker.podDatabaseBackup.successTime";
    String POD_DATABASE_BACKUP_FAILURE_COMPLETION_TIME = "worker.podDatabaseBackup.failureTime";

    String ROTATE_POD_DB_CREDENTIALS_SUCCESS_COUNT = "rotateWarehousePodDbCredentials.Success.Count";
    String ROTATE_POD_DB_CREDENTIALS_SUCCESSFUL_COMPLETION_TIME = "worker.rotateWarehousePodDbCredentials.successTime";
    String ROTATE_POD_DB_CREDENTIALS_FAILURE_COMPLETION_TIME = "worker.rotateWarehousePodDbCredentials.failureTime";

    String ROTATE_POD_DB_WALLETS_SUCCESS_COUNT = "rotateWarehousePodDbWallets.Success.Count";
    String ROTATE_POD_DB_WALLETS_SUCCESSFUL_COMPLETION_TIME = "worker.rotateWarehousePodDbWallets.successTime";
    String ROTATE_POD_DB_WALLETS_FAILURE_COMPLETION_TIME = "worker.rotateWarehousePodDbWallets.failureTime";

    String ROTATE_DATAPLANE_SECRET_CREDENTIALS_FAILURE_COMPLETION_TIME = "ROTATE_DATAPLANE_SECRET_CREDENTIALS_FAILURE_COMPLETION_TIME";
    String ROTATE_DATAPLANE_SECRET_CREDENTIALS_SUCCESSFUL_COMPLETION_TIME = "ROTATE_DATAPLANE_SECRET_CREDENTIALS_SUCCESSFUL_COMPLETION_TIME";
    String ROTATE_DATAPLANE_SECRET_CREDENTIALS_SUCCESS_COUNT = "ROTATE_DATAPLANE_SECRET_CREDENTIALS_SUCCESS_COUNT";

    String UNIFIED_CREATE_WAREHOUSE_POD_SUCCESS_COUNT = "UnifiedCreateAnalyticsWarehousePod.Success.Count";
    String UNIFIED_CREATE_WAREHOUSE_POD_FAILURE_COUNT = "UnifiedCreateAnalyticsWarehousePod.Failure.Count";
    String UNIFIED_CREATE_WAREHOUSE_POD_SUCCESSFUL_COMPLETION_TIME = "worker.unifiedCreateAnalyticsWarehousePod.successTime";
    String UNIFIED_CREATE_WAREHOUSE_POD_FAILURE_COMPLETION_TIME = "worker.unifiedCreateAnalyticsWarehousePod.failureTime";

    String UNIFIED_PRE_PATCH_POD_SUCCESS_COUNT = "UnifiedCreateAnalyticsWarehousePod.Success.Count";
    String UNIFIED_PRE_PATCH_POD_FAILURE_COUNT = "UnifiedCreateAnalyticsWarehousePod.Failure.Count";
    String UNIFIED_PRE_PATCH_POD_SUCCESSFUL_COMPLETION_TIME = "worker.unifiedCreateAnalyticsWarehousePod.successTime";
    String UNIFIED_PRE_PATCH_POD_FAILURE_COMPLETION_TIME = "worker.unifiedCreateAnalyticsWarehousePod.failureTime";

    String UNIFIED_POST_PATCH_POD_SUCCESS_COUNT = "UnifiedCreateAnalyticsWarehousePod.Success.Count";
    String UNIFIED_POST_PATCH_POD_FAILURE_COUNT = "UnifiedCreateAnalyticsWarehousePod.Failure.Count";
    String UNIFIED_POST_PATCH_POD_SUCCESSFUL_COMPLETION_TIME = "worker.unifiedCreateAnalyticsWarehousePod.successTime";
    String UNIFIED_POST_PATCH_POD_FAILURE_COMPLETION_TIME = "worker.unifiedCreateAnalyticsWarehousePod.failureTime";

}
