package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import org.eclipse.jetty.util.log.Log;
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
        UserData user = dataAccess.getUser(username);
        if (user == null || !BCrypt.checkpw(password, user.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        dataAccess.createAuth(authData);
        return new LoginResult(username,token);
    }
}
