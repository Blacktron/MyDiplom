package models.implementation;

import models.Entity;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
public class HR extends Entity {
    private String firstName;
    private String lastName;
    private String phone;
    private int companyId;

    public HR() { }

    public HR(String firstName, String lastName, String phone, int companyId) {
        setFirstName(firstName);
        setLastName(lastName);
        setPhone(phone);
        setCompanyId(companyId);
    }

    public HR(int hrId, String firstName, String lastName, String phone, int companyId) {
        setId(hrId);
        setFirstName(firstName);
        setLastName(lastName);
        setPhone(phone);
        setCompanyId(companyId);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return "HR{" +
                "id='" + id + '\'' +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", companyId=" + companyId +
                '}';
    }
}