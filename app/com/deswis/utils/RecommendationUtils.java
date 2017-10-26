package com.deswis.utils;

import models.Destination;
import models.Recommendation;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import play.Logger;

import java.io.InputStream;
import java.util.*;

/**
 * Created by nothere on 23/05/2017.
 */
public class RecommendationUtils {
    private static final String prefix = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"
            + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
            + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
            + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
            + "PREFIX base: <http://www.semanticweb.org/nikhosagala/ontologies/2017/4/destinasi#>";
    private static final String base = "http://www.semanticweb.org/nikhosagala/ontologies/2017/4/destinasi#";
    private static final String inputFileName = "conf/data/attractions.owl";

    private static RecommendationUtils instance = null;

    public static RecommendationUtils getInstance() {
        if (instance == null) {
            instance = new RecommendationUtils();
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
//        System.out.println(getInstance().parseResult(getInstance().getFirstChild()));
        Map<String, Double> map = new TreeMap<>();
        map = getInstance().assignInterestValue(map, "Alam", 60);
        map = getInstance().assignInterestValue(map, "Taman", 30);
        getInstance().printHash(map, 0);
    }

    // Start Query to OWL Files

    List<QuerySolution> getChild(String node) {
        String query = prefix + "SELECT ?child  WHERE { ?child rdfs:subClassOf base:" + node.replace(' ', '_') + " }";
        return executeQuery(query);
    }

    private List<QuerySolution> getParent(String node) {
        String query = prefix + "SELECT ?parent  WHERE { base:" + node.replace(' ', '_') + " rdfs:subClassOf ?parent }";
        return executeQuery(query);
    }

    private List<QuerySolution> getIndividual(String node) {
        String query = prefix + "SELECT ?individu  WHERE { ?individu rdf:type base:"
                + node.replace(' ', '_') + "}";
        return executeQuery(query);
    }

    @SuppressWarnings("unused")
    List<QuerySolution> getFirstChild() {
        return getChild("Destinasi Wisata");
    }

    @SuppressWarnings("unused")
    private List<QuerySolution> getAllIndividual() {
        String query = prefix + "SELECT *  WHERE { ?name rdf:type owl:NamedIndividual }";
        return executeQuery(query);
    }

    // End of Query to OWL Files

    @SuppressWarnings("unchecked")
    // assign value from user input to Ontology
    public Map assignInterestValue(Map<String, Double> map, String node, double interestValue) {
        double equal;
//        interestValue = interestValue / 100;
        List<QuerySolution> child = getChild(node);
        Map pair;
        if (map != null) {
            pair = map;
        } else {
            pair = new HashMap();
        }
        if (child.size() != 0 && interestValue != 0) {
            equal = interestValue / child.size();
//            Logger.info("real value= " + interestValue + " " + equal);
            for (String s : parseResult(child)) {
                if (pair.containsKey(s)) {
                    pair.put(s, (double) pair.get(s) + equal);
                } else {
                    pair.put(s, equal);
                }
                assignInterestValue(pair, s, equal);
            }
        }
        return pair;
    }

    @SuppressWarnings("unused")
    public Map assignPreferenceValue(Map<String, Double> interestValueMap, Map<String, Double> preferenceValueMap, double alpha) {
        interestValueMap = CommonUtils.getInstance().sortByValue(interestValueMap);
        interestValueMap.forEach((k, v) -> {
            Double preferenceValue = 0.0;
            int counter = 0;
            for (String s : parseResult(getParent(k))) {
//                Logger.info("parent= " + s + " " + resultPreferenceValueMap.get(s));
                if (preferenceValueMap.get(s) != null) {
                    counter++;
                    preferenceValue += preferenceValueMap.get(s);
                }
            }
            counter = counter > 0 ? counter : 1;
            preferenceValue = (preferenceValue / counter) - alpha;
            if (!preferenceValueMap.containsKey(k)) {
                preferenceValueMap.put(k, preferenceValue);
            }
//            Logger.info("key= " + k + " preference= " + resultPreferenceValueMap.get(k) + " " + counter);
        });
        return preferenceValueMap;
    }

    public Map assignInterestValueParent(Map<String, Double> map, String node, double interestValue) {
        Map<String, Double> interestValueMap;
//        interestValue = interestValue / 100;
        if (map != null) {
            interestValueMap = map;
        } else {
            interestValueMap = new TreeMap<>();
        }
        if (interestValueMap.containsKey(node)) {
            interestValueMap.put(node, interestValueMap.get(node) + interestValue);
        } else {
            interestValueMap.put(node, interestValue);
        }

        return interestValueMap;
    }

    @SuppressWarnings("unused")
    public Map assignPreferenceValueParent(Map<String, Double> map, String node, double preferenceValue) {
        Map<String, Double> preferenceValueMap;
        if (map != null) {
            preferenceValueMap = map;
        } else {
            preferenceValueMap = new TreeMap<>();
        }
        preferenceValueMap.put(node, preferenceValue);
        return preferenceValueMap;
    }

    @SuppressWarnings("unchecked")
    // get all destination name that contain categories from user input
    private Set<String> getAllDestinations(Map<String, Double> map) {
        Set<String> destinations = new HashSet<>();
        map.forEach((k, v) -> destinations.addAll(parseResult(getIndividual(k))));
        return destinations;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Double> propagateValue(Map<String, Double> interestValueMap) {
        Set<String> fromOntology = getAllDestinations(interestValueMap);
        Map<String, Double> newMap = new TreeMap();
//        Logger.error("Mulai Propagasi Nilai Interest ke Destinasi Wisata");
        for (String destinationName : fromOntology) {
            Double value = 0.0;
            Destination destination = Destination.find.where().eq("name", destinationName).findUnique();
            for (String b : destination.categories.split(";")) {
                if (interestValueMap.get(b) != null) {
                    value += interestValueMap.get(b);
                }
            }
            Logger.warn(destinationName);
            Logger.debug(destination.categories + " value: " + value);
            newMap.put(destinationName, value);
        }
        return CommonUtils.getInstance().sortByValue(newMap);
    }

    @SuppressWarnings("unused")
    private Set<String> getCategories(List<String> categories) {
        Set<String> result = new HashSet<>();
        Map<String, String> temporary = new TreeMap<>();
        for (String a : categories) {
            for (String b : parseResult(getParent(a))) {
                temporary.put(b, a);
            }
        }
        temporary.forEach((k, v) -> result.add(v));
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Destination> getRecommedations(Map<String, Double> map, Recommendation recommendation) {
        List<Destination> destinations = new ArrayList<>();
        Map<String, Double> newMap = getMaut(map, recommendation);
        newMap.forEach((k, v) -> {
            Destination destination = Destination.find.where().eq("name", k).findUnique();
            if (destination != null && v != 0 && v > recommendation.threshold) {
                destinations.add(destination);
                Logger.error(destination.name + " " + v);
            }
        });
        return destinations;
    }

    @SuppressWarnings("unchecked, unused")
    private Map<String, Double> getSmart(Map<String, Double> map, Double durationImportance, Double ontologyImportance, Double ratingImportance) {
        Logger.info("Value after doing SMART");
        map.forEach((k, v) -> {
            Destination destination = Destination.find.where().eq("name", k).findUnique();
            Double score = CommonUtils.getInstance().SMART(destination.duration, v, destination.rating, durationImportance, ontologyImportance, ratingImportance);
            map.put(destination.name, score);
        });
        return CommonUtils.getInstance().sortByValue(map);
    }

    private Map<String, Double> getMaut(Map<String, Double> map, Recommendation recommendation) {
        Map<String, Double> distanceMap = new TreeMap<>();
        Map<String, Double> durationMap = new TreeMap<>();
        Map<String, Double> interestMap = new TreeMap<>();
        Map<String, Double> ratingMap = new TreeMap<>();
        Map<String, Double> mautMap = new TreeMap<>();
        map.forEach((k, v) -> {
            Destination destination = Destination.find.where().eq("name", k).findUnique();
//            if (recommendation.lat != null || recommendation.lng != null) {
//                Double distance = CommonUtils.getInstance().getUserDistance(recommendation.lat, recommendation.lng, destination.name);
//                Logger.debug("user using location: " + destination.name + " distance (local, user) = (" + destination.distance + ", " + distance + ')');
//                distanceMap.put(destination.name, distance);
//                durationMap.put(destination.name, destination.duration);
//                interestMap.put(destination.name, v);
//                ratingMap.put(destination.name, destination.rating);
//            } else {
            distanceMap.put(destination.name, destination.distance);
            durationMap.put(destination.name, destination.duration);
            interestMap.put(destination.name, v);
            ratingMap.put(destination.name, destination.rating);
//            }
        });
        Double maxDistance = Collections.max(distanceMap.values());
        Double minDistance = Collections.min(distanceMap.values());
        Double maxDuration = Collections.max(durationMap.values());
        Double minDuration = Collections.min(durationMap.values());
        Double maxInterest = Collections.max(interestMap.values());
        Double minInterest = Collections.min(interestMap.values());
        Double maxRating = Collections.max(ratingMap.values());
        Double minRating = Collections.min(ratingMap.values());
        Logger.error("max : " + maxDuration + " " + maxInterest + " " + maxRating + "; min : " + minDuration + " " + minInterest + " " + minRating);
//        Logger.error("Mulai Proses Maut");
        map.forEach((k, v) -> {
            Destination destination = Destination.find.where().eq("name", k).findUnique();
            Logger.warn(destination.name);
            Logger.debug("distanceValue: " + distanceMap.get(destination.name) + " duration value: " + durationMap.get(destination.name) + "; interestValue: " + interestMap.get(destination.name) + "; ratingValue: " + ratingMap.get(destination.name) + ';');
            Double maut = CommonUtils.getInstance().MAUT(minDuration, maxDuration, maxInterest, minInterest, maxRating, minRating, durationMap.get(destination.name), v, ratingMap.get(destination.name), recommendation.durationWeight, recommendation.interestWeight, recommendation.ratingWeight);
            Logger.debug("total utility: " + maut);
            mautMap.put(destination.name, maut);
        });
        return CommonUtils.getInstance().sortByValue(mautMap);
    }

    @SuppressWarnings("unchecked")
    public void printHash(Map<String, Double> map, int type) {
        map = CommonUtils.getInstance().sortByValue(map);
        map.forEach((k, v) -> {
            switch (type) {
                case 0:
                    Logger.info("categories: " + k + "; value: " + v);
                    break;
                case 1:
                    Logger.info("destination: " + k + "; value: " + v);
                    break;
                default:
                    Logger.info("key: " + k + "; value: " + v);
                    break;
            }
        });
    }

    List<String> parseResult(List<QuerySolution> querySolutions) {
        List<String> list = new ArrayList<>();
        for (QuerySolution querySolution : querySolutions) {
            String s = querySolution.toString().replaceAll(base, "");
            String last = s.replace('_', ' ').substring(s.lastIndexOf('<') + 1, s.lastIndexOf(')') - 2);
            list.add(last);
        }
        return list;
    }

    private List<QuerySolution> executeQuery(String q) {
        Model model = getModel();
        List<QuerySolution> solutions = new ArrayList<>();
        Query query = QueryFactory.create(q);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet resultSet = qe.execSelect();
        solutions.addAll(ResultSetFormatter.toList(resultSet));
        return solutions;
    }

    public Model getModel() {
        Model model = ModelFactory.createDefaultModel();
        // use the FileManager to find the input file
        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException("File: " + inputFileName + " not found");
        }
        // read the RDF/XML file
        model.read(in, null);
        return model;
    }

}
