package com.deswis.utils;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import models.Category;
import models.Destination;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import play.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Created by nothere on 29/05/2017.
 */
public class CommonUtils {

    private static CommonUtils instance = null;
    private RecommendationUtils recommendationUtils = new RecommendationUtils();
    public static File file = new File(Config.getInstance().getPath() + "conf//data//data.xlsx");


    public static CommonUtils getInstance() {
        if (instance == null) {
            instance = new CommonUtils();
        }
        return instance;
    }

    public static void main(String[] args) {
        System.out.println(getInstance().getFirstChild());
    }

    public List<String> getFirstChild() {
        List<String> result = recommendationUtils.parseResult(recommendationUtils.getFirstChild());
        return result;
    }

    public List<String> getChild(String parent) {
        List<String> result = recommendationUtils.parseResult(recommendationUtils.getChild(parent));
        Collections.sort(result);
        return result;
    }

    public List<Category> getAllChild(List<String> stringList) {
        List<Category> categories = new ArrayList<>();
        for (String s : stringList) {
            Category category = Category.find.where().eq("name", s).findUnique();
            if (category != null) {
                categories.add(category);
            }
        }
        return categories;
    }

    @SuppressWarnings("unused")
    private static void saveImage(byte[] image) {
        try {
            InputStream in = new ByteArrayInputStream(image);
            BufferedImage bImageFromConvert = ImageIO.read(in);
            ImageIO.write(bImageFromConvert, "jpg", new File(
                    "d:/test.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> sortByValue(Map<String, Double> unsortMap) {
        // 1. Convert Map to List of Map
        List<Map.Entry<String, Double>> list =
                new LinkedList<>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    @SuppressWarnings("unused")
    public Map<String, Double> convertMap(Map<String, Double> unconvert) {
        Map<String, Double> convertMap = new TreeMap<>();
        Double a = Collections.min(unconvert.values());
        Double b = Collections.max(unconvert.values());
        unconvert.forEach((k, v) ->
                convertMap.put(k, normalization(a, b, 0.1, 0.9, v))
        );
        return convertMap;
    }

    @SuppressWarnings("unused")
    public Double average(Map<String, Double> map) {
        Double[] result = {0.0};
        map.forEach((k, v) ->
                result[0] += v
        );
        return result[0] / map.size();
    }

    @SuppressWarnings("unused")
    public Double median(Map<String, Double> map) {
        Double result;
        List<Double> doubles = new ArrayList<>();
        map.forEach((k, v) -> doubles.add(v));
        Double[] temp = doubles.toArray(new Double[0]);
        Arrays.sort(temp);
        if (temp.length % 2 == 0) {
            result = (temp[temp.length / 2] + temp[temp.length / 2 - 1]) / 2;
        } else {
            result = temp[temp.length / 2];
        }
        return result;
    }

    @NotNull
    @Contract(pure = true)
    private Double normalization(Double a, Double b, Double min, Double max, Double value) {
        return (max * (value - a) / (b - a)) + min;
    }

    public Map<String, String> readCategoryFromDb() {
        Map<String, String> result = new TreeMap<>();
        for (Category category : Category.find.all()) {
            if (category.children == null) {
                result.put(category.name, "");
            } else {
                result.put(category.name, category.children);
            }
        }
        return result;
    }

    public Map<String, String> readDestinationsFromDb() {
        Map<String, String> result = new TreeMap<>();
        for (Destination destination : Destination.find.all()) {
            result.put(destination.name, destination.categories);
        }
        return result;
    }

    public Map<String, String> readExcelCategory(File file, int sheetNum) {
        Map<String, String> result = new TreeMap<>();
        String key;
        String value;
        try {
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(sheetNum);
            for (Row row : sheet) {
                if (row.getRowNum() >= 1) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    key = "";
                    value = "";
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        if (cell.getColumnIndex() == 0) {
                            key = cell.getStringCellValue();
                        }
                        if (cell.getColumnIndex() == 1) {
                            value = cell.getStringCellValue();
                        }
                        result.put(key, value);
                    }
                }
                workbook.close();
            }
        } catch (Exception e) {
            play.Logger.error(e.getMessage());
        }
        return result;
    }

    public Map<String, String> readExcelDestinastion(File file, int sheetNum) {
        Map<String, String> result = new TreeMap<>();
        String key;
        String value;
        try {
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(sheetNum);
            for (Row row : sheet) {
                if (row.getRowNum() >= 1) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    key = "";
                    value = "";
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        if (cell.getColumnIndex() == 1) {
                            key = cell.getStringCellValue();
                        }
                        if (cell.getColumnIndex() == 5) {
                            value = cell.getStringCellValue();
                        }
                        result.put(key, value);
                    }
                }
                workbook.close();
            }
        } catch (Exception e) {
            play.Logger.error(e.getMessage());
        }
        return result;
    }


    public List<String> parseString(String input) {
        return Arrays.asList(input.replace(' ', '_').split(";"));
    }

    Double SMART(Double distance, Double ontology, Double rating, Double durationImportance, Double ontologyImportance, Double ratingImportance) {
        Double totalImportance = ratingImportance + durationImportance + ontologyImportance;
        Double weightRating = ratingImportance / totalImportance;
        Double weightDistance = durationImportance / totalImportance;
        Double weightOntology = ontologyImportance / totalImportance;
        Double valueRating = multipleWeight(convertRating(rating), weightRating);
        Double valueDistance = multipleWeight(convertDistance(distance), weightDistance);
        Double valueOntology = multipleWeight(convertOntology(ontology), weightOntology);
        return valueRating + valueDistance + valueOntology;
    }

