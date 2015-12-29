package models.implementation;

import models.Entity;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
public class Position extends Entity {
    private int hrId;
    private int companyId;
    private String positionName;
    private String hrFirstName;
    private String hrLastName;
    private String companyName;

    public Position() { }

    public Position(int hrId, int companyId, String positionName) {
        setHrId(hrId);
        setCompanyId(companyId);
        setPositionName(positionName);
    }

    public Position(int positionId, int hrId, int companyId, String positionName) {
        setId(positionId);
        setHrId(hrId);
        setCompanyId(companyId);
        setPositionName(positionName);
    }

    /**
     * Constructor used when searching entity by different parameters.
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

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String name) {
        this.positionName = name;
    }

    public int getHrId() {
        return hrId;
    }

    public void setHrId(int hrId) {
        this.hrId = hrId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getHrFirstName() {
        return hrFirstName;
    }

    public void setHrFirstName(String hrFirstName) {
        this.hrFirstName = hrFirstName;
    }

    public String getHrLastName() {
        return hrLastName;
    }

    public void setHrLastName(String hrLastName) {
        this.hrLastName = hrLastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id='" + id + '\'' +
                ", hrId=" + hrId +
                ", companyId=" + companyId +
                ", name='" + positionName + '\'' +
                '}';
    }
}