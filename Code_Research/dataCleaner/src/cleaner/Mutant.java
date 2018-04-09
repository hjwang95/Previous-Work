package cleaner;

public class Mutant {
	int cate;
	String name;
	int line1;
	int line2;
	double line1Score;
	double line2Score;
	public String toString(){
		return name+'\n'+line1+" "+line1Score+'\n'+line2+" "+line2Score;
	}
}
