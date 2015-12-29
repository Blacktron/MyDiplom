package db;

import models.Entity;
import models.EntityDbHandler;
import models.implementation.Position;
import utils.DBUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @Created by Terrax on 10-Dec-2015.
 */
public class DBPositionQueryHandler implements EntityDbHandler {
    private static DBPositionQueryHandler instance;

    private DBPositionQueryHandler() { }

    public static DBPositionQueryHandler getInstance() {
        if (instance == null) {
            instance = new DBPositionQueryHandler();
        }

        return instance;
    }

    /**
     * Returns a list of all positions currently entered in the database.
     * @return the list of all positions.
     * @throws SQLException
     */
    public List<Entity> getAllEntities() throws SQLException {
        String query = "SELECT positions.positionId, positions.positionName, positions.hrId, positions.companyId, hr.firstName, hr.lastName, company.companyName " +
                "FROM positions, hr, company " +
                "WHERE positions.hrId = hr.hrId AND positions.companyId = company.companyId";
        return DBUtils.execQueryAndBuildResult(query, null, this);
    }

    /**
     * Adds a new position to the database.
     * @param entity the entity holding the information to create a new entry in the database.
     * @throws SQLException
     */
    public boolean addEntity(Entity entity) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        Position position = null;

        // Open a connection with the database and execute the query.
        try {
            // Check if the entity is a Position and cast it.
            if (entity instanceof Position) {
                position = (Position) entity;
            }

            connection = DBConnectionHandler.openDatabaseConnection();

            // Prepare the query and execute it.
            assert position != null;
            int hrId = position.getHrId();
            int companyId = position.getCompanyId();
            String positionName = position.getPositionName();

            // Check if an HR with such ID exists in the database.
            //String hrQuery = "SELECT hr.hrId, hr.companyId" +
            String hrQuery = "SELECT hr.hrId" +
                    " FROM hr " +
                    "INNER JOIN company ON hr.companyId = company.companyId " +
                    "WHERE hr.hrId = ? AND hr.companyId = ?";
            boolean hrExists = isParamExists(hrId, companyId, hrQuery);

            // Check if a Company with such ID exists in the database.
//            String companyQuery = "SELECT hr.companyId" +
//                    " FROM company " +
//                    "WHERE companyId = ?";
//            boolean companyExists = isParamExists(companyId, companyQuery);

            // If such HR exists, continue with adding the entry into the database.
            if (hrExists) {
                String query = "INSERT INTO positions(positionName, hrId, companyId) VALUES(?, ?, ?)";

                if (connection != null) {
                    statement = connection.prepareStatement(query);

                    statement.setString(1, positionName);
                    statement.setInt(2, hrId);
                    statement.setInt(3, companyId);

                    statement.execute();
                    check = true;
                }
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
     * Deletes a selected position from the database.
     * @param positionId the id of the position to be removed.
     * @throws SQLException
     */
    public void deleteEntity(int positionId) throws SQLException {
        Object[] params = {positionId};
        String query = "DELETE FROM positions WHERE positionId = ?";
        DBUtils.execQuery(query, params);
    }

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

                parameters.put(key, value);
            }

            result = this.searchPositionByParams(parameters);
        }

        return result;
    }

    /**
     * Search if position with the specified ID exists in the database.
     * @param positionId the ID of the position to be searched.
     * @return the position as object.
     * @throws SQLException
     */
    public Entity searchEntityById(int positionId) throws SQLException {
        Object[] params = {positionId};
        String query = "SELECT positions.positionId, positions.hrId, positions.companyId, positions.positionName, hr.firstName, hr.lastName, company.companyName " +
                "FROM positions " +
                "INNER JOIN hr ON positions.hrId = hr.hrId " +
                "INNER JOIN company ON positions.companyId = company.companyId " +
                "WHERE positions.positionId = ?";
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
    public List<Entity> searchPositionByParams(Map<String, String> parameters) throws SQLException {
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
     * Update a position entry in the database with provided details.
     * @param entity the details of the position which to be used for the modification.
     * @return true if the modification is successful, false otherwise.
     * @throws SQLException
     */
    public boolean updateEntity(Entity entity) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        Position position = null;

        // Open a connection with the database, prepare a List which will hold the result and execute the query.
        try {
            if (entity instanceof Position) {
                position = (Position) entity;
            }

            connection = DBConnectionHandler.openDatabaseConnection();

            assert position != null;
            int id = position.getId();
            int hrId = position.getHrId();
            int companyId = position.getCompanyId();
            String positionName = position.getPositionName();

            // Check if an HR with such ID exists in the database.
            String hrQuery = "SELECT hr.hrId, hr.companyId" +
                    " FROM hr " +
                    "INNER JOIN company ON hr.companyId = company.companyId " +
                    "WHERE hr.hrId = ? AND hr.companyId = ?";
            boolean hrExists = isParamExists(hrId, companyId, hrQuery);

            // Check if a Company with such ID exists in the database.
//            String companyQuery = "SELECT hr.companyId" +
//                    " FROM company " +
//                    "WHERE companyId = ?";
//            boolean companyExists = isParamExists(companyId, companyQuery);

            // If both exists, continue with adding the entry into the database.
            if (hrExists) {
                String query = "UPDATE positions SET hrId = ?, companyId = ?, positionName = ? WHERE positionId = ?";

                if (connection != null) {
                    statement = connection.prepareStatement(query);

                    statement.setInt(1, hrId);
                    statement.setInt(2, companyId);
                    statement.setString(3, positionName);
                    statement.setInt(4, id);

                    statement.executeUpdate();
                    check = true;
                }
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
                    int id = resultSet.getInt("positionId");
                    int hrId = resultSet.getInt("hrId");
                    int companyId = resultSet.getInt("companyId");
                    String positionName = resultSet.getString("positionName");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    String companyName = resultSet.getString("companyName");

                    Position position = new Position(id, hrId, companyId, positionName, firstName, lastName, companyName);
                    data.add(position);
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
        return "SELECT positions.positionId, hr.hrId, company.companyId, hr.firstName, hr.lastName, company.companyName, positions.positionName" +
                " FROM positions " +
                "INNER JOIN hr ON positions.hrId = hr.hrId " +
                "INNER JOIN company ON positions.companyId = company.companyId";
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