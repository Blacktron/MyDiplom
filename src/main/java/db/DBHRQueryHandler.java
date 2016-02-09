package db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Entity;
import models.EntityDbHandler;
import models.implementation.HR;
import utils.DBUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
     * @return the list of all HRs.
     * @throws SQLException
     */
    public List<Entity> getAllEntities() throws SQLException {
        String query = "SELECT hr.hrId, hr.hrFirstName, hr.hrLastName, hr.phone, hr.hrEmail, company.companyId, company.companyName, FROM hr, company WHERE hr.companyId = company.companyId ORDER BY company.companyName, hr.hrFirstName, hr.hrLastName";
        return DBUtils.execQueryAndBuildResult(query, null, this);
    }

    /**
     * Adds a new HR to the database.
     * @param node the JSON object holding the data of the HR.
     * @throws SQLException
     */
    public boolean addEntity(JsonNode node) throws SQLException {
        boolean check = false;
        HR hr = new HR(node);

        // Check if an HR with such email address is already present in the database.
        String hrEmail = hr.getHrEmail();
        String hrExistsQuery = "SELECT hrId FROM hr WHERE hrEmail = ?";
        boolean hrExists = DBUtils.isParamExists(hrEmail, hrExistsQuery);

        // Add the new Candidate if the email address is not found in the database.
        if (!hrExists) {
            // Prepare the query and execute it.
            String hrFirstName = hr.getHrFirstName();
            String hrLastName = hr.getHrLastName();
            String phone = hr.getPhone();
            int companyId = hr.getCompanyId();

            // Check if such Company already exists in the database.
            String companyExistsQuery = "SELECT companyId FROM company WHERE companyId = ?";
            boolean companyExists = DBUtils.isParamExists(companyId, companyExistsQuery);

            // If there is such Company in the database, prepare the query and execute it.
            if (companyExists) {
                String query = "INSERT INTO hr(companyId, hrFirstName, hrLastName, phone) VALUES(?, ?, ?, ?)";
                Object[] params = {companyId, hrFirstName, hrLastName, phone};
                DBUtils.execQuery(query, params);
                check = true;
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
        int searchParametersCount = data.size();
        List<Entity> result;

        // Make the proper database query based on the existing query parameter.
        if (searchParametersCount == 0) {
            // If there are no parameters provided - show all entries in the database.
            result = this.getAllEntities();
        }
        else {
            /*
                Make a Map holding the keys and their corresponding values.
                Pass the Map as parameter to a method which dynamically builds a query based on the
                existing parameters.
             */
            Map<String, String> parameters = new HashMap<String, String>();

            for (String key : data.keySet()) {
                String value = data.getFirst(key);
                //System.out.println(key + " " + value);
                parameters.put(key, value);
            }

            result = this.searchHRByParams(parameters);
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
        String query = "SELECT hr.hrId, hr.hrFirstName, hr.hrLastName, hr.phone, hr.hrEmail, company.companyId, company.companyName FROM hr, company WHERE hrId = ? AND hr.companyId = company.companyId GROUP BY hr.hrId ORDER BY hr.hrFirstName, hr.hrLastName";
        List<Entity> result = DBUtils.execQueryAndBuildResult(query, params, this);

        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        else {
            throw new SQLException();
        }
    }

    /**
     * Method which searches for entries based on the provided parameters.
     * @param parameters the parameters to search for.
     * @return a List holding the result of the search.
     * @throws SQLException
     */
    public List<Entity> searchHRByParams(Map<String, String> parameters) throws SQLException {

        String query = "SELECT hr.hrId, hr.hrFirstName, hr.hrLastName, hr.phone, hr.hrEmail, company.companyId, company.companyName FROM hr, company";
        int count = 0;                                                  // Used to build the array holding the parameters for query execution.
        Object[] params = new Object[parameters.size()];                // Array of objects holding the parameters for the query execution.
        Set<String> keys = parameters.keySet();                         // Set holding the keys of the Map used to iterate through it and build the array.
        String fullQuery = DBUtils.buildQuery(parameters, query);       // The query which to be used when execute request to the database.
        fullQuery += " AND hr.companyId = company.companyId ORDER BY hr.hrFirstName, hr.hrLastName";

        System.out.println("FULL QUERY: " + fullQuery);

        for (String key : keys) {
            params[count] = parameters.get(key);
            count++;
        }

        return DBUtils.execQueryAndBuildResult(fullQuery, params, this);
    }

    /**
     * Update a HR entry in the database with provided details.
     * @param hrId the ID of the HR to be edited.
     * @param node the details of the HR which to be used for the modification.
     * @return true if the modification is successful, false otherwise.
     * @throws SQLException
     */
    public boolean updateEntity(int hrId, JsonNode node) throws SQLException {
        boolean check = false;

        String operation;       // The operation which has to take place.
        String query;           // The query which to be executed.
        Object[] params;        // Array of Objects holding the parameters used for the update.

        try {
            JsonNode operations = new ObjectMapper().readTree(String.valueOf(node)).get("operations");
            System.out.println(operations.toString());

            if (operations.isArray()) {
                for (JsonNode action : operations) {
                    operation = action.toString().replaceAll("\"", "");
                    System.out.println("OPERATION: " + operation);

                    // If we want to modify the Company ID which the HR is working for.
                    if (operation.equalsIgnoreCase("modifyCompany")) {
                        // Check if the Company exists in the database.
                        int companyId = Integer.parseInt(node.get("companyId").textValue());
                        String companyExistsQuery = "SELECT companyId FROM company WHERE companyId = ?";
                        boolean companyExists = DBUtils.isParamExists(companyId, companyExistsQuery);

                        // Modify the Company.
                        if (companyExists) {
                            System.out.println("COMPANY EXISTS");
                            query = "UPDATE hr SET companyId = ? WHERE hrId = ?";
                            params = new Object[]{companyId, hrId};
                            DBUtils.execQuery(query, params);
                            check = true;
                        }
                    }

                    // If we want to modify the name(s) of the HR.
                    if (operation.equalsIgnoreCase("modifyName")) {
                        if (node.has("hrFirstName") && node.has("hrLastName")) {
                            String hrFirstName = node.get("hrFirstName").textValue();
                            String hrLastName = node.get("hrLastName").textValue();
                            query = "UPDATE hr SET hrFirstName = ?, hrLastName = ? WHERE hrId = ?";
                            params = new Object[]{hrFirstName, hrLastName, hrId};
                            DBUtils.execQuery(query, params);
                        } else if (node.has("hrFirstName")) {
                            String hrFirstName = node.get("hrFirstName").textValue();
                            query = "UPDATE hr SET hrFirstName = ? WHERE hrId = ?";
                            params = new Object[]{hrFirstName, hrId};
                            DBUtils.execQuery(query, params);
                        } else if (node.has("hrLastName")) {
                            String hrLastName = node.get("hrLastName").textValue();
                            query = "UPDATE hr SET hrLastName = ? WHERE hrId = ?";
                            params = new Object[]{hrLastName, hrId};
                            DBUtils.execQuery(query, params);
                        }

                        check = true;
                    }

                    // If we want to modify the phone of the HR.
                    if (operation.equalsIgnoreCase("modifyPhone")) {
                        String phone = node.get("phone").textValue();
                        query = "UPDATE hr SET phone = ? WHERE hrId = ?";
                        params = new Object[]{phone, hrId};
                        DBUtils.execQuery(query, params);
                        check = true;
                    }

                    // If we want to modify the email of the HR.
                    if (operation.equalsIgnoreCase("modifyEmail")) {
                        String phone = node.get("hrEmail").textValue();
                        query = "UPDATE hr SET hrEmail = ? WHERE hrId = ?";
                        params = new Object[]{phone, hrId};
                        DBUtils.execQuery(query, params);
                        check = true;
                    }
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
                    int hrId = resultSet.getInt("hrId");                        // The ID of the HR.
                    String hrFirstName = resultSet.getString("hrFirstName");    // The first name of the HR.
                    String hrLastName = resultSet.getString("hrLastName");      // The last name of the HR.
                    String phone = resultSet.getString("phone");                // The phone of the HR.
                    String hrEmail = resultSet.getString("hr.hrEmail");         // The email of the HR.
                    String companyName = resultSet.getString("companyName");    // The name of the Company which the HR is working for.

                    // Create the HR object, and put it into the List.
                    HR hr = new HR(hrId, hrFirstName, hrLastName, phone, hrEmail, companyName);
                    data.add(hr);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}