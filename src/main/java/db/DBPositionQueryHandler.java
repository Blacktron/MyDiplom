package db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Entity;
import models.EntityDbHandler;
import models.implementation.Candidate;
import models.implementation.Experience;
import models.implementation.Position;
import models.implementation.Requirement;
import utils.DBUtils;
import utils.RatingCalculator;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.sql.*;
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
     * Returns a list of all Positions currently entered in the database.
     * @return the list of all Positions.
     * @throws SQLException
     */
    public List<Entity> getAllEntities() throws SQLException {
        String query = "SELECT positions.positionId, positions.positionName, positions.hrId, hr.hrFirstName, hr.hrLastName, positions.companyId, company.companyName, GROUP_CONCAT(DISTINCT technology.technologyName, ' ', requirement.years, ' ', requirement.priority ORDER BY positions.positionId SEPARATOR ',') AS 'Requirement', GROUP_CONCAT(DISTINCT candidate.candidateId, ' ', candidate.candidateFirstName, ' ', candidate.candidateLastName, ' ', candidate.age ORDER BY candidate.candidateId SEPARATOR ',') AS 'Applicant' FROM positions, hr, company, requirement, technology, candidate, position_has_candidate WHERE positions.hrId = hr.hrId AND positions.companyId = company.companyId AND requirement.positionId = positions.positionId AND requirement.technologyId = technology.technologyId AND position_has_candidate.candidate_candidateId = candidate.candidateId AND position_has_candidate.position_positionId = positions.positionId GROUP BY positions.positionId ORDER BY positions.positionName";
        return DBUtils.execQueryAndBuildResult(query, null, this);
    }

    /**
     * Adds a new position to the database.
     * @param node the JSON object holding the data of the Position.
     * @throws SQLException
     */
    public boolean addEntity(JsonNode node) throws SQLException {
        boolean check = false;
        Connection connection = null;
        PreparedStatement statement = null;
        Position position;
        int positionId = 0;

        // Open a connection with the database and execute the query.
        try {
            position = new Position(node);

            connection = DBConnectionHandler.openDatabaseConnection();

            // Prepare the query and execute it.
            int hrId = position.getHrId();
            int companyId = position.getCompanyId();
            String positionName = position.getPositionName();

            // TODO: Change this to search by email when email property implemented in the Entity.
            // Check if an HR with such ID exists in the database.
            String hrQuery = "SELECT hr.hrId FROM hr INNER JOIN company ON hr.companyId = company.companyId WHERE hr.hrId = ? AND hr.companyId = ?";
            boolean hrExists = DBUtils.isParamExists(hrId, companyId, hrQuery);

            // Check if a Company with such ID exists in the database.
//            String companyQuery = "SELECT hr.companyId" +
//                    " FROM company " +
//                    "WHERE companyId = ?";
//            boolean companyExists = isParamExists(companyId, companyQuery);

            // If such HR exists, continue with adding the Position entry into the database.
            if (hrExists) {
                String positionQuery = "INSERT INTO positions(hrId, companyId, positionName) VALUES(?, ?, ?)";

                if (connection != null) {
                    connection.setAutoCommit(false);
                    statement = connection.prepareStatement(positionQuery, Statement.RETURN_GENERATED_KEYS);

                    statement.setInt(1, hrId);
                    statement.setInt(2, companyId);
                    statement.setString(3, positionName);

                    statement.execute();

                    ResultSet resultSet = statement.getGeneratedKeys();
                    resultSet.next();
                    positionId = resultSet.getInt(1);
                    System.out.println("New position's ID: " + positionId);
                    connection.commit();
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

        // Insert the data if the Technology already exists.
        try {
            JsonNode arrNode = new ObjectMapper().readTree(String.valueOf(node)).get("requirement");
            System.out.println(arrNode.toString());


            if (arrNode.isArray() && positionId > 0) {
                boolean technologyExists;
                boolean positionHasTechnology;
                for (JsonNode object : arrNode) {
                    if (object.has("technologyId")) {
                        // Check if Technology exists.
                        int technologyId = Integer.parseInt(object.get("technologyId").textValue());
                        String technologyExistsQuery = "SELECT technologyId FROM technology WHERE technologyId = ?";
                        technologyExists = DBUtils.isParamExists(technologyId, technologyExistsQuery);

                        // Check if the Position already has such Technology added.
                        if (technologyExists) {
                            String technologyQuery = "SELECT * FROM requirement WHERE positionId = ? AND technologyId = ?";
                            positionHasTechnology = DBUtils.isParamExists(positionId, technologyId, technologyQuery);

                            if (!positionHasTechnology) {
                                String requirementQuery = "INSERT INTO requirement(positionId, technologyId, years, priority) VALUES(?, ?, ?, ?)";
                                int years = Integer.parseInt(object.get("years").textValue());
                                int priority = Integer.parseInt(object.get("priority").textValue());
                                Object[] params = {positionId, technologyId, years, priority};
                                DBUtils.execQuery(requirementQuery, params);
                            }
                        }
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
     * Deletes a selected position from the database.
     * @param positionId the id of the position to be removed.
     * @throws SQLException
     */
    public void deleteEntity(int positionId) throws SQLException {
        Object[] params = {positionId};

        // Delete the Requirement.
        String query = "DELETE FROM requirement WHERE positionId = ?";
        DBUtils.execQuery(query, params);

        // Delete the application.
        query = "DELETE FROM position_has_candidate WHERE position_positionId = ?";
        DBUtils.execQuery(query, params);

        // Delete the Position.
        query = "DELETE FROM positions WHERE positionId = ?";
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
        String query = "SELECT positions.positionId, positions.positionName, positions.hrId, hr.hrFirstName, hr.hrLastName, positions.companyId, company.companyName, GROUP_CONCAT(DISTINCT technology.technologyName, ' ', requirement.years, ' ', requirement.priority ORDER BY positions.positionId SEPARATOR ',') AS 'Requirement', GROUP_CONCAT(DISTINCT candidate.candidateId, ' ', candidate.candidateFirstName, ' ', candidate.candidateLastName, ' ', candidate.age ORDER BY candidate.candidateId SEPARATOR ',') AS 'Applicant' FROM mydb.positions, mydb.hr, mydb.company, mydb.requirement, mydb.technology, mydb.candidate, mydb.position_has_candidate WHERE positions.positionId = ? AND positions.hrId = hr.hrId AND positions.companyId = company.companyId AND requirement.positionId = positions.positionId AND requirement.technologyId = technology.technologyId AND position_has_candidate.candidate_candidateId = candidate.candidateId AND position_has_candidate.position_positionId = positions.positionId GROUP BY positions.positionId ORDER BY positions.positionName";
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
        // The SELECT and FROM part of the query.
        String query = "SELECT positions.positionId, positions.positionName, positions.hrId, hr.hrFirstName, hr.hrLastName, positions.companyId, company.companyName, GROUP_CONCAT(DISTINCT technology.technologyName, ' ', requirement.years, ' ', requirement.priority ORDER BY positions.positionId SEPARATOR ',') AS 'Requirement', GROUP_CONCAT(DISTINCT candidate.candidateId, ' ', candidate.candidateFirstName, ' ', candidate.candidateLastName, ' ', candidate.age ORDER BY candidate.candidateId SEPARATOR ',') AS 'Applicant' FROM mydb.positions, mydb.hr, mydb.company, mydb.requirement, mydb.technology, mydb.candidate, mydb.position_has_candidate";
        int count = 0;                                                      // Used to build the array holding the parameters for query execution.
        Object[] params = new Object[parameters.size()];                    // Array of objects holding the parameters for the query execution.
        Set<String> keys = parameters.keySet();                             // Set holding the keys of the Map used to iterate through it and build the array.
        String fullQuery = DBUtils.buildQuery(parameters, query);           // The query which to be used when execute request to the database.
        fullQuery += " AND positions.hrId = hr.hrId AND positions.companyId = company.companyId AND requirement.positionId = positions.positionId AND requirement.technologyId = technology.technologyId GROUP BY positions.positionId ORDER BY positions.positionName";

        System.out.println("fullQuery: " + fullQuery);

        for (String key : keys) {
            params[count] = parameters.get(key);
            count++;
        }

        return DBUtils.execQueryAndBuildResult(fullQuery, params, this);
    }

    /**
     * Update a position entry in the database with provided details.
     * @param positionId the ID of the Position to be edited.
     * @param node the details of the position which to be used for the modification.
     * @return true if the modification is successful, false otherwise.
     * @throws SQLException
     */
    public boolean updateEntity(int positionId, JsonNode node) throws SQLException {
        boolean check = false;

        // Query which checks if a Requirement already exists in the database.
        String requirementExistsQuery = "SELECT requirementId FROM requirement WHERE positionId = ? AND technologyId = ?";
        String operation;                   // The operation which has to take place.
        String query;                       // The query which to be executed.
        Object[] params;                    // Array of Objects holding the parameters used for the update.
        int companyId;                      // The ID of the Company for which the Position is opened.
        int technologyId;                   // The ID of the Technology needed as Requirement.
        int hrId;                           // The ID of the HR which is responsible for the Position.
        int years;                          // The years of experience needed as Requirement.
        int priority;                       // The priority of the Requirement.

        try {
            JsonNode operations = new ObjectMapper().readTree(String.valueOf(node)).get("operations");
            System.out.println(operations.toString());

            if (operations.isArray() && positionId > 0) {
                for (JsonNode action : operations) {
                    operation = action.toString().replaceAll("\"", "");
                    System.out.println("OPERATION: " + operation);

                    // If we want to add new Requirement to a Position.
                    if (operation.equalsIgnoreCase("addRequirement")) {
                        // Check if Technology exists in the database.
                        technologyId = Integer.parseInt(node.get("technologyId").textValue());
                        String technologyExistsQuery = "SELECT technologyId FROM technology WHERE technologyId = ?";
                        boolean technologyExists = DBUtils.isParamExists(technologyId, technologyExistsQuery);

                        // If Technology exists, check if the Position already has such Requirement.
                        if (technologyExists) {
                            boolean requirementExists = DBUtils.isParamExists(positionId, technologyId, requirementExistsQuery);

                            // Add the Requirement.
                            if (!requirementExists) {
                                years = Integer.parseInt(node.get("years").textValue());
                                priority = Integer.parseInt(node.get("priority").textValue());
                                query = "INSERT INTO requirement(positionId, technologyId, years, priority) VALUES(?, ?, ?, ?)";
                                params = new Object[]{positionId, technologyId, years, priority};
                                DBUtils.execQuery(query, params);
                            }
                        }

                        check = true;
                    }
                    // If we want to remove a Requirement from a Position.
                    else if (operation.equalsIgnoreCase("removeRequirement")) {
                        // Check if the Position has such Requirement added.
                        technologyId = Integer.parseInt(node.get("technologyId").textValue());
                        boolean positionHasRequirement = DBUtils.isParamExists(positionId, technologyId, requirementExistsQuery);

                        if (positionHasRequirement) {
                            query = "DELETE FROM requirement WHERE positionId = ? AND technologyId = ?";
                            params = new Object[]{positionId, technologyId};
                            DBUtils.execQuery(query, params);
                        }

                        check = true;
                    }
                    // If we want to modify the years or priority of a Requirement.
                    else if (operation.equalsIgnoreCase("modifyYears") || operation.equalsIgnoreCase("modifyPriority")) {
                        // Check if the Position has such Requirement added.
                        technologyId = Integer.parseInt(node.get("technologyId").textValue());
                        boolean positionHasRequirement = DBUtils.isParamExists(positionId, technologyId, requirementExistsQuery);

                        if (operation.equalsIgnoreCase("modifyYears")) {
                            if (positionHasRequirement) {
                                years = Integer.parseInt(node.get("years").textValue());
                                query = "UPDATE requirement SET years = ? WHERE positionId = ? AND technologyId = ?";
                                params = new Object[]{years, positionId, technologyId};
                                DBUtils.execQuery(query, params);
                            }
                        }

                        if (operation.equalsIgnoreCase("modifyPriority")) {
                            if (positionHasRequirement) {
                                priority = Integer.parseInt(node.get("priority").textValue());
                                query = "UPDATE requirement SET priority = ? WHERE positionId = ? AND technologyId = ?";
                                params = new Object[]{priority, positionId, technologyId};
                                DBUtils.execQuery(query, params);
                            }
                        }

                        check = true;
                    }
                    // If we want to modify the ID of the HR which is dealing with that Position.
                    else if (operation.equalsIgnoreCase("modifyHR")) {
                        // Check if such HR exists.
                        hrId = Integer.parseInt(node.get("hrId").textValue());
                        String hrExistsQuery = "SELECT hrId, FROM hr WHERE hrId = ?";
                        boolean hrExists = DBUtils.isParamExists(hrId, hrExistsQuery);

                        if (hrExists) {
                            query = "UPDATE positions SET hrId = ? WHERE positions.positionId = ?";
                            params = new Object[]{hrId, positionId};
                            DBUtils.execQuery(query, params);
                        }

                        check = true;
                    }
                    // If we want to modify the ID of the Company for which the Position is.
                    else if (operation.equalsIgnoreCase("modifyCompany")) {
                        // Check if such Company exists.
                        companyId = Integer.parseInt(node.get("companyId").textValue());
                        String companyExistsQuery = "SELECT companyId FROM company WHERE companyId = ?";
                        boolean companyExists = DBUtils.isParamExists(companyId, companyExistsQuery);

                        if (companyExists) {
                            query = "UPDATE positions SET companyId = ? WHERE positions.positionId = ?";
                            params = new Object[]{companyId, positionId};
                            DBUtils.execQuery(query, params);
                        }

                        check = true;
                    }
                    // If we want to modify the Name of the Position.
                    else if (operation.equalsIgnoreCase("modifyPositionName")) {
                        // Check if such Position exists.
                        String positionExistsQuery = "SELECT positionId FROM positions WHERE positionId = ?";
                        boolean positionExists = DBUtils.isParamExists(positionId, positionExistsQuery);

                        if (positionExists) {
                            String positionName = node.get("positionName").textValue();
                            query = "UPDATE positions SET positionName = ? WHERE positions.positionId = ?";
                            params = new Object[]{positionName, positionId};
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
     * Method which get the necessary data to decide which is the best candidate
     * applied for the position based on rating calculation.
     * @param positionId the ID of the Position.
     * @return the selected Candidates and their ratings.
     * @throws SQLException
     */
    public List<Entity> searchForMatch(String positionId) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnectionHandler.openDatabaseConnection();
            String query = "SELECT positions.positionId, positions.positionName, GROUP_CONCAT(DISTINCT technology.technologyName, ' ', requirement.years, ' ', requirement.priority ORDER BY requirement.years SEPARATOR ',') AS 'Requirements', GROUP_CONCAT(DISTINCT candidate.candidateId, ' ', candidate.candidateFirstName, ' ', candidate.candidateLastName, ' ', candidate.age, ' ', technology.technologyName, ' ', experience.years ORDER BY candidate.candidateId) AS 'Candidates' FROM mydb.positions, mydb.candidate, mydb.technology, mydb.experience, mydb.requirement, mydb.position_has_candidate WHERE positions.positionId = ? AND positions.positionId = position_has_candidate.position_positionId AND candidate.candidateId = position_has_candidate.candidate_candidateId AND requirement.technologyId = technology.technologyId AND candidate.candidateId = experience.candidateId AND technology.technologyId = experience.technologyId GROUP BY positions.positionName;";
            List<Entity> matches;       // The List holding the data returned from the database.

            if (connection != null) {
                statement = connection.prepareStatement(query);

                // Build the query and place the received parameters on the placeholders.
                statement.setString(1, positionId);

                // Execute the query.
                System.out.println("EXEC QUERY: " + statement.toString());
                resultSet = statement.executeQuery();
            }

            // Build the result and return in.
            matches = buildRatingData(resultSet);

            return matches;
        } finally {
            if (connection != null) {
                connection.close();
            }

            if (statement != null) {
                statement.close();
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
                    int positionId = resultSet.getInt("positionId");                            // The ID of the Position.
                    int hrId = resultSet.getInt("hrId");                                        // The ID of the HR which is responsible for the Position.
                    int companyId = resultSet.getInt("companyId");                              // The ID of the Company for which the Position is opened.
                    String positionName = resultSet.getString("positionName");                  // The name of the Position.
                    String hrFirstName = resultSet.getString("hrFirstName");                    // The first name of the HR.
                    String hrLastName = resultSet.getString("hrLastName");                      // The last name of the HR.
                    String companyName = resultSet.getString("companyName");                    // The name of the Company.
                    String requirementStr = resultSet.getString("Requirement");                 // The string holding the details of the Requirements.
                    String applicantStr = resultSet.getString("Applicant");                     // The string holding the details of the applicant.

                    ArrayList<Requirement> positionRequirement = new ArrayList<Requirement>();            // The List holding all details of the Requirement for a Position.
                    ArrayList<Candidate> candidateApplicant = new ArrayList<Candidate>();               // The List holding all details for the Candidate applied for the Position.

                    // Get the Requirements as group.
                    String[] requirementGroups = requirementStr.split(",");
                    for (String requirementGroup : requirementGroups) {
                        // Get the details for each Requirement (Technology, years and priority).
                        String[] requirementData = requirementGroup.split(" ");

                        String technologyName = requirementData[0];             // The name of the Technology which is required for the Position.
                        int years = Integer.parseInt(requirementData[1]);       // The years of experience with certain Technology required for the Position.
                        int priority = Integer.parseInt(requirementData[2]);    // The priority of the Requirement for the Position.

                        Requirement requirement = new Requirement(positionId, technologyName, years, priority);
                        positionRequirement.add(requirement);
                    }

                    // Get the applicant as group.
                    String[] applicantGroups = applicantStr.split(",");
                    for (String applicantGroup : applicantGroups) {
                        // Get the details for each applicant (ID, first name, last name and age).
                        String[] applicantData = applicantGroup.split(" ");

                        int candidateId = Integer.parseInt(applicantData[0]);   // The ID of the Candidate which applied for the position.
                        String candidateFirstName = applicantData[1];           // The first name of the Candidate.
                        String candidateLastName = applicantData[2];            // The last name of the Candidate.
                        int age = Integer.parseInt(applicantData[3]);           // The age of the Candidate.

                        Candidate candidate = new Candidate(candidateId, candidateFirstName, candidateLastName, age);
                        candidateApplicant.add(candidate);
                    }

                    // Create the Position object, set its Requirements and put it into the List.
                    Position position = new Position(positionId, hrId, companyId, positionName, hrFirstName, hrLastName, companyName);
                    position.setRequirements(positionRequirement);
                    position.setApplicants(candidateApplicant);
                    data.add(position);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    /**
     * Method which builds the data used for Candidate rating calculation.
     * @param resultSet the result given by the database.
     * @return list holding the result.
     */
    public List<Entity> buildRatingData(ResultSet resultSet) {
        List<Entity> data = new ArrayList<Entity>();

        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    Candidate candidate;
                    Experience experience;
                    int positionId = resultSet.getInt("positionId");                             // The ID of the Position.
                    String positionName = resultSet.getString("positionName");                   // The name of the Position.
                    String requirementStr = resultSet.getString("Requirements");                 // The string holding the details of the Requirements.
                    String applicantStr = resultSet.getString("Candidates");                     // The string holding the details of the applicant.

                    ArrayList<Requirement> positionRequirement = new ArrayList<Requirement>();          // The List holding all details of the Requirement for a Position.
                    ArrayList<Candidate> candidateApplicant = new ArrayList<Candidate>();               // The List holding all details for the Candidate applied for the Position.

                    // Get the Requirements as group.
                    String[] requirementGroups = requirementStr.split(",");
                    for (String requirementGroup : requirementGroups) {
                        // Get the details for each Requirement (Technology, years and priority).
                        String[] requirementData = requirementGroup.split(" ");

                        String technologyName = requirementData[0];             // The name of the Technology which is required for the Position.
                        int years = Integer.parseInt(requirementData[1]);       // The years of experience with certain Technology required for the Position.
                        int priority = Integer.parseInt(requirementData[2]);    // The priority of the Requirement for the Position.

                        Requirement requirement = new Requirement(positionId, technologyName, years, priority);
                        positionRequirement.add(requirement);
                    }

                    // Get the applicant as group.
                    ArrayList<Experience> candidateExperience = new ArrayList<Experience>();
                    String[] applicantGroups = applicantStr.split(",");
                    for (String applicantGroup : applicantGroups) {
                        // Get the details for each applicant (ID, first name, last name and age).
                        String[] applicantData = applicantGroup.split(" ");

                        int candidateId = Integer.parseInt(applicantData[0]);   // The ID of the Candidate which applied for the position.
                        String candidateFirstName = applicantData[1];           // The first name of the Candidate.
                        String candidateLastName = applicantData[2];            // The last name of the Candidate.
                        int age = Integer.parseInt(applicantData[3]);           // The age of the Candidate.
                        String technologyName = applicantData[4];               // The name of the Technology.
                        int years = Integer.parseInt(applicantData[5]);         // The years of experience the Candidate has with that Technology.

                        // If there are no Candidate in the List yet, create the first one and its Experience and add it to the List.
                        if (candidateApplicant.size() == 0) {
                            candidate = new Candidate(candidateId, candidateFirstName, candidateLastName, age);
                            experience = new Experience();
                            experience.setTechnologyName(technologyName);
                            experience.setYears(years);
                            candidateExperience.add(experience);
                            candidate.setExperiences(candidateExperience);
                            candidateApplicant.add(candidate);
                        } else if (candidateId == candidateApplicant.get(candidateApplicant.size() - 1).getId()) {
                            candidate = candidateApplicant.get(candidateApplicant.size() - 1);
                            candidateExperience = (ArrayList<Experience>) candidate.getExperiences();
                            experience = new Experience();
                            experience.setTechnologyName(technologyName);
                            experience.setYears(years);
                            candidateExperience.add(experience);
                            candidate.setExperiences(candidateExperience);
                        } else {
                            candidate = new Candidate(candidateId, candidateFirstName, candidateLastName, age);
                            experience = new Experience();
                            experience.setTechnologyName(technologyName);
                            experience.setYears(years);
                            candidateExperience.add(experience);
                            candidate.setExperiences(candidateExperience);
                            candidateApplicant.add(candidate);
                        }
                    }

                    // Create the Position object, set its Requirements and put it into the List.
                    Position position = new Position();
                    position.setId(positionId);
                    position.setPositionName(positionName);
                    position.setRequirements(positionRequirement);
                    position.setApplicants(candidateApplicant);
                    RatingCalculator ratingCalculator = new RatingCalculator(position);
                    ratingCalculator.calculatePoints();
                    data.add(position);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}