package gtools;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GLibrary {
    public static final int TYPE_STRING=0;
    public static final int TYPE_LIBRARY=1;
	public static final int TYPE_STRINGS=2;
	public static final int TYPE_LIBRARIES=3;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Map<String,String> StringMap;
    private Map<String,GLibrary> LibsMap;
    private Map<String, List<String>> StringList;
    private Map<String, List<GLibrary>> LibsList;
    private static final Pattern format=Pattern.compile("(.*?)=(.*)");
    private static final Pattern format2=Pattern.compile(",'(.*?)'");
    private File library;
    private boolean isConnected =false;
	private String name;
    private FileWriter f2;
	public GLibrary(File file){
	    this(file.getName(),file);
    }
    public GLibrary(File file,boolean connect){
        this(file.getName(),file,connect);
    }
    public GLibrary(String name,File file){
        this.library=file;
		this.name=name;
        try {
            BufferedReader br=new BufferedReader(new FileReader(library));
            ArrayList<String> a=new ArrayList<>();
            String line;
            while((line=br.readLine())!=null){
                a.add(line);
            }
            f2=new FileWriter(library,false);
            for(String l:a){
                f2.write(l+newLine);
            }
            f2.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public GLibrary(String name,String filepath){
		this(name,new File(filepath));
	}
    public GLibrary(String name,File file,boolean connect) {
		this(name,file);
        if(connect)
            connect();
    }
    public void connect(File file) {
        close();
        this.library=file;
        connect();
    }
    public boolean connect() {
        try{
		if(!exists())
			return false;
		isConnected =true;
		reader=new BufferedReader(new FileReader(library));
		writer=new BufferedWriter(new FileWriter(library,true));
		StringMap=new HashMap<>();
		StringList=new Hashtable<>();
		LibsMap=new HashMap<>();
		LibsList=new Hashtable<>();
		String line=null;
		Matcher matcher=null;
		while ((line=reader.readLine())!=null){
		    matcher=format.matcher(line);
		    if(matcher.find()) {
		        String prefix = matcher.group(1);
		        String object = matcher.group(2);
		        if(object.contains(",")){
		            if(object.contains("'")){
		                ArrayList<String> aList=new ArrayList<>();
		                Matcher matcher1=format2.matcher(object);
		                while (matcher1.find()){
		                    aList.add(matcher1.group(1));
		                    object=object.substring(matcher1.group(0).length());
		                    matcher1=format2.matcher(object);
                        }
		                StringList.put(prefix,aList);
		            }else{
		                ArrayList<GLibrary> aList=new ArrayList<>();
		                while (object.contains(",")){
		                    String object2=object.substring(1,object.indexOf(",",2));
		                    File f=new File(library.getParent(),library.getName()+"$"+object2);
		                    aList.add(new GLibrary(object2,f));
		                    object=object.substring(object2.length());
		                }
		                LibsList.put(prefix,aList);
		            }
		        }else {
		            StringMap.put(prefix, object.substring(1,object.length()-1));
		        }
		    }else {
		        File library2file=new File(library.getParent(),library.getName()+"$"+line);
		        GLibrary library2=new GLibrary(line,library2file);
		        LibsMap.put(line,library2);
		    }
		}

        }catch (Exception e){
            isConnected=false;
        }
        return isConnected;
    }
    public void add(String symbol,Object object,int type){
        if(!isConnected())
            return;
        String string=object.toString();
        if (type==TYPE_STRING) {
            if(StringMap.containsKey(symbol)){
				StringMap.remove(symbol);
				StringMap.put(symbol,string);
                //StringMap.replace(symbol,string);
            }else {
                StringMap.put(symbol,string);
            }
        }
        if (type==TYPE_LIBRARY) {
			if(LibsMap.containsKey(symbol)){
			}else{
				File f=new File(library.getParent(),library.getName()+"$"+symbol);
				GLibrary l=new GLibrary(symbol,f);
				LibsMap.put(symbol,l);
			}
        }
        if(type==TYPE_STRINGS){
            if(StringList.containsKey(symbol)){
                StringList.get(symbol).add(string);
            }else {
                List<String> list=new ArrayList<>();
                list.add(string);
                StringList.put(symbol, list);
            }
        }
    }
    public void add(String symbol,String[] objects,int type)throws IOException{
        add(symbol,Arrays.asList(objects),type);
    }
    public void add(String symbol,List<String> objects,int type) throws IOException{
        if(!isConnected())
            return;
        if (type==TYPE_STRINGS) {
            if(StringList.containsKey(symbol)){
				StringList.remove(symbol);
				StringList.put(symbol,objects);
//                StringList.replace(symbol,objects);
            }else {
                StringList.put(symbol,objects);
                //writer.write(symbol+"="+"'"+string+"'"+newLine);
            }
        }
        if (type==TYPE_LIBRARIES) {
			ArrayList<GLibrary> aList=new ArrayList<>();
			for(String name2:objects){
				File f=new File(library.getParent(),library.getName()+"$"+name2);
				f.createNewFile();
				aList.add(new GLibrary(name2,f));
			}
			if(LibsList.containsKey(symbol)){
				LibsList.remove(symbol);
				LibsList.put(symbol,aList);
			}else{
				LibsList.put(symbol,aList);
			}
        }
    }
    public synchronized void save() throws IOException {
        if(!isConnected()){
            throw new IOException("lib E");}
//            return;
        f2=new FileWriter(library,false);
        for (String prefix:StringMap.keySet()) {
           writer.write(prefix+"="+"'"+StringMap.get(prefix)+"'"+newLine);
        }
        for (String prefix:LibsMap.keySet()) {
            writer.write(prefix+newLine);
        }
		for(String prefix:StringList.keySet()){
			String object="";
			for(String object2:StringList.get(prefix)){
				object+=",'"+object2+"'";
			}
			writer.write(prefix+"="+object+newLine);
		}
		for(String prefix:LibsList.keySet()){
			String object="";
			for(GLibrary object2:LibsList.get(prefix)){
				object+=","+object2.getName();
			}
			writer.write(prefix+"="+object+newLine);
		}
        writer.flush();
    }
    public void remove(String symbol,int type) {
        if(!isConnected())
            return;
        if(type==TYPE_STRING){
            if(StringMap.containsKey(symbol)) {
                StringMap.remove(symbol);
            }
        }
        if(type==TYPE_LIBRARY){
            if(LibsMap.containsKey(symbol)) {
                LibsMap.get(symbol).getLibrary().delete();
                LibsMap.remove(symbol);
            }
        }
		if(type==TYPE_STRINGS){
			if(StringList.containsKey(symbol)) {
                StringList.remove(symbol);
            }
		}if(type==TYPE_LIBRARIES){
            if(LibsList.containsKey(symbol)) {
				for(GLibrary lib:LibsList.get(symbol)){
					lib.delete();
				}
                LibsList.remove(symbol);
            }
        }
    }
    public String get(String symbol){
        if(StringMap.containsKey(symbol))
            return StringMap.get(symbol);
        return null;
    }
    public GLibrary get(String symbol,boolean isConnect) {
        if(LibsMap.containsKey(symbol)){
            if(isConnect)
                LibsMap.get(symbol).connect();
            return LibsMap.get(symbol);
        }
        return null;
    }
	public List<String> getStrings(String symbol){
		if(StringList.containsKey(symbol))
            return StringList.get(symbol);
        return null;
	}
	public List<GLibrary> getLibraries(String symbol){
        if(LibsList.containsKey(symbol)){
            return LibsList.get(symbol);
        }
        return null;
    }
    public Map<String, String> getStringMap() {
        return StringMap;
    }
    public Map<String, GLibrary> getLibsMap() {
        return LibsMap;
    }
    public Map<String, List<String>> getStringList() {
        return StringList;
    }
    public Map<String, List<GLibrary>> getLibsList() {
        return LibsList;
    }
    public void close(){
        try {
			StringMap=null;
			LibsMap=null;
			StringList=null;
			LibsList=null;
            if(reader!=null) {
                reader.close();
                reader=null;
            }
            if(writer!=null) {
                writer.flush();
                writer.close();
                writer=null;
            }
            isConnected =false;
        }catch (Exception e ){
            e.printStackTrace();
        }
    }
	public void close(boolean save) throws IOException{
		if(save)
			save();
		close();
	}
//	public boolean create() throws IOException{
//		return create(false);
//	}
//	public boolean create(boolean override) throws IOException{
//		if(override)
//			delete();
//		if(!exists())
//			return library.createNewFile();
//		return false;
//	}
	public boolean exists(){
		return library.exists();
	}
//	public boolean exists(boolean create) throws IOException{
//		if(create)
//			create();
//		return exists();
//	}
	public String[] getAllSymbols(){
		String[] result=new String[StringList.size()+StringMap.size()+LibsMap.size()+LibsList.size()];
		Set<String> set=new HashSet<>();
		set.addAll(StringList.keySet());
		set.addAll(StringMap.keySet());
		set.addAll(LibsMap.keySet());
		set.addAll(LibsList.keySet());
		set.toArray(result);
		return result;
	}
	public void delete(){
		library.delete();
	}
    public File getLibrary() {
        return library;
    }
    public boolean isConnected() {
        return isConnected;
    }
	public String getName(){
		return name;
	}
	private static final String newLine="\n";
}
