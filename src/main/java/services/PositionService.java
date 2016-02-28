package services;

import com.fasterxml.jackson.databind.JsonNode;
import db.DBPositionQueryHandler;
import models.Entity;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Created by Terrax on 26-Dec-2015.
 */
@Path("positions")
public class PositionService {

    /**
     * A method which searches for a Position using ID as criteria.
     * @param positionId the ID which to be searched.
     * @return the Position if such is found.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPositionById(@PathParam("id") String positionId) {
        System.out.println("GET /positions get");
        Entity position;

        try {
            int id = Integer.parseInt(positionId);
            position = DBPositionQueryHandler.getInstance().searchEntityById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Position with such ID was not found!\"}").build();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Unable to convert the provided parameter!\"}").build();
        }

        return Response.ok(position).build();
    }

    /**
     * Create a new Position and insert it into the database.
     * @param node the details from which the Position will be created..
     * @return the response from the database (either successful or failed creation).
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPosition(JsonNode node) {
        System.out.println("POST /positions add");

        boolean check;

        try {
            check = DBPositionQueryHandler.getInstance().addEntity(node);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        if (check) {
            return Response.ok("{\"Status\" : \"New Position successfully created!\"}").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Unable to add the new Position!!!\"").build();
        }
    }

    /**
     * Deletes a Position from the database.
     * @param positionId the ID of the position to be deleted.
     * @return the response from the database (either successful or failed deletion).
     */
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePosition(@PathParam("id") String positionId) {
        System.out.println("DELETE /positions delete");

        try {
            int id = Integer.parseInt(positionId);
            DBPositionQueryHandler.getInstance().deleteEntity(id);
        } catch (NumberFormatException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format\"").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        return Response.ok("{\"Status\" : \"Position successfully deleted!\"}").build();
    }

    /**
     * Searches for Positions in the database by provided first and last name. If such are not provided the default
     * action is to query the database for all Positions entered so far.
     * @return the Position searched for or all Positions entered into the database so far.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchPosition(@Context UriInfo data) {
        System.out.println("GET /positions search");

        MultivaluedMap<String, String> info = data.getQueryParameters();

        List<Entity> result = new ArrayList<Entity>();
        try {
            result = DBPositionQueryHandler.getInstance().searchEntity(info);
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
     * Edits the details of the selected Position.
     * @param posId the ID of the Position to be edited.
     * @param positionDetails the new details which should be set in the database.
     * @return the response from the database (either successful or failed modification).
     */
    @POST
    @Path("{posId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editPosition(@PathParam("posId") String posId, JsonNode positionDetails) {
        System.out.println("POST /positions edit");

        int positionId = Integer.parseInt(posId);

        try {
            boolean check = DBPositionQueryHandler.getInstance().updateEntity(positionId, positionDetails);

            if (check) {
                return Response.ok("{\"Status\" : \"Position with ID " + positionId + " was successfully modified!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Position with ID " + positionId + " does not exists\"}").build();
            }
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format!!!\"}").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"Error\" : \"Position with ID " + positionId + " was not modified\"}").build();
        }
    }

    @GET
    @Path("/match/{positionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchCandidateForPosition(@PathParam("positionId") String positionId) {
        System.out.println("GET /positions searchCandidateForPosition");

        List<Entity> matches = new ArrayList<Entity>();
        try {
            matches = DBPositionQueryHandler.getInstance().searchForMatch(positionId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (matches == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid query parameters\"}").build();
        } else {
            return Response.ok(matches).build();
        }
    }
}