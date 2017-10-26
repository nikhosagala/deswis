package controllers;

import com.avaje.ebean.Query;
import com.deswis.filter.UniversalResponse;
import com.deswis.utils.CommonUtils;
import com.deswis.utils.Config;
import com.deswis.utils.RecommendationUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.*;
import models.*;
import org.jetbrains.annotations.Contract;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.*;

@Api(value = "/api/Recommendation", description = "Recommendation Main Function")
public class RecommendationController extends Controller {
    @SuppressWarnings("rawtypes")
    private static final UniversalResponse response = new UniversalResponse();

    @ApiOperation(value = "Get recommendation", notes = "Return recommendation by id", response = Recommendation.class, httpMethod = "GET")
    public static Result get(Integer id) {
        Recommendation data = Recommendation.find.byId(id);
        if (data == null) {
            response.setResponse(404, "", "recommendation not found", 1, "recommendation not found", 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("get recommendation by id", "recommendation not found"));
            return notFound(Json.toJson(response));
        } else {
            response.setResponse(200, "success", "", 1, data, 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("get recommendation by id"));
            return ok(Json.toJson(response));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @ApiOperation(value = "Get Destination", notes = "Return list of Destinations", response = Destination.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", dataType = "Integer", paramType = "url", value = "10"),
            @ApiImplicitParam(name = "offset", dataType = "Integer", paramType = "url", value = "0"),
            @ApiImplicitParam(name = "sort", dataType = "json", paramType = "url", value = "[{\"property\":\"name\", \"direction\" : \"asc\"}]"),
            @ApiImplicitParam(name = "filter", dataType = "json", paramType = "url", value = "[{\"property\":\"name\", \"operator\":\"eq\", \"values\":[{\"value\":\"abcd\"}]}]")})
    public static Result getDestinations(Integer id, String filter, String sort, int offset, int limit) {
        Recommendation data = Recommendation.find.byId(id);
        if (data == null) {
            response.setResponse(404, "", "recommendation not found", 1, "recommendation not found", 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("get recommendation by id", "recommendation not found"));
            return notFound(Json.toJson(response));
        } else {
            try {
                Query query = Destination.find.where().in("id", getDestinationId(getDestinations(data.recommendationResults))).query();
                UniversalResponse<Destination> response = com.deswis.filter.ApiResponse.getInstance().getResponse(query, sort, filter, offset,
                        limit);
                Logger.info(Config.getInstance().getSuccessActivity("get destination from recommendation"));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
                Logger.error(Config.getInstance().getErrorActivity("get destination from recommendation", e.getMessage()));
                return badRequest(Json.toJson(response));
            }
        }
    }

    @ApiOperation(nickname = "createRecommendation", value = "Create RecommendationUtils", httpMethod = "POST", response = Recommendation.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "RecommendationUtils", required = true, paramType = "body", value = "RecommendationUtils")})
    @ApiResponses(value = {
            @com.wordnik.swagger.annotations.ApiResponse(code = 400, message = "Json Processing Exception")})
    public static Result submit(int save) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
//            Logger.info("json body= " + json + " " + save);
            long startTime = System.nanoTime();
            Recommendation recommendation = mapper.readValue(json.toString(), Recommendation.class);
            if (validateValue(recommendation.recommendationDetails)) {
                recommendation.recommendationDetails = removeEmpty(recommendation.recommendationDetails);
                Logger.error("User Input");
                Logger.error("time before: " + recommendation.processTime);
                setRecommedationDetails(recommendation.recommendationDetails);
                Logger.info("Location - lat: " + recommendation.lat + " lng: " + recommendation.lng);
                saveRecommendation(recommendation, save);
                long endTime = System.nanoTime();
                long timer = (endTime - startTime) / 1000000;
                long before = recommendation.processTime > 0 ? (long) recommendation.processTime : 0;
                recommendation.processTime = timer + before;
                if (save != 0) {
                    recommendation = Recommendation.find.byId(recommendation.id);
                    response.setResponse(200, "success", "", recommendation.recommendationResults.size(), recommendation, 1, 1);
                } else {
                    response.setResponse(200, "success", "", recommendation.recommendationResults.size(), recommendation.processTime, 1, 1);
                }
                Logger.error("submited by: " + recommendation.name);
                Logger.info("took " + timer + " milis for this process, before: " + before + " milis, total: " + recommendation.processTime + " milis");
                Logger.info(Config.getInstance().getSuccessActivity("submit recommendation"));
                return ok(Json.toJson(response));
            } else {
                response.setResponse(400, "", "you submit nothing", 1, "", 1, 1);
                Logger.error(Config.getInstance().getErrorActivity("submit recommendation", "submit nothing"));
                return badRequest(Json.toJson(response));
            }
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            e.printStackTrace();
            Logger.error(Config.getInstance().getErrorActivity("submit recommendation", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(nickname = "getLatestHighestCategories", value = "Get Highest Categories from Recommendation", httpMethod = "POST", response = Recommendation.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "Recommendation", required = true, paramType = "body", value = "Category")})
    @ApiResponses(value = {
            @com.wordnik.swagger.annotations.ApiResponse(code = 400, message = "Json Processing Exception")})
    @SuppressWarnings("unchecked")
    public static Result latest() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
