package services;

import com.fasterxml.jackson.databind.JsonNode;
import models.Entity;
import models.implementation.Candidate;
import db.DBCandidateQueryHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
@Path("candidates")
public class CandidateService {

    /**
     * A method which searches for a candidate using ID as criteria.
     * @param candId the ID which to be searched.
     * @return the candidate if such is found.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCandidateById(@PathParam("id") String candId) {
        System.out.println("GET /candidates get");
        Entity candidate;

        try {
            int id = Integer.parseInt(candId);
            candidate = DBCandidateQueryHandler.getInstance().searchEntityById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Candidate with such ID was not found!\"}").build();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Unable to convert the provided parameter!\"}").build();
        }

        return Response.ok(candidate).build();
    }

    /**
     * Create a new candidate and insert it into the database.
     * @param node the details from which the candidate will be created.
     * @return the response from the database (either successful or failed creation).
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCandidate(JsonNode node) {
        System.out.println("POST /candidates add");

        String firstName = node.get("firstName").textValue();
        String lastName = node.get("lastName").textValue();
        int age = Integer.parseInt(node.get("age").textValue());
        String language1 = node.get("language1").textValue();
        String language2 = node.get("language2").textValue();
        String language3 = node.get("language3").textValue();

        Candidate candidate = new Candidate(firstName, lastName, age, language1, language2, language3);

        try {
            DBCandidateQueryHandler.getInstance().addEntity(candidate);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Response.ok("{\"Status\" : \"OK\"}").build();
    }

    /**
     * Deletes a candidate from the database.
     * @param candId the ID of the candidate to be deleted.
     * @return the response from the database (either successful or failed deletion).
     */
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCandidate(@PathParam("id") String candId) {
        System.out.println("DELETE /candidates delete");

        try {
            int id = Integer.parseInt(candId);
            DBCandidateQueryHandler.getInstance().deleteEntity(id);
        } catch (NumberFormatException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format\"").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        return Response.ok("{\"Status\" : \"OK\"}").build();
    }

    /**
     * Searches for candidates in the database by provided first and last name. If such are not provided the default
     * action is to query the database for all candidates entered so far.
     * @return the candidate searched for or all candidates entered into the database so far.
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
     * Edits the details of the selected candidate.
     * @param candId the ID of the candidate.
     * @param candidateDetails the new details which should be set in the database.
     * @return the response from the database (either successful or failed modification).
     */
    @POST
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editCandidate(@PathParam("id") String candId, JsonNode candidateDetails) {
        System.out.println("POST /candidates edit");

        try {
            int id = Integer.parseInt(candId);
            Entity entity = DBCandidateQueryHandler.getInstance().searchEntityById(id);
            Candidate candidate = null;

            if (entity instanceof Candidate) {
                candidate = (Candidate) entity;
            }

            if (candidate != null) {
                Candidate newCandidate = new Candidate();
                newCandidate.setId(id);
                newCandidate.setFirstName(candidateDetails.get("firstName").textValue());
                newCandidate.setLastName(candidateDetails.get("lastName").textValue());
                newCandidate.setAge(Integer.parseInt(candidateDetails.get("age").textValue()));
                newCandidate.setLanguage1(candidateDetails.get("language1").textValue());
                newCandidate.setLanguage2(candidateDetails.get("language2").textValue());
                newCandidate.setLanguage3(candidateDetails.get("language3").textValue());

                DBCandidateQueryHandler.getInstance().updateEntity(newCandidate);

                return Response.ok("{\"Status\" : \"Candidate with ID " + id + " was successfully modified!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Candidate with ID " + id + " does not exists\"}").build();
            }
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format!!!\"}").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"Error\" : \"Candidate with ID" + candId + " was not modified\"}").build();
        }
    }
}