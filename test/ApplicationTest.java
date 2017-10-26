import controllers.ApplicationController;
import org.junit.Test;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.WithApplication;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 */
public class ApplicationTest extends WithApplication {

    FakeApplication fakeAppWithGlobal = fakeApplication(new GlobalSettings() {
        @Override
        public void onStart(Application app) {
            Logger.info("Starting FakeApplication");
        }
    });

    @Override
    protected FakeApplication provideFakeApplication() {
        return fakeAppWithGlobal;
    }

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }

    @Test
    public void testBadRoute() {
        Result result = route(fakeRequest(GET, "destination/kasd"));
        assertThat(result).isNull();
    }

}
