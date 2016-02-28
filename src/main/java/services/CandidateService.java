package services;

import com.fasterxml.jackson.databind.JsonNode;
import models.Entity;
import db.DBCandidateQueryHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
@Path("candidates")
public class CandidateService {

    /**
     * A method which searches for a Candidate using ID as criteria.
     * @param candidateId the ID which to be searched.
     * @return the Candidate if such is found.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCandidateById(@PathParam("id") String candidateId) {
        System.out.println("GET /candidates get");
        List<Entity> result = new ArrayList<Entity>();

        try {
            int id = Integer.parseInt(candidateId);
            Entity candidate = DBCandidateQueryHandler.getInstance().searchEntityById(id);
            result.add(candidate);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Candidate with such ID was not found!\"}").build();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Unable to convert the provided parameter!\"}").build();
        }

        return Response.ok(result).build();
    }

    /**
     * Receives the details of the entry and sends them for processing.
     * @param node the details from which the Candidate will be created.
     * @return the response from the database (either successful or failed creation).
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCandidate(JsonNode node) {
        System.out.println("POST /candidates add");

        boolean check = false;

        try {
            check = DBCandidateQueryHandler.getInstance().addEntity(node);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (check) {
            return Response.ok("{\"Status\" : \"New Candidate successfully created!\"}").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Candidate already exists or the database server is down\"").build();
        }
    }

    /**
     * Deletes a Candidate from the database.
     * @param candidateId the ID of the Candidate to be deleted.
     * @return the response from the database (either successful or failed deletion).
     */
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCandidate(@PathParam("id") String candidateId) {
        System.out.println("DELETE /candidates delete");

        try {
            int id = Integer.parseInt(candidateId);
            DBCandidateQueryHandler.getInstance().deleteEntity(id);
        } catch (NumberFormatException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format!!!\"").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        return Response.ok("{\"Status\" : \"Candidate successfully deleted!\"}").build();
    }

    /**
     * Searches for Candidates in the database by provided first and last name. If such are not provided the default
     * action is to query the database for all Candidates entered so far.
     * @return the Candidate searched for or all Candidates entered into the database so far.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchCandidate(@Context UriInfo data) {
        System.out.println("GET /candidates search");

        MultivaluedMap<String, String> info = data.getQueryParameters();

        List<Entity> result = new ArrayList<Entity>();
        try {
            result = DBCandidateQueryHandler.getInstance().searchEntity(info);
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
     * Edits the details of the selected Candidate.
     * @param candId the ID of the Candidate to be edited.
     * @param candidateDetails the new details which should be set in the database.
     * @return the response from the database (either successful or failed modification).
     */
    @POST
    @Path("{candId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editCandidate(@PathParam("candId") String candId, JsonNode candidateDetails) {
        System.out.println("POST /candidates edit");

        int candidateId = Integer.parseInt(candId);

        try {
            boolean check = DBCandidateQueryHandler.getInstance().updateEntity(candidateId, candidateDetails);

            if (check) {
                return Response.ok("{\"Status\" : \"Candidate with ID " + candidateId + " was successfully modified!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Candidate with ID " + candidateId + " does not exists\"}").build();
            }
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format!!!\"}").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"Error\" : \"Candidate with ID " + candidateId + " was not modified\"}").build();
        }
    }
}