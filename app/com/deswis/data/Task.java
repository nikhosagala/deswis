package com.deswis.data;

import com.deswis.utils.CommonUtils;
import com.deswis.utils.Config;
import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.PhotoResult;
import com.google.maps.model.PlaceDetails;
import models.Destination;
import models.GooglePlace;
import org.jetbrains.annotations.Nullable;
import play.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Task {

    private static Task instance = null;

    public static Task getInstance() {
        if (instance == null) {
            instance = new Task();
        }
        Logger.info("Running Task at : " + Config.getInstance().getStringDate(new Date()));
        return instance;
    }

    public boolean geoLocation() {
        List<Destination> destinations = Destination.find.where().eq("google_place_id", null).findList();
        boolean status = false;
        if (destinations.size() != 0) {
            Logger.info("Need to do " + destinations.size() + " geolocation more.");
            destinations = cutDestination(destinations);
            for (Destination destination : destinations) {
                GooglePlace googlePlace = new GooglePlace();
                if (saveGooglePlace(googlePlace, destination)) {
                    googlePlace.createdAt = new Date();
                    googlePlace.updatedAt = new Date();
                    googlePlace.save();
                    destination.updatedAt = new Date();
                    destination.save();
                    status = true;
                    Logger.info("Geolocation success : " + destination.name + " - distance : " + destination.distance);
                } else {
                    status = false;
                    Logger.error("Geolocation failed : " + destination.name);
                }
            }
        } else {
            status = false;
            Logger.warn("Geolocation done, no need to do anymore.");
        }
        return status;
    }

    public boolean createOWL(int model) {
        boolean status = false;
        try {
            OWLCreation.getInstance().init(model);
            Logger.info("OWL successfuly created.");
            status = true;
        } catch (Exception e) {
            Logger.error("Error creating OWL files " + e.getMessage());
        }
        return status;
    }

    private List<Destination> cutDestination(List<Destination> destinations) {
        if (destinations.size() > 10) {
            return destinations.subList(0, 10);
        } else {
            return destinations;
        }
    }

    private boolean saveGooglePlace(GooglePlace googlePlace, Destination destination) {
        GeoApiContext context = new GeoApiContext();
        context.setApiKey(Config.getInstance().getAPIKey());
        PlaceDetails placeDetails = getPlaceDetails(destination, context);
        if (placeDetails != null) {
            googlePlace.lat = placeDetails.geometry.location.lat;
            googlePlace.lng = placeDetails.geometry.location.lng;
            Random random = new Random();
            PhotoResult photoResult = getPhotoResults(destination, random.nextInt(5));
            if (photoResult != null) {
                googlePlace.imageData = photoResult.imageData;
                googlePlace.contentType = photoResult.contentType;
            }
            destination.googlePlace = googlePlace;
            googlePlace.placeId = placeDetails.placeId;
            googlePlace.name = placeDetails.name;
            if (placeDetails.rating > 0) {
                destination.rating = (double) placeDetails.rating;
            }
            destination.distance = CommonUtils.getInstance().getDistance("Dinas Bina Marga Provinsi Jawa Barat", destination.name, context);
            destination.duration = CommonUtils.getInstance().getDirections("Dinas Bina Marga Provinsi Jawa Barat", destination.name, context);
            if (("".equals(destination.phone) || destination.phone == null) && !"".equals(placeDetails.formattedPhoneNumber)) {
                destination.phone = placeDetails.formattedPhoneNumber;
            }
            StringBuilder types = new StringBuilder();
            for (int i = 0; i < placeDetails.types.length; i++) {
                types.append(placeDetails.types[i]).append(";");
            }
            googlePlace.types = types.toString();
            saveDestinationDetails(destination, placeDetails);
            return true;
        } else {
            return false;
        }
    }

    private void saveDestinationDetails(Destination destination, PlaceDetails placeDetails) {
        destination.address = placeDetails.formattedAddress;
        if (destination.address == null) {
            destination.address = "No address found from Google.";
        }
        try {
            destination.sunday = placeDetails.openingHours.weekdayText[6];
            destination.monday = placeDetails.openingHours.weekdayText[0];
            destination.tuesday = placeDetails.openingHours.weekdayText[1];
            destination.wednesday = placeDetails.openingHours.weekdayText[2];
            destination.thursday = placeDetails.openingHours.weekdayText[3];
            destination.friday = placeDetails.openingHours.weekdayText[4];
            destination.saturday = placeDetails.openingHours.weekdayText[5];
        } catch (NullPointerException npe) {
            destination.sunday = "-";
            destination.monday = "-";
            destination.tuesday = "-";
            destination.wednesday = "-";
            destination.thursday = "-";
            destination.friday = "-";
            destination.saturday = "-";
        }
    }

    @Nullable
    private PlaceDetails getPlaceDetails(Destination dest, GeoApiContext context) {
        GeocodingResult result[];
        try {
            result = GeocodingApi.geocode(context, dest.name).await();
            if (result.length > 0) {
                PlaceDetailsRequest pdr = PlacesApi.placeDetails(context, result[0].placeId).language("id");
                return pdr.await();
            }
        } catch (ApiException | InterruptedException | IOException e) {
            Logger.error(e.getMessage());
        }
        return null;
    }

    @Nullable
    private PhotoResult getPhotoResults(Destination dest, int number) {
        GeoApiContext context = new GeoApiContext();
        context.setApiKey(Config.getInstance().getAPIKey());
        GeocodingResult result[];
        try {
            result = GeocodingApi.geocode(context, dest.name).await();
            if (result.length > 0) {
                PlaceDetailsRequest pdr = PlacesApi.placeDetails(context, result[0].placeId);
                PlaceDetails pd = pdr.await();
                PhotoRequest pr = new PhotoRequest(context);
                if (number < pd.photos.length) {
                    pr.photoReference(pd.photos[number].photoReference);
                    pr.maxHeight(1000);
                    pr.maxWidth(720);
                    return pr.await();
                } else if (pd.photos.length > 0) {
                    pr.photoReference(pd.photos[0].photoReference);
                    pr.maxHeight(1000);
                    pr.maxWidth(720);
                    return pr.await();
                }
            }
        } catch (ApiException | InterruptedException | IOException e) {
            Logger.error(e.getMessage());
        } catch (NullPointerException npe) {
            Logger.error(npe.getMessage());
            return null;
        }
        return null;
    }
}
