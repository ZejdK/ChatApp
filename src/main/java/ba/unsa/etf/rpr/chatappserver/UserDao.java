package ba.unsa.etf.rpr.chatappserver;

import at.favre.lib.crypto.bcrypt.BCrypt;
import ba.unsa.etf.rpr.LoginData;
import ba.unsa.etf.rpr.User;

import java.sql.ResultSet;
import java.util.List;

public class UserDao {

    private static UserDao instance = null;
    private static DatabaseConnection dbConn = null;

    private UserDao() {}

    public static void setDatabaseConnection(DatabaseConnection databaseConnection) {

        dbConn = databaseConnection;
    }

    public static UserDao getInstance() {

        if (instance == null)
            instance = new UserDao();

        return instance;
    }

    public User get(int id) {

        try {

            int rowCount = 0;
            User user = null;
            ResultSet rs = dbConn.runQuery(String.format("SELECT * FROM users WHERE id = '%d'", id));

            while (rs.next()) {

                user = new User(rs.getLong("id"), rs.getString("username"), rs.getString("passwordhash"));
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

                user = new User(rs.getLong("id"), rs.getString("username"), rs.getString("passwordhash"));
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

    public List<User> getAll() {

        return null;
    }
}
