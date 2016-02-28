package models.implementation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import models.Entity;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Technology extends Entity {
    private String technologyName;      // The name of the Technology.

    /**
     * Default constructor.
     */
    public Technology() { }

    /**
     * Custom constructor which builds the object based on the existing parameters.
     * @param node the details from which to create the new object.
     */
    public Technology(JsonNode node) {
        if (node.has("technologyName")) {
            setTechnologyName(node.get("technologyName").textValue());
        }
    }

    /**
     * Custom constructor which accepts two parameters. It is used when building the result after search.
     * @param technologyId the ID of the Technology.
     * @param technologyName the name of the Technology.
     */
    public Technology(int technologyId, String technologyName) {
        setId(technologyId);
        setTechnologyName(technologyName);
    }

    /**
     * Shows the name of the Technology.
     * @return the name of the Technology.
     */
    public String getTechnologyName() {
        return technologyName;
    }

    /**
     * Sets the name of the Technology.
     * @param technologyName the name of the Technology.
     */
    public void setTechnologyName(String technologyName) {
        this.technologyName = technologyName;
    }

    /**
     * Gets the details of the Technology object and shows them.
     * @return the details of the Technology.
     */
    @Override
    public String toString() {
        return "Technology{" +
                "id='" + id + '\'' +
                "technologyName='" + technologyName +
                '}';
    }
}