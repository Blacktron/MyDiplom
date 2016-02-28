package db;

import com.fasterxml.jackson.databind.JsonNode;
import models.Entity;
import models.EntityDbHandler;
import models.implementation.Company;
import utils.DBUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
     * Returns a list of all Companies currently entered in the database.
     * @return the list of all Companies.
     * @throws SQLException
     */
    public List<Entity> getAllEntities() throws SQLException {
        String query = "SELECT * FROM company";
        return DBUtils.execQueryAndBuildResult(query, null, this);
    }

    /**
     * Adds a new Company to the database.
     * @param node the JSON object holding the data of the Company.
     * @throws SQLException
     */
    public boolean addEntity(JsonNode node) throws SQLException {
        boolean check = false;
        Company company = new Company(node);

        // Check if such Company exists in the database.
        String companyName = company.getCompanyName();
        String companyExistsQuery = "SELECT companyId FROM company WHERE companyName = ?";
        boolean companyExists = DBUtils.isParamExists(companyName, companyExistsQuery);

        // If there is no such Company in the database, prepare the query and execute it.
        if (!companyExists) {
            String query = "INSERT INTO company(companyName) VALUES(?)";
            Object[] params = {companyName};
            DBUtils.execQuery(query, params);
            check = true;
        }

        return check;
    }

    /**
     * Deletes a selected Company from the database.
     * @param companyId the ID of the Company to be removed.
     * @throws SQLException
     */
    public void deleteEntity(int companyId) throws SQLException {
        Object[] params = {companyId};
        String query = "DELETE FROM company WHERE companyId = ?";
        DBUtils.execQuery(query, params);
    }

    /**
     * Method which decides which exact search method to use based on the given parameters.
     * @param data map which holds the parameters.
     * @return a list with found results.
     * @throws SQLException
     */
    public List<Entity> searchEntity(MultivaluedMap<String, String> data) throws SQLException {
        int searchParametersCount = data.size();
        String companyName;
        List<Entity> result;

        // Check which parameters exist in the query.
        if (searchParametersCount == 0) {
            // If there are no parameters provided - show all entries in the database.
            result = this.getAllEntities();
        } else if (searchParametersCount == 1 && data.containsKey("companyName")) {
            companyName = data.getFirst("companyName");
            result = searchCompanyByName(companyName);
        } else {
            /*
                Make a Map holding the keys and their corresponding values.
                Pass the Map as parameter to a method which dynamically builds a query based on the
                existing parameters.
             */
            Map<String, String> parameters = new HashMap<String, String>();

            for (String key : data.keySet()) {
                String value = data.getFirst(key);
                parameters.put(key, value);
            }

            result = this.searchCompanyByParams(parameters);
        }

        return result;
    }

    /**
     * Search if Company with the specified ID exists in the database.
     * @param companyId the ID of the Company to be searched.
     * @return the Company as object.
     * @throws SQLException
     */
    public Entity searchEntityById(int companyId) throws SQLException {
        Object[] params = {companyId};
        String query = "SELECT * FROM company WHERE companyId = ?";
        List<Entity> result = DBUtils.execQueryAndBuildResult(query, params, this);

        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        else {
            throw new SQLException();
        }
    }

    /**
     * Search Company by name if exists in the database.
     * @param name the name of the Company to search for.
     * @return the Company as object.
     * @throws SQLException
     */
    public List<Entity> searchCompanyByName(String name) throws SQLException {
        Object[] params = {name};
        String query = "SELECT * FROM company WHERE companyName LIKE ?";
        return DBUtils.execQueryAndBuildResult(query, params, this);
    }

    /**
     * Method which searches for entries based on the provided parameters.
     * @param parameters the parameters to search for.
     * @return a List holding the result of the search.
     * @throws SQLException
     */
    public List<Entity> searchCompanyByParams(Map<String, String> parameters) throws SQLException {
        // The SELECT and FROM part of the query.
        String query = "SELECT * FROM company";
        int count = 0;                                                  // Used to build the array holding the parameters for query execution.
        Object[] params = new Object[parameters.size()];                // Array of objects holding the parameters for the query execution.
        Set<String> keys = parameters.keySet();                         // Set holding the keys of the Map used to iterate through it and build the array.
        String fullQuery = DBUtils.buildQuery(parameters, query);       // The query which to be used when execute request to the database.

        System.out.println("FULL QUERY: " + fullQuery);

        for (String key : keys) {
            params[count] = parameters.get(key);
            count++;
        }

        return DBUtils.execQueryAndBuildResult(fullQuery, params, this);
    }

    /**
     * Update a Company entry in the database with provided details.
     * @param companyId the ID of the Company to be edited.
     * @param node the details of the Company which to be used for the modification.
     * @return true if the modification is successful, false otherwise.
     * @throws SQLException
     */
    public boolean updateEntity(int companyId, JsonNode node) throws SQLException {
        boolean check = false;

        String operation = node.get("operations").textValue();
        String query;
        Object[] params;

        // If we want to edit the name of a Company.
        if (operation.equalsIgnoreCase("modifyCompany")) {
            // Check if such Company exists in the database.
            String companyExistsQuery = "SELECT companyId FROM company WHERE companyId = ?";
            boolean companyExists = DBUtils.isParamExists(companyId, companyExistsQuery);

            // Update the Company.
            if (companyExists) {
                String companyName = node.get("companyName").textValue();
                query = "UPDATE company SET companyName = ? WHERE companyId = ?";
                params = new Object[]{companyName, companyId};
                DBUtils.execQuery(query, params);
            }

            check = true;
        }

        return check;
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
                    String companyName = resultSet.getString("companyName");

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