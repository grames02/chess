package service;


import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class RegisterService {
    private final DataAccess dataAccess;

    public RegisterService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void registerService() throws DataAccessException {
        dataAccess.();

    }
}
