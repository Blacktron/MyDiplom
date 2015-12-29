package db;

import models.Entity;
import models.EntityDbHandler;
import models.implementation.Technology;
import utils.DBUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Created by Terrax on 18-Oct-2015.
 */
public class DBTechnologyQueryHandler implements EntityDbHandler {
    private static DBTechnologyQueryHandler instance;

    private DBTechnologyQueryHandler() { }

    public static DBTechnologyQueryHandler getInstance() {
        if (instance == null) {
            instance = new DBTechnologyQueryHandler();
        }

        return instance;
    }

    /**
     * Returns a list of all technologies currently entered in the database.
     * @return the list of all technologies.
     * @throws SQLException
     */
    public List<Entity> getAllEntities() throws SQLException {
        String query = "SELECT * FROM tech";
        return DBUtils.execQueryAndBuildResult(query, null, this);
//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//
//        try {
//            connection = DBConnectionHandler.openDatabaseConnection();
//            List<Entity> allTechnologies;
//
//            String query = "SELECT * FROM tech";
//
//            if (connection != null) {
//                statement = connection.prepareStatement(query);
//                resultSet = statement.executeQuery();
//            }
//
//            allTechnologies = buildResult(resultSet);
//
//            return allTechnologies;
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
     * Adds a new technology to the database.
     * @param entity the entity holding the information to create a new entry in the database.
     * @throws SQLException
     */
    public boolean addEntity(Entity entity) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        Technology technology = null;

        // Open a connection with the database and execute the query.
        try {
            // Check if the entity is a Technology and cast it.
            if (entity instanceof Technology) {
                technology = (Technology) entity;
            }

            connection = DBConnectionHandler.openDatabaseConnection();

            // Prepare the query and execute it.
            assert technology != null;
            String techName = technology.getName();

            String query = "INSERT INTO tech(name) VALUES(?)";

            if (connection != null) {
                statement = connection.prepareStatement(query);
                statement.setString(1, techName);

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
     * Deletes a selected technology from the database.
     * @param techId the id of the technology to be removed.
     * @throws SQLException
     */
    public void deleteEntity(int techId) throws SQLException {
        Object[] params = {techId};
        String query = "DELETE FROM tech WHERE techId = ?";
        DBUtils.execQuery(query, params);
//        Connection connection = null;
//        PreparedStatement statement = null;
//
//        // Open a connection with the database, prepare the query and execute it.
//        try {
//            connection = DBConnectionHandler.openDatabaseConnection();
//            String query = "DELETE FROM tech WHERE techId = ?";
//
//            if (connection != null) {
//                statement = connection.prepareStatement(query);
//                statement.setInt(1, id);
//
//                statement.execute();
//            }
//        } finally {
//            if (statement != null) {
//                statement.close();
//            }
//
//            if (connection != null) {
//                connection.close();
//            }
//        }
    }

    /**
     * Method which decides which exact search method to use based on the given parameters.
     * @param data map which holds the parameters.
     * @return a list with found results.
     * @throws SQLException
     */
    public List<Entity> searchEntity(MultivaluedMap<String, String> data) throws SQLException {
        String techName = "";
        List<Entity> result = null;

        // Check which parameters exist in the query.
        if (data.containsKey("name")) {
            techName = data.getFirst("name");
        }

        if (techName == null || techName.equals("")) {
            result = this.getAllEntities();
        }
        if (techName != null && !techName.equals("")) {
            result = searchTechnologyByName(techName);
        }

        return result;
    }

    /**
     * Search if technology with the specified ID exists in the database.
     * @param techId the ID of the technology to be searched.
     * @return the technology as object.
     * @throws SQLException
     */
    public Entity searchEntityById(int techId) throws SQLException {
        Object[] params = {techId};
        String query = "SELECT * FROM tech WHERE techId = ?";
        List<Entity> result = DBUtils.execQueryAndBuildResult(query, params, this);

        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        else {
            throw new SQLException();
        }

//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//        Technology technology = null;
//
//        // Open a connection with the database, prepare the query and execute it.
//        try {
//            connection = DBConnectionHandler.openDatabaseConnection();
//            String query = "SELECT * FROM tech WHERE techId = ?";
//
//            if (connection != null) {
//                statement = connection.prepareStatement(query);
//                statement.setInt(1, techId);
//                resultSet = statement.executeQuery();
//            }
//
//            if (resultSet != null) {
//                while (resultSet.next()) {
//                    int id = resultSet.getInt("techId");
//                    String techName = resultSet.getString("name");
//
//                    technology = new Technology(id, techName);
//                }
//            }
//
//            return technology;
//        } finally {
//            if (statement != null) {
//                statement.close();
//            }
//
//            if (connection != null) {
//                connection.close();
//            }
//        }
    }

    /**
     * Search company by name if exists in the database.
     * @param name the name of the technology to search for.
     * @return the technology as object.
     * @throws SQLException
     */
    private List<Entity> searchTechnologyByName(String name) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        // Open a connection with the database, prepare a List which will hold the result and execute the query.
        try {
            List<Entity> found;
            connection = DBConnectionHandler.openDatabaseConnection();

            String query = "SELECT * FROM tech WHERE name LIKE ?";

            if (connection != null) {
                statement = connection.prepareStatement(query);
                statement.setString(1, "%" + name + "%");
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
     * Update a technology entry in the database with provided details.
     * @param entity the details of the technology which to be used for the modification.
     * @return true if the modification is successful, false otherwise.
     * @throws SQLException
     */
    public boolean updateEntity(Entity entity) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        Technology technology = null;

        // Open a connection with the database, prepare a List which will hold the result and execute the query.
        try {
            if (entity instanceof Technology) {
                technology = (Technology) entity;
            }

            connection = DBConnectionHandler.openDatabaseConnection();

            assert technology != null;
            int id = technology.getId();
            String techName = technology.getName();

            String query = "UPDATE tech SET name = ? WHERE techId = ?";

            if (connection != null) {
                statement = connection.prepareStatement(query);

                statement.setString(1, techName);
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
                    int id = resultSet.getInt("techId");
                    String techName = resultSet.getString("name");

                    Technology technology = new Technology(id, techName);
                    data.add(technology);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}