package db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Entity;
import models.EntityDbHandler;
import models.implementation.Candidate;
import models.implementation.Experience;
import models.implementation.Position;
import utils.DBUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.sql.*;
import java.util.*;

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
     * Returns a list of all Candidates currently entered in the database.
     * @return the list of all Candidates.
     * @throws SQLException
     */
    public List<Entity> getAllEntities() throws SQLException {
        String query = "SELECT candidate.candidateId, candidate.candidateFirstName, candidate.candidateLastName, candidate.age, GROUP_CONCAT(DISTINCT technology.technologyName, ' ', experience.years ORDER BY experience.years SEPARATOR ',') AS 'Experience', GROUP_CONCAT(DISTINCT positions.positionName, ' ', company.companyName ORDER BY positions.positionName SEPARATOR ',') AS 'Applications' FROM candidate, experience, technology, company, positions, position_has_candidate WHERE candidate.candidateId = experience.candidateId AND experience.technologyId = technology.technologyId AND position_has_candidate.candidate_candidateId = candidate.candidateId AND position_has_candidate.position_positionId = positions.positionId AND positions.companyId = company.companyId GROUP BY candidate.candidateId ORDER BY candidate.candidateFirstName, candidate.candidateLastName";
        return DBUtils.execQueryAndBuildResult(query, null, this);
    }

    /**
     * Adds a new Candidate to the database.
     * @param node the JSON object holding the data of the Candidate.
     * @throws SQLException
     */
    public boolean addEntity(JsonNode node) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        Candidate candidate;
        int candidateId = 0;

        // Open a connection with the database and execute the query.
        try {
            candidate = new Candidate(node);

            connection = DBConnectionHandler.openDatabaseConnection();

            // TODO: Search if such Candidate already exists in the database by email after the email property is implemented in the Entity.

            // Prepare the query and execute it.
            String candidateFirstName = candidate.getCandidateFirstName();
            String candidateLastName = candidate.getCandidateLastName();
            int age = candidate.getAge();

            String candidateQuery = "INSERT INTO candidate(candidateFirstName, candidateLastName, age) VALUES(?, ?, ?)";

            if (connection != null) {
                connection.setAutoCommit(false);
                statement = connection.prepareStatement(candidateQuery, Statement.RETURN_GENERATED_KEYS);

                statement.setString(1, candidateFirstName);
                statement.setString(2, candidateLastName);
                statement.setInt(3, age);

                statement.execute();

                ResultSet resultSet = statement.getGeneratedKeys();
                resultSet.next();
                candidateId = resultSet.getInt(1);
                System.out.println("New candidate's ID: " + candidateId);
                connection.commit();
            }
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }

        // Insert data into the Experience table.
        try {
            JsonNode arrNode = new ObjectMapper().readTree(String.valueOf(node)).get("experience");
            System.out.println(arrNode.toString());

            if (arrNode.isArray() && candidateId > 0) {
                for (JsonNode object : arrNode) {
                    int technologyId = Integer.parseInt(object.get("technologyId").textValue());
                    String technologyExistsQuery = "SELECT technologyId FROM technology WHERE technologyId = ?";
                    String candidateHasTechnologyQuery = "SELECT experienceId FROM experience WHERE candidateId = ? AND technologyId = ?";
                    boolean technologyExists = DBUtils.isParamExists(technologyId, technologyExistsQuery);
                    boolean candidateHasTechnology = DBUtils.isParamExists(candidateId, technologyId, candidateHasTechnologyQuery);

                    if (technologyExists && !candidateHasTechnology) {
                        int years = Integer.parseInt(object.get("years").textValue());
                        String experienceQuery = "INSERT INTO experience(candidateId, technologyId, years) VALUES(?, ?, ?)";
                        Object[] params = {candidateId, technologyId, years};
                        DBUtils.execQuery(experienceQuery, params);
                    }
                }
            }

            check = true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return check;
    }

    /**
     * Deletes a selected Candidate from the database.
     * @param candidateId the ID of the Candidate to be removed.
     * @throws SQLException
     */
    public void deleteEntity(int candidateId) throws SQLException {
        Object[] params = {candidateId};

        // Delete the Experience.
        String query = "DELETE FROM experience WHERE candidateId = ?";
        DBUtils.execQuery(query, params);

        // Delete the application.
        query = "DELETE FROM position_has_candidate WHERE candidate_candidateId = ?";
        DBUtils.execQuery(query, params);

        // Delete the Candidate.
        query = "DELETE FROM candidate WHERE candidateId = ?";
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

            result = this.searchCandidateByParams(parameters);
        }

        return result;
    }

    /**
     * Search if Candidate with the specified ID exists in the database.
     * @param candidateId the ID of the Candidate to be searched.
     * @return the Candidate as object.
     * @throws SQLException
     */
    public Entity searchEntityById(int candidateId) throws SQLException {
        Object[] params = {candidateId};
        String query = "SELECT candidate.candidateId, candidate.candidateFirstName, candidate.candidateLastName, candidate.age, GROUP_CONCAT(DISTINCT technology.technologyName, ' ', experience.years ORDER BY experience.years SEPARATOR ',') AS 'Experience', GROUP_CONCAT(DISTINCT positions.positionName, ' ', company.companyName ORDER BY positions.positionName SEPARATOR ',') AS 'Applications' FROM candidate, experience, technology, company, positions, position_has_candidate WHERE candidate.candidateId = ? AND candidate.candidateId = experience.candidateId AND experience.technologyId = technology.technologyId AND position_has_candidate.candidate_candidateId = candidate.candidateId AND position_has_candidate.position_positionId = positions.positionId AND positions.companyId = company.companyId GROUP BY candidate.candidateId ORDER BY candidate.candidateFirstName, candidate.candidateLastName;";
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
    public List<Entity> searchCandidateByParams(Map<String, String> parameters) throws SQLException {
        // The SELECT and FROM part of the query.
        String query = "SELECT candidate.candidateId, candidate.candidateFirstName, candidate.candidateLastName, candidate.age, GROUP_CONCAT(DISTINCT technology.technologyName, ' ', experience.years ORDER BY experience.years SEPARATOR ',') AS 'Experience', GROUP_CONCAT(DISTINCT positions.positionName, ' ', company.companyName ORDER BY positions.positionName SEPARATOR ',') AS 'Applications' FROM candidate, experience, technology, company, positions, position_has_candidate";
        int count = 0;                                                  // Used to build the array holding the parameters for query execution.
        Object[] params = new Object[parameters.size()];                // Array of objects holding the parameters for the query execution.
        Set<String> keys = parameters.keySet();                         // Set holding the keys of the Map used to iterate through it and build the array.
        String fullQuery = DBUtils.buildQuery(parameters, query);       // The query which to be used when execute request to the database.
        fullQuery += " AND candidate.candidateId = experience.candidateId AND experience.technologyId = technology.technologyId AND position_has_candidate.candidate_candidateId = candidate.candidateId AND position_has_candidate.position_positionId = positions.positionId AND positions.companyId = company.companyId GROUP BY candidate.candidateId ORDER BY candidate.candidateFirstName, candidate.candidateLastName";

        System.out.println("FULL QUERY: " + fullQuery);

        for (String key : keys) {
            params[count] = parameters.get(key);
            count++;
        }

        return DBUtils.execQueryAndBuildResult(fullQuery, params, this);
    }

    /**
     * Search by either first or last name if candidate exists in the database.
     * @param name the name of the candidate to search for.
     * @return the candidate as object.
     * @throws SQLException
     */
//    private List<Entity> searchCandidateByName(String name) throws SQLException {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//
//        // Open a connection with the database, prepare a List which will hold the result and execute the query.
//        try {
//            List<Entity> found;
//            connection = DBConnectionHandler.openDatabaseConnection();
//            String query = "SELECT * FROM candidate WHERE candidate.candidateFirstName LIKE ? OR candidate.candidateLastName LIKE ?";
//
//            if (connection != null) {
//                statement = connection.prepareStatement(query);
//
//                statement.setString(1, "%" + name + "%");
//                statement.setString(2, "%" + name + "%");
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
//    }

    /**
     * Search if Candidate exists in the database by both first and last names.
     * @param fName the first name of the Candidate to search for.
     * @param lName the last name of the Candidate to search for.
     * @return the Candidate as object.
     * @throws SQLException
     */
//    private List<Entity> searchCandidateByName(String fName, String lName) throws SQLException {
//        Object[] params = {fName, lName};
//        String query = "SELECT * FROM candidate WHERE candidateFirstName = ? AND candidateLastName = ?";
//        return DBUtils.execQueryAndBuildResult(query, params, this);
//    }

    /**
     * Update a Candidate entry in the database with provided details.
     * @param candidateId the ID of the Candidate to be edited.
     * @param node the details of the Candidate which to be used for the modification.
     * @return true if the modification is successful, false otherwise.
     * @throws SQLException
     */
    public boolean updateEntity(int candidateId, JsonNode node) throws SQLException {
        boolean check = false;

        // Query which checks if a Candidate already has such Technology added.
        String candidateHasTechnologyQuery = "SELECT experienceId FROM experience WHERE candidateId = ? AND technologyId = ?";
        String operation;                   // The operation which has to take place.
        String query;                       // The query which to be executed.
        Object[] params;                    // Array of Objects holding the parameters used for the update.
        int technologyId;                   // The ID of the Technology.
        int years;                          // The years of experience the Candidate has with certain Technology.
        boolean candidateHasTechnology;     // Used to check if a Candidate already has such Technology added.

        try {
            JsonNode operations = new ObjectMapper().readTree(String.valueOf(node)).get("operations");
            System.out.println(operations.toString());

            if (operations.isArray() && candidateId > 0) {
                for (JsonNode action : operations) {
                    operation = action.toString().replaceAll("\"", "");
                    System.out.println("ACTION: " + operation);

                    // If we want to add new Technology to a Candidate.
                    if (operation.equalsIgnoreCase("addTechnology")) {
                        // Check if Technology exists in the database.
                        technologyId = Integer.parseInt(node.get("technologyId").textValue());
                        String technologyExistsQuery = "SELECT technologyId FROM technology WHERE technologyId = ?";
                        boolean technologyExists = DBUtils.isParamExists(technologyId, technologyExistsQuery);

                        // If Technology exists, check if the Candidate already has it added.
                        if (technologyExists) {
                            candidateHasTechnology = DBUtils.isParamExists(candidateId, technologyId, candidateHasTechnologyQuery);

                            // Add the Technology.
                            if (!candidateHasTechnology) {
                                years = Integer.parseInt(node.get("years").textValue());
                                query = "INSERT INTO experience(candidateId, technologyId, years) VALUES(?, ?, ?)";
                                params = new Object[]{candidateId, technologyId, years};
                                DBUtils.execQuery(query, params);
                            }
                        }

                        check = true;
                    }

                    // If we want to delete a Technology from a Candidate.
                    if (operation.equalsIgnoreCase("removeTechnology")) {
                        // Check if the Candidate has such Technology added.
                        technologyId = Integer.parseInt(node.get("technologyId").textValue());
                        candidateHasTechnology = DBUtils.isParamExists(candidateId, technologyId, candidateHasTechnologyQuery);

                        if (candidateHasTechnology) {
                            query = "DELETE FROM experience WHERE candidateId = ? AND technologyId = ?";
                            params = new Object[]{candidateId, technologyId};
                            DBUtils.execQuery(query, params);
                        }

                        check = true;
                    }

                    // If we want to modify the years of experience for a Technology of a Candidate.
                    if (operation.equalsIgnoreCase("modifyYears")) {
                        // Check if the Candidate has such Technology added.
                        technologyId = Integer.parseInt(node.get("technologyId").textValue());
                        candidateHasTechnology = DBUtils.isParamExists(candidateId, technologyId, candidateHasTechnologyQuery);

                        if (candidateHasTechnology) {
                            years = Integer.parseInt(node.get("years").textValue());
                            query = "UPDATE experience SET years = ? WHERE candidateId = ? AND technologyId = ?";
                            params = new Object[]{years, candidateId, technologyId};
                            DBUtils.execQuery(query, params);
                        }

                        check = true;
                    }

                    // If we want to modify the first name of a Candidate.
                    if (operation.equalsIgnoreCase("modifyFirstName")) {
                        System.out.println();
                        String candidateFirstName = node.get("candidateFirstName").textValue();
                        query = "UPDATE candidate SET candidateFirstName = ? WHERE candidateId = ?";
                        params = new Object[]{candidateFirstName, candidateId};
                        DBUtils.execQuery(query, params);

                        check = true;
                    }

                    // If we want to modify the last name of a Candidate.
                    if (operation.equalsIgnoreCase("modifyLastName")) {
                        String candidateLastName = node.get("candidateLastName").textValue();
                        query = "UPDATE candidate SET candidateLastName = ? WHERE candidateId = ?";
                        params = new Object[]{candidateLastName, candidateId};
                        DBUtils.execQuery(query, params);

                        check = true;
                    }

                    // If we want to modify the age of a Candidate.
                    if (operation.equalsIgnoreCase("modifyAge")) {
                        int age = Integer.parseInt(node.get("age").textValue());
                        query = "UPDATE candidate SET age = ? WHERE candidateId = ?";
                        params = new Object[]{age, candidateId};
                        DBUtils.execQuery(query, params);
                        check = true;
                    }

                    // If we want to add a Position for which the Candidate applied.
                    if (operation.equalsIgnoreCase("addApplication")) {
                        // Check if the Candidate already applied for that Position.
                        int positionId = Integer.parseInt(node.get("positionId").textValue());
                        String candidateApplicationExistsQuery = "SELECT * FROM position_has_candidate WHERE position_positionId = ? AND candidate_candidateId = ?";
                        boolean candidateApplicationExists = DBUtils.isParamExists(positionId, candidateId, candidateApplicationExistsQuery);

                        if (!candidateApplicationExists) {
                            query = "INSERT INTO position_has_candidate(position_positionId, candidate_candidateId) VALUES(?, ?)";
                            params = new Object[]{positionId, candidateId};
                            DBUtils.execQuery(query, params);
                        }

                        check = true;
                    }

                    // If we want to remove a Position for which the Candidate applied.
                    if (operation.equalsIgnoreCase("removeApplication")) {
                        // Check if the Candidate applied for that Position.
                        int positionId = Integer.parseInt(node.get("positionId").textValue());
                        String candidateApplicationExistsQuery = "SELECT * FROM position_has_candidate WHERE position_positionId = ? AND candidate_candidateId = ?";
                        boolean candidateApplicationExists = DBUtils.isParamExists(positionId, candidateId, candidateApplicationExistsQuery);

                        if (candidateApplicationExists) {
                            query = "DELETE FROM position_has_candidate WHERE position_positionId = ? AND candidate_candidateId = ?";
                            params = new Object[]{positionId, candidateId};
                            DBUtils.execQuery(query, params);
                        }

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
                    int candidateId = resultSet.getInt("candidateId");                          // The ID of the Candidate.
                    String candidateFirstName = resultSet.getString("candidateFirstName");      // The first name of the Candidate.
                    String candidateLastName = resultSet.getString("candidateLastName");        // The last name of the Candidate.
                    int age = resultSet.getInt("age");                                          // The age of the Candidate.
                    String experienceStr = resultSet.getString("Experience");                   // The string holding the details with the experience.
                    String applicationStr = resultSet.getString("Applications");                 // The string holding the details with the applications.

                    ArrayList<Experience> candidateExperience = new ArrayList<Experience>();      // The List holding all details of the Experience of the Candidate.
                    ArrayList<Position> candidateApplication = new ArrayList<Position>();       // The List holding all details of the Positions for which the Candidate applied.

                    // Get the Experience as group.
                    String[] experienceGroups = experienceStr.split(",");
                    for (String experienceGroup : experienceGroups) {
                        // Get the details for each Experience (Technology and years).
                        String[] experienceData = experienceGroup.split(" ");

                        String technologyName = experienceData[0];         // The name of the Technology which is required for the Position.
                        int years = Integer.parseInt(experienceData[1]);   // The years of experience with certain Technology required for the Position.

                        Experience experience = new Experience(candidateId, technologyName, years);
                        candidateExperience.add(experience);
                    }

                    // Get the application as group.
                    String[] applicationGroups = applicationStr.split(",");
                    for (String applicationGroup : applicationGroups) {
                        // Get the details for each application (Position name and company).
                        String[] applicationData = applicationGroup.split(" ");

                        String positionName = applicationData[0];       // The name of the Position for which the Candidate applied.
                        String companyName = applicationData[1];        // The name of the Company for which the Position is opened.

                        Position position = new Position(positionName, companyName);
                        candidateApplication.add(position);
                    }

                    // Create the Candidate object, set its Experience and application and put them into the proper List.
                    Candidate candidate = new Candidate(candidateId, candidateFirstName, candidateLastName, age);
                    candidate.setExperiences(candidateExperience);
                    candidate.setApplications(candidateApplication);
                    data.add(candidate);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}