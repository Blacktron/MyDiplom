package models.implementation;

import models.Entity;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
public class Experience extends Entity {
    private int candidateId;
    private int techId;
    private int years;
    private String candidateFirstName;
    private String candidateLastName;
    private String techName;

    public Experience() { }

    public Experience(int candidateId, int techId, int years) {
        setCandidateId(candidateId);
        setTechId(techId);
        setYears(years);
    }

    public Experience(int experienceId, int candidateId, int techId, int years) {
        setId(experienceId);
        setCandidateId(candidateId);
        setTechId(techId);
        setYears(years);
    }

    public Experience(int experienceId, int candidateId, int techId, int years, String candidateFirstName, String candidateLastName, String techName) {
        setId(experienceId);
        setCandidateId(candidateId);
        setTechId(techId);
        setYears(years);
        setCandidateFirstName(candidateFirstName);
        setCandidateLastName(candidateLastName);
        setTechName(techName);
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getTechId() {
        return techId;
    }

    public void setTechId(int techId) {
        this.techId = techId;
    }

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public String getCandidateFirstName() {
        return candidateFirstName;
    }

    public void setCandidateFirstName(String candidateFirstName) {
        this.candidateFirstName = candidateFirstName;
    }

    public String getCandidateLastName() {
        return candidateLastName;
    }

    public void setCandidateLastName(String candidateLastName) {
        this.candidateLastName = candidateLastName;
    }

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    @Override
    public String toString() {
        return "Experience{" +
                "id=" + id + '\'' +
                "candidateId=" + candidateId + '\'' +
                ", techId=" + techId + '\'' +
                ", years=" + years + '\'' +
                '}';
    }
}