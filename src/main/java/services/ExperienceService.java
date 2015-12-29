package services;

import com.fasterxml.jackson.databind.JsonNode;
import db.DBExperienceQueryHandler;
import models.Entity;
import models.implementation.Experience;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Created by Terrax on 06-Dec-2015.
 */
@Path("experiences")
public class ExperienceService {
    /**
     * A method which searches for a experience using ID as criteria.
     * @param experienceId the ID which to be searched.
     * @return the experience if such is found.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExperienceById(@PathParam("id") String experienceId) {
        System.out.println("GET /experiences get");
        Entity experience;

        try {
            int id = Integer.parseInt(experienceId);
            experience = DBExperienceQueryHandler.getInstance().searchEntityById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Experience with such ID was not found!\"}").build();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Unable to convert the provided parameter!\"}").build();
        }

        return Response.ok(experience).build();
    }

    /**
     * Create a new experience and insert it into the database.
     * @param node the details from which the experience will be created.
     * @return the response from the database (either successful or failed creation).
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addExperience(JsonNode node) {
        System.out.println("POST /experiences add");

        int candidateId = Integer.parseInt(node.get("candidateId").textValue());
        int techId = Integer.parseInt(node.get("techId").textValue());
        int years = Integer.parseInt(node.get("years").textValue());

        Experience experience = new Experience(candidateId, techId, years);

        try {
            DBExperienceQueryHandler.getInstance().addEntity(experience);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Response.ok("{\"Status\" : \"OK\"}").build();
    }

    /**
     * Deletes a experience from the database.
     * @param experienceId the ID of the experience to be deleted.
     * @return the response from the database (either successful or failed deletion).
     */
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteExperience(@PathParam("id") String experienceId) {
        System.out.println("DELETE /experiences delete");

        try {
            int id = Integer.parseInt(experienceId);
            DBExperienceQueryHandler.getInstance().deleteEntity(id);
        } catch (NumberFormatException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format\"").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        return Response.ok("{\"Status\" : \"OK\"}").build();
    }

    /**
     * Searches for experiences in the database by provided first and last name. If such are not provided the default
     * action is to query the database for all experiences entered so far.
     * @return the experience searched for or all experiences entered into the database so far.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchExperience(@Context UriInfo data) {
        System.out.println("GET /experiences search");

        MultivaluedMap<String, String> info = data.getQueryParameters();

        List<Entity> result = new ArrayList<Entity>();
        try {
            result = DBExperienceQueryHandler.getInstance().searchEntity(info);
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
     * Edits the details of the selected experience.
     * @param experienceId the ID of the experience.
     * @param experienceDetails the new details which should be set in the database.
     * @return the response from the database (either successful or failed modification).
     */
    @POST
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editExperience(@PathParam("id") String experienceId, JsonNode experienceDetails) {
        System.out.println("POST /experiences edit");

        try {
            int id = Integer.parseInt(experienceId);
            Entity entity = DBExperienceQueryHandler.getInstance().searchEntityById(id);
            Experience experience = null;

            if (entity instanceof Experience) {
                experience = (Experience) entity;
            }

            if (experience != null) {
                Experience newExperience = new Experience();
                newExperience.setId(id);
                newExperience.setCandidateId(Integer.parseInt(experienceDetails.get("candidateId").textValue()));
                newExperience.setTechId(Integer.parseInt(experienceDetails.get("techId").textValue()));
                newExperience.setYears(Integer.parseInt(experienceDetails.get("years").textValue()));

                DBExperienceQueryHandler.getInstance().updateEntity(newExperience);

                return Response.ok("{\"Status\" : \"Experience with ID " + id + " was successfully modified!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Experience with ID " + id + " does not exists\"}").build();
            }
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format!!!\"}").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"Error\" : \"Experience with ID" + experienceId + " was not modified\"}").build();
        }
    }
}