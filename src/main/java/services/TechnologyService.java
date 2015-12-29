package services;

import com.fasterxml.jackson.databind.JsonNode;
import models.Entity;
import models.implementation.Technology;
import db.DBTechnologyQueryHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Created by Terrax on 13-Oct-2015.
 */
@Path("technologies")
public class TechnologyService {

    /**
     * A method which searches for a technology using ID as criteria.
     * @param techId the ID which to be searched.
     * @return the technology if such is found.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTechnologyById(@PathParam("id") String techId) {
        System.out.println("GET /technologies get");
        Entity technology;

        try {
            int id = Integer.parseInt(techId);
            technology = DBTechnologyQueryHandler.getInstance().searchEntityById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Technology with such ID was not found!\"}").build();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Unable to convert the provided parameter!\"}").build();
        }

        return Response.ok(technology).build();
    }

//    /**
//     * This method is for internal use only.
//     * It queries the database for all entries in the tech table.
//     * @return all technologies currently existing in the database.
//     */
//    @Produces(MediaType.APPLICATION_JSON)
//    private Response getAllTechnologies() {
//        try {
//            List<Technology> technologies = DBTechnologyQueryHandler.getAllTechnologies();
//            return Response.ok(technologies).build();
//        }
//        catch (TechnologyException exception) {
//            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
//        }
//        catch (SQLException e) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
//        }
//    }

    /**
     * /**
     * Create a new technology and insert it into the database.
     * @param techName the name which will be used to create the new technology.
     * @return the response from the database (either successful or failed creation).
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTechnology(JsonNode techName) throws SQLException {
        System.out.println("POST /technologies add");

        String tech = techName.get("name").textValue();
        Technology technology = new Technology(tech);

        DBTechnologyQueryHandler.getInstance().addEntity(technology);

        return Response.ok("{\"Status\" : \"OK\"}").build();
    }

    /**
     * Deletes a technology from the database.
     * @param techId the ID of the technology to be deleted.
     * @return the response from the database (either successful or failed deletion).
     */
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTechnology(@PathParam("id") String techId) throws SQLException {
        System.out.println("DELETE /technologies delete");

        try {
            int id = Integer.parseInt(techId);
            DBTechnologyQueryHandler.getInstance().deleteEntity(id);
            return Response.ok("{\"Status\" : \"OK\"}").build();
        } catch (NumberFormatException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format!!!\"}").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Searches for technology in the database by provided name. If such is not provided the default
     * action is to query the database for all technologies entered so far.
     * @return the technology searched for or all technologies entered into the database so far.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchTechnology(@Context UriInfo data) throws SQLException {
        System.out.println("GET /technologies search");

        MultivaluedMap<String, String> info = data.getQueryParameters();

        List<Entity> result = new ArrayList<Entity>();
        try {
            result = DBTechnologyQueryHandler.getInstance().searchEntity(info);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid query parameters\"}").build();
        } else {
            return Response.ok(result).build();
        }
    }

    /**
     * Edits the details of the selected technology.
     * @param techId the ID of the technology.
     * @param technologyDetails the new details which should be set in the database.
     * @return the response from the database (either successful or failed modification).
     */
    @POST
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editTechnology(@PathParam("id") String techId, JsonNode technologyDetails) {
        System.out.println("POST /technologies edit");

        try {
            int id = Integer.parseInt(techId);
            Entity entity = DBTechnologyQueryHandler.getInstance().searchEntityById(id);
            Technology technology = null;

            if (entity instanceof Technology) {
                technology = (Technology) entity;
            }

            if (technology != null) {
                Technology newTechnology = new Technology();
                newTechnology.setId(id);
                newTechnology.setName(technologyDetails.get("name").textValue());

                DBTechnologyQueryHandler.getInstance().updateEntity(newTechnology);
                return Response.ok("{\"Status\" : \"Technology with ID " + id + " was successfully modified!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Technology with ID " + id + " does not exists\"}").build();
            }
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format!!!\"}").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"Error\" : \"Technology with ID" + techId + " was not modified\"}").build();
        }
    }
}