package models.implementation;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
public class Requirement {
    private int requirementId;
    private int positionId;
    private int techId;
    private int years;
    private int priority;

    public Requirement() { }

    public Requirement(int requirementId, int positionId, int techId, int years, int priority) {
        this.requirementId = requirementId;
        this.positionId = positionId;
        this.techId = techId;
        this.years = years;
        this.priority = priority;
    }

    public int getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(int requirementId) {
        this.requirementId = requirementId;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}