package SOM;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;


public class SecondOrderMutantGenerator {

	private int[][] nodes;
	private int[][] edges;

	public void printCFG() { // this is only for testing
		for(int[] node : nodes)
			System.out.println(Arrays.toString(node));
		for(int[] edge : edges)
			System.out.println(Arrays.toString(edge));
	}

	public void loadCFG(String file) {
		Scanner scan = null;
		try {
			scan = new Scanner(new File(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		int numNodes = scan.nextInt();
		nodes = new int[numNodes][];
		int numEdges = scan.nextInt();
		edges = new int[numEdges][2];

		// Read the nodes
		for(int i=0; i<numNodes; i++) {
			int numStatements = scan.nextInt();
			nodes[i] = new int[numStatements];
			for(int j=0; j<numStatements; j++) {
				nodes[i][j] = scan.nextInt();
			}
		}

		// Read the edges
		for(int i=0; i<numEdges; i++) {
			edges[i][0] = scan.nextInt();
			edges[i][1] = scan.nextInt();
		}
		scan.close();
	}

	public int getSOMCategory(int mutantLine1, int mutantLine2) {
		int numNodes = nodes.length;
		int numEdges = edges.length;

		int nodeOfLine1 = getNodeNumber(mutantLine1);
		int nodeOfLine2 = getNodeNumber(mutantLine2);

		if(nodeOfLine1 == nodeOfLine2)  { // category 1 or 2
			if ((mutantLine1 == 1 + mutantLine2) || (1 + mutantLine1 == mutantLine2)) // consecutive statements in same node
				return 1; // category 1
			else 
				return 2; // category 2
		}

		if(onEdge(nodeOfLine1, nodeOfLine2)) // consecutive nodes
			return 3; // category 3
		else
			return 4; // category 4
	}

	private int getNodeNumber(int mutantLine) {
		for(int i = 0; i < nodes.length; i++) {
			for(int j = 0; j < nodes[i].length; j++) {
				if(nodes[i][j] == mutantLine) {
					return i+1; // Array indexing begins at 0, but node numbering begins at 1.
				}
			}
		}
		return -1; // did not find the node
	}

	private boolean onEdge(int node1, int node2) {
		for(int i=0; i<edges.length; i++) {
			if((edges[i][0]==node1 && edges[i][1]==node2) && (edges[i][0]==node1 && edges[i][1]==node2)) {
				return true;
			}
		}
		return false;
	}

	public int generateSecondOrderMutants(String fomLogFilePath, String fomFolderPath, String sourceFileName, String sourceFilePath, String outputFolderPath) {

		Scanner fomLogFileScanner = null; // this scans the log file generated by MuJava
		try {
			fomLogFileScanner = new Scanner(new File(fomLogFilePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}


		int category1 = 0; // mutants of category 1, initially 0
		int category2 = 0;
		int category3 = 0;
		int category4 = 0;
		int sameLineMutants = 0; // both mutants are on the same line, so SOM not created
		int numberOfFOMS = 0; // first order mutants counted so far
		int numberOfSOMS = 0; // second order mutants created so far

		int file1Nonexistant = 0;
		int file2Nonexistant = 0;
		int numberCombinationsTested = 0;

		List<String> fomNames = new ArrayList<String>();	 // stores the FOM names, used to create folders and names of SOMs
		List<Integer> lineNumbers = new ArrayList<Integer>(); // stores the FOM line numbers, also used to create SOM names

		// get the names and line numbers of FOMs and store them
		while(fomLogFileScanner.hasNextLine()) { // as long as there are more FOMs to process
			numberOfFOMS++;
			String currentFOM = fomLogFileScanner.nextLine();
			String[] parts = currentFOM.split(":");
			fomNames.add(parts[0]);
			lineNumbers.add(Integer.parseInt(parts[1]));
		}

		(new File(outputFolderPath)).mkdir(); // create the folder for output second order mutants

		String somLogFileName = new String(outputFolderPath + "second_order_mutant_log.txt");
		PrintWriter somLogFile = null;
		try {
			somLogFile = new PrintWriter(somLogFileName); // this creates a new log file for the second order mutants
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		} 


		for(int i=0; i<numberOfFOMS; i++) {

			for(int j=i+1; j<numberOfFOMS; j++) {

				numberCombinationsTested++;
				
				File file1 = new File(fomFolderPath + fomNames.get(i) + File.separator + sourceFileName);
				Scanner file1Scanner = null;
				try {
					file1Scanner = new Scanner(file1);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.exit(0);
				}

				if(!file1.exists()) { // sometimes the FOM file doesn't exist. Not sure why. MuJava issue
					file1Nonexistant++;
					break; 
				}


				File file2 = new File(fomFolderPath + fomNames.get(j) + File.separator + sourceFileName);

				int line1 = lineNumbers.get(i);
				int line2 = lineNumbers.get(j);
				if (line1 == line2) {// mutants are from the same line
					sameLineMutants++;
					continue;
				}

				if(!file2.exists()) { // sometimes the FOM file doesn't exist. Not sure why. MuJava issue
					file2Nonexistant++;
					continue;
				}

				// create a second order mutant now
				int category=0;
				numberOfSOMS++; // increment number of second order mutants created
				switch(getSOMCategory(line1, line2)) {
				case 1: category1++; category = 1; break;
				case 2: category2++; category = 2; break;
				case 3: category3++; category = 3; break;
				case 4: category4++; category = 4; break;
				}

				// create the file for the second order mutant
				String outputMutantFolderName = new String(fomNames.get(i) + "_" + line1 + "_" + fomNames.get(j) + "_" + line2);
				System.err.println("Creating SOM " + outputMutantFolderName);
				(new File(outputFolderPath+outputMutantFolderName)).mkdir(); // create the folder for this second order mutant
				String outputFileName = new String(outputFolderPath + outputMutantFolderName + File.separator + sourceFileName);
				PrintWriter outputFile = null;
				try {
					outputFile = new PrintWriter(outputFileName);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					System.exit(0);
				}


				// create an entry in the second order mutant log file
				somLogFile.println(outputMutantFolderName + ":" + category);

				// open the second FOM file for reading
				Scanner file2Scanner=null;
				try {
					file2Scanner = new Scanner(file2);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.exit(0);
				}

				// open the original file for reading
				Scanner originalFileScanner = null;
				try {
					originalFileScanner = new Scanner(new File(sourceFilePath + sourceFileName));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.exit(0);
				}

				// Assuming that no lines were deleted in either mutant
				while(originalFileScanner.hasNextLine()) {
					String originalLine = originalFileScanner.nextLine();
					String file1Line = file1Scanner.nextLine();
					String file2Line = file2Scanner.nextLine();

					if(originalLine.equals(file1Line)) {
						if(originalLine.equals(file2Line)) {
							outputFile.println(originalLine);
						} else {
							outputFile.println(file2Line);
						}

					} else {
						outputFile.println(file1Line);
					}
				}

				originalFileScanner.close();
				file2Scanner.close();
				outputFile.close();
				file1Scanner.close();
				
				// Compile the generated mutant
				System.err.println("Compiling SOM " + outputMutantFolderName);
				File sourceFile = new File(outputFileName);
				JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
				String targetFolder = outputFolderPath + outputMutantFolderName + File.separator;
				compiler.run(null, null, null, sourceFile.getPath(), "-d",  targetFolder);
			}
		}


		// close all open files
		fomLogFileScanner.close();
		somLogFile.close();

		System.err.println("Number of category 1 mutants: " + category1);
		System.err.println("Number of category 2 mutants: " + category2);
		System.err.println("Number of category 3 mutants: " + category3);
		System.err.println("Number of category 4 mutants: " + category4);
		System.err.println("Number of mutants not created because both mutants are on the same line: " + sameLineMutants);
		System.err.println("Number of times file 1 non-existant: " + file1Nonexistant);
		System.err.println("Number of times file 2 non-existant: " + file2Nonexistant);
		System.err.println("Number of combinations tested: " + numberCombinationsTested);

		return numberOfSOMS;
	}

	public static void main(String[] args) {

		// args_0 = location of CFG file
		// args_1 = location of source FOM log file
		// args_2 = location of source FOMs folder
		// args_3 = name of file to mutate
		// args_4 = path of file to mutate
		// args_5 = desired location of output SOMS
		
		args[0] =  "/Users/HJ/Documents/workspaceResearch/AFSort/cfg.txt";
		args[1] = "/Users/HJ/Documents/workspaceResearch/FOM.AFSort.Creater/result/Af.AmericanFlagSort/traditional_mutants/mutation_log";
		args[2] = "/Users/HJ/Documents/workspaceResearch/FOM.AFSort.Creater/result/Af.AmericanFlagSort/traditional_mutants/int_getMaxNumberOfDigits(java.lang.Integer[])/AmericanFlagSort.java";
		args[3] = "/Users/HJ/Documents/workspaceResearch/FOM.AFSort.Creater/result/Af.AmericanFlagSort/original";
		args[4] = "/Users/HJ/Documents/workspaceResearch/FOM.AFSort.Creater/result/Af.AmericanFlagSort/original";
		args[5] = "/Users/HJ/Documents/workspaceResearch/AFSort/SOM";

		

		SecondOrderMutantGenerator gen = new SecondOrderMutantGenerator();

		// read the CFG file and build up the nodes and edges
		gen.loadCFG(args[0]);
		//gen.printCFG();
		int total = gen.generateSecondOrderMutants(args[1], args[2], args[3], args[4], args[5]);
		System.err.println("Total number of SOMs created = " + total);

	}
}
