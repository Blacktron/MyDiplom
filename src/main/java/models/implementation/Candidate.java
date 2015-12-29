package models.implementation;

import models.Entity;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
public class Candidate extends Entity {
    private String firstName;
    private String lastName;
    private int age;
    private String language1;
    private String language2;
    private String language3;

    public Candidate() { }

    public Candidate(String firstName, String lastName, int age, String language1, String language2, String language3) {
        setFirstName(firstName);
        setLastName(lastName);
        setAge(age);
        setLanguage1(language1);
        setLanguage2(language2);
        setLanguage3(language3);
    }

    public Candidate(int candidateId, String firstName, String lastName, int age, String language1, String language2, String language3) {
        setId(candidateId);
        setFirstName(firstName);
        setLastName(lastName);
        setAge(age);
        setLanguage1(language1);
        setLanguage2(language2);
        setLanguage3(language3);
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLanguage1() {
        return language1;
    }

    public void setLanguage1(String language1) {
        this.language1 = language1;
    }

    public String getLanguage2() {
        return language2;
    }

    public void setLanguage2(String language2) {
        this.language2 = language2;
    }

    public String getLanguage3() {
        return language3;
    }

    public void setLanguage3(String language3) {
        this.language3 = language3;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id='" + id + '\'' +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", language1='" + language1 + '\'' +
                ", language2='" + language2 + '\'' +
                ", language3='" + language3 + '\'' +
                '}';
    }
}