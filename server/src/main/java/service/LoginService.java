package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class LoginService {
    private final DataAccess dataAccess;
    public LoginService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public void loginService() throws DataAccessException {
        dataAccess.getUser();
    }
}
