package db;

import models.Entity;
import models.EntityDbHandler;
import models.implementation.Experience;
import utils.DBUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @Created by Terrax on 06-Dec-2015.
 */
public class DBExperienceQueryHandler implements EntityDbHandler {
    private static DBExperienceQueryHandler instance;

    private DBExperienceQueryHandler() { }

    public static DBExperienceQueryHandler getInstance() {
        if (instance == null) {
            instance = new DBExperienceQueryHandler();
        }

        return instance;
    }

    /**
     * Returns a list of all experiences currently entered in the database.
     * @return the list of all experiences.
     * @throws SQLException
     */
    public List<Entity> getAllEntities() throws SQLException {
        String query = "SELECT * FROM experience";
        return DBUtils.execQueryAndBuildResult(query, null, this);
    }

    /**
     * Adds a new experience to the database.
     * @param entity the entity holding the information to create a new entry in the database.
     * @throws SQLException
     */
    public boolean addEntity(Entity entity) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        Experience experience = null;

        // Open a connection with the database and execute the query.
        try {
            // Check if the entity is a Experience and cast it.
            if (entity instanceof Experience) {
                experience = (Experience) entity;
            }

            connection = DBConnectionHandler.openDatabaseConnection();

            // Prepare the query and execute it.
            assert experience != null;
            int candidateId = experience.getCandidateId();
            int techId = experience.getTechId();
            int years = experience.getYears();

            // Check if a Candidate with such ID exists in the database.
            String candidateQuery = "SELECT candidate.candidateId" +
                    " FROM candidate " +
                    //"INNER JOIN company ON hr.companyId = company.companyId " +
                    "WHERE candidate.candidateId = ? AND (candidate.technology1 = ? OR candidate.technology2 = ? OR candidate.technology3 = ?)";
            boolean hrExists = isParamExists(hrId, companyId, candidateQuery);

            String query = "INSERT INTO experience(candidateId, techId, years) VALUES(?, ?, ?)";

            if (connection != null) {
                statement = connection.prepareStatement(query);

                statement.setInt(1, candidateId);
                statement.setInt(2, techId);
                statement.setInt(3, years);

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
     * Deletes a selected experience from the database.
     * @param experienceId the id of the experience to be removed.
     * @throws SQLException
     */
    public void deleteEntity(int experienceId) throws SQLException {
        Object[] params = {experienceId};
        String query = "DELETE FROM experience WHERE experienceId = ?";
        DBUtils.execQuery(query, params);
    }

    /**
     * Method which decides which exact search method to use based on the given parameters.
     * @param data map which holds the parameters.
     * @return a list with found results.
     * @throws SQLException
     */
    public List<Entity> searchEntity(MultivaluedMap<String, String> data) throws SQLException {
        // TODO: In the front end - if a param is 0 then don't put it in the request URL.
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

            result = this.searchExperienceByParams(parameters);
        }

        return result;
    }

    /**
     * Search if experience with the specified ID exists in the database.
     * @param experienceId the ID of the experience to be searched.
     * @return the experience as object.
     * @throws SQLException
     */
    public Entity searchEntityById(int experienceId) throws SQLException {
        Object[] params = {experienceId};
        String query = "SELECT * FROM experience WHERE experienceId = ?";
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
    public List<Entity> searchExperienceByParams(Map<String, String> parameters) throws SQLException {
        int count = 0;                                      // Used to build the array holding the parameters for query execution.
        Object[] params = new Object[parameters.size()];    // Array of objects holding the parameters for the query execution.
        Set<String> keys = parameters.keySet();             // Set holding the keys of the Map used to iterate through it and build the array.
        String query = buildQuery(parameters);              // The query which to be used when execute request to the database.
        System.out.println(query);

        for (String key : keys) {
            params[count] = parameters.get(key);
            count++;
        }

        return DBUtils.execQueryAndBuildResult(query, params, this);
    }

    /**
     * Update a experience entry in the database with provided details.
     * @param entity the details of the experience which to be used for the modification.
     * @return true if the modification is successful, false otherwise.
     * @throws SQLException
     */
    public boolean updateEntity(Entity entity) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        Experience experience = null;

        // Open a connection with the database, prepare a List which will hold the result and execute the query.
        try {
            if (entity instanceof Experience) {
                experience = (Experience) entity;
            }

            connection = DBConnectionHandler.openDatabaseConnection();

            assert experience != null;
            int id = experience.getId();
            int candidateId = experience.getCandidateId();
            int techId = experience.getTechId();
            int years = experience.getYears();

            String query = "UPDATE experience SET candidateId = ?, techId = ?, years = ? WHERE experienceId = ?";

            if (connection != null) {
                statement = connection.prepareStatement(query);

                statement.setInt(1, candidateId);
                statement.setInt(2, techId);
                statement.setInt(3, years);
                statement.setInt(4, id);

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
                    int id = resultSet.getInt("experienceId");
                    int candidateId = resultSet.getInt("candidateId");
                    int techId = resultSet.getInt("techId");
                    int years = resultSet.getInt("years");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    String techName = resultSet.getString("name");

                    Experience experience = new Experience(id, candidateId, techId, years, firstName, lastName, techName);
                    data.add(experience);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    /**
     * Method which builds a query based on the provided criteria.
     * @param parameters the parameters to search for.
     * @return the query used for request execution.
     */
    private String buildQuery(Map<String, String> parameters) {
        String query = buildSelectAndFromPart();
        query += buildWherePart(parameters);

        return query;
    }

    /**
     * Method which builds the SELECT and FROM part of the query.
     * @return the query build so far.
     */
    private String buildSelectAndFromPart() {
        return "SELECT experience.experienceId, candidate.candidateId, technology.techId, candidate.firstName, candidate.lastName, technology.name, years" +
                " FROM experience " +
                "INNER JOIN candidate ON experience.candidateId = candidate.candidateId " +
                "INNER JOIN technology ON experience.techId = technology.techId";
    }

    /**
     * Method which builds the WHERE part of the query.
     * @param parameters the parameters to search for.
     * @return the full query.
     */
    private String buildWherePart(Map<String, String> parameters) {
        int paramsAdded = 0;
        StringBuilder where = new StringBuilder();
        where.append(" WHERE");

        for (String key : parameters.keySet()) {
            where.append(" ");
            where.append(key);
            where.append(" = ?");

            paramsAdded++;
            if (paramsAdded != parameters.size()) {
                where.append(" AND");
            }
        }

        return where.toString();
    }

    /**
     * Method which checks if an entry exists in the database.
     * This is used when making a new entry or an update to an entry making
     * sure not to add something which is missing from the database.
     * @param hrId the ID of the HR for which to search in the database.
     * @param companyId the ID of the Company for which to search in the database.
     * @param query the query to be executed.
     * @return true if the parameter exists in the database, false otherwise.
     * @throws SQLException
     */
    private boolean isParamExists(int hrId, int companyId, String query) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnectionHandler.openDatabaseConnection();

            if (connection != null) {
                statement = connection.prepareStatement(query);

                statement.setInt(1, hrId);
                statement.setInt(2, companyId);

                resultSet = statement.executeQuery();
            }

            if (resultSet != null) {
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
}