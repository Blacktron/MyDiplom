package models.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import models.Entity;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
public class Requirement extends Entity {
    private int positionId;
    private int technologyId;
    private int years;
    private int priority;
    private String technologyName;

    /**
     * Default constructor.
     */
    public Requirement() { }

    /**
     * Custom constructor which builds the object based on the existing parameters.
     * @param node the details from which to create the new object.
     */
    public Requirement(JsonNode node) {
        if (node.has("positionId")) {
            int positionId = Integer.parseInt(node.get("positionId").textValue());
            setPositionId(positionId);
        }

        if (node.has("technologyId")) {
            int technologyId = Integer.parseInt(node.get("technologyId").textValue());
            setTechnologyId(technologyId);
        }

        if (node.has("years")) {
            int years = Integer.parseInt(node.get("years").textValue());
            setYears(years);
        }

        if (node.has("priority")) {
            int priority = Integer.parseInt(node.get("priority").textValue());
            setPriority(priority);
        }
    }

    /**
     * Custom constructor which accepts four parameters. It is used when building the result after search.
     * @param positionId the ID of the Position.
     * @param technologyName the name of the Technology.
     * @param years the years of experience with certain Technology required for the Position.
     * @param priority the priority of the Requirement for the Position.
     */
    public Requirement(int positionId, String technologyName, int years, int priority) {
        setPositionId(positionId);
        setTechnologyName(technologyName);
        setYears(years);
        setPriority(priority);
    }

    /**
     * Shows the ID of the Position.
     * @return the ID of the Position.
     */
    public int getPositionId() {
        return positionId;
    }

    /**
     * Sets the ID of the Position.
     * @param positionId the ID of the Position.
     */
    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    /**
     * Shows the ID of the Technology.
     * @return the ID of the Technology.
     */
    public int getTechnologyId() {
        return technologyId;
    }

    /**
     * Sets the ID of the Technology.
     * @param technologyId the ID of the Technology.
     */
    public void setTechnologyId(int technologyId) {
        this.technologyId = technologyId;
    }

    /**
     * Shows the years of experience with certain Technology.
     * @return the years of experience with certain Technology.
     */
    public int getYears() {
        return years;
    }

    /**
     * Sets the years of experience with certain Technology.
     * @param years the years of experience with certain Technology.
     */
    public void setYears(int years) {
        this.years = years;
    }

    /**
     * Shows the priority of the Requirement.
     * @return the priority of the Requirement.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the Requirement.
     * @param priority the priority of the Requirement.
     */
    public void setPriority(int priority) {
        this.priority = priority;
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
     * Gets the details of the Requirement object and shows them.
     * @return the details of the Requirement.
     */
    @Override
    public String toString() {
        return "Requirement{" +
                "id=" + id + '\'' +
                ", positionId=" + positionId + '\'' +
                ", technologyId=" + technologyId + '\'' +
                ", years=" + years + '\'' +
                ", priority=" + priority + '\'' +
                '}';
    }
}