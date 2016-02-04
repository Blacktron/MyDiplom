package models.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import models.Entity;

import java.util.List;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
public class Candidate extends Entity {
    private String candidateFirstName;          // The first name of the Candidate.
    private String candidateLastName;           // The last name of the Candidate.
    private int age;                            // The age of the Candidate.
    private List<Experience> experiences;       // The List holding the Technologies and corresponding Experience of the Candidate.
    private List<Position> applications;        // The List holding the Positions for which the Candidate applied.
    private int rating = 0;                     // The rating calculated for each Candidate applied for a selected Position.

    /**
     * Default constructor.
     */
    public Candidate() { }

    /**
     * Custom constructor which builds the object based on the existing parameters.
     * @param node the details from which to create the new object.
     */
    public Candidate(JsonNode node) {
        if (node.has("candidateFirstName")) {
            setCandidateFirstName(node.get("candidateFirstName").textValue());
        }

        if (node.has("candidateLastName")) {
            setCandidateLastName(node.get("candidateLastName").textValue());
        }

        if (node.has("age")) {
            int age = Integer.parseInt(node.get("age").textValue());
            setAge(age);
        }
    }

    /**
     * Custom constructor which accepts six parameters. It is used when building the result after search.
     * @param candidateId the ID of the Candidate.
     * @param candidateFirstName the first name of the Candidate.
     * @param candidateLastName the last name of the Candidate.
     * @param age the age of the Candidate.
     */
    public Candidate(int candidateId, String candidateFirstName, String candidateLastName, int age) {
        setId(candidateId);
        setCandidateFirstName(candidateFirstName);
        setCandidateLastName(candidateLastName);
        setAge(age);
    }

    /**
     * Shows the first name of the Candidate.
     * @return the first name of the Candidate.
     */
    public String getCandidateFirstName() {
        return candidateFirstName;
    }

    /**
     * Sets the first name of the Candidate.
     * @param candidateFirstName the first name of the Candidate.
     */
    public void setCandidateFirstName(String candidateFirstName) {
        this.candidateFirstName = candidateFirstName;
    }

    /**
     * Shows the last name of the Candidate.
     * @return the last name of the Candidate.
     */
    public String getCandidateLastName() {
        return candidateLastName;
    }

    /**
     * Sets the last name of the Candidate.
     * @param candidateLastName the last name of the Candidate.
     */
    public void setCandidateLastName(String candidateLastName) {
        this.candidateLastName = candidateLastName;
    }

    /**
     * Shows the age of the Candidate.
     * @return the age of the Candidate.
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age of the Candidate.
     * @param age the age of the Candidate.
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Shows the List with Experience of the Candidate.
     * @return the List with Experience of the Candidate.
     */
    public List<Experience> getExperiences() {
        return experiences;
    }

    /**
     * Sets the List with Experience of the Candidate.
     * @param experiences the List with Experience of the Candidate.
     */
    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }

    /**
     * Shows the List with Positions for which the Candidate applied.
     * @return the List with Positions for which the Candidate applied.
     */
    public List<Position> getApplications() {
        return applications;
    }

    /**
     * Sets the the List with Positions for which the Candidate applied.
     * @param applications the List with Positions for which the Candidate applied.
     */
    public void setApplications(List<Position> applications) {
        this.applications = applications;
    }

    /**
     * Shows the rating of the Candidate for a certain Position.
     * @return the rating of the Candidate for a certain Position.
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the rating of the Candidate for a certain Position.
     * @param rating the rating of the Candidate for a certain Position.
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Gets the details of the Candidate object and shows them.
     * @return the details of the Candidate.
     */
    @Override
    public String toString() {
        return "Candidate{" +
                "id='" + id + '\'' +
                ", candidateFirstName='" + candidateFirstName + '\'' +
                ", candidateLastName='" + candidateLastName + '\'' +
                ", age=" + age +
                '}';
    }
}