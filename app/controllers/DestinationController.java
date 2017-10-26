package controllers;

import com.avaje.ebean.Query;
import com.deswis.filter.ApiResponse;
import com.deswis.filter.UniversalResponse;
import com.deswis.utils.CommonUtils;
import com.deswis.utils.Config;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.*;
import models.Category;
import models.Destination;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(value = "/api/Destination", description = "Destinations Management")
public class DestinationController extends Controller {
    @SuppressWarnings("rawtypes")
    private static final UniversalResponse response = new UniversalResponse();
    private static CommonUtils commonUtils = new CommonUtils();

    @SuppressWarnings({"rawtypes", "unchecked"})
    @ApiOperation(value = "Get Destination", notes = "Return list of Destinations", response = Destination.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", dataType = "Integer", paramType = "url", value = "10"),
            @ApiImplicitParam(name = "offset", dataType = "Integer", paramType = "url", value = "0"),
            @ApiImplicitParam(name = "sort", dataType = "json", paramType = "url", value = "[{\"property\":\"name\", \"direction\" : \"asc\"}]"),
            @ApiImplicitParam(name = "filter", dataType = "json", paramType = "url", value = "[{\"property\":\"name\", \"operator\":\"eq\", \"values\":[{\"value\":\"abcd\"}]}]")})
    public static Result gets(String filter, String sort, int offset, int limit) {
        try {
            Query query = Destination.find.order("id");
            UniversalResponse<Destination> response = ApiResponse.getInstance().getResponse(query, sort, filter, offset,
                    limit);
            Logger.info(Config.getInstance().getSuccessActivity("get all destination"));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("get all destination", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Get Destination", notes = "Return destination by id", response = Destination.class, httpMethod = "GET")
    public static Result get(Integer id) {
        Destination data = Destination.find.byId(id);
        if (data == null) {
            response.setResponse(404, "", "destination not found", 1, "destination not found", 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("get destination by id", "destination not found"));
            return notFound(Json.toJson(response));
        } else {
            response.setResponse(200, "success", "", 1, data, 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("get destination by id"));
            return ok(Json.toJson(response));
        }
    }

    @ApiOperation(nickname = "createDestination", value = "Create Destination", httpMethod = "POST", response = Destination.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "Destination", required = true, paramType = "body", value = "Destination")})
    @ApiResponses(value = {
            @com.wordnik.swagger.annotations.ApiResponse(code = 400, message = "Json Processing Exception")})
    public static Result submit() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            Destination data = mapper.readValue(json.toString(), Destination.class);
            data.createdAt = new Date();
            data.updatedAt = new Date();
            data.save();
            response.setResponse(200, "success", "", 1, data, 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("submit destination"));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("submit destination", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(nickname = "updateDestination", value = "Update Destination", httpMethod = "PUT", response = Destination.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "Destination", required = true, paramType = "body", value = "Destination")})
    @ApiResponses(value = {
            @com.wordnik.swagger.annotations.ApiResponse(code = 400, message = "Json Processing Exception")})
    public static Result update() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            Destination data = mapper.readValue(json.toString(), Destination.class);
            data.updatedAt = new Date();
            data.update();
            response.setResponse(200, "success", "", 1, Destination.find.byId(data.id), 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("update destination"));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("update destination", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Delete Destination", notes = "Return deleted Destination", response = Destination.class, httpMethod = "DELETE")
    public static Result delete(Integer id) {
        Destination data = Destination.find.byId(id);
        try {
            if (data == null) {
                response.setResponse(404, "", "Destination not found", 1, "Destination not found", 1, 1);
                Logger.error(Config.getInstance().getErrorActivity("delete destination", "destination not found"));
                return notFound(Json.toJson(response));
            } else {
                data.delete();
                response.setResponse(200, "success", "", 1, data, 1, 1);
                Logger.info(Config.getInstance().getSuccessActivity("delete destination"));
                return ok(Json.toJson(response));
            }
        } catch (Exception e) {
            response.setResponse(500, "", Config.getInstance().getBadRequestMessage(), 1, "", 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("delete destination", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @ApiOperation(value = "Get destination by category", notes = "Return destination by category id", response = Destination.class, httpMethod = "GET")
    public static Result getByCategory(Integer id, String filter, String sort, int offset, int limit) {
        try {
            Category category = Category.find.where().eq("id", id).findUnique();
            List<String> strings = commonUtils.getChild(category.name);
            List<Category> listCategory = commonUtils.getAllChild(strings);
            Query query = Destination.find.where().in("id", convertId(listCategory)).query();
            UniversalResponse<Destination> response = ApiResponse.getInstance().getResponse(query, sort, filter, offset,
                    limit);
            Logger.info(Config.getInstance().getSuccessActivity("get destination by category"));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("get destination by category", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    private static List<Integer> convertId(List<Category> list) {
        List<Integer> id = new ArrayList<>();
        for (Category category : list) id.add(category.id);
        return id;
    }

}
