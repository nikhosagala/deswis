package com.deswis.filter;

public class ApiSort {
    private String property;
    private String direction;

    public ApiSort() {
        super();
    }

    String getDirection() {
        return direction;
    }

    @SuppressWarnings("unused")
    public void setDirection(String direction) {
        this.direction = direction;
    }

    String getProperty() {
        return property;
    }

    @SuppressWarnings("unused")
    public void setProperty(String property) {
        this.property = property;
    }

}
