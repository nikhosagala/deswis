import com.deswis.data.MasterData;
import com.deswis.utils.IgnoreConverter;
import com.wordnik.swagger.converter.ModelConverters;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;
import play.libs.Akka;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application application) {
        super.onStart(application);
        Logger.info("Starting application");
        MasterData.getInstance().createGooglePlace();
        MasterData.getInstance().createCategory();
        MasterData.getInstance().createDestination();
        Akka.system().scheduler().schedule(Duration.create(0, TimeUnit.MILLISECONDS),
                Duration.create(10, TimeUnit.MINUTES), // Frequency 10 minutes
                () -> {
//                    Task.getInstance().geoLocation();
                }, Akka.system().dispatcher()

        );
    }

    public void beforeStart(Application app) {
        Logger.info("Registering model converter");
        ModelConverters.addConverter(new IgnoreConverter(), true);
    }

    @Override
    public Action<?> onRequest(Http.Request request, java.lang.reflect.Method actionMethod) {
        return new ActionWrapper(super.onRequest(request, actionMethod));
    }

    private class ActionWrapper extends Action.Simple {
        public ActionWrapper(Action<?> action) {
            this.delegate = action;
        }

        @Override
        public Promise<Result> call(Http.Context ctx) throws java.lang.Throwable {
            Promise<Result> result = this.delegate.call(ctx);
            Http.Response response = ctx.response();
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Headers",
                    "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, X-Token, x-token, Token, token, x-api-token, X-API-TOKEN, x-xsrf-token");
            return result;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{GzipFilter.class};
    }
}
