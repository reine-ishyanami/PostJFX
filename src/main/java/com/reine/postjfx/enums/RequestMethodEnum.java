package com.reine.postjfx.enums;

/**
 * @author reine
 */
public enum RequestMethodEnum {
    GET(1, "GET"), POST(2, "POST"), PUT(3, "PUT"), DELETE(4, "DELETE");
    private int id;
    private String name;

    RequestMethodEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
