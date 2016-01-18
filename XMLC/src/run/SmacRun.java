package run;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import Data.AVTable;
import IO.DataReader;
import IO.Evaluator;
import Learner.AbstractLearner;
import util.MasterSeed;

public class SmacRun {

	/*
	 * ################### SMAC Params ###################
	 */
	// // should be empty. not used, but for parsing smac stuff
	@Parameter()
	private final List<String> mainParams = new ArrayList<>();

	// just for parsing the one silly smac parameter
	@Parameter(names = "-1", hidden = true)
	private Boolean bla;
	
	
	@Parameter(names="-gamma")
	double gamma;
	
	@Parameter(names="-lambda")
	double lambda;
	
	@Parameter(names="-k")
	int k;
	
	@Parameter(names="-epochs")
	int epochs;
	
	String trainFile;
	
	String testFile;
	
	Properties properties = new Properties();

	private AVTable traindata;

	private AVTable testdata;

	
	public static void main(String[] args) {
		final SmacRun main = new SmacRun();
		final JCommander jc = new JCommander(main);
		MasterSeed.setSeed(Long.parseLong(main.mainParams.get(5)));
		main.trainFile = main.mainParams.get(0);
		main.trainFile = main.mainParams.get(1);
		jc.parse(args);
		main.run();
	}

	private void run() {
		properties.put("gamma", gamma);
		properties.put("lambda", lambda);
		properties.put("k", k);
		properties.put("epochs", epochs);
		properties.put("Learner","PLTFHRKary");
		AbstractLearner learner = AbstractLearner.learnerFactory(properties);
		
		learner.allocateClassifiers(traindata);
		learner.train(traindata);
		
		
		Map<String,Double> perftestpreck = Evaluator.computePrecisionAtk(learner, testdata, 1);
		
		for ( String perfName : perftestpreck.keySet() ) {
			System.out.println("Result for SMAC: SUCCESS, 0, 0, " + (1 - perftestpreck.get(perfName)) + ", 0");
		}
	}
	
	public void readTrainData() throws Exception {
		// reading train data
		DataReader datareader = new DataReader(trainFile, false, true);
		traindata = datareader.read();
	}

	public void readTestData() throws Exception {
		// test
		DataReader testdatareader = new DataReader(testFile,false, true);
		testdata = testdatareader.read();
	}
}