
import java.io.*;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class TCPClient extends TCPBase
{
    public TCPClient(){ }
    public void connect(String serverIP,int serverPort){
        try {
            Socket client=new Socket();
            client.connect(new InetSocketAddress(serverIP, serverPort),1000);
            setSocket(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
