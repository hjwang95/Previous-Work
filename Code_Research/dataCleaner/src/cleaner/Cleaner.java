package cleaner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.awt.List;


public class Cleaner {
	private int[][] nodes;
	private int[][] edges;
	public static ArrayList<Mutant> my;
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
	private String getScoreCategory(String name, double line1Score, double line2Score, int category) {
		

		if(line1Score == line2Score){
			if(line1Score == 1){
				return "A";
			}
			else if(1 >line1Score&&line1Score > 0.5){
				return "B";
			}
			else{
				return "C";
			}
		}
		else if(line1Score != line2Score){
			return "D";
		}
		else{return "E";}

	}
	
	private static boolean alreadyHave(ArrayList<Mutant> myLibraryF, Mutant mutant) {
		int c = 0;
		while(c < myLibraryF.size()){
			if(myLibraryF.get(c).name.compareTo(mutant.name) == 0){
				return true;
			}
			c++;
		}
		return false;
		
	}
	
	
	
	
	
	
	public static  void CleanRoundOne(File f) throws IOException{
		Scanner s = new Scanner(f);
		File out = new File("RoundOne.txt");
	    PrintWriter w = new PrintWriter(new FileWriter(out));
		String nextLine ;
		int c = 0;
		while(s.hasNextLine() || c< 507857){
			nextLine = s.nextLine();
			if(nextLine.contains("classify") || nextLine.contains("SOM")){
				w.println(nextLine);

			}
			else{

			}
			

			c++;
		}
	}
	
	public static  void CleanRoundTwo(File RoundOne) throws IOException {
		Scanner s = new Scanner(RoundOne); 
		String nextLine ;
		double line1Score;
		double line2Score;
		File res = new File("RoundTwo.txt");
	    PrintWriter w = new PrintWriter(new FileWriter(res));
		
		Scanner s2 = new Scanner(res); 
		String nextLine2 ;
		
		while(s.hasNext()){
			nextLine = s.nextLine();
			if(nextLine.contains("(")){

				nextLine = nextLine.replaceAll("\\(", " ");
				nextLine = nextLine.replaceAll("\\:", " "); 
				nextLine = nextLine.replaceAll("\\,", " "); 


				//System.out.println(nextLine);
				w.println(nextLine);
			}
			else{
				w.println(nextLine);

			}
			
		}
		
		while(s2.hasNextLine() ){
			nextLine2 = s2.nextLine();
			if(nextLine2.length()==0){
				continue;
			}
			if(nextLine2.contains("SOM") == true){
				//w.println(nextLine2);

			}
			else{
				Scanner sGetInt = new Scanner(nextLine2);
				nextLine2.split(" ");
				sGetInt.next();

				sGetInt.next();



				//line1Score = sGetInt.nextDouble();
				//line2Score = sGetInt.nextDouble();

				//System.out.print(line1Score);
				//System.out.print(line2Score);

			}
		}
		
		
	}

	private static void CleanRoundThree(File RoundTwo) throws IOException {

			Scanner s2 = new Scanner(RoundTwo); 
			String nextLine2;
			File res = new File("RoundThree.txt");

		    PrintWriter w = new PrintWriter(new FileWriter(res));

			while(s2.hasNextLine() ){
				nextLine2 = s2.nextLine();
				if(nextLine2.length()==0){
					continue;
				}
				if(nextLine2.contains("SOM") == true){
					nextLine2=nextLine2.replaceAll("\\_", " ");

					w.println(nextLine2.substring(nextLine2.indexOf("M")+2, nextLine2.length()));
					//System.out.println(nextLine2);
				}
				else{
					Scanner sGetInt = new Scanner(nextLine2);
					nextLine2.split(" ");
					sGetInt.next();
					w.print(sGetInt.next());
					w.print("   ");


					//System.out.println(sGetInt.next());
					sGetInt.next();
					w.println(sGetInt.next());

				

				}
			}	
			
			
			
			
			
		}
	
