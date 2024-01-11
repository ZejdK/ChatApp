package ba.unsa.etf.rpr.chatapp.server.dao;

import at.favre.lib.crypto.bcrypt.BCrypt;
import ba.unsa.etf.rpr.chatapp.server.beans.User;
import ba.unsa.etf.rpr.chatapp.server.business.DatabaseConnection;
import ba.unsa.etf.rpr.chatapp.shared.dto.LoginData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDao {

    private static UserDao instance = null;
    private static DatabaseConnection dbConn = null;

    private UserDao() {

        dbConn = DatabaseConnection.getInstance();
    }

    public static UserDao getInstance() {

        if (instance == null)
            instance = new UserDao();

        return instance;
    }

    public User get(long id) {

        try {

            int rowCount = 0;
            User user = null;
            ResultSet rs = dbConn.runQuery(String.format("SELECT * FROM users WHERE id = '%d'", id));

            while (rs.next()) {

                user = new User(rs.getLong("id"), rs.getString("username"), rs.getString("password"));
                user.setRoles(getRoles(user.getId()));
                ++rowCount;
            }

            if (rowCount == 0) {

                System.out.println("[UserDao][get(int)] Found 0 users, returning null");
                return null;
            }

            if (rowCount > 1)
                System.out.println("WARNING: Multiple users with the same id returned from the database!");
            System.out.println("Read " + rowCount + " rows from the database");

            return user;

        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    public User get(String username) {

        try {

            int rowCount = 0;
            User user = null;
            ResultSet rs = dbConn.runQuery(String.format("SELECT * FROM users WHERE username = '%s'", username));

            while (rs.next()) {

                user = new User(rs.getLong("id"), rs.getString("username"), rs.getString("password"));
                user.setRoles(getRoles(user.getId()));
                ++rowCount;
            }

            if (rowCount == 0) {

                System.out.println("[UserDao][get(String)] Found 0 users, returning null");
                return null;
            }

            if (rowCount > 1)
                System.out.println("WARNING: Multiple users with the same username returned from the database!");
            System.out.println("Read " + rowCount + " rows from the database");

            return user;

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

    public User add(LoginData loginData) {

        try {

            String[] s = { loginData.username, BCrypt.withDefaults().hashToString(12, loginData.password.toCharArray()) };
            ResultSet keys = dbConn.runInsertQuery("INSERT INTO users (username, passwordhash) VALUES (?, ?)", s);

            keys.next();
            long userId = keys.getLong(1);

            return get((int) userId);

        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Error creating user!");
            return null;
        }
    }

    public User update(User user) {
        return null;
    }

    public User delete(int id) {
        return null;
    }

    public ArrayList<User> getAll() throws SQLException {

        ResultSet rs = dbConn.runQuery("SELECT * FROM users");

        ArrayList<User> users = new ArrayList<>();
        while (rs.next()) {

            User u = new User(rs.getLong("id"), rs.getString(2), rs.getString(3));
            u.setRoles(getRoles(u.getId()));

            users.add(u);
        }

        return users;
    }

    private ArrayList<Long> getRoles(long id) throws SQLException {

        ResultSet rs = dbConn.runQuery(String.format("SELECT * FROM roleownership WHERE userid = '%d'", id));

        ArrayList<Long> perms = new ArrayList<>();
        while (rs.next())
            perms.add(rs.getLong("roleId"));

        for (Long p : perms)
            System.out.println("Read permission for id " + id + " " + p);

        return perms;
    }
}