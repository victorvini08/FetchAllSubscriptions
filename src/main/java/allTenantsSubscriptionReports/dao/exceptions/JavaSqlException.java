package allTenantsSubscriptionReports.dao.exceptions;

public class JavaSqlException extends RuntimeException {
    public JavaSqlException(Exception ex) {
        super(ex);
    }

    public JavaSqlException(String s) {
        super(s);
    }
}
