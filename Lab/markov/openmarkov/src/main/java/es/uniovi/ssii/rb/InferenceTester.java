package es.uniovi.ssii.rb;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.huginPropagation.HuginPropagation;
import org.openmarkov.inference.likelihoodWeighting.LikelihoodWeighting;
import org.openmarkov.inference.likelihoodWeighting.LogicSampling;
import org.openmarkov.inference.variableElimination.tasks.VEPropagation;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

// This class carries out evidence propagation on a given network printing out
// the time taken by different algorithms
public class InferenceTester {

	private ProbNet probNet;
	private Long seed;
	private Random rnd;

	public InferenceTester(String fileName) throws Exception {
		String path = "src/main/resources/networks/" + fileName;
		PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
		probNet = pgmxReader.loadProbNet(fileName, new FileInputStream(path));
		seed = null;
		rnd = new Random();
	}

	public ProbNet getProbNet() {
		return probNet;
	}

	public void setProbNet(ProbNet probNet) {
		this.probNet = probNet;
	}

	public Long getSeed() {
		return seed;
	}

	public void setSeed(Long seed) {
		this.seed = seed;
		rnd.setSeed(seed);
	}

	public long VEInference(List<Variable> variablesOfInterest, EvidenceCase evidence) {

		VEPropagation propagation = null;
		try {
			propagation = new VEPropagation(probNet);
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
		propagation.setVariablesOfInterest(variablesOfInterest);

		propagation.setPostResolutionEvidence(evidence);

		System.out.print("Variable elimination\n");
		long startTime = System.nanoTime();
		try {
			Map<Variable, TablePotential> posteriorProbabilities = propagation.getPosteriorValues();
			printProbabilities(evidence, variablesOfInterest, posteriorProbabilities);

		} catch (IncompatibleEvidenceException | NotEvaluableNetworkException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		long endTime = System.nanoTime();

		printTime(endTime - startTime);

		return (endTime - startTime);
	}

	public long HuginPropagation(List<Variable> variablesOfInterest, EvidenceCase evidence) {
		HuginPropagation propagation = null;
		try {
			propagation = new HuginPropagation(probNet);
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
		propagation.setVariablesOfInterest(variablesOfInterest);

		propagation.setPostResolutionEvidence(evidence);

		System.out.print("Variable elimination\n");
		long startTime = System.nanoTime();
		try {
			Map<Variable, TablePotential> posteriorProbabilities = propagation.getPosteriorValues();
			printProbabilities(evidence, variablesOfInterest, posteriorProbabilities);

		} catch (IncompatibleEvidenceException | UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		long endTime = System.nanoTime();

		printTime(endTime - startTime);

		return (endTime - startTime);
	}

	private long LogicSampling(List<Variable> variablesOfInterest, EvidenceCase evidence, int sampleSize) {
		LogicSampling propagation = null;
		try {
			propagation = new LogicSampling(probNet);
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
		propagation.setSampleSize(sampleSize);

		propagation.setVariablesOfInterest(variablesOfInterest);

		propagation.setPostResolutionEvidence(evidence);

		System.out.print("Variable elimination\n");
		long startTime = System.nanoTime();
		try {
			Map<Variable, TablePotential> posteriorProbabilities = propagation.getPosteriorValues();
			printProbabilities(evidence, variablesOfInterest, posteriorProbabilities);

		} catch (IncompatibleEvidenceException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		long endTime = System.nanoTime();

		printTime(endTime - startTime);

		return (endTime - startTime);
	}

	private long LikelihoodWeighting(List<Variable> variablesOfInterest, EvidenceCase evidence, int sampleSize) {
		LikelihoodWeighting propagation = null;
		try {
			propagation = new LikelihoodWeighting(probNet);
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
		propagation.setSampleSize(sampleSize);

		propagation.setVariablesOfInterest(variablesOfInterest);

		propagation.setPostResolutionEvidence(evidence);

		System.out.print("Variable elimination\n");
		long startTime = System.nanoTime();
		try {
			Map<Variable, TablePotential> posteriorProbabilities = propagation.getPosteriorValues();
			printProbabilities(evidence, variablesOfInterest, posteriorProbabilities);

		} catch (IncompatibleEvidenceException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		long endTime = System.nanoTime();

		printTime(endTime - startTime);

		return (endTime - startTime);
	}

	public static void printProbabilities(EvidenceCase evidence, List<Variable> variablesOfInterest,
			Map<Variable, TablePotential> posteriorProbabilities) {

		String evidenceString = "";
		for (Finding finding : evidence.getFindings()) {
			evidenceString += " " + finding.getVariable() + "=" + finding.getState();
		}

		for (Variable variable : variablesOfInterest) {
			TablePotential posteriorProbabilitiesPotential = posteriorProbabilities.get(variable);
			System.out.format("P( %s=%s | %s) = %.5f\n", variable, variable.getStates()[0].getName(), evidenceString,
					posteriorProbabilitiesPotential.values[0]);
		}
	}

	public static void printTime(long nanoseconds) {
		System.out.format("Total time: %.2f miliseconds\n", nanoseconds / 1000000.0);
	}

	/**
	 * Returns a evidence case picking randomly a set of variables and a random
	 * state for each one of them
	 * 
	 * @param numEvidenceVariables the number of variables in the evidence
	 * @return a evidence case
	 */
	public EvidenceCase getRandomEvidence(int numEvidenceVariables) {
		EvidenceCase evidence = new EvidenceCase();

		try {
			List<Variable> variablesToPick = probNet.getVariables();
			for (int i = 0; i < numEvidenceVariables && variablesToPick.size() > 0; i++) {
				int idx = rnd.nextInt(variablesToPick.size());
				Variable variable = variablesToPick.get(idx);
				variablesToPick.remove(idx);
				String name = variable.getName();
				String state = variable.getStates()[rnd.nextInt(variable.getNumStates())].getName();

				evidence.addFinding(probNet, name, state);
			}
		} catch (NodeNotFoundException | InvalidStateException | IncompatibleEvidenceException e) {
			e.printStackTrace();
		}

		return evidence;
	}

	/**
	 * Returns a list of variables of interest, not included in the evidence for the
	 * current network
	 * 
	 * @param numVoI   number of variables to select
	 * @param evidence the evidence
	 * @return the list of variables selected
	 */
	public List<Variable> getRandomVariablesOfInterest(int numVoI, EvidenceCase evidence) {
		List<Variable> variablesOfInterest = new ArrayList<Variable>();
		List<Variable> possibleVariables = probNet.getVariables();
		possibleVariables.removeAll(evidence.getVariables());
		for (int i = 0; i < numVoI && i < possibleVariables.size(); i++) {
			int index = rnd.nextInt(possibleVariables.size());
			variablesOfInterest.add(possibleVariables.get(index));
			possibleVariables.remove(index);
		}
		return variablesOfInterest;
	}

	public static void main(String[] args) throws Exception {

		List<InferenceTester> nets = new ArrayList<>();

		// nets.add(new InferenceTester("asia.pgmx"));
		nets.add(new InferenceTester("alarm.pgmx"));
		nets.add(new InferenceTester("Barley.pgmx"));
		nets.add(new InferenceTester("Child.pgmx"));
		nets.add(new InferenceTester("Diabetes.pgmx"));
		nets.add(new InferenceTester("insurance.pgmx"));
		nets.add(new InferenceTester("Link.pgmx"));
		nets.add(new InferenceTester("Pigs.pgmx"));
		nets.add(new InferenceTester("water.pgmx"));
		nets.add(new InferenceTester("win95pts.pgmx"));

		// Para cada una de las redes realizamos 5 ejecuciones distintas
		for (InferenceTester obj : nets) {
			long tiempoVE = 0;
			long tiempoLogicS = 0;
			long tiempoHugin = 0;
			long tiempoLikely = 0;
			for (int i = 0; i < 5; i++) {
				System.out.format("Network \"%s\" with %d nodes and %d links\n", obj.getProbNet().getName(),
						obj.getProbNet().getNumNodes(), obj.getProbNet().getLinks().size());

				EvidenceCase evidence = obj.getRandomEvidence(1);

				List<Variable> variablesOfInterest = Collections.singletonList(obj.probNet.getVariables().get(6));

				obj.setSeed(9762L);

				int sampleSize = 10000;

				if (obj.probNet.getName().toLowerCase().equals("diabetes.pgmx")) {
					tiempoHugin += ejecutarHuginPropagation(obj, variablesOfInterest, evidence);
					tiempoLogicS += ejecutarLogicSampling(obj, variablesOfInterest, evidence, sampleSize);
					tiempoLikely += ejecutarLikelihoodWeighting(obj, variablesOfInterest, evidence, sampleSize);
				} else {
					tiempoVE += ejecutarVEInference(obj, variablesOfInterest, evidence);
					tiempoHugin += ejecutarHuginPropagation(obj, variablesOfInterest, evidence);
					tiempoLogicS += ejecutarLogicSampling(obj, variablesOfInterest, evidence, sampleSize);
					tiempoLikely += ejecutarLikelihoodWeighting(obj, variablesOfInterest, evidence, sampleSize);
				}
			}
			System.out.println("----------------------------------------------------------------------------------");
			System.out.println("RED: " + obj.probNet.getName() + ", TIEMPO VE: " + (tiempoVE / 5) + ", TIEMPO HUGIN: "
					+ (tiempoHugin / 5) + ", TIEMPO LOGIC: " + (tiempoLogicS / 5) + ", TIEMPO LIKELY: "
					+ (tiempoLikely / 5));
			System.out.println("----------------------------------------------------------------------------------");
		}

//		InferenceTester obj = new InferenceTester("asia.pgmx");

//		System.out.format("Network \"%s\" with %d nodes and %d links\n", obj.getProbNet().getName(),
//				obj.getProbNet().getNumNodes(), obj.getProbNet().getLinks().size());
//
//		// asking for P(Has bronchitis=no|Dyspnoea?=yes, Smoker?=No)
//		EvidenceCase evidence = new EvidenceCase();
//		evidence.addFinding(obj.probNet, "Dyspnoea?", "yes");
//		evidence.addFinding(obj.probNet, "Smoker?", "no");
//		List<Variable> variablesOfInterest = Collections.singletonList(obj.probNet.getVariable("Has bronchitis"));
//		obj.VEInference(variablesOfInterest, evidence);
//
//		// random query
//		obj.setSeed(9762L);
//		evidence = obj.getRandomEvidence(2);
//		variablesOfInterest = obj.getRandomVariablesOfInterest(1, evidence);
//
//		obj.VEInference(variablesOfInterest, evidence);
	}

	private static long ejecutarLikelihoodWeighting(InferenceTester obj, List<Variable> variablesOfInterest,
			EvidenceCase evidence, int sampleSize) {
		return obj.LikelihoodWeighting(variablesOfInterest, evidence, sampleSize);
	}

	private static long ejecutarLogicSampling(InferenceTester obj, List<Variable> variablesOfInterest,
			EvidenceCase evidence, int sampleSize) {
		return obj.LogicSampling(variablesOfInterest, evidence, sampleSize);
	}

	private static long ejecutarHuginPropagation(InferenceTester obj, List<Variable> variablesOfInterest,
			EvidenceCase evidence) {
		return obj.HuginPropagation(variablesOfInterest, evidence);
	}

	private static long ejecutarVEInference(InferenceTester obj, List<Variable> variablesOfInterest,
			EvidenceCase evidence) {
		return obj.VEInference(variablesOfInterest, evidence);
	}

}
