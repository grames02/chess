package service;


import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DataManager;
import model.*;

import java.util.UUID;

public class RegisterService {
    private final DataAccess dataAccess;

    public RegisterService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(RegisterRequest request) throws DataAccessException {
        String username = request.getUsername();
        String email = request.getEmail();
        String password = request.getPassword();

        if (username == null || email == null || password == null) {
            return null;
        }
        if (dataAccess.getUser(username) != null) {
            return null;
        }
        UserData user = new UserData(username, password, email);
        dataAccess.createUser(user);

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        dataAccess.createAuth(auth);
        return auth;
    }
}