//            Logger.info("json body= " + json + " ");
            Recommendation recommendation = mapper.readValue(json.toString(), Recommendation.class);
            if (validateValue(recommendation.recommendationDetails)) {
                recommendation.recommendationDetails = removeEmpty(recommendation.recommendationDetails);
                Map<String, Double> interestValueMap = new TreeMap<>();
                for (RecommendationDetail recommendationDetail : recommendation.recommendationDetails) {
                    interestValueMap = RecommendationUtils.getInstance().assignInterestValueParent(interestValueMap, recommendationDetail.category.name, recommendationDetail.interestValue);
                    interestValueMap = RecommendationUtils.getInstance().assignInterestValue(interestValueMap, recommendationDetail.category.name, recommendationDetail.interestValue);
                }
                List<Category> categories = getLatestCategory(recommendation.recommendationDetails);
                response.setResponse(200, "success", "", categories.size(), categories, 1, 1);
                Logger.info(Config.getInstance().getSuccessActivity("get latest highest category"));
                return ok(Json.toJson(response));
            } else {
                response.setResponse(400, "", "you submit nothing", 1, "", 1, 1);
                Logger.error(Config.getInstance().getErrorActivity("get latest highest category", "submit nothing"));
                return badRequest(Json.toJson(response));
            }
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("submit recommendation", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(nickname = "updateRecommendation", value = "Update RecommendationUtils", httpMethod = "PUT", response = Recommendation.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "Category", required = true, paramType = "body", value = "RecommendationUtils")})
    @ApiResponses(value = {
            @com.wordnik.swagger.annotations.ApiResponse(code = 400, message = "Json Processing Exception")})
    public static Result update() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            Recommendation data = mapper.readValue(json.toString(), Recommendation.class);
            data.updatedAt = new Date();
            data.update();
            Logger.info("updated by " + data.name);
            response.setResponse(200, "success", "", 1, Recommendation.find.byId(data.id), 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("update recommendation"));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("update recommendation", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Delete recommendation", notes = "Return deleted recommendation", response = Category.class, httpMethod = "DELETE")
    public static Result delete(Integer id) {
        Recommendation data = Recommendation.find.byId(id);
        try {
            if (data == null) {
                response.setResponse(404, "", "Recommendation not found", 1, "Recommendation not found", 1, 1);
                Logger.error(Config.getInstance().getErrorActivity("delete recommendation", "recommendation not found"));
                return notFound(Json.toJson(response));
            } else {
                data.delete();
                response.setResponse(200, "success", "", 1, data, 1, 1);
                Logger.info(Config.getInstance().getSuccessActivity("delete recommendation"));
                return ok(Json.toJson(response));
            }
        } catch (Exception e) {
            response.setResponse(500, "", Config.getInstance().getBadRequestMessage(), 1, "", 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("delete recommendation", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(nickname = "createRecommendation", value = "Create RecommendationUtils", httpMethod = "POST", response = Recommendation.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "RecommendationUtils", required = true, paramType = "body", value = "RecommendationUtils")})
    @ApiResponses(value = {
            @com.wordnik.swagger.annotations.ApiResponse(code = 400, message = "Json Processing Exception")})
    public static Result submitSurvey() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            Survey data = mapper.readValue(json.toString(), Survey.class);
            data.createdAt = new Date();
            data.updatedAt = new Date();
            data.save();
            response.setResponse(200, "success", "", 1, data, 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("submit survey by " + data.name));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("submit survey", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(nickname = "updateSurvey", value = "Update Survey", httpMethod = "PUT", response = Recommendation.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "Survey", required = true, paramType = "body", value = "Survey")})
    @ApiResponses(value = {
            @com.wordnik.swagger.annotations.ApiResponse(code = 400, message = "Json Processing Exception")})
    public static Result updateSurvey() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = request().body().asJson();
            Survey data = mapper.readValue(json.toString(), Survey.class);
            data.updatedAt = new Date();
            data.update();
            response.setResponse(200, "success", "", 1, data, 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("update survey by " + data.name));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", "bad request", 1, Config.getInstance().getBadRequestMessage(), 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("update survey", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Get valid survey", notes = "Return all valid survey", response = Survey.class, httpMethod = "GET")
    public static Result getSurvey(Integer id) {
        Survey data = Survey.find.byId(id);
        if (data == null) {
            response.setResponse(404, "", "survey not found", 1, "survey not found", 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("get survey by id", "survey not found"));
            return notFound(Json.toJson(response));
        } else {
            response.setResponse(200, "success", "", 1, data, 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("get survey by id"));
            return ok(Json.toJson(response));
        }
    }

    private static void setRecommedationDetails(List<RecommendationDetail> recommendationDetails) {
        for (RecommendationDetail recommendationDetail : recommendationDetails) {
            Category category;
            category = Category.find.where().eq("name", recommendationDetail.category.name.replace('_', ' ')).findUnique();
            Logger.info(category.name + " " + recommendationDetail.interestValue);
            recommendationDetail.category = category;
        }
    }

    @SuppressWarnings("unused")
    private static List<Survey> getValidSurvey(int valid) {
        List<Survey> surveyValid = new ArrayList<>();
        List<Survey> surveyInvalid = new ArrayList<>();
        for (Recommendation recommendation : Recommendation.find.all()) {
            Survey survey = new Survey();
            survey.name = recommendation.name;
        }
        if (valid == 0) {
            return surveyValid;
        } else {
            return surveyInvalid;
        }
    }

    @SuppressWarnings("unchecked")
    private static List<RecommendationResult> setRecommendationResults(Recommendation recommendation) {
        List<RecommendationResult> recommendationResults = new ArrayList<>();
        Map<String, Double> interestValueMap = new TreeMap<>();
        for (RecommendationDetail recommendationDetail : recommendation.recommendationDetails) {
            interestValueMap = RecommendationUtils.getInstance().assignInterestValueParent(interestValueMap, recommendationDetail.category.name, recommendationDetail.interestValue);
            interestValueMap = RecommendationUtils.getInstance().assignInterestValue(interestValueMap, recommendationDetail.category.name, recommendationDetail.interestValue);
        }
        Logger.error("Propagasi Nilai Interest ke Kategori");
        RecommendationUtils.getInstance().printHash(interestValueMap, 0);
        Map<String, Double> interestValueMapFinal = RecommendationUtils.getInstance().propagateValue(interestValueMap);
        List<Destination> destinations = RecommendationUtils.getInstance().getRecommedations(interestValueMapFinal, recommendation);
        Logger.info("Total destinasi: " + destinations.size());
        for (Destination destination : destinations) {
            RecommendationResult recommendationResult = new RecommendationResult();
            recommendationResult.destination = destination;
            recommendationResult.interestValue = interestValueMapFinal.get(destination.name);
            recommendationResults.add(recommendationResult);
        }
        return recommendationResults;
    }

    private static void saveRecommendation(Recommendation submit, Integer save) {
        if (save != 0) {
            submit.createdAt = new Date();
            submit.updatedAt = new Date();
            submit.save();
            for (RecommendationDetail detail : submit.recommendationDetails) {
                detail.recommendation = submit;
                detail.createdAt = new Date();
                detail.updatedAt = new Date();
                detail.save();
            }
            for (RecommendationResult result : setRecommendationResults(submit)) {
                result.recommendation = submit;
                result.createdAt = new Date();
                result.updatedAt = new Date();
                result.save();
            }
        } else {
            submit.recommendationResults = setRecommendationResults(submit);
        }
    }

    @Contract(pure = true)
    private static boolean validateValue(List<RecommendationDetail> recommendationDetails) {
        int count = 0;
        for (RecommendationDetail recommendationDetail : recommendationDetails) {
            if (recommendationDetail.interestValue != 0) {
                count++;
            }
        }
        return count > 0;
    }

    private static List<RecommendationDetail> removeEmpty(List<RecommendationDetail> recommendationDetails) {
        List<RecommendationDetail> details = new ArrayList<>();
        for (RecommendationDetail detail : recommendationDetails) {
            if (detail.interestValue != 0) {
                details.add(detail);
            }
        }
        return details;
    }

    private static List<Integer> getDestinationId(List<Destination> destinationList) {
        List<Integer> id = new ArrayList<>();
        for (Destination dest : destinationList) id.add(dest.id);
        return id;
    }

    private static List<Destination> getDestinations(List<RecommendationResult> recommendationResults) {
        List<Destination> destinationList = new ArrayList<>();
        for (RecommendationResult recommendationResult : recommendationResults)
            destinationList.add(recommendationResult.destination);
        return destinationList;
    }

    private static List<Category> getLatestCategory(List<RecommendationDetail> recommendationDetails) {
        List<Category> categories = new ArrayList<>();
        List<String> children = new ArrayList<>();
        for (RecommendationDetail detail : recommendationDetails) {
            children.addAll(CommonUtils.getInstance().getChild(detail.category.name));
        }
        for (String category : children) {
            Category find = Category.find.where().eq("name", category).findUnique();
            if (find != null) {
                categories.add(find);
            }
        }
        return categories;
    }

    @SuppressWarnings("unused")
    private static Map<String, Double> getConfidenceValue(Map<String, Double> interestValueMap, Map<String, Double> preferenceValueMap) {
        Map<String, Double> confidenceValueMap = new TreeMap<>();
        interestValueMap = CommonUtils.getInstance().sortByValue(interestValueMap);
        interestValueMap.forEach((k, v) ->
                confidenceValueMap.put(k, v * preferenceValueMap.get(k))
        );
        return confidenceValueMap;
    }
}
