package db;

import models.Entity;
import models.EntityDbHandler;
import models.implementation.Company;
import utils.DBUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Created by Terrax on 27-Nov-2015.
 */
public class DBCompanyQueryHandler implements EntityDbHandler {
    private static DBCompanyQueryHandler instance;

    private DBCompanyQueryHandler() { }

    public static DBCompanyQueryHandler getInstance() {
        if (instance == null) {
            instance = new DBCompanyQueryHandler();
        }

        return instance;
    }

    /**
     * Returns a list of all companies currently entered in the database.
     * @return the list of all companies.
     * @throws SQLException
     */
    public List<Entity> getAllEntities() throws SQLException {
        String query = "SELECT * FROM companies";
        return DBUtils.execQueryAndBuildResult(query, null, this);

//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//
//        try {
//            connection = DBConnectionHandler.openDatabaseConnection();
//            List<Entity> allCompanies;
//
//
//
//            if (connection != null) {
//                statement = connection.prepareStatement(query);
//                resultSet = statement.executeQuery();
//            }
//
//            allCompanies = buildResult(resultSet);
//
//            return allCompanies;
//        } finally {
//            if (statement != null) {
//                statement.close();
//            }
//
//            if (resultSet != null) {
//                resultSet.close();
//            }
//
//            if (connection != null) {
//                connection.close();
//            }
//        }
    }

    /**
     * Adds a new company to the database.
     * @param entity the entity holding the information to create a new entry in the database.
     * @throws SQLException
     */
    public boolean addEntity(Entity entity) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        Company company = null;

        // Open a connection with the database and execute the query.
        try {
            // Check if the entity is a Company and cast it.
            if (entity instanceof Company) {
                company = (Company) entity;
            }

            connection = DBConnectionHandler.openDatabaseConnection();

            // Prepare the query and execute it.
            assert company != null;
            String companyName = company.getCompanyName();

            String query = "INSERT INTO companies(name) VALUES(?)";

            if (connection != null) {
                statement = connection.prepareStatement(query);
                statement.setString(1, companyName);

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
     * Deletes a selected company from the database.
     * @param companyId the id of the company to be removed.
     * @throws SQLException
     */
    public void deleteEntity(int companyId) throws SQLException {
        Object[] params = {companyId};
        String query = "DELETE FROM companies WHERE companyId = ?";
        DBUtils.execQuery(query, params);
    }

    /**
     * Method which decides which exact search method to use based on the given parameters.
     * @param data map which holds the parameters.
     * @return a list with found results.
     * @throws SQLException
     */
    public List<Entity> searchEntity(MultivaluedMap<String, String> data) throws SQLException {
        String companyName = "";
        List<Entity> result = null;

        // Check which parameters exist in the query.
        if (data.containsKey("name")) {
            companyName = data.getFirst("name");
        }

        if (companyName == null || companyName.equals("")) {
            result = this.getAllEntities();
        }
        if (companyName != null && !companyName.equals("")) {
            result = searchCompanyByName(companyName);
        }

        return result;
    }

    /**
     * Search if company with the specified ID exists in the database.
     * @param companyId the ID of the company to be searched.
     * @return the candidate as object.
     * @throws SQLException
     */
    public Entity searchEntityById(int companyId) throws SQLException {
        Object[] params = {companyId};
        String query = "SELECT * FROM companies WHERE companyId = ?";
        List<Entity> result = DBUtils.execQueryAndBuildResult(query, params, this);

        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        else {
            throw new SQLException();
        }
    }

    /**
     * Search company by name if exists in the database.
     * @param name the name of the company to search for.
     * @return the company as object.
     * @throws SQLException
     */
    private List<Entity> searchCompanyByName(String name) throws SQLException {
        Object[] params = {name};
        String query = "SELECT * FROM companies WHERE name LIKE ?";
        return DBUtils.execQueryAndBuildResult(query, params, this);
    }

    /**
     * Update a company entry in the database with provided details.
     * @param entity the details of the company which to be used for the modification.
     * @return true if the modification is successful, false otherwise.
     * @throws SQLException
     */
    public boolean updateEntity(Entity entity) throws SQLException {
        boolean check = false;

        Connection connection = null;
        PreparedStatement statement = null;
        Company company = null;

        // Open a connection with the database, prepare a List which will hold the result and execute the query.
        try {
            if (entity instanceof Company) {
                company = (Company) entity;
            }

            connection = DBConnectionHandler.openDatabaseConnection();

            assert company != null;
            int id = company.getId();
            String companyName = company.getCompanyName();

            String query = "UPDATE companies SET name = ? WHERE companyId = ?";

            if (connection != null) {
                statement = connection.prepareStatement(query);

                statement.setString(1, companyName);
                statement.setInt(2, id);

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
                    int id = resultSet.getInt("companyId");
                    String companyName = resultSet.getString("name");

                    Company company = new Company(id, companyName);
                    data.add(company);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}