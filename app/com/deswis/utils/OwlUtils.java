package com.deswis.utils;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikhosagala on 10/06/2017.
 */
public class OwlUtils {

    @SuppressWarnings("unused")
    private static final String inputFileName = "conf/data/attractions.owl";
    @SuppressWarnings("unused")
    private static final String testFileName = "conf/data/test.owl";
    private static OwlUtils instance = null;

    public static OwlUtils getInstance() {
        if (instance == null) {
            instance = new OwlUtils();
        }
        return instance;
    }

    public static void main(String args[]) throws OWLOntologyCreationException, OWLOntologyStorageException {

    }

    public String getBase() {
        return "http://www.semanticweb.org/nikhosagala/ontologies/2017/4/destinasi";
    }

    public OWLOntologyManager create() {
        return OWLManager.createOWLOntologyManager();
    }

    @SuppressWarnings("unused")
    public OWLOntology loadOntology(OWLOntologyManager manager, String path) throws OWLOntologyCreationException {
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(path));
        System.out.println("Loaded ontology: " + ontology);
        IRI documentIRI = manager.getOntologyDocumentIRI(ontology);
        System.out.println("    from: " + documentIRI);
        return ontology;
    }

    public void saveOntology(OWLOntology ontology, OWLOntologyManager manager, String path, PrefixDocumentFormat format) throws OWLOntologyStorageException {
        File output = new File(path);
        IRI documentIRI2 = IRI.create(output);
        manager.saveOntology(ontology, format, documentIRI2);
        manager.removeOntology(ontology);
    }

    public IRI createIRI(String iri) {
        return IRI.create(iri);
    }

    @SuppressWarnings("unused")
    public OWLObjectProperty addObjectProperty(IRI iri, String propertyName) {
        return new OWLObjectPropertyImpl(IRI.create(iri + "#" + propertyName));
    }

    public OWLDataProperty addDataProperty(IRI iri, String propertyName) {
        return new OWLDataPropertyImpl(createIRI(iri + "#" + propertyName));
    }

    public OWLClass addClass(IRI iri, String className) {
        return new OWLClassImpl(IRI.create(iri + "#" + className));
    }

    public OWLNamedIndividual addIndividual(IRI iri, String individualName) {
        return new OWLNamedIndividualImpl(IRI.create(iri + "#" + individualName));
    }

    public void insertIndividual(OWLOntology ontology, OWLOntologyManager manager, OWLClass owlClass, OWLNamedIndividual owlNamedIndividual) {
        manager.addAxiom(ontology, manager.getOWLDataFactory().getOWLClassAssertionAxiom(owlClass, owlNamedIndividual));
    }

    public List<OWLClass> buildClass(IRI iri, List<String> listClass) {
        List<OWLClass> classes = new ArrayList<>();
        for (String cls : listClass) {
            classes.add(addClass(iri, cls));
        }
        return classes;
    }

    private void buildCommonRelation(OWLOntology ontology, OWLOntologyManager manager, OWLClass owlClass, OWLClass owlClass2) {
        manager.addAxiom(ontology, manager.getOWLDataFactory().getOWLSubClassOfAxiom(owlClass, owlClass2));
    }

    public void buildCommonTree(OWLOntology ontology, OWLOntologyManager manager, OWLClass owlClass, List<OWLClass> classes) {
        for (OWLClass owlClass1 : classes) {
            buildCommonRelation(ontology, manager, owlClass1, owlClass);
        }
    }

    @SuppressWarnings("unused")
    public void addClassInto(OWLOntology ontology, OWLOntologyManager manager, OWLClass owlClass) {
        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        OWLAxiom owlAxiom = dataFactory.getOWLDeclarationAxiom(owlClass);
        manager.addAxiom(ontology, owlAxiom);
    }

    @SuppressWarnings("unused")
    public void addObjectInto(OWLOntology ontology, OWLOntologyManager manager, OWLObjectProperty property) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLAxiom owlAxiom = factory.getOWLDeclarationAxiom(property);
        manager.addAxiom(ontology, owlAxiom);
    }

    public void addDataPropertyTo(OWLOntology ontology, OWLOntologyManager manager, OWLDataProperty dataProperty, OWLDatatype owlDatatype) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLDataPropertyRangeAxiom rangeAxiom = factory.getOWLDataPropertyRangeAxiom(dataProperty, owlDatatype);
        manager.addAxiom(ontology, rangeAxiom);
        OWLAxiom owlAxiom = factory.getOWLDeclarationAxiom(dataProperty);
        manager.addAxiom(ontology, owlAxiom);
    }

    public void insertHasId(OWLOntology ontology, OWLOntologyManager manager, OWLIndividual owlIndividual, OWLDataProperty dataProperty, Integer value) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLAxiom owlAxiom = factory.getOWLDataPropertyAssertionAxiom(dataProperty, owlIndividual, value);
        manager.addAxiom(ontology, owlAxiom);
    }

    public void insertHasRating(OWLOntology ontology, OWLOntologyManager manager, OWLIndividual owlIndividual, OWLDataProperty dataProperty, Double value) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLAxiom owlAxiom = factory.getOWLDataPropertyAssertionAxiom(dataProperty, owlIndividual, value);
        manager.addAxiom(ontology, owlAxiom);
    }

}
