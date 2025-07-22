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
        UserData database_user = dataAccess.getUser(username);
        if (database_user == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        String db_hash_password = database_user.password();
        if (!BCrypt.checkpw(password, db_hash_password)) {
            throw new DataAccessException("Error: unauthorized");
        }
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        dataAccess.createAuth(authData);
        return new LoginResult(username,token);
    }
}
