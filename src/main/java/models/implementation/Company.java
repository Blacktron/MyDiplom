package models.implementation;

import models.Entity;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
public class Company extends Entity {
    private String companyName;

    public Company() { }

    public Company(String companyName) {
        this.companyName = companyName;
    }

    public Company(int companyId, String companyName) {
        setId(companyId);
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id='" + id + '\'' +
                "companyName='" + companyName + '\'' +
                '}';
    }
}