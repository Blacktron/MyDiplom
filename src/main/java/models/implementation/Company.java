package models.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import models.Entity;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
public class Company extends Entity {
    private String companyName;     // The name of the Company.

    /**
     * Default constructor.
     */
    public Company() { }

    /**
     * Custom constructor which builds the object based on the existing parameters.
     * @param node the details from which to create the new object.
     */
    public Company(JsonNode node) {
        if (node.has("companyName")) {
            setCompanyName(node.get("companyName").textValue());
        }
    }

    /**
     * Custom constructor which accepts two parameters.
     * @param companyId the ID of the Company.
     * @param companyName the name of the Company.
     */
    public Company(int companyId, String companyName) {
        setId(companyId);
        setCompanyName(companyName);
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
     * Gets the details of the Company object and shows them.
     * @return the details of the Company.
     */
    @Override
    public String toString() {
        return "Company{" +
                "id='" + id + '\'' +
                "companyName='" + companyName + '\'' +
                '}';
    }
}