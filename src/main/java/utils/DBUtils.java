package utils;

import db.DBConnectionHandler;
import models.Entity;
import models.EntityDbHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @Created by Terrax on 27-Nov-2015.
 */
public class DBUtils {

    /**
     * Method which makes a connection to the database, executes a query and builds a List with the
     * result returned from the database.
     * @param query the query which to be executed.
     * @param params the parameters which will be used for the query.
     * @param entityDbHandler interface object used to process data related to the object type used in the context (Candidate, HR and etc.).
     * @return the List build using the data received from the database.
     * @throws SQLException
     */
    public static List<Entity> execQueryAndBuildResult(String query, Object[] params, EntityDbHandler entityDbHandler) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnectionHandler.openDatabaseConnection();
            List<Entity> allEntities;       // The List holding the data returned from the database.

            if (connection != null) {
                statement = connection.prepareStatement(query);

                // Build the query and place the received parameters on the placeholders.
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        Object param = params[i];
                        statement.setObject(i + 1, param);
                    }
                }

                // Execute the query.
                resultSet = statement.executeQuery();
            }

            // Build the result and return in.
            allEntities = entityDbHandler.buildResult(resultSet);

            return allEntities;
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (resultSet != null) {
                resultSet.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Method which makes a connection to the database, executes a query and builds a List with the
     * result returned from the database.
     * @param query the query which to be executed.
     * @param params the parameters which will be used for the query.
     * @throws SQLException
     */
    public static void execQuery(String query, Object[] params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnectionHandler.openDatabaseConnection();

            // Build the query and place the received parameters on the placeholders.
            if (connection != null) {
                statement = connection.prepareStatement(query);

                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    statement.setObject(i + 1, param);
                }

                // Execute the query.
                statement.execute();
            }

        } finally {
            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
    }
}