
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends TCPBase
{
    ServerSocket server;
    public TCPServer(int localPort){
        try {
            server = new ServerSocket(localPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    Socket target=null;
    private Thread AcceptThread=new Thread(){
        public void run(){
            try {
                target=server.accept();
                setSocket(target);
            } catch (IOException e) {}
        }
    };
    public void accept() throws Exception{
        if(target !=null)
            throw new Exception("Already had socket");
        AcceptThread.start();
    }
    public void stopAccept(){
        AcceptThread.stop();
    }
    public boolean isAccept(){
        if(target !=null)
            return true;
        return false;
    }
}
