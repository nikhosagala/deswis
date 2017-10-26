package controllers;

import com.avaje.ebean.Query;
import com.deswis.filter.ApiResponse;
import com.deswis.filter.UniversalResponse;
import com.deswis.utils.Config;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.*;
import models.GooglePlace;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Date;

@Api(value = "/api/GooglePlace", description = "Google Place Management")
public class GooglePlaceController extends Controller {
    @SuppressWarnings("rawtypes")
    private static final UniversalResponse response = new UniversalResponse();

    @SuppressWarnings({"rawtypes", "unchecked"})
    @ApiOperation(value = "Get Google Place", notes = "Return list of Google Place", response = GooglePlace.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", dataType = "Integer", paramType = "url", value = "10"),
            @ApiImplicitParam(name = "offset", dataType = "Integer", paramType = "url", value = "0"),
            @ApiImplicitParam(name = "sort", dataType = "json", paramType = "url", value = "[{\"property\":\"name\", \"direction\" : \"asc\"}]"),
            @ApiImplicitParam(name = "filter", dataType = "json", paramType = "url", value = "[{\"property\":\"name\", \"operator\":\"eq\", \"values\":[{\"value\":\"abcd\"}]}]")})
    public static Result gets(String filter, String sort, int offset, int limit) {
        try {
            Query query = GooglePlace.find.order("id");
            UniversalResponse<GooglePlace> response = ApiResponse.getInstance().getResponse(query, sort, filter, offset,
                    limit);
            Logger.info(Config.getInstance().getSuccessActivity("get all google place"));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("get all google place", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Get googlePlace", notes = "Return googlePlace by id", response = GooglePlace.class, httpMethod = "GET")
    public static Result get(Integer id) {
        GooglePlace data = GooglePlace.find.byId(id);
        if (data == null) {
            response.setResponse(404, "", "googlePlace not found", 1, "googlePlace not found", 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("get google place by id", "google place not found"));
            return notFound(Json.toJson(response));
        } else {
            response.setResponse(200, "success", "", 1, data, 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("get google place by id"));
            return ok(Json.toJson(response));
        }
    }

    @ApiOperation(nickname = "createGooglePlace", value = "Create GooglePlace", httpMethod = "POST", response = GooglePlace.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "GooglePlace", required = true, paramType = "body", value = "GooglePlace")})
    @ApiResponses(value = {
            @com.wordnik.swagger.annotations.ApiResponse(code = 400, message = "Json Processing Exception")})
    public static Result submit() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            Logger.info("json body= " + json);
            GooglePlace data = mapper.readValue(json.toString(), GooglePlace.class);
            data.createdAt = new Date();
            data.updatedAt = new Date();
            data.save();
            response.setResponse(200, "success", "", 1, data, 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("submit google place"));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("submit google place", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(nickname = "updateGooglePlace", value = "Update GooglePlace", httpMethod = "PUT", response = GooglePlace.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "GooglePlace", required = true, paramType = "body", value = "GooglePlace")})
    @ApiResponses(value = {
            @com.wordnik.swagger.annotations.ApiResponse(code = 400, message = "Json Processing Exception")})
    public static Result update() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            GooglePlace data = mapper.readValue(json.toString(), GooglePlace.class);
            data.updatedAt = new Date();
            data.update();
            response.setResponse(200, "success", "", 1, GooglePlace.find.byId(data.id), 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("update google place"));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("update google place", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Delete GooglePlace", notes = "Return deleted GooglePlace", response = GooglePlace.class, httpMethod = "DELETE")
    public static Result delete(Integer id) {
        GooglePlace data = GooglePlace.find.byId(id);
        try {
            if (data == null) {
                response.setResponse(404, "", "GooglePlace not found", 1, "GooglePlace not found", 1, 1);
                Logger.error(Config.getInstance().getErrorActivity("delete google place", "google place not found"));
                return notFound(Json.toJson(response));
            } else {
                data.delete();
                response.setResponse(200, "success", "", 1, data, 1, 1);
                Logger.error(Config.getInstance().getSuccessActivity("delete google place"));
                return ok(Json.toJson(response));
            }
        } catch (Exception e) {
            response.setResponse(500, "", Config.getInstance().getBadRequestMessage(), 1, "", 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("delete google place", e.getMessage()));
            return badRequest();
        }
    }
}
