package models.implementation;

import models.Entity;

/**
 * @Created by Terrax on 13-Sep-2015.
 */
public class Technology extends Entity {
    private String name;

    public Technology() { }

    public Technology(String name) {
        setName(name);
    }

    public Technology(int techId, String name) {
        setId(techId);
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Technology{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                '}';
    }
}