	private  void CleanRoundFive(File RoundFour) throws IOException {
			Scanner s = new Scanner(RoundFour);

			String temp ;int c=0;int c2 =0; String line1temp;String line2temp;
			//Mutant m = new Mutant();
			ArrayList<Mutant> myLibrary = new ArrayList<Mutant>();
			
			while(s.hasNextLine()){
				Mutant m = new Mutant();

				temp = s.nextLine();
				line1temp = s.nextLine();line1temp = line1temp.replace(",", " ");
				line2temp = s.nextLine();line2temp = line2temp.replace(",", " ");

					m.name = temp;
					
					Scanner sL1 = new Scanner(line1temp);
					sL1.next();
					m.line1 = Integer.parseInt(sL1.next());
					sL1.next();
					m.line1Score = Double.parseDouble(sL1.next());
					
					Scanner sL2 = new Scanner(line2temp);
					sL2.next();
					m.line2 = Integer.parseInt(sL2.next());
					sL2.next();
					m.line2Score = Double.parseDouble(sL2.next());
					m.cate = Integer.parseInt(m.name.substring(m.name.length()-1, m.name.length()));
					myLibrary.add(m);
					//System.out.println(m.toString());

				
			}
			ArrayList<Mutant> myLibraryF = new ArrayList<Mutant>();
			int ct = myLibrary.size()-1; int ctF =0;
			while(ct >0){
				if(alreadyHave(myLibraryF,myLibrary.get(ct)) == false){
					ctF++;
					myLibraryF.add(myLibrary.get(ct));
					}
				ct--;
			}

			System.out.println(myLibraryF.get(2).toString());
			
			String csvFile = "Clean4.csv";
	        FileWriter writer = new FileWriter(csvFile);
			for(int i = 0;i< myLibraryF.size();i++){
				CSV.writeLine(writer, Arrays.asList(myLibraryF.get(i).toString()));

				
			}

			my = myLibraryF;
			
			

			
		}
	
	
	

	public static void main(String[] args) throws IOException {
		Cleaner cl = new Cleaner();
		
		//SuspiciousScoreReport.txt
		File SuspiciousScoreReport = new File(args[0]);
		cl.CleanRoundOne(SuspiciousScoreReport);
		
		//RoundONe.txt
		File RoundOne = new File(args[1]);
		//CleanRoundTwo(RoundOne);
		
		//RoundTwo.txt
		File RoundTwo = new File(args[2]);
	//CleanRoundThree(RoundTwo);
		
		//RoundThree.txt
		
		File RoundThree = new File(args[3]);
		cl.loadCFG(args[4]);
		cl.CleanRoundFour(RoundThree);

		
	}

