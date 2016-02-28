package services;

import com.fasterxml.jackson.databind.JsonNode;
import db.DBHRQueryHandler;
import models.Entity;
import models.implementation.HR;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Created by Terrax on 09-Dec-2015.
 */
@Path("hrs")
public class HRService {
    /**
     * A method which searches for a HR using ID as criteria.
     * @param hrId the ID which to be searched.
     * @return the HR if such is found.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHRById(@PathParam("id") String hrId) {
        System.out.println("GET /hrs get");
        Entity hr;

        try {
            int id = Integer.parseInt(hrId);
            hr = DBHRQueryHandler.getInstance().searchEntityById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"HR with such ID was not found!\"}").build();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Unable to convert the provided parameter!\"}").build();
        }

        return Response.ok(hr).build();
    }

    /**
     * Create a new HR and insert it into the database.
     * @param node the details from which the HR will be created.
     * @return the response from the database (either successful or failed creation).
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addHR(JsonNode node) {
        System.out.println("POST /hrs add");

        boolean check;

        try {
            check = DBHRQueryHandler.getInstance().addEntity(node);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        if (check) {
            return Response.ok("{\"Status\" : \"New HR successfully created!\"}").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Unable to add the new HR!!!\"").build();
        }
    }

    /**
     * Deletes a HR from the database.
     * @param hrId the ID of the HR to be deleted.
     * @return the response from the database (either successful or failed deletion).
     */
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteHR(@PathParam("id") String hrId) {
        System.out.println("DELETE /hrs delete");

        try {
            int id = Integer.parseInt(hrId);
            DBHRQueryHandler.getInstance().deleteEntity(id);
        } catch (NumberFormatException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format\"").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        return Response.ok("{\"Status\" : \"HR successfully deleted!\"}").build();
    }

    /**
     * Searches for HR in the database by provided first and last name. If such are not provided the default
     * action is to query the database for all HRs entered so far.
     * @return the HR searched for or all HRs entered into the database so far.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchHR(@Context UriInfo data) {
        System.out.println("GET /hrs search");

        MultivaluedMap<String, String> info = data.getQueryParameters();

        List<Entity> result = new ArrayList<Entity>();
        try {
            result = DBHRQueryHandler.getInstance().searchEntity(info);
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
     * Edits the details of the selected HR.
     * @param hrID the ID of the HR to be edited.
     * @param hrDetails the new details which should be set in the database.
     * @return the response from the database (either successful or failed modification).
     */
    @POST
    @Path("{hrID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editHR(@PathParam("hrID") String hrID, JsonNode hrDetails) {
        System.out.println("POST /hrs edit");

        int hrId = Integer.parseInt(hrID);

        try {
            boolean check = DBHRQueryHandler.getInstance().updateEntity(hrId, hrDetails);

            if (check) {
                return Response.ok("{\"Status\" : \"HR with ID " + hrId + " was successfully modified!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"HR with ID " + hrId + " does not exists\"}").build();
            }
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format!!!\"}").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"Error\" : \"HR with ID " + hrId + " was not modified\"}").build();
        }
    }
}