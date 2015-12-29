package services;

import com.fasterxml.jackson.databind.JsonNode;
import db.DBPositionQueryHandler;
import models.Entity;
import models.implementation.Position;

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
     * A method which searches for a position using ID as criteria.
     * @param positionId the ID which to be searched.
     * @return the position if such is found.
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
     * Create a new position and insert it into the database.
     * @param node the details from which the position will be created.
     * @return the response from the database (either successful or failed creation).
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPosition(JsonNode node) {
        System.out.println("POST /positions add");

        String positionName = node.get("positionName").textValue();
        int hrId = Integer.parseInt(node.get("hrId").textValue());
        int companyIdId = Integer.parseInt(node.get("companyId").textValue());

        Position position = new Position(hrId, companyIdId, positionName);

        try {
            DBPositionQueryHandler.getInstance().addEntity(position);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Response.ok("{\"Status\" : \"OK\"}").build();
    }

    /**
     * Deletes a position from the database.
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

        return Response.ok("{\"Status\" : \"OK\"}").build();
    }

    /**
     * Searches for positions in the database by provided first and last name. If such are not provided the default
     * action is to query the database for all positions entered so far.
     * @return the position searched for or all positions entered into the database so far.
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
     * Edits the details of the selected position.
     * @param positionId the ID of the position.
     * @param positionDetails the new details which should be set in the database.
     * @return the response from the database (either successful or failed modification).
     */
    @POST
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editPosition(@PathParam("id") String positionId, JsonNode positionDetails) {
        System.out.println("POST /positions edit");

        try {
            int id = Integer.parseInt(positionId);
            Entity entity = DBPositionQueryHandler.getInstance().searchEntityById(id);
            Position position = null;

            if (entity instanceof Position) {
                position = (Position) entity;
            }

            if (position != null) {
                Position newPosition = new Position();
                newPosition.setId(id);
                newPosition.setHrId(Integer.parseInt(positionDetails.get("hrId").textValue()));
                newPosition.setCompanyId(Integer.parseInt(positionDetails.get("companyId").textValue()));
                newPosition.setPositionName(positionDetails.get("positionName").textValue());

                DBPositionQueryHandler.getInstance().updateEntity(newPosition);

                return Response.ok("{\"Status\" : \"Position with ID " + id + " was successfully modified!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Position with ID " + id + " does not exists\"}").build();
            }
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format!!!\"}").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"Error\" : \"Position with ID " + positionId + " was not modified\"}").build();
        }
    }
}