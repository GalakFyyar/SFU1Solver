import java.util.ArrayList;

class TargetsList{
	private static ArrayList<TargetSet> targetList = new ArrayList<>();
	private static int[][] frequencyReached;
	
	static void addTargetSet(String qName, ArrayList<String> tarsAndVars){
		int[] targetSet = new int[tarsAndVars.size()];
		int[] variabilitySet = new int[tarsAndVars.size()];
		int[][] minAndMax = new int[tarsAndVars.size()][2];
		
		int i =  0;
		for(String tarAndVarRaw : tarsAndVars){
			if(tarAndVarRaw.contains("\u00b1")){
				String[] tarAndVar = tarAndVarRaw.split(" \u00b1 ");
				int tar = Integer.parseInt(tarAndVar[0]);
				int var = Integer.parseInt(tarAndVar[1]);
				targetSet[i] = tar;
				variabilitySet[i] = var;
				minAndMax[i][0] = tar - var;
				minAndMax[i][1] = tar + var;
			}else{
				int tar = Integer.parseInt(tarAndVarRaw);
				targetSet[i] = tar;
				variabilitySet[i] = 0;
				minAndMax[i][0] = tar;
				minAndMax[i][1] = tar;
			}
			i++;
		}
		
		targetList.add(new TargetSet(qName, targetList.size(), targetSet, variabilitySet, minAndMax));
	}
	
	//Completes that have a code that the target is 0 for, should be deleted.
	//This function searches the target sets for 0s and then checks if a Selected complete has that code, that means something is really wrong, throw an error.
	static void checkSelectedForExcluded(ArrayList<Complete> selected){
		for(int i = 0; i < targetList.size(); i++){
			int[] targetSet = targetList.get(i).targetSet;
			for(int j = 0; j < targetSet.length; j++){
				if(targetSet[j] == 0){						//Found a target of 0, meaning completes with this code should be excluded
					//System.out.println(targetList.get(i).qName);
					for(Complete selectedComplete : selected){
						//System.out.println(selectedComplete.qFields[i]);
						if(selectedComplete.qFields[i] == j+1){
							//System.out.println("Uh oh");
							Main.throwError("Complete " + selectedComplete.id + " should not have been selected.\nIt has a code we are trying to exclude.");
						}
					}
				}
			}
		}
	}
	
	//Completes that have a code that the target is 0 for, should be deleted.
	//This function searches the target sets for 0s and then checks if a complete has that code, if so, we delete it.
	static void checkCompletesForExcluded(ArrayList<Complete> completes){
		for(int i = 0; i < targetList.size(); i++){
			int[][] minAndMax = targetList.get(i).minAndMax;
			for(int j = 0; j < minAndMax.length; j++){
				if(minAndMax[j][0] == 0 && minAndMax[j][1] == 0){				//Found a target of 0, meaning completes with this code should be excluded
					int finalI = i;
					int finalJ = j;
					completes.removeIf(complete -> complete.qFields[finalI] == finalJ +1);
				}
			}
		}
	}
		
	static void subtractSelectedFromTargets(ArrayList<Complete> selected){
		for(Complete selectedComplete : selected){
			//System.out.println(selectedComplete.id);
			//System.out.println(Arrays.toString(selectedComplete.qFields));
			final int len = selectedComplete.qFields.length;			//Number of questions
			for(int i = 0; i < len; i++){
				//System.out.println(targetList.get(i).qName);
				int code = selectedComplete.qFields[i];
				int index = code - 1;
				TargetSet tar = targetList.get(i);
				//System.out.println(tar.targetSet[index] + " -> " + tar.minAndMax[index][0] + "-" + tar.minAndMax[index][1] + "\t" + code);
				tar.targetSet[index] -= 1;
				if(tar.minAndMax[index][0] == 0 && tar.minAndMax[index][1] == 0)
					Main.throwError("Selected complete " + selectedComplete.id + " overfills the targets");
				if(tar.minAndMax[index][0] > 0)            //only decrement min or max if they're above zero
					tar.minAndMax[index][0] -= 1;
				if(tar.minAndMax[index][1] > 0)
					tar.minAndMax[index][1] -= 1;
				//System.out.println(tar.targetSet[index] + " -> " + tar.minAndMax[index][0] + "-" + tar.minAndMax[index][1] + "\t" + code);
			}
		}
	}
	
	//Fully initialized freq array to avoid object creation during checkIfTargetsMetWithCompletes()
	static void initFreqArray(int len){
		frequencyReached = new int[len][];
		for(int i = 0; i < targetList.size(); i++){
			TargetSet targetSet = targetList.get(i);
			frequencyReached[i] = new int[targetSet.targetSet.length];
		}
	}
	
	static boolean checkIfTargetsMetWithCompletes(Complete[] comps, int tarLen){
		for(Complete comp : comps){
			for(int j = 0; j < tarLen; j++){
				int code = comp.qFields[j] - 1;			//optimise later make code -= 1 so we can use it as the index
				int freq = ++frequencyReached[j][code];
				if(freq > targetList.get(j).minAndMax[code][1])
					return false;						//max exceeded
			}
		}
		return true;
	}
	
	//For testing
	@SuppressWarnings("unused")
	static void printTargetsList(){
		for(TargetSet ts : targetList){
			System.out.println(ts.qName);
			for(int i = 0; i < ts.targetSet.length; i++){
				System.out.println(ts.targetSet[i] + " -> " + ts.minAndMax[i][0] + "-" + ts.minAndMax[i][1]);
			}
		}
	}
	
	//For testing
	@SuppressWarnings("unused")
	static void shortenTargets(){
		ArrayList<TargetSet> newList = new ArrayList<>();
		for(int i = 0; i < 2; i++){
			newList.add(targetList.get(i));
		}
		targetList = newList;
	}
	
	static int getListLength(){
		return targetList.size();
	}
	
	private static class TargetSet{
		String qName;
		int id;
		int[] targetSet;
		int[] variability;
		int[][] minAndMax;
		
		TargetSet(String qName, int id, int[] targetSet, int[] variability, int[][] minAndMax){
			this.qName = qName;
			this.id = id;
			this.targetSet = targetSet;
			this.variability = variability;
			this.minAndMax = minAndMax;
		}
	}
}