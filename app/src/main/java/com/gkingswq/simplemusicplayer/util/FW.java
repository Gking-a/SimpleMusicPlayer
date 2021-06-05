package com.gkingswq.simplemusicplayer.util;

import java.io.FileWriter;
import java.io.IOException;

public class FW {
	static FileWriter fw;
		public static void w(Object o){
			try {
				fw.write(o.toString()+"\n");
				fw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		static{
			try{
				fw=new FileWriter("/sdcard/AppProjects/1.txt");
				fw.write("");
				fw.flush();
			}catch(Exception e){}
		}

}
