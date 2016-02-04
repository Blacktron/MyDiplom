package utils;

import db.DBConnectionHandler;
import models.Entity;
import models.EntityDbHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
                        int parameter = 0;
                        boolean check = false;

                        try {
                            parameter = Integer.parseInt(String.valueOf(params[i]));
                            check = true;
                        } catch (NumberFormatException exception) {
                            System.out.println("Parameter is not a number");
                        }

                        if (check && parameter > 0) {
                            statement.setInt(i + 1, parameter);
                        } else {
                            statement.setString(i + 1, "%" + param + "%");
                        }

//                        if (param instanceof Integer) {
//                            System.out.println("INSTANCEOF");
//                            Integer parameter = (Integer) params[i];
//
//                            //if (!parameter.contains(".")) {
//
//                            //}
//                        } else {
//
//                        }
                    }
                }

                // Execute the query.
                System.out.println("EXEC QUERY: " + statement.toString());
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

    /**
     * Method which checks if an entry exists in the database.
     * This is used when making a new entry or an update to an entry making
     * sure not to add something which is missing from the database.
     * @param param1 the ID of the first parameter which to search for in the database.
     * @param param2 the ID of the second parameter which to search for in the database.
     * @param query the query to be executed.
     * @return true if the parameter exists in the database, false otherwise.
     * @throws SQLException
     */
    public static boolean isParamExists(int param1, int param2, String query) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnectionHandler.openDatabaseConnection();

            if (connection != null) {
                statement = connection.prepareStatement(query);

                statement.setInt(1, param1);
                statement.setInt(2, param2);

                resultSet = statement.executeQuery();
            }

            if (resultSet != null && resultSet.next()) {
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
     * Method which checks if an entry exists in the database.
     * This is used when making a new entry or an update to an entry making
     * sure not to add something which is missing from the database.
     * @param param1 the ID of the first parameter which to search for in the database.
     * @param query the query to be executed.
     * @return true if the parameter exists in the database, false otherwise.
     * @throws SQLException
     */
    public static boolean isParamExists(int param1, String query) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnectionHandler.openDatabaseConnection();

            if (connection != null) {
                statement = connection.prepareStatement(query);

                statement.setInt(1, param1);

                resultSet = statement.executeQuery();
            }

            if (resultSet != null && resultSet.next()) {
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
     * Method which checks if an entry exists in the database.
     * This is used when making a new entry or an update to an entry making
     * sure not to add something which is missing from the database.
     * @param param1 the first parameter which to search for in the database.
     * @param query the query to be executed.
     * @return true if the parameter exists in the database, false otherwise.
     * @throws SQLException
     */
    public static boolean isParamExists(String param1, String query) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnectionHandler.openDatabaseConnection();

            if (connection != null) {
                statement = connection.prepareStatement(query);

                statement.setString(1, param1);

                resultSet = statement.executeQuery();
            }

            if (resultSet != null && resultSet.next()) {
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
     * Method which builds a query based on the provided criteria.
     * @param parameters the parameters to search for.
     * @return the query used for request execution.
     */
    public static String buildQuery(Map<String, String> parameters, String query) {
        int paramsAdded = 0;
        StringBuilder where = new StringBuilder();
        where.append(" WHERE");

        for (String key : parameters.keySet()) {
            where.append(" ");
            where.append(key);
            if (key.toLowerCase().contains("name")) {
                where.append(" LIKE ?");
            } else {
                where.append(" = ?");
            }

            paramsAdded++;
            if (paramsAdded != parameters.size()) {
                where.append(" AND");
            }
        }

        return query + where.toString();
    }

    /**
     * Method which builds the WHERE part of the query.
     * @param parameters the parameters to search for.
     * @return the full query.
     */
//    public static String buildWherePart(Map<String, String> parameters) {
//
//    }
}