package services;

import com.fasterxml.jackson.databind.JsonNode;
import db.DBCompanyQueryHandler;
import models.Entity;
import models.implementation.Company;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Created by Terrax on 05-Dec-2015.
 */
@Path("companies")
public class CompanyService {

    /**
     * A method which searches for a company using ID as criteria.
     * @param companyId the ID which to be searched.
     * @return the company if such is found.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompanyById(@PathParam("id") String companyId) {
        System.out.println("GET /companies get");
        Entity company;

        try {
            int id = Integer.parseInt(companyId);
            company = DBCompanyQueryHandler.getInstance().searchEntityById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Company with such ID was not found!\"}").build();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Unable to convert the provided parameter!\"}").build();
        }

        return Response.ok(company).build();
    }

    /**
     * Create a new company and insert it into the database.
     * @param companyName the name which will be used to create the new company.
     * @return the response from the database (either successful or failed creation).
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCompany(JsonNode companyName) throws SQLException {
        System.out.println("POST /companies add");

        String comp = companyName.get("companyName").textValue();
        Company company = new Company(comp);

        DBCompanyQueryHandler.getInstance().addEntity(company);

        return Response.ok("{\"Status\" : \"OK\"}").build();
    }

    /**
     * Deletes a company from the database.
     * @param companyId the ID of the company to be deleted.
     * @return the response from the database (either successful or failed deletion).
     */
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCompany(@PathParam("id") String companyId) throws SQLException {
        System.out.println("DELETE /companies delete");

        try {
            int id = Integer.parseInt(companyId);
            DBCompanyQueryHandler.getInstance().deleteEntity(id);
            return Response.ok("{\"Status\" : \"OK\"}").build();
        } catch (NumberFormatException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format!!!\"}").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Searches for company in the database by provided name. If such is not provided the default
     * action is to query the database for all companies entered so far.
     * @return the company searched for or all companies entered into the database so far.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchCompany(@Context UriInfo data) throws SQLException {
        System.out.println("GET /companies search");

        MultivaluedMap<String, String> info = data.getQueryParameters();

        List<Entity> result = new ArrayList<Entity>();
        try {
            result = DBCompanyQueryHandler.getInstance().searchEntity(info);
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
     * Edits the details of the selected company.
     * @param companyId the ID of the company.
     * @param companyDetails the new details which should be set in the database.
     * @return the response from the database (either successful or failed modification).
     */
    @POST
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editCompany(@PathParam("id") String companyId, JsonNode companyDetails) {
        System.out.println("POST /companies edit");

        try {
            int id = Integer.parseInt(companyId);
            Entity entity = DBCompanyQueryHandler.getInstance().searchEntityById(id);
            Company company = null;

            if (entity instanceof Company) {
                company = (Company) entity;
            }

            if (company != null) {
                Company newCompany = new Company();
                newCompany.setId(id);
                newCompany.setCompanyName(companyDetails.get("name").textValue());

                DBCompanyQueryHandler.getInstance().updateEntity(newCompany);

                return Response.ok("{\"Status\" : \"Company with ID " + id + " was successfully modified!\"}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Company with ID " + id + " does not exists\"}").build();
            }
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\" : \"Invalid ID format!!!\"}").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"Error\" : \"Company with ID" + companyId + " was not modified\"}").build();
        }
    }
}