    @NotNull
    @Contract(pure = true)
    private Double convertMAUT(Double max, Double min, Double value) {
        return (value - min) / (max - min);
    }

    Double MAUT(Double maxDuration, Double minDuration, Double maxOntology, Double minOntology, Double maxRating, Double minRating, Double valueDistance, Double valueOntology, Double valueRating, Double importanceDuration, Double importanceOntology, Double importanceRating) {
        Double durationWeight = convertMAUT(maxDuration, minDuration, valueDistance);
        Double interestWeight = convertMAUT(maxOntology, minOntology, valueOntology);
        Double ratingWeight = convertMAUT(maxRating, minRating, valueRating);
        Double totalImportance;
        Double durationUtility;
        Double interestUtility;
        Double ratingUtility;
        if (!maxOntology.equals(minOntology)) {
            totalImportance = importanceDuration + importanceOntology + importanceRating;
            durationUtility = (importanceDuration / totalImportance) * durationWeight;
            interestUtility = (importanceOntology / totalImportance) * interestWeight;
            ratingUtility = (importanceRating / totalImportance) * ratingWeight;
            Logger.debug("durationWeight= " + durationWeight + " interestWeight= " + interestWeight + " ratingWeight= " + ratingWeight + ';');
            Logger.debug("durationUtility: " + durationUtility + "; interestUtility: " + interestUtility + "; ratingUtility= " + ratingUtility + ';');
            return durationUtility + interestUtility + ratingUtility;
        } else {
            totalImportance = importanceDuration + importanceRating;
            durationUtility = (importanceDuration / totalImportance) * durationWeight;
            ratingUtility = (importanceRating / totalImportance) * ratingWeight;
//            Logger.debug("durationWeight: " + durationWeight + "; ratingWeight: " + ratingWeight + ';');
//            Logger.debug("durationUtility: " + durationUtility + "; ratingUtility= " + ratingUtility + ';');
            return durationUtility + ratingUtility;
        }
    }


    @Contract(pure = true)
    private int convertRating(Double rating) {
        if (rating <= 2) {
            return 1;
        } else if (rating <= 3) {
            return 2;
        } else if (rating <= 4) {
            return 3;
        } else if (rating <= 5) {
            return 4;
        } else {
            return 1;
        }
    }

    @Contract(pure = true)
    private int convertDistance(Double distance) {
        if (distance <= 5000) {
            return 4;
        } else if (distance <= 15000) {
            return 3;
        } else if (distance < 25000) {
            return 2;
        } else if (distance >= 25000) {
            return 1;
        } else {
            return 1;
        }
    }

    @Contract(pure = true)
    private int convertOntology(Double ontology) {
        if (ontology >= 0.8) {
            return 4;
        } else if (ontology >= 0.6) {
            return 3;
        } else if (ontology >= 0.3) {
            return 2;
        } else if (ontology >= 0.1) {
            return 1;
        } else {
            return 1;
        }
    }

    @NotNull
    @Contract(pure = true)
    private Double multipleWeight(int paramValue, Double weightAverage) {
        return convertUtilities(paramValue) * weightAverage;
    }

    @NotNull
    @Contract(pure = true)
    private Double convertUtilities(int paramValue) {
        switch (paramValue) {
            case 1:
                return (1.0 - 1.0) / (4.0 - 1.0);
            case 2:
                return (2.0 - 1.0) / (4.0 - 1.0);
            case 3:
                return (3.0 - 1.0) / (4.0 - 1.0);
            case 4:
                return (4.0 - 1.0) / (4.0 - 1.0);
            default:
                return 0.0;
        }
    }

    public Double getUserDistance(double lat, double lng, String userDestination) {
        Double distance = 0.0;
        GeoApiContext context = new GeoApiContext();
        context.setApiKey(Config.getInstance().getAPIKey());
        LatLng location = new LatLng(lat, lng);
        DistanceMatrixApiRequest matrixApiRequest = new DistanceMatrixApiRequest(context);
        matrixApiRequest.destinations(userDestination);
        matrixApiRequest.origins(location);
        try {
            DistanceMatrix distanceMatrix = matrixApiRequest.await();
            if (distanceMatrix != null) {
                distance = (double) distanceMatrix.rows[0].elements[0].distance.inMeters;
            }
        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
//        Logger.info("destination : " + userDestination + " distance " + distance);
        return distance;
    }

    public Double getDistance(String origin, String destination, GeoApiContext context) {
        DistanceMatrixApiRequest matrixApiRequest;
        String[] origins = {origin};
        String[] destinations = {destination};
        Double result = 0.0;
        matrixApiRequest = DistanceMatrixApi.getDistanceMatrix(context, origins, destinations);
        try {
            DistanceMatrix distanceMatrix = matrixApiRequest.await();
            if (distanceMatrix != null) {
                result = (double) distanceMatrix.rows[0].elements[0].distance.inMeters;
            }
        } catch (ApiException | InterruptedException | IOException e) {
            Logger.error(e.getMessage());
        } catch (NullPointerException npe) {
            result = 0.0;
        }
        return result;
    }

    public Double getDirections(String origin, String destination, GeoApiContext context) {
        DirectionsApiRequest directionsApiRequest;
        directionsApiRequest = DirectionsApi.getDirections(context, origin, destination);
        Double result = 0.0;
        try {
            DirectionsResult directionsResult = directionsApiRequest.await();
            if (directionsResult != null) {
                result = (double) directionsResult.routes[0].legs[0].duration.inSeconds;
            }
        } catch (ApiException | IOException | InterruptedException e) {
            Logger.error(e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.error(e.getMessage());
            result = 0.0;
        }
        return result;
    }
}
