import com.deswis.utils.RecommendationUtils;
import models.Destination;
import org.junit.Test;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.test.FakeApplication;
import play.test.WithApplication;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;

/**
 * Created by nikhosagala on 16/06/2017.
 */
public class RecommendationTest extends WithApplication {
    private static RecommendationTest instance = null;

    FakeApplication fakeAppWithGlobal = fakeApplication(new GlobalSettings() {
        @Override
        public void onStart(Application app) {
            Logger.info("Starting FakeApplication");
        }
    });

    public static RecommendationTest getInstance() {
        if (instance == null) {
            instance = new RecommendationTest();
        }
        return instance;
    }

    @Override
    protected FakeApplication provideFakeApplication() {
        return fakeAppWithGlobal;
    }

}
