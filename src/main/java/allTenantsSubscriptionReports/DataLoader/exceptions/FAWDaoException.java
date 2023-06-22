package allTenantsSubscriptionReports.DataLoader.exceptions;

public class FAWDaoException extends RuntimeException {
    public FAWDaoException(Exception ex) {
        super(ex);
    }

    public FAWDaoException(String s) {
        super(s);
    }

    public FAWDaoException(String message, Throwable t) {
        super(message, t);
    }
}
