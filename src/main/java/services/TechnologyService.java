package services;

import com.fasterxml.jackson.databind.JsonNode;
import models.Entity;
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
     * A method which searches for a Technology using ID as criteria.
     * @param technologyId the ID which to be searched.
     * @return the technology if such is found.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTechnologyById(@PathParam("id") String technologyId) {
        System.out.println("GET /technologies get");
        Entity technology;

        try {
            int id = Integer.parseInt(technologyId);
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

    /**
     * Create a new Technology and insert it into the database.
     * @param node the details from which the Technology will be created..
     * @return the response from the database (either successful or failed creation).
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTechnology(JsonNode node) throws SQLException {
        System.out.println("POST /technologies add");

        boolean check;

        try {
            check = DBTechnologyQueryHandler.getInstance().addEntity(node);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        if (check) {
            return Response.ok("{\"Status\" : \"New Technology successfully created!\"}").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Such Technology already exists!!!\"}").build();
        }
    }

    /**
     * Deletes a Technology from the database.
     * @param technologyId the ID of the Technology to be deleted.
     * @return the response from the database (either successful or failed deletion).
     */
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTechnology(@PathParam("id") String technologyId) throws SQLException {
        System.out.println("DELETE /technologies delete");

        try {
            int id = Integer.parseInt(technologyId);
            DBTechnologyQueryHandler.getInstance().deleteEntity(id);
        } catch (NumberFormatException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format!!!\"}").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        return Response.ok("{\"Status\" : \"Technology successfully deleted!\"}").build();
    }

    /**
     * Searches for Technology in the database by provided name. If such is not provided the default
     * action is to query the database for all Technologies entered so far.
     * @return the Technology searched for or all Technologies entered into the database so far.
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
     * Edits the details of the selected Technology.
     * @param techId the ID of the Technology to be edited.
     * @param technologyDetails the new details which should be set in the database.
     * @return the response from the database (either successful or failed modification).
     */
    @POST
    @Path("{techId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editTechnology(@PathParam("techId") String techId, JsonNode technologyDetails) {
        System.out.println("POST /technologies edit");

        int technologyId = Integer.parseInt(techId);

        try {
            boolean check = DBTechnologyQueryHandler.getInstance().updateEntity(technologyId, technologyDetails);

            if (check) {
                return Response.ok("{\"Status\" : \"Technology with ID " + technologyId + " was successfully modified!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Technology with ID " + technologyId + " does not exists\"}").build();
            }
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format!!!\"}").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"Error\" : \"Technology with ID " + technologyId + " was not modified\"}").build();
        }
    }
}