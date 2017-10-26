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
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Date;
import java.util.List;

@Api(value = "/api/Category", description = "Categories Management")
public class CategoryController extends Controller {
    @SuppressWarnings("rawtypes")
    private static final UniversalResponse response = new UniversalResponse();
    private static CommonUtils commonUtils = new CommonUtils();

    @SuppressWarnings({"rawtypes", "unchecked"})
    @ApiOperation(value = "Get Categories", notes = "Return list of category", response = Category.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", dataType = "Integer", paramType = "url", value = "10"),
            @ApiImplicitParam(name = "offset", dataType = "Integer", paramType = "url", value = "0"),
            @ApiImplicitParam(name = "sort", dataType = "json", paramType = "url", value = "[{\"property\":\"name\", \"direction\" : \"asc\"}]"),
            @ApiImplicitParam(name = "filter", dataType = "json", paramType = "url", value = "[{\"property\":\"name\", \"operator\":\"eq\", \"values\":[{\"value\":\"abcd\"}]}]")})
    public static Result gets(String filter, String sort, int offset, int limit) {
        try {
            Query query = Category.find.order("id");
            UniversalResponse<Category> response = ApiResponse.getInstance().getResponse(query, sort, filter, offset,
                    limit);
            Logger.info(Config.getInstance().getSuccessActivity("get destination"));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("get destination", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Get child category", notes = "Return all child category", response = Category.class, httpMethod = "GET")
    public static Result getChildren(String node) {
        try {
            List<Category> categories = commonUtils.getAllChild(commonUtils.getChild(node));
            response.setResponse(200, "success", "", categories.size(), categories, 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("get child category"));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            Logger.info(e.getMessage());
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("get parent", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Get category", notes = "Return category by id", response = Category.class, httpMethod = "GET")
    public static Result get(Integer id) {
        Category data = Category.find.byId(id);
        if (data == null) {
            response.setResponse(404, "", "category not found", 1, "category not found", 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("get category by id", "category not found"));
            return notFound(Json.toJson(response));
        } else {
            response.setResponse(200, "success", "", 1, data, 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("get category by id"));
            return ok(Json.toJson(response));
        }
    }

    @ApiOperation(nickname = "createCategory", value = "Create Category", httpMethod = "POST", response = Category.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "Category", required = true, paramType = "body", value = "Category")})
    @ApiResponses(value = {
            @com.wordnik.swagger.annotations.ApiResponse(code = 400, message = "Json Processing Exception")})
    public static Result submit() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            Category data = mapper.readValue(json.toString(), Category.class);
            data.createdAt = new Date();
            data.updatedAt = new Date();
            data.save();
            response.setResponse(200, "success", "", 1, data, 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("submit category"));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("submit category", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(nickname = "updateCategory", value = "Update Category", httpMethod = "PUT", response = Category.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "Category", required = true, paramType = "body", value = "Category")})
    @ApiResponses(value = {
            @com.wordnik.swagger.annotations.ApiResponse(code = 400, message = "Json Processing Exception")})
    public static Result update() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            Category data = mapper.readValue(json.toString(), Category.class);
            data.updatedAt = new Date();
            data.update();
            response.setResponse(200, "success", "", 1, Category.find.byId(data.id), 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("update category"));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("update category", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Delete Category", notes = "Return deleted Category", response = Category.class, httpMethod = "DELETE")
    public static Result delete(Integer id) {
        Category data = Category.find.byId(id);
        try {
            if (data == null) {
                response.setResponse(404, "", "Category not found", 1, "Category not found", 1, 1);
                Logger.error(Config.getInstance().getErrorActivity("delete category", "category not found"));
                return notFound(Json.toJson(response));
            } else {
                data.delete();
                response.setResponse(200, "success", "", 1, data, 1, 1);
                Logger.info(Config.getInstance().getSuccessActivity("delete category"));
                return ok(Json.toJson(response));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return badRequest();
        }
    }
}
