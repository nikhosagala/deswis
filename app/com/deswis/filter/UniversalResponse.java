package com.deswis.filter;

public class UniversalResponse<T> {
    private int code;
    private String message;
    private String error;
    private int total_record;
    private int limit = 1;
    private int offset = 1;
    private Object data;

    public void setResponse(int code, String message, String error, int total_record, Object data, int limit,
                            int offset) {
        this.code = code;
        this.message = message;
        this.error = error;
        this.total_record = total_record;
        this.data = data;
        this.limit = limit;
        this.offset = offset;
    }

    public int getTotal_record() {
        return total_record;
    }

    public void setTotal_record(int total_record) {
        this.total_record = total_record;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}
