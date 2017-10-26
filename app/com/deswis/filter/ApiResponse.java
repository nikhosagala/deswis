package com.deswis.filter;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApiResponse<T> {
    @SuppressWarnings("rawtypes")
    private static ApiResponse instance;

    @SuppressWarnings("rawtypes")
    public static ApiResponse getInstance() {
        if (instance == null) {
            instance = new ApiResponse();
        }
        return instance;
    }

    public UniversalResponse<T> getResponse(Query<T> query, String sort, String filter, int offset, int limit)
            throws IOException {

        // SORT
        if (!"".equals(sort)) {
            ApiSort[] sorts = new ObjectMapper().readValue(sort, ApiSort[].class);
            for (ApiSort apiSort : sorts) {
                query = query.orderBy(apiSort.getProperty() + " " + apiSort.getDirection());
            }
        }

        // FILTER
        ExpressionList<T> exp = query.where();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        if (!"".equals(filter)) {
            ApiFilters filters = new ObjectMapper().readValue(filter, ApiFilters.class);
            System.out.println(Json.toJson(filters));
            if (filters.getLogic() == null) {
                exp = exp.conjunction();
            } else if (filters.getLogic().equals("and")) {
                exp = exp.conjunction();
            } else {
                exp = exp.disjunction();
            }

            ApiFilter[] apiFilters = filters.getFilters();
            for (int i = 0; i < apiFilters.length; i++) {
                ApiFilter apiFilter = apiFilters[i];
                setFilter(exp, formatter, apiFilter);
            }
            exp = exp.endJunction();
        }

        query = exp.query();
        int total = query.findList().size();

        if (limit != 0) {
            query = query.setMaxRows(limit);
        }

        List<T> list = query.findPagingList(limit).getPage(offset - 1).getList();
        System.out.println(query.getGeneratedSql());

        UniversalResponse<T> response = new UniversalResponse<>();
        response.setCode(200);
        response.setData(list);
        response.setTotal_record(total);
        response.setLimit(limit != 0 ? limit : total);
        response.setOffset(offset);
        response.setError("");
        response.setMessage("success");

        return response;
    }

    private void setFilter(ExpressionList<T> exp, SimpleDateFormat formatter, ApiFilter af) {
        try {
            setDateFilter(exp, formatter, af);
        } catch (ParseException e) {
            setObjectFilter(exp, af);
        }
    }

    private void setObjectFilter(ExpressionList<T> exp, ApiFilter af) {
        if (af.getOperator() == null) {
            exp.eq(af.getProperty(), (af.getValues()[0]).getValue());
        } else {
            switch (af.getOperator()) {
                case "equals":
                    exp.eq(af.getProperty(), (af.getValues()[0]).getValue());
                    break;
                case "not_equals":
                    exp.ne(af.getProperty(), (af.getValues()[0]).getValue());
                    break;
                case "between":
                    exp.between(af.getProperty(), (af.getValues()[0]).getValue(), (af.getValues()[1]).getValue());
                    break;
                case "greater_than":
                    exp.gt(af.getProperty(), (af.getValues()[0]).getValue());
                    break;
                case "greater_than_or_equals":
                    exp.ge(af.getProperty(), (af.getValues()[0]).getValue());
                    break;
                case "less_than":
                    exp.lt(af.getProperty(), (af.getValues()[0]).getValue());
                    break;
                case "less_than_or_equals":
                    exp.le(af.getProperty(), (af.getValues()[0]).getValue());
                    break;
                case "is_null":
                    exp.isNull(af.getProperty());
                    break;
                case "is_not_null":
                    exp.isNotNull(af.getProperty());
                    break;
                case "like":
                    exp.ilike(af.getProperty(), "%" + (af.getValues()[0]).getValue().toString() + "%");
                    break;
                case "in":
                    List<Object> objs = new ArrayList<>();
                    for (int i = 0; i < af.getValues().length; i++) {
                        objs.add(i, (af.getValues()[i]).getValue());
                    }
                    exp.in(af.getProperty(), objs);
                    break;
                case "not_in":
                    List<Object> not_objs = new ArrayList<>();
                    for (int i = 0; i < af.getValues().length; i++) {
                        not_objs.add(i, (af.getValues()[i]).getValue());
                    }
                    exp.not(Expr.in(af.getProperty(), not_objs));
                    break;
            }
        }
    }

    private void setDateFilter(ExpressionList<T> exp, SimpleDateFormat formatter, ApiFilter af) throws ParseException {
        if ((af.getValues()[0]).getValue().toString().length() == 10) {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        }
        if (af.getOperator() == null) {
            exp.eq(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
        } else {
            switch (af.getOperator()) {
                case "equals":
                    exp.eq(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
                    break;
                case "not_equals":
                    exp.ne(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
                    break;
                case "between":
                    exp.between(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()),
                            formatter.parse((af.getValues()[1]).getValue().toString()));
                    break;
                case "greater_than":
                    exp.gt(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
                    break;
                case "greater_than_or_equals":
                    exp.ge(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
                    break;
                case "less_than":
                    exp.lt(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
                    break;
                case "less_than_or_equals":
                    exp.le(af.getProperty(), formatter.parse((af.getValues()[0]).getValue().toString()));
                    break;
                case "is_null":
                    exp.isNull(af.getProperty());
                    break;
                case "like":
                    formatter.parse((af.getValues()[0]).getValue().toString());
                    break;
                case "is_not_null":
                    exp.isNotNull(af.getProperty());
                    break;
                case "in":
                    List<Date> dates = new ArrayList<>();
                    for (int i = 0; i < af.getValues().length; i++) {
                        dates.add(i, formatter.parse((af.getValues()[i]).getValue().toString()));
                    }
                    exp.in(af.getProperty(), dates);
                    break;
                case "not_in":
                    List<Date> not_dates = new ArrayList<>();
                    for (int i = 0; i < af.getValues().length; i++) {
                        not_dates.add(i, formatter.parse((af.getValues()[i]).getValue().toString()));
                    }
                    exp.not(Expr.in(af.getProperty(), not_dates));
                    break;
            }
        }
    }

}
