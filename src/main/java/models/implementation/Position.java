package models.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import models.Entity;

import java.util.List;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
public class Position extends Entity {
    private int hrId;                           // The ID of the HR representative.
    private int companyId;                      // The ID of the company.
    private String positionName;                // The name of the position.
    private String hrFirstName;                 // The first name of the HR representative.
    private String hrLastName;                  // The last name of the HR representative.
    private String companyName;                 // The name of the company.
    private List<Requirement> requirements;     // The List holding the Requirements and corresponding details of the Position.
    private List<Candidate> applicants;         // The List holding the Candidates which applied for the Position.

    /**
     * Default constructor.
     */
    public Position() { }

    /**
     * Custom constructor which builds the object based on the existing parameters.
     * @param node the details from which to create the new object.
     */
    public Position(JsonNode node) {
        if (node.has("hrId")) {
            int hrId = Integer.parseInt(node.get("hrId").textValue());
            setHrId(hrId);
        }

        if (node.has("companyId")) {
            int companyId = Integer.parseInt(node.get("companyId").textValue());
            setCompanyId(companyId);
        }

        if (node.has("positionName")) {
            setPositionName(node.get("positionName").textValue());
        }
    }

    /**
     * Custom constructor which accepts two parameters. It is used when building the result after searching for a Candidate.
     * @param positionName the name of the Position for which the Candidate applied.
     * @param companyName the name of the Company for which the Position is opened.
     */
    public Position(String positionName, String companyName) {
        setPositionName(positionName);
        setCompanyName(companyName);
    }

    /**
     * Custom constructor which accepts seven parameters. It is used when building the result after search.
     * @param positionId the ID of the position in the database.
     * @param hrId the ID of the HR in the database.
     * @param companyId the ID of the company in the database.
     * @param positionName the name of the position.
     * @param hrFirstName the first name of the HR.
     * @param hrLastName the last name of the HR.
     * @param companyName the name of the company.
     */
    public Position(int positionId, int hrId, int companyId, String positionName, String hrFirstName, String hrLastName, String companyName) {
        setId(positionId);
        setHrId(hrId);
        setCompanyId(companyId);
        setPositionName(positionName);
        setHrFirstName(hrFirstName);
        setHrLastName(hrLastName);
        setCompanyName(companyName);
    }

    /**
     * Shows the name of the Position.
     * @return the name of the Position.
     */
    public String getPositionName() {
        return positionName;
    }

    /**
     * Sets the name of the Position.
     * @param name the name of the Position.
     */
    public void setPositionName(String name) {
        this.positionName = name;
    }

    /**
     * Shows the ID of the HR representative.
     * @return the ID of the HR representative.
     */
    public int getHrId() {
        return hrId;
    }

    /**
     * Sets the ID of the HR representative.
     * @param hrId the ID of the HR representative.
     */
    public void setHrId(int hrId) {
        this.hrId = hrId;
    }

    /**
     * Shows the ID of the Company.
     * @return the ID of the Company.
     */
    public int getCompanyId() {
        return companyId;
    }

    /**
     * Sets the ID of the Company.
     * @param companyId the ID of the Company.
     */
    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    /**
     * Shows the first name of the HR representative.
     * @return the first name of the HR representative.
     */
    public String getHrFirstName() {
        return hrFirstName;
    }

    /**
     * Sets the first name of the HR representative.
     * @param hrFirstName the first name of the HR representative.
     */
    public void setHrFirstName(String hrFirstName) {
        this.hrFirstName = hrFirstName;
    }

    /**
     * Shows the last name of the HR representative.
     * @return the last name of the HR representative.
     */
    public String getHrLastName() {
        return hrLastName;
    }

    /**
     * Sets the last name of the HR representative.
     * @param hrLastName the last name of the HR representative.
     */
    public void setHrLastName(String hrLastName) {
        this.hrLastName = hrLastName;
    }

    /**
     * Shows the name of the Company.
     * @return the name of the Company.
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the name of the Company.
     * @param companyName the name of the Company.
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Shows the List with Requirements for the Position.
     * @return the List with Requirements for the Position.
     */
    public List<Requirement> getRequirements() {
        return requirements;
    }

    /**
     * Sets the List with Requirements for the Position.
     * @param requirements the List with Requirements for the Position.
     */
    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    /**
     * Shows the List holding the Candidates which applied for the Position.
     * @return the List holding the Candidates which applied for the Position.
     */
    public List<Candidate> getApplicants() {
        return applicants;
    }

    /**
     * Sets the List holding the Candidates which applied for the Position.
     * @param applicants the List holding the Candidates which applied for the Position.
     */
    public void setApplicants(List<Candidate> applicants) {
        this.applicants = applicants;
    }

    /**
     * Gets the details of the Position object and shows them.
     * @return the details of the Position.
     */
    @Override
    public String toString() {
        return "Position{" +
                "id='" + id + '\'' +
                ", hrId=" + hrId + '\'' +
                ", companyId=" + companyId + '\'' +
                ", name='" + positionName + '\'' +
                '}';
    }
}