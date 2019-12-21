package com.example.piremedtime;

public class Model {
    String phone;
    String condition;
    String description;


    public Model() {
    }

    public Model(String condition,String description, String phone) {
        this.condition = condition;
        this.description = description;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
