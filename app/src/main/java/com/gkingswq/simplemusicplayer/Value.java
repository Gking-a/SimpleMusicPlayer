package com.gkingswq.simplemusicplayer;

import java.io.File;
import android.os.Build;
public final class Value {
    public static final class StringPool{
		public static final String CommonURI="https://y.music.163.com/m/song?id=";
		public static final String Lyric_a="http://music.163.com/api/song/lyric?id=\\search\\&lv=1";
		public static final String PlaylistURI="http://music.163.com/api/playlist/detail?id=";
		public static final String Outer="http://music.163.com/song/media/outer/url?id=";
		public static final String Detail="https://api.imjad.cn/cloudmusic/?type=detail&id=";
		public static final String PLAYLIST_PREFIX ="https://api.imjad.cn/cloudmusic/?type=playlist&id=";
		public static final String SearchSong="http://musicapi.leanapp.cn/search?keywords=\\search\\&type=1";
		public static final String UnExistName="com.gkingswq.simplemusicplayer.unexistname";
        private static final String test="https://y.music.163.com/m/song?id=142888";

	}
	public static final class Flags{
		public static final int FLAG_RANDOM=0;
		public static final int FLAG_SOLO=1;
		public static final int FLAG_LOOP=2;
	}
	public static final class IntentKeys{
		public static final String playid="id";
		public static final String playflag="mflag";
		public static final String playids="ids";
		public static final String randomindex="randomplayposition";
	}
	public static final class Actions{
        public static final String ACTION_NEXT="com.gking.simplemusicplayer.next";
		public static final String ACTION_LAST = "com.gking.simplemusicplayer.last";
		public static final String ACTION_PAUSE="com.gking.simplemusicplayer.pause";
		public static final String ACTION_STOPSERVICE="com.gkingswq.simplemusicplayer.stopservice";
        public static final String ACTION_LOOP = "com.gkingswq.simplemusicplayer.loop";
		public static final String ACTION_WINDOW = "com.gkingswq.simplemusicplayer.window";
		public static final String ACTION_RANDOM_NEXT="com.gkingswq.simplemusicplayer.randomnext";
	}
	public static final class Settings{
        public static final String DEFAULT_LIST ="defaultlist";
	    public static final String LOCKEDNOTIFICATIONSHOW="lockednotificationshow";
	    public static final String WINDOW_COLOR="windowcolor";
		public static final String DEFAULT_WINDOW_SHOW="defaultwindow";
	}
	public static final class Files{
		public static final File _LOCATION =new File("/data/user/0/com.gkingswq.simplemusicplayer/files/Location/");
        public static final File _SETTINGS =new File("/data/user/0/com.gkingswq.simplemusicplayer/files/Settings");
        public static final File _LINK = new File("/data/user/0/com.gkingswq.simplemusicplayer/files/Link");
        public static final File _NAME = new File("/data/user/0/com.gkingswq.simplemusicplayer/files/Name");
    }
}
