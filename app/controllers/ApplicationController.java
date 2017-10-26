package controllers;

import com.deswis.data.Task;
import com.deswis.filter.UniversalResponse;
import com.deswis.utils.Config;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import models.Document;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.FileCopyUtils;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.index;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Api(value = "/api/Application", description = "Application Main Function")
public class ApplicationController extends Controller {
    private final static String DOCUMENT_URL = Play.application().path() + "//uploads";
    @SuppressWarnings("rawtypes")
    private static final UniversalResponse response = new UniversalResponse();

    @NotNull
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Result index() {
        Map<String, String> map = null;
        try {
            map = getGithub();
        } catch (IOException e) {
            Config.getInstance().getErrorActivity("index", e.getMessage());
        }
        return ok(index.render());
    }

    private static void uploadFile(Http.MultipartFormData.FilePart file) throws Exception {

        String filePath = DOCUMENT_URL + "/" + file.getFilename();

        File dir = new File(DOCUMENT_URL);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dstFile = new File(filePath);
        if (dstFile.exists()) {
            dstFile.delete();
        }
        File srcFile = file.getFile();
        Document data = new Document();
        data.name = file.getFilename();
        data.url = filePath;
        data.uploadedAt = new Date();
        data.save();
        FileCopyUtils.copy(srcFile, dstFile);
    }

    @ApiOperation(value = "Upload file to server", notes = "Upload file to server", response = ApplicationController.class, httpMethod = "POST")
    public static Result upload() throws Exception {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("upload");

        if (filePart != null) {
            uploadFile(filePart);
            response.setResponse(200, "File uploaded", "", 1, filePart, 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("upload"));
            return ok(Json.toJson(response));
        } else {
            response.setResponse(500, "", Config.getInstance().getBadRequestMessage(), 1, "", 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("upload", Config.getInstance().getBadRequestMessage()));
            return badRequest(Json.toJson(response));
        }
    }


    @ApiOperation(value = "Running automate geolocation", notes = "Run automate task", response = ApplicationController.class, httpMethod = "GET")
    public static Result geolocation() {
        try {
            if (Task.getInstance().geoLocation()) {
                response.setResponse(200, "Geolocation task succesfuly run", "", 1, "", 1, 1);
                Logger.info(Config.getInstance().getSuccessActivity("geolocation"));
                return ok(Json.toJson(response));
            } else {
                response.setResponse(500, "", Config.getInstance().getBadRequestMessage(), 1, "", 1, 1);
                Logger.error(Config.getInstance().getErrorActivity("geolocation", Config.getInstance().getBadRequestMessage()));
                return badRequest(Json.toJson(response));
            }
        } catch (Exception e) {
            response.setResponse(500, "", Config.getInstance().getBadRequestMessage(), 1, "", 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("geolocation", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Create OWL files from database", notes = "Run automate task", response = ApplicationController.class, httpMethod = "GET")
    public static Result createOWL(int model) {
        try {
            if (Task.getInstance().createOWL(model)) {
                response.setResponse(200, "OWL Created", "", 1, "", 1, 1);
                Logger.info(Config.getInstance().getSuccessActivity("creating OWL"));
                return ok(Json.toJson(response));
            } else {
                response.setResponse(500, "", Config.getInstance().getBadRequestMessage(), 1, "", 1, 1);
                Logger.error(Config.getInstance().getErrorActivity("creating OWL", Config.getInstance().getBadRequestMessage()));
                return badRequest(Json.toJson(response));
            }
        } catch (Exception e) {
            response.setResponse(500, "", Config.getInstance().getBadRequestMessage(), 1, "", 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("creating OWL", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Get Commit date from Github", notes = "Run automate task", response = ApplicationController.class, httpMethod = "GET")
    public static Result latestUpdate() {
        try {
            Map<String, String> map = getGithub();
            response.setResponse(200, "Success get commit date", "", map.size(), map, 1, 1);
            Logger.info(Config.getInstance().getSuccessActivity("get commit date from Github"));
            return ok(Json.toJson(response));
        } catch (Exception e) {
            response.setResponse(500, "", Config.getInstance().getBadRequestMessage(), 1, "", 1, 1);
            Logger.error(Config.getInstance().getErrorActivity("get commit date from Github", e.getMessage()));
            return badRequest(Json.toJson(response));
        }
    }

    @NotNull
    public static Result preflight(String all) {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader("Allow", "*");
        response().setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        response().setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept , Referer, User-Agent");
        return ok();
    }

    private static Map<String, String> getGithub() throws IOException {
        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(Config.getInstance().getGithubApiKey());
        Map<String, String> map = new TreeMap<>();
        RepositoryService repositoryServiceservice = new RepositoryService(client);
        CommitService commitService = new CommitService(client);
        for (Repository repo : repositoryServiceservice.getRepositories()) {
            if (repo.getName().contains("deswis")) {
                map.put("backend", "Backend: " + Config.getInstance().getStringWithDay(commitService.getCommits(repo).get(0).getCommit().getAuthor().getDate()));
            }
        }
        return map;
    }
}
