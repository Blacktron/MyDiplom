package models;

import javax.ws.rs.core.MultivaluedMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @Created by Terrax on 22-Oct-2015.
 */
public interface EntityDbHandler {

    List<Entity> getAllEntities() throws SQLException;
    boolean addEntity(Entity entity) throws SQLException;
    void deleteEntity(int id) throws SQLException;
    List<Entity> searchEntity(MultivaluedMap<String, String> data) throws SQLException;
    Entity searchEntityById(int id) throws SQLException;
    boolean updateEntity(Entity entity) throws SQLException;

    List<Entity> buildResult(ResultSet resultSet);
}