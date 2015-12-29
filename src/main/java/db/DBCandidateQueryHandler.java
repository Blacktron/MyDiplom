package db;

import models.Entity;
import models.EntityDbHandler;
import models.implementation.Candidate;
import utils.DBUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Created by Terrax on 18-Oct-2015.
 */
public class DBCandidateQueryHandler implements EntityDbHandler {
    private static DBCandidateQueryHandler instance;

    private DBCandidateQueryHandler() { }

    public static DBCandidateQueryHandler getInstance() {
        if (instance == null) {
            instance = new DBCandidateQueryHandler();
        }

        return instance;
    }

    /**
     * Returns a list of all candidates currently entered in the database.
     * @return the list of all candidates.
     * @throws SQLException
     */
    public List<Entity> getAllEntities() throws SQLException {
        String query = "SELECT * FROM candidates";
        return DBUtils.execQueryAndBuildResult(query, null, this);
//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//
//        // Open a connection with the database, prepare a List which will hold the result and execute the query.
//        try {
//            connection = DBConnectionHandler.openDatabaseConnection();
//            List<Entity> allCandidates;
//
//            String query = "SELECT * FROM candidates";
//
//            if (connection != null) {
//                statement = connection.prepareStatement(query);
//                resultSet = statement.executeQuery();
//            }
//
//            allCandidates = this.buildResult(resultSet);
//
//            return allCandidates;
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
     * Adds a new candidate to the database.
     * @param entity the entity holding the information to create a new entry in the database.
     * @throws SQLException
     */
    public boolean addEntity(Entity entity) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        Candidate candidate = null;

        // Open a connection with the database and execute the query.
        try {
            // Check if the entity is a Candidate and cast it.
            if (entity instanceof Candidate) {
                candidate = (Candidate) entity;
            }

            connection = DBConnectionHandler.openDatabaseConnection();

            // Prepare the query and execute it.
            assert candidate != null;
            String fname = candidate.getFirstName();
            String lname = candidate.getLastName();
            int age = candidate.getAge();
            String language1 = candidate.getLanguage1();
            String language2 = candidate.getLanguage2();
            String language3 = candidate.getLanguage3();

            String query = "INSERT INTO candidates(firstName, lastName, age, language1, language2, language3)" +
                    "VALUES(?, ?, ?, ?, ?, ?)";

            if (connection != null) {
                statement = connection.prepareStatement(query);

                statement.setString(1, fname);
                statement.setString(2, lname);
                statement.setInt(3, age);
                statement.setString(4, language1);
                statement.setString(5, language2);
                statement.setString(6, language3);

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
     * Deletes a selected candidate from the database.
     * @param candidateId the id of the candidate to be removed.
     * @throws SQLException
     */
    public void deleteEntity(int candidateId) throws SQLException {
        Object[] params = {candidateId};
        String query = "DELETE FROM candidates WHERE candidateId = ?";
        DBUtils.execQuery(query, params);

//        Connection connection = null;
//        PreparedStatement statement = null;
//
//        // Open a connection with the database, prepare the query and execute it.
//        try {
//            connection = DBConnectionHandler.openDatabaseConnection();
//
//
//            if (connection != null) {
//                statement = connection.prepareStatement(query);
//                statement.setInt(1, id);
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
            result = this.searchCandidateByName(firstName);
        }
        else {
            result = this.searchCandidateByName(firstName, lastName);
        }

        return result;
    }

    /**
     * Search if candidate with the specified ID exists in the database.
     * @param candidateId the ID of the candidate to be searched.
     * @return the candidate as object.
     * @throws SQLException
     */
    public Entity searchEntityById(int candidateId) throws SQLException {
        Object[] params = {candidateId};
        String query = "SELECT * FROM candidates WHERE candidateId = ?";
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
//        Candidate candidate = null;
//
//        // Open a connection with the database, prepare the query and execute it.
//        try {
//            connection = DBConnectionHandler.openDatabaseConnection();
//            String query = "SELECT * FROM candidates WHERE candidateId = '" + candidateId + "'";
//
//            if (connection != null) {
//                statement = connection.prepareStatement(query);
//                resultSet = statement.executeQuery();
//            }
//
//            if (resultSet != null) {
//                while (resultSet.next()) {
//                    int id = resultSet.getInt("candidateId");
//                    String firstName = resultSet.getString("firstName");
//                    String lastName = resultSet.getString("lastName");
//                    int age = resultSet.getInt("age");
//                    String language1 = resultSet.getString("language1");
//                    String language2 = resultSet.getString("language2");
//                    String language3 = resultSet.getString("language3");
//
//                    candidate = new Candidate(id, firstName, lastName, age, language1, language2, language3);
//                }
//            }
//
//            return candidate;
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
     * Search by either first or last name if candidate exists in the database.
     * @param name the name of the candidate to search for.
     * @return the candidate as object.
     * @throws SQLException
     */
    private List<Entity> searchCandidateByName(String name) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        // Open a connection with the database, prepare a List which will hold the result and execute the query.
        try {
            List<Entity> found;
            connection = DBConnectionHandler.openDatabaseConnection();
            String query = "SELECT * FROM candidates WHERE firstName LIKE ? OR lastName LIKE ?";

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
     * Search if candidate exists in the database by both first and last names.
     * @param fName the first name of the candidate to search for.
     * @param lName the last name of the candidate to search for.
     * @return the candidate as object.
     * @throws SQLException
     */
    private List<Entity> searchCandidateByName(String fName, String lName) throws SQLException {
        Object[] params = {fName, lName};
        String query = "SELECT * FROM candidates WHERE firstName = ? AND lastName = ?";
        return DBUtils.execQueryAndBuildResult(query, params, this);
//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//
//        // Open a connection with the database, prepare a List which will hold the result and execute the query.
//        try {
//            List<Entity> found;
//            connection = DBConnectionHandler.openDatabaseConnection();
//
//
//            if (connection != null) {
//                statement = connection.prepareStatement(query);
//
//                statement.setString(1, fName);
//                statement.setString(2, lName);
//
//                resultSet = statement.executeQuery();
//            }
//
//            found = this.buildResult(resultSet);
//
//            return found;
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
     * Update a candidate entry in the database with provided details.
     * @param entity the details of the candidate which to be used for the modification.
     * @return true if the modification is successful, false otherwise.
     * @throws SQLException
     */
    public boolean updateEntity(Entity entity) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        Candidate candidate = null;

        // Open a connection with the database, prepare a List which will hold the result and execute the query.
        try {
            if (entity instanceof Candidate) {
                candidate = (Candidate) entity;
            }

            connection = DBConnectionHandler.openDatabaseConnection();

            assert candidate != null;
            int id = candidate.getId();
            String firstName = candidate.getFirstName();
            String lastName = candidate.getLastName();
            int age = candidate.getAge();
            String language1 = candidate.getLanguage1();
            String language2 = candidate.getLanguage2();
            String language3 = candidate.getLanguage3();

            String query = "UPDATE candidate SET firstName = ?, lastName = ?, age = ?, language1 = ?, language2 = ?, language3 = ? WHERE candidateId = ?";

            if (connection != null) {
                statement = connection.prepareStatement(query);

                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setInt(3, age);
                statement.setString(4, language1);
                statement.setString(5, language2);
                statement.setString(6, language3);
                statement.setInt(7, id);

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
                    int id = resultSet.getInt("candidateId");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    int age = resultSet.getInt("age");
                    String language1 = resultSet.getString("language1");
                    String language2 = resultSet.getString("language2");
                    String language3 = resultSet.getString("language3");

                    Candidate candidate = new Candidate(id, firstName, lastName, age, language1, language2, language3);
                    data.add(candidate);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}