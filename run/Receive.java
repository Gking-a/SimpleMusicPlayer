import java.io.File;
public class Receive {
    public static void main(String[] args) throws Exception {
        if(args==null||args.length==0)
            args=new String[]{"45460"};
        new Receive().main(Integer.valueOf(args[0]));
    }
    private void main(int port) throws Exception {
        GLibrary config=new GLibrary(new File("/sdcard/AppProjects/SimpleMusicPlayer/run","config"));
        config.connect();
        config.add("port",port,GLibrary.TYPE_STRING);
        config.add("ip",Util.getIP(),GLibrary.TYPE_STRING);
        config.save();
        File file = new File("/sdcard/AppProjects/apk");
        TCPServer s=new TCPServer(port);
        s.accept();
        while(!s.isAccept()){
            Thread.sleep(200);
        }
        s.receive(file);
    }
}
