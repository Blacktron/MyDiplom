package db;

import models.Entity;
import models.EntityDbHandler;
import models.implementation.HR;
import utils.DBUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Created by Terrax on 09-Dec-2015.
 */
public class DBHRQueryHandler implements EntityDbHandler {
    private static DBHRQueryHandler instance;

    private DBHRQueryHandler() { }

    public static DBHRQueryHandler getInstance() {
        if (instance == null) {
            instance = new DBHRQueryHandler();
        }

        return instance;
    }

    /**
     * Returns a list of all HRs currently entered in the database.
     * @return the list of all candidates.
     * @throws SQLException
     */
    public List<Entity> getAllEntities() throws SQLException {
        String query = "SELECT * FROM hr";
        return DBUtils.execQueryAndBuildResult(query, null, this);
    }

    /**
     * Adds a new HR to the database.
     * @param entity the entity holding the information to create a new entry in the database.
     * @throws SQLException
     */
    public boolean addEntity(Entity entity) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        HR hr = null;

        // Open a connection with the database and execute the query.
        try {
            // Check if the entity is a Candidate and cast it.
            if (entity instanceof HR) {
                hr = (HR) entity;
            }

            connection = DBConnectionHandler.openDatabaseConnection();

            // Prepare the query and execute it.
            assert hr != null;
            String fname = hr.getFirstName();
            String lname = hr.getLastName();
            String phone = hr.getPhone();
            int companyId = hr.getCompanyId();

            String query = "INSERT INTO hr(firstName, lastName, phone, companyId) VALUES(?, ?, ?, ?)";

            if (connection != null) {
                statement = connection.prepareStatement(query);

                statement.setString(1, fname);
                statement.setString(2, lname);
                statement.setString(3, phone);
                statement.setInt(4, companyId);

                statement.execute();
                check = true;
            }
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }

        return check;
    }

    /**
     * Deletes a selected HR from the database.
     * @param hrId the id of the HR to be removed.
     * @throws SQLException
     */
    public void deleteEntity(int hrId) throws SQLException {
        Object[] params = {hrId};
        String query = "DELETE FROM hr WHERE hrId = ?";
        DBUtils.execQuery(query, params);
    }

    /**
     * Method which decides which exact search method to use based on the given parameters.
     * @param data map which holds the parameters.
     * @return a list with found results.
     * @throws SQLException
     */
    public List<Entity> searchEntity(MultivaluedMap<String, String> data) throws SQLException {
        String firstName = "";
        String lastName = "";
        List<Entity> result;

        // Check which parameters exist in the query.
        if (data.containsKey("fName")) {
            firstName = data.getFirst("fName");
        }

        if (data.containsKey("lName")) {
            lastName = data.getFirst("lName");
        }

        // Make the proper database query based on the existing query parameter.
        if ((firstName == null || firstName.equals("")) && (lastName == null || lastName.equals(""))) {
            result = this.getAllEntities();
        }
        else if (firstName != null & (lastName == null || lastName.equals(""))) {
            result = this.searchHRByName(firstName);
        }
        else {
            result = this.searchHRByName(firstName, lastName);
        }

        return result;
    }

    /**
     * Search if HR with the specified ID exists in the database.
     * @param hrId the ID of the HR to be searched.
     * @return the HR as object.
     * @throws SQLException
     */
    public Entity searchEntityById(int hrId) throws SQLException {
        Object[] params = {hrId};
        String query = "SELECT * FROM hr WHERE hrId = ?";
        List<Entity> result = DBUtils.execQueryAndBuildResult(query, params, this);

        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        else {
            throw new SQLException();
        }
    }

    /**
     * Search by either first or last name if HR exists in the database.
     * @param name the name of the HR to search for.
     * @return the HR as object.
     * @throws SQLException
     */
    private List<Entity> searchHRByName(String name) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        // Open a connection with the database, prepare a List which will hold the result and execute the query.
        try {
            List<Entity> found;
            connection = DBConnectionHandler.openDatabaseConnection();
            String query = "SELECT * FROM hr WHERE firstName LIKE ? OR lastName LIKE ?";

            if (connection != null) {
                statement = connection.prepareStatement(query);

                statement.setString(1, "%" + name + "%");
                statement.setString(2, "%" + name + "%");

                resultSet = statement.executeQuery();
            }

            found = this.buildResult(resultSet);

            return found;
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Search if HR exists in the database by both first and last names.
     * @param fName the first name of the HR to search for.
     * @param lName the last name of the HR to search for.
     * @return the HR as object.
     * @throws SQLException
     */
    private List<Entity> searchHRByName(String fName, String lName) throws SQLException {
        Object[] params = {fName, lName};
        String query = "SELECT * FROM hr WHERE firstName = ? AND lastName = ?";
        return DBUtils.execQueryAndBuildResult(query, params, this);
    }

    /**
     * Update a HR entry in the database with provided details.
     * @param entity the details of the HR which to be used for the modification.
     * @return true if the modification is successful, false otherwise.
     * @throws SQLException
     */
    public boolean updateEntity(Entity entity) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        HR hr = null;

        // Open a connection with the database, prepare a List which will hold the result and execute the query.
        try {
            if (entity instanceof HR) {
                hr = (HR) entity;
            }

            connection = DBConnectionHandler.openDatabaseConnection();

            assert hr != null;
            int id = hr.getId();
            String fname = hr.getFirstName();
            String lname = hr.getLastName();
            String phone = hr.getPhone();
            int companyId = hr.getCompanyId();

            String query = "UPDATE hr SET firstName = ?, lastName = ?, phone = ?, companyId = ? WHERE hrId = ?";

            if (connection != null) {
                statement = connection.prepareStatement(query);

                statement.setString(1, fname);
                statement.setString(2, lname);
                statement.setString(3, phone);
                statement.setInt(4, companyId);
                statement.setInt(5, id);

                statement.executeUpdate();
                check = true;
            }

            return check;
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Method which builds the result returned from the database.
     * @param resultSet the result given by the database.
     * @return list holding the result.
     */
    public List<Entity> buildResult(ResultSet resultSet) {
        List<Entity> data = new ArrayList<Entity>();

        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    int id = resultSet.getInt("hrId");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    String phone = resultSet.getString("phone");
                    int companyId = resultSet.getInt("companyId");

                    HR hr = new HR(id, firstName, lastName, phone, companyId);
                    data.add(hr);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}