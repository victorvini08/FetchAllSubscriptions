package AllTenantsSubscriptionReports.util;


import com.oracle.pic.commons.exceptions.server.ErrorCode;
import com.oracle.pic.commons.exceptions.server.RenderableException;
import com.oracle.pic.telemetry.commons.metrics.Metrics;

public class RenderableExceptionsGenerator {

    /**
     * Generate a renderable exception that the resource is not found or the user is not authorized to access it.
     *
     * @param resourceId the resource FawId that could not be reached
     * @return an exception indicating that the resource id could not be reached.
     */
    public static RenderableException generateNotFoundOrNotAuthorizedException(String resourceId) {
        return new RenderableException(ErrorCode.NotAuthorizedOrNotFound,
                String.format("Not Authorized or Unknown resource %s", resourceId));
    }
    public static RenderableException generateInternalServerErrorException() {
        Metrics.emit(MetricsConstants.API_RESPONSE_5XX, 1.0);
        throw new RenderableException(ErrorCode.InternalError, ErrorCode.InternalError.getDefaultMessage());
    }

    public static RenderableException generateInternalServerErrorException(String message) {
        Metrics.emit(MetricsConstants.API_RESPONSE_5XX, 1.0);
        throw new RenderableException(ErrorCode.InternalError, message);
    }

    public static RenderableException generateIncorrectStateException(String message) {
        return new RenderableException(ErrorCode.IncorrectState, message);
    }

    public static RenderableException generateUnsupportedOperationException(String message) {
        return new RenderableException(ErrorCode.UnprocessableEntity, message);
    }

    public static RenderableException generateValidationException(String message) {
        return new RenderableException(ErrorCode.InvalidParameter, message);
    }

    public static RenderableException generateConflictException(String message) {
        return new RenderableException(ErrorCode.NotAuthorizedOrResourceAlreadyExists, message);
    }
}
