package com.deswis.data;

import com.deswis.utils.CommonUtils;
import com.deswis.utils.Config;
import com.deswis.utils.OwlUtils;
import models.Destination;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by nikhosagala on 11/06/2017.
 */
public class OWLCreation {
    private static OWLCreation instance = null;
    private OwlUtils owlUtils = new OwlUtils();
    private CommonUtils commonUtils = new CommonUtils();
    private String path = Config.getInstance().getPath() + "conf//data//attractions.owl";
    private IRI iri = IRI.create(owlUtils.getBase());

    public static OWLCreation getInstance() {
        if (instance == null) {
            instance = new OWLCreation();
        }
        return instance;
    }

    public static void main(String args[]) {
        try {
            getInstance().init(0);
        } catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }

    void init(int type) throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLOntology ontology = (type == 0) ? createOntologyFromDb() : createOntologyFromExcel();
        OWLOntologyManager manager = owlUtils.create();
        OWLDataProperty dataPropertyID = owlUtils.addDataProperty(iri, "hasID");
        OWLDataProperty dataPropertyRating = owlUtils.addDataProperty(iri, "hasRating");
        OWLDatatype owlDatatypeInteger = manager.getOWLDataFactory().getOWLDatatype(OWL2Datatype.XSD_INTEGER);
        OWLDatatype owlDatatypeDouble = manager.getOWLDataFactory().getOWLDatatype(OWL2Datatype.XSD_DOUBLE);
        owlUtils.addDataPropertyTo(ontology, manager, dataPropertyID, owlDatatypeInteger);
        owlUtils.addDataPropertyTo(ontology, manager, dataPropertyRating, owlDatatypeDouble);
        insertHasId(ontology, manager, dataPropertyID);
        insertHasRating(ontology, manager, dataPropertyRating);
        saveOntology(ontology, manager, path, new RDFXMLDocumentFormat());
    }

    private void saveOntology(OWLOntology ontology, OWLOntologyManager manager, String path, PrefixDocumentFormat format) throws OWLOntologyStorageException {
        owlUtils.saveOntology(ontology, manager, path, format);
    }

    @NotNull
    @SuppressWarnings("unused")
    private List<String> creation(String createThis[]) {
        return Arrays.asList(parseStrings(createThis));
    }

    @Contract(pure = true)
    private String[] parseStrings(String list[]) {
        String result[] = new String[list.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = list[i].replace(' ', '_');
        }
        return result;
    }

    @NotNull
    @Contract(pure = true)
    private String parseString(String s) {
        return s.replace(' ', '_');
    }


    private OWLOntology createOntologyFromExcel() throws OWLOntologyCreationException {
        OWLOntologyManager manager = owlUtils.create();
        IRI iri = owlUtils.createIRI(owlUtils.getBase());
        OWLOntology ontology = manager.createOntology(iri);
        Map<String, String> map = CommonUtils.getInstance().readExcelCategory(CommonUtils.file, 1);
        map.forEach((String k, String v) -> {
            if (!"".equals(v))
                createAllClass(ontology, manager, commonUtils.parseString(k).get(0), commonUtils.parseString(v));
        });
        map = commonUtils.readExcelDestinastion(CommonUtils.file, 0);
        map.forEach((String k, String v) -> {
            if (!"".equals(k)) {
                createAllIndividual(ontology, manager, commonUtils.parseString(v), commonUtils.parseString(k).get(0));
            }
        });
        return ontology;
    }

    private OWLOntology createOntologyFromDb() throws OWLOntologyCreationException {
        OWLOntologyManager manager = owlUtils.create();
        IRI iri = owlUtils.createIRI(owlUtils.getBase());
        OWLOntology ontology = manager.createOntology(iri);
        Map<String, String> map = CommonUtils.getInstance().readCategoryFromDb();
        map.forEach((String k, String v) -> {
            if (!"".equals(v))
                createAllClass(ontology, manager, commonUtils.parseString(k).get(0), commonUtils.parseString(v));
        });
        map = commonUtils.readDestinationsFromDb();
        map.forEach((String k, String v) -> {
            if (!"".equals(k))
                createAllIndividual(ontology, manager, commonUtils.parseString(v), commonUtils.parseString(k).get(0));
        });
        return ontology;
    }

    @SuppressWarnings("unused")
    private void createIndividual(OWLOntology ontology, OWLOntologyManager manager, OWLClass owlClass, List<String> listIndividual) {
        if (listIndividual.size() == 0) {
            System.out.println("You are not creating any individual.");
        } else {
            for (String individual : listIndividual) {
                owlUtils.insertIndividual(ontology, manager, owlClass, owlUtils.addIndividual(iri, individual));
            }
        }
    }

    private void createAllClass(OWLOntology ontology, OWLOntologyManager manager, String classNameParent, List<String> classNameChild) {
        OWLClass owlClass = owlUtils.addClass(iri, classNameParent);
        owlUtils.buildCommonTree(ontology, manager, owlClass, owlUtils.buildClass(iri, classNameChild));
//        owlUtils.allDisjointClass(ontology, manager, owlUtils.buildClass(iri, classNameChild));
    }

    private void createAllIndividual(OWLOntology ontology, OWLOntologyManager manager, List<String> owlClasses, String individual) {
        for (String s : owlClasses) {
            OWLClass owlClass = owlUtils.addClass(iri, s);
            owlUtils.insertIndividual(ontology, manager, owlClass, owlUtils.addIndividual(iri, individual));
        }
    }

    private void insertHasId(OWLOntology ontology, OWLOntologyManager manager, OWLDataProperty dataProperty) {
        for (Destination destination : Destination.find.all()) {
            OWLNamedIndividual owlNamedIndividual = owlUtils.addIndividual(iri, parseString(destination.name));
            owlUtils.insertHasId(ontology, manager, owlNamedIndividual, dataProperty, destination.id);
        }
    }

    private void insertHasRating(OWLOntology ontology, OWLOntologyManager manager, OWLDataProperty dataProperty) {
        for (Destination destination : Destination.find.all()) {
            OWLNamedIndividual owlNamedIndividual = owlUtils.addIndividual(iri, parseString(destination.name));
            if (destination.rating != null) {
                owlUtils.insertHasRating(ontology, manager, owlNamedIndividual, dataProperty, destination.rating);
            }
        }
    }
}
