import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.io.File;
import java.util.*;

public class Main{
	private static final int TARGET = 38;
	private static final String targetFilePath = "_TargetTest.csv";
	private static final String completesFilePath = "_CompletesTest.csv";
	private static final String selectedFilePath = "_SelectedTest.csv";
	
	private static long count = 1;
	private static int[] setOfIndices = null;
	private static int tarLength;
	
	public static void main(String[] args){
		loadTargets();
		loadCompletes();
		loadSelected();
		
		TargetsList.checkSelectedForExcluded(Completes.getSelected());
		TargetsList.subtractSelectedFromTargets(Completes.getSelected());
		System.out.println(Completes.getCompletes().size());
		Completes.removeSelectedFromCompletes();
		System.out.println(Completes.getCompletes().size());
		TargetsList.checkCompletesForExcluded(Completes.getCompletes());
		System.out.println(Completes.getCompletes().size());
		
		//Testing only, remove for real run
		//TargetsList.shortenTargets();
		tarLength = TargetsList.getListLength();
		
		TargetsList.initFreqArray(TARGET);
		runThroughCombs(Completes.getCompletes().size(), TARGET);
		if(setOfIndices != null){
			writeOutputFile(setOfIndices);
		}else{
			throwError("After many millenia, long after the original programmer has died,\nI can finally tell you:\nNo satisfying combinations were found :(");
		}
	}
	
	static void throwError(String msg){
		JOptionPane.showMessageDialog(new JFrame(), msg, "Error", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}
	
	private static void printArr(int[] arr){
		System.out.println(Arrays.toString(arr));
	}
	
	private static void loadTargets(){
		Scanner sc;
		try{
			sc = new Scanner(new File(targetFilePath), "latin1");
		}catch(Exception e){
			System.out.println("Error");
			return;
		}
		
		sc.nextLine();        //throw out first two lines
		sc.nextLine();
		
		//Parse
		ArrayList<String[]> lines = new ArrayList<>();
		while(sc.hasNextLine()){
			String[] line = sc.nextLine().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			
			lines.add(new String[]{line[0], line[5]});
		}
		
		//Add Targets
		String name = lines.get(0)[0];
		ArrayList<String> tarAndVar = new ArrayList<>();
		for(int i = 1; i < lines.size(); i++){
			String[] line = lines.get(i);
			if(line[1].isEmpty()){
				TargetsList.addTargetSet(name, tarAndVar);
				name = line[0];
				tarAndVar.clear();
			}else{
				tarAndVar.add(line[1]);
			}
		}
		TargetsList.addTargetSet(name, tarAndVar);			//Add last one
	}
	
	private static void loadCompletes(){
		Scanner sc;
		try{
			sc = new Scanner(new File(completesFilePath), "latin1");
		}catch(Exception e){
			System.out.println("Error");
			return;
		}
		
		if(sc.hasNextLine())
			Completes.addHeader(sc.nextLine().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1));
		
		while(sc.hasNextLine()){
			String[] line = sc.nextLine().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			Completes.addComplete(line);
		}
	}
	
	private static void loadSelected(){
		Scanner sc;
		try{
			sc = new Scanner(new File(selectedFilePath), "latin1");
		}catch(Exception e){
			System.out.println("Error");
			return;
		}
		
		sc.nextLine();		//throw out header
		
		while(sc.hasNextLine()){
			String[] line = sc.nextLine().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			Completes.addSelected(line);
		}
	}
	
	//k is the sequence length
	private static void runThroughCombs(int len, int k){
		int[] set = new int[k];							//set of indices
		
		for(int i = 0; i < k; i++){
			set[i] = i;
		}
		if(checkIfTargetsMet(set)){
			setOfIndices = set;
			return;
		}
		
		while(true){
			int tail;
			//find tail, the last item that can be incremented
			//noinspection StatementWithEmptyBody
			for(tail = k - 1; tail >= 0 && set[tail] == len - k + tail; tail--);
			if(tail < 0){
				return;		//No satisfying combinations found
			}
			set[tail]++;
			for(++tail; tail < k; tail++){
				set[tail] = set[tail - 1] + 1;
			}
			if(checkIfTargetsMet(set)){
				setOfIndices = set;
				return;
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static boolean checkIfTargetsMet2(){
		if(count == 10000000L){
			return true;
		}else{
			count++;
			return false;
		}
	}
	
	private static boolean checkIfTargetsMet(int[] set){
		Complete[] comps = Completes.makeSetFromIndices(set, TARGET);
		boolean t = TargetsList.checkIfTargetsMetWithCompletes(comps, tarLength);
		if(count % 10000000L == 0){
			System.out.println(count + " " + t);
			printArr(set);
			count++;
			return t;
		}else{
			count++;
			return t;
		}
	}
	
	private static void writeOutputFile(int[] set){
		printArr(set);
		System.out.println("WRITE!");
	}
}