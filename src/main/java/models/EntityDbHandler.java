package models;

import com.fasterxml.jackson.databind.JsonNode;

import javax.ws.rs.core.MultivaluedMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @Created by Terrax on 22-Oct-2015.
 */
public interface EntityDbHandler {

    /**
     * Returns a list of all Entities currently entered in the database.
     * @return the list of all Entities.
     * @throws SQLException
     */
    List<Entity> getAllEntities() throws SQLException;

    /**
     * Adds a new Entity to the database.
     * @param node the JSON object holding the data of the Entity.
     * @throws SQLException
     */
    boolean addEntity(JsonNode node) throws SQLException;

    /**
     * Deletes a selected Entity from the database.
     * @param id the ID of the Entity to be removed.
     * @throws SQLException
     */
    void deleteEntity(int id) throws SQLException;

    /**
     * Method which decides which exact search method to use based on the given parameters.
     * @param data map which holds the parameters.
     * @return a list with found results.
     * @throws SQLException
     */
    List<Entity> searchEntity(MultivaluedMap<String, String> data) throws SQLException;

    /**
     * Search if Entity with the specified ID exists in the database.
     * @param id the ID of the Entity to be searched.
     * @return the Entity as object.
     * @throws SQLException
     */
    Entity searchEntityById(int id) throws SQLException;

    /**
     * Update a Entity entry in the database with provided details.
     * @param id the ID of the entry to be edited.
     * @param node the details of the Entity which to be used for the modification.
     * @return true if the modification is successful, false otherwise.
     * @throws SQLException
     */
    boolean updateEntity(int id, JsonNode node) throws SQLException;

    /**
     * Method which builds the result returned from the database.
     * @param resultSet the result given by the database.
     * @return list holding the result.
     */
    List<Entity> buildResult(ResultSet resultSet);
}