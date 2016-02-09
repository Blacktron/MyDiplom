package models.implementation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import models.Entity;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Experience extends Entity {
    private int candidateId;
    private int technologyId;
    private int years;
    private String technologyName;

    /**
     * Default constructor.
     */
    public Experience() { }

    /**
     * Custom constructor which builds the object based on the existing parameters.
     * @param node the details from which to create the new object.
     */
    public Experience(JsonNode node) {
        if (node.has("candidateId")) {
            int candidateId = Integer.parseInt(node.get("candidateId").textValue());
            setCandidateId(candidateId);
        }

        if (node.has("technologyId")) {
            int technologyId = Integer.parseInt(node.get("technologyId").textValue());
            setTechnologyId(technologyId);
        }

        if (node.has("years")) {
            int years = Integer.parseInt(node.get("years").textValue());
            setYears(years);
        }
    }

    /**
     * Custom constructor which accepts three parameters. It is used when building the result after search.
     * @param candidateId the ID of the Candidate.
     * @param technologyName the name of the Technology.
     * @param years the years of experience a Candidate has with a Technology.
     */
    public Experience(int candidateId, String technologyName, int years) {
        setCandidateId(candidateId);
        setTechnologyName(technologyName);
        setYears(years);
    }

    /**
     * Shows the ID of the Candidate.
     * @return the ID of the Candidate.
     */
    public int getCandidateId() {
        return candidateId;
    }

    /**
     * Sets the ID of the Candidate.
     * @param candidateId the ID of the Candidate.
     */
    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
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
     * Shows the years of experience a Candidate has with a Technology.
     * @return the years of experience a Candidate has with a Technology.
     */
    public int getYears() {
        return years;
    }

    /**
     * Sets the years of experience a Candidate has with a Technology.
     * @param years the years of experience a Candidate has with a Technology.
     */
    public void setYears(int years) {
        this.years = years;
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
     * @param technologyName the name of the Technology
     */
    public void setTechnologyName(String technologyName) {
        this.technologyName = technologyName;
    }

    /**
     * Gets the details of the Experience object and shows them.
     * @return the details of the Experience.
     */
    @Override
    public String toString() {
        return "Experience{" +
                "id=" + id + '\'' +
                "candidateId=" + candidateId + '\'' +
                ", technologyId=" + technologyId + '\'' +
                ", years=" + years +
                '}';
    }
}