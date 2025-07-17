package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class LogoutService {
    private final DataAccess dataAccess;
    public LogoutService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void logoutService(String authToken) throws DataAccessException {
        var auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        else {
            dataAccess.deleteAuth(authToken);
        }
    }
}
