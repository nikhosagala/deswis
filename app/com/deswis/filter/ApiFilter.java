package com.deswis.filter;

public class ApiFilter {
    private String property;
    private String operator;
    private ApiFilterValue[] values;

    String getProperty() {
        return property;
    }

    @SuppressWarnings("unused")
    public void setProperty(String property) {
        this.property = property;
    }

    String getOperator() {
        return operator;
    }

    @SuppressWarnings("unused")
    public void setOperator(String operator) {
        this.operator = operator;
    }

    ApiFilterValue[] getValues() {
        return values;
    }

    @SuppressWarnings("unused")
    public void setValues(ApiFilterValue[] values) {
        this.values = values;
    }

}
