package ba.unsa.etf.rpr.chatapp.server.dao;

import ba.unsa.etf.rpr.chatapp.server.beans.Role;
import ba.unsa.etf.rpr.chatapp.server.business.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoleDao {

    private static RoleDao instance = null;

    private RoleDao() {}

    public static RoleDao getInstance() {

        if (instance == null)
            instance = new RoleDao();

        return instance;
    }

    public Role get(long id) {

        try {

            int rowCount = 0;
            Role role = null;
            ResultSet rs = DatabaseConnection.getInstance().runQuery(String.format("SELECT * FROM roles WHERE id = '%d'", id));

            while (rs.next()) {

                role = new Role(rs.getInt("id"), rs.getString("name"), rs.getString("color"), rs.getString("description"));
                ++rowCount;
            }

            if (rowCount == 0) {

                System.out.println("[UserDao][get(int)] Found 0 roles, returning null");
                return null;
            }

            if (rowCount > 1)
                System.out.println("WARNING: Multiple roles with the same id returned from the database!");
            System.out.println("Read " + rowCount + " rows from the database");

            return role;

        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    public ArrayList<Role> get(ArrayList<Long> ids) throws SQLException {

        DatabaseConnection dbConn = DatabaseConnection.getInstance();

        String sqlParameters = ids.stream().map(v -> "?").collect(Collectors.joining(", "));
        String query = String.format("SELECT * FROM roles WHERE id IN (%s)", sqlParameters);

        ArrayList<Role> r = new ArrayList<>();

        ResultSet rs = dbConn.runInQuery(query, ids.toArray());
        while (rs.next())
            r.add(new Role(rs.getInt("id"), rs.getString("name"), rs.getString("color"), rs.getString("description")));

        return r;
    }

    public List<Role> getAll() throws SQLException {

        ArrayList<Role> roles = new ArrayList<>();
        ResultSet rs = DatabaseConnection.getInstance().runQuery("SELECT * FROM roles");

        while (rs.next())
            roles.add(new Role(rs.getInt("id"), rs.getString("name"), rs.getString("color"), rs.getString("description")));

        for (Role r : roles)
            System.out.println("Read permission for id " + r.getId() + " " + r);

        return roles;
    }

    public Role add(Role role) {

        try {

            String[] s = { role.getName(), role.getColor(), role.getDescription() };
            ResultSet keys = DatabaseConnection.getInstance().runInsertQuery("INSERT INTO roles (name, color, description) VALUES (?, ?, ?)", s);

            keys.next();
            long userId = keys.getLong(1);

            return get((int) userId);

        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Error adding role!");
            return null;
        }
    }

    public Role update(Role role) {

        return null;
    }

    public void delete(int id) {

    }
}
