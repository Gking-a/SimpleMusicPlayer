package com.gking.simplemusicplayer.util;

import java.io.File;
import java.io.IOException;

public class GFile {
    public static void createFile(File... files) throws IOException {
        for(File file:files) {
            file.createNewFile();
        }
    }
    public static void createDirs(File... files){
        for (File file:files){
            file.mkdirs();
        }
    }
}
