package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class LoginService {
    private final DataAccess dataAccess;
    public LoginService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public LoginResult loginService(LoginRequest request) throws DataAccessException {
        var username = request.username();
        var password = request.password();
        UserData databaseUser = dataAccess.getUser(username);
        if (databaseUser == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        String dbHashPassword = databaseUser.password();
        if (!BCrypt.checkpw(password, dbHashPassword)) {
            throw new DataAccessException("Error: unauthorized");
        }
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        dataAccess.createAuth(authData);
        return new LoginResult(username,token);
    }
}
