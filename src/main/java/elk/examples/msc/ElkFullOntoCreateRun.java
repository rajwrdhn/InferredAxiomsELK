package elk.examples.msc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
/**
 * Ontology creation for Inferred Axioms 
 * Subsumption
 * ClassAssertion
 * Equivalence (although not required)
 * @author raj
 *
 */
public class ElkFullOntoCreateRun {
	public static int count =0;
	public static NodeSet<OWLClass> axi;
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
		StopWatch timer = new StopWatch();
		timer.start("Start ELK reasoning! ");
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	    OWLDataFactory dataFactory = man.getOWLDataFactory();
	    System.out.println("load");
	    // Load your ontology.
	    OWLOntology ont = man.loadOntologyFromOntologyDocument(new File("/home/raj/Documents/thesis/artist.owl"));
	    
	    OWLOntologyManager outputOntologyManager = OWLManager.createOWLOntologyManager();
	    
	    
	    // Create an ELK reasoner.
	    System.out.println("start!");
	    OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
	    OWLReasoner reasoner = reasonerFactory.createReasoner(ont);
	    
	    reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
	    //ont.classesInSignature().forEach(x -> System.out.println(x));
	    
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		gens.add(new InferredClassAssertionAxiomGenerator());
		gens.add(new InferredSubClassAxiomGenerator());
		gens.add(new InferredEquivalentClassAxiomGenerator());
		
		// Put the inferred axioms into a fresh empty ontology.
		OWLOntology infOnt = outputOntologyManager.createOntology();
		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner,
				gens);
		iog.fillOntology(outputOntologyManager.getOWLDataFactory(), infOnt);

		// Save the inferred ontology.
		outputOntologyManager.saveOntology(infOnt,
				IRI.create((new File("/home/raj/onto_elk.owl").toURI())));
	    
/*	    ont.classesInSignature().forEach(x -> {
	    	axi = reasoner.getSubClasses(x);
	    	System.out.println(axi);
	    });*/
/*	    for (Node<OWLClass> cls: axi) {
	    	System.out.println(cls);
	    }*/
	    reasoner.dispose();
	    timer.stop("finished!!");
	}
}
