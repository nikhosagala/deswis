import com.deswis.utils.Config;
import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.PlaceDetails;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.test.FakeApplication;
import play.test.WithApplication;

import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.fakeApplication;

/**
 * Created by nikhosagala on 15/06/2017.
 */
public class MapTest extends WithApplication {
    private static GeoApiContext context = null;

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

    public static void main(String args[]) {
        Random random = new Random();
        System.out.println(random.nextInt(4));
    }

    @Test
    public void getDistance() throws InterruptedException, ApiException, IOException {
        DistanceMatrixApiRequest matrixApiRequest;
        String[] origin = {"Dinas Bina Marga Provinsi Jawa Barat"};
        String[] destination = {"Tangkuban Perahu"};
        matrixApiRequest = DistanceMatrixApi.getDistanceMatrix(getContext(), origin, destination);
        DistanceMatrix distanceMatrix = matrixApiRequest.await();
        assertNotNull(distanceMatrix);
    }

    private GeoApiContext getContext() {
        if (context == null) {
            context = new GeoApiContext();
        }
        context.setApiKey(Config.getInstance().getAPIKey());
        return context;
    }

    @Test
    public void getLongTrip() throws InterruptedException, ApiException, IOException {
        DirectionsApiRequest directionsApiRequest;
        String origin = "Dinas Bina Marga Provinsi Jawa Barat";
        String destination = "Tangkuban Perahu";
        directionsApiRequest = DirectionsApi.getDirections(getContext(), origin, destination);
        DirectionsResult directionsResult = directionsApiRequest.await();
        System.out.println(directionsResult.routes[0].legs[0].duration.inSeconds);
    }

    @Nullable
    private PlaceDetails getPlaceDetail(String destination) {
        GeocodingResult result[];
        try {
            result = GeocodingApi.geocode(getContext(), destination).await();
            if (result.length > 0) {
                PlaceDetailsRequest pdr = PlacesApi.placeDetails(getContext(), result[0].placeId).language("id");
                PlaceDetails pd = pdr.await();
                return pd;
            }
        } catch (ApiException | InterruptedException | IOException e) {
            Logger.error(e.getMessage());
        }
        return null;
    }

    @Test
    public void testGetPlace() {
        PlaceDetails placeDetails = getPlaceDetail("Kampung Gajah");
        assertNotNull(placeDetails);
    }
}
