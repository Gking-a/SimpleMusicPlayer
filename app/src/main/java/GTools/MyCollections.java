package GTools;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collection;
import java.util.List;

public class MyCollections {
	private MyCollections(){
		
	}
	public static <T> T[] toArray(List<T> c,T[] result){
		if(result.length<c.size()){throw new RuntimeException("array length mix");}
		for(int i=0;i<c.size();i++){
			result[i]=c.get(i);
		}
		return result;
	}
	public static String[] randomsort(String[] data){
		Random ran=new Random();
		for(int i=0;i<data.length;i++){
			int r=Math.abs(ran.nextInt()%data.length);
			String t=data[i];
			data[i]=data[r];
			data[r]=t;
		}
		return data;
	}
	public static <T> ArrayList<T> randomsort(ArrayList<T> data){
		Random ran=new Random();
		for(int i=0;i<data.size();i++){
			int r=Math.abs(ran.nextInt()%data.size());
			T t=data.get(i);
			data.set(i,data.get(r));
			data.set(r,t);
		}
		
		return data;
	}
}
