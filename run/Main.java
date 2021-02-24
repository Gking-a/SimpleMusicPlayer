import java.io.File;

public class Main {
    public static void main(String[] args) {
        new Main().main();
    }
    private void main() {
        String root="/home/runner/work/SimpleMusicPlayer/SimpleMusicPlayer";
        GLibrary config=new GLibrary(new File(root+"/run","config"));
        config.connect();
        String port=config.get("port");
        String ip=config.get("ip");
        File f=new File(root+"/app/build/outputs/apk","debug");
        TCPClient c=new TCPClient();
        c.connect(ip, Integer.parseInt(port));
        c.send(f);
        config.close();
    }
}
