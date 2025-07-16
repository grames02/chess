package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class LogoutService {
    private final DataAccess dataAccess;
    public LogoutService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public void logoutService() throws DataAccessException {
        dataAccess.logout();
    }
}