	private void CleanRoundFour(File roundThree) throws IOException {
		Scanner sA = new Scanner(roundThree);
		Scanner sB = new Scanner(roundThree);
		String name = null;
		int line1 = 0; double line1Score = 2;
		int line2 = 0; double line2Score = 2;
		int category = 0;
        int category1 = 0;int category2 = 0;int category3 = 0;int category4 = 0;

		int category1A = 0;int category1B = 0;int category1C = 0;int category1D = 0;
        int category2A = 0;int category2B = 0;int category2C = 0;int category2D = 0;
        int category3A = 0;int category3B = 0;int category3C = 0;int category3D = 0;
        int category4A = 0;int category4B = 0;int category4C = 0;int category4D = 0;
		
		
		
		String csvFile = "Clean3.csv";
        FileWriter writer = new FileWriter(csvFile);

		String temp; String tempB; int c =0;
		sB.nextLine();
		while(sA.hasNextLine()){
			
			temp = sA.nextLine();

			if( Character.isLetter(temp.charAt(0) )){
				
				name = temp;
				Scanner sLine = new Scanner(temp);
				sLine.next();
				sLine.next();
				line1 = Integer.parseInt(sLine.next());
				sLine.next();
				sLine.next();
				line2 = Integer.parseInt(sLine.next());
				
			}
		
			else{
				Scanner sLineInt = new Scanner(temp);
				int tempLine1 =Integer.parseInt(sLineInt.next());
				
				if( tempLine1 !=line1 &&tempLine1 !=line2){
					

				}
				else{
					if(tempLine1 == line1){
						line1Score =Double.parseDouble( sLineInt.next());
					}
					else{
						line2Score =Double.parseDouble( sLineInt.next());
					}
					
					if(line1Score <2 && line2Score <2){
						CSV.writeLine(writer, Arrays.asList(name + "  "+getSOMCategory(line1, line2)));
				        CSV.writeLine(writer, Arrays.asList("Line",String.valueOf(line1)," Score ",Double.toString(line1Score)) );
				        CSV.writeLine(writer, Arrays.asList("Line",String.valueOf(line2)," Score ",String.valueOf(line2Score)) );
				        switch(getSOMCategory(line1, line2)) {
						case 1: category1++; category = 1; break;
						case 2: category2++; category = 2; break;
						case 3: category3++; category = 3; break;
						case 4: category4++; category = 4; break;
						}	
				        

				        if(getSOMCategory(line1, line2) == 1 ){
				        	if(getScoreCategory(name,line1Score, line2Score, 1).compareTo("A") ==0 ){
				        		category1A++;
				        	}
				        	else if(getScoreCategory(name,line1Score, line2Score, 1).compareTo("B") ==0){
				        		category1B++;
				        	}
				        	else if(getScoreCategory(name,line1Score, line2Score, 1).compareTo("C") ==0){
				        		category1C++;
				        	}
				        	else if(getScoreCategory(name,line1Score, line2Score, 1).compareTo("D") ==0){
				        		category1D++;
				        	}
				        	else{}
				        }
				        else if(getSOMCategory(line1, line2) == 2  ){
				        	if(getScoreCategory(name,line1Score, line2Score, 2).compareTo("A") ==0 ){
				        		category2A++;
				        	}
				        	else if(getScoreCategory(name,line1Score, line2Score, 2).compareTo("B") ==0){
				        		category2B++;
				        	}
				        	else if(getScoreCategory(name,line1Score, line2Score, 2).compareTo("C") ==0){
				        		category2C++;
				        	}
				        	else if(getScoreCategory(name,line1Score, line2Score, 2).compareTo("D") ==0){
				        		category2D++;
				        	}
				        	else{}
				        }
				        else if(getSOMCategory(line1, line2) == 3 ){
				        	if(getScoreCategory(name,line1Score, line2Score,3).compareTo("A") ==0 ){
				        		category3A++;
				        	}
				        	else if(getScoreCategory(name,line1Score, line2Score, 3).compareTo("B") ==0){
				        		category3B++;
				        	}
				        	else if(getScoreCategory(name,line1Score, line2Score, 3).compareTo("C") ==0){
				        		category3C++;
				        	}
				        	else if(getScoreCategory(name,line1Score, line2Score, 3).compareTo("D") ==0){
				        		category3D++;
				        	}
				        	else{}
				        } else if(getSOMCategory(line1, line2) == 4 ){
				        	if(getScoreCategory(name,line1Score, line2Score, 4).compareTo("A") ==0 ){
				        		category4A++;
				        	}
				        	else if(getScoreCategory(name,line1Score, line2Score, 4).compareTo("B") ==0){
				        		category4B++;
				        	}
				        	else if(getScoreCategory(name,line1Score, line2Score, 4).compareTo("C") ==0){
				        		category4C++;
				        	}
				        	else if(getScoreCategory(name,line1Score, line2Score, 4).compareTo("D") ==0){
				        		category4D++;
				        	}
				        	else{}
				        }
				        else{}
				        
				        
				        
				        
				        
				        
				        
				        
				        
					}
					
					
					
					
				}
						
	}
		}
		
		
		
		
		
		
		
		System.out.println("1: "+category1);
		System.out.println("S1: "+category1A+" S2: "+category1B+" S3: "+category1C+" S4: "+category1D);
		System.out.println();

		System.out.println("2: "+category2);
		System.out.println("S1: "+category2A+" S2: "+category2B+" S3: "+category2C+" S4: "+category2D);
		System.out.println();

		
		System.out.println("3: "+category3);
		System.out.println("S1: "+category3A+" S2: "+category3B+" S3: "+category3C+" S4: "+category3D);
		System.out.println();
		
		System.out.println("4: "+category4);
		System.out.println("S1: "+category4A+" S2: "+category4B+" S3: "+category4C+" S4: "+category4D);
		System.out.println();

		
		
	}
	
	

	

}
