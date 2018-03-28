import java.util.ArrayList;

class Completes{
	private static ArrayList<Complete> completes = new ArrayList<>();
	private static ArrayList<Complete> selected = new ArrayList<>();
	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private static String[] header;
	
	static void addHeader(String[] aHeader){
		header = aHeader;
	}
	
	static void addComplete(String[] fields){
		int[] qFields = parseQFields(fields, "A complete has a non-number for a question response.");
		completes.add(new Complete(Integer.parseInt(fields[0]), qFields));	//todo: minus one here
	}
	
	static void addSelected(String[] fields){
		int[] qFields = parseQFields(fields, "A selected complete has a non-number for a question response.");
		selected.add(new Complete(Integer.parseInt(fields[0]), qFields));
	}
	
	static ArrayList<Complete> getSelected(){
		return selected;
	}
	
	static ArrayList<Complete> getCompletes(){
		return completes;
	}
	
	static void removeSelectedFromCompletes(){
		for(Complete selectedComplete : selected){
			completes.removeIf(complete -> complete.id == selectedComplete.id);
		}
	}
	
	static Complete[] makeSetFromIndices(int[] indices, int len){
		Complete[] c = new Complete[len];
		for(int i = 0; i < len; i++){
			c[i] =completes.get(indices[i]);
		}
		return c;
	}
	
	private static int[] parseQFields(String[] fields, String errMsg){
		int[] qFields;
		try{
			qFields = new int[]{Integer.parseInt(fields[13]), Integer.parseInt(fields[14]), Integer.parseInt(fields[21]), Integer.parseInt(fields[17]), Integer.parseInt(fields[16]), Integer.parseInt(fields[15]), Integer.parseInt(fields[18]), Integer.parseInt(fields[20]), Integer.parseInt(fields[7]), Integer.parseInt(fields[8]), Integer.parseInt(fields[11]), Integer.parseInt(fields[12]), Integer.parseInt(fields[9]), Integer.parseInt(fields[10]), Integer.parseInt(fields[5])};
		}catch(NumberFormatException e){
			e.printStackTrace();
			Main.throwError(errMsg);
			return null;
		}
		return qFields;
	}
}