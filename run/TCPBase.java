import java.io.*;
import java.net.Socket;

public abstract class TCPBase {

    public void receive(final File parent)   {
        Thread t=new Thread(){
            public void run(){
                try {
                    int size=20;
                    long time=System.currentTimeMillis();
                    long uset=Long.parseLong(br.readLine());
                    System.out.println("Start");
                    System.out.println(uset);
                    int use=Integer.parseInt(br.readLine());
                    File target=new File(parent,br.readLine());
                    target.delete();
                    target.createNewFile();
                    bw.write(0);
                    bw.flush();
                    BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(target));
                    byte[] b=new byte[size];
                    int i=0;
                    int read;
                    while((read=bis.read(b))!=-1){
                    //    System.out.print(read+"\t");
                        i++;
                        if(i==uset){
                            fos.write(b,0,use);
                            bos.write(0);
                            bos.flush();
                            break;
                        }
                        while(i!=uset-1&&bis.available()<size){}
                      //  System.out.println(bis.available());
                        fos.write(b,0,read);
                    }
                    fos.flush();
                    fos.close();
                    System.out.println(target.length());
                    System.out.println("time:"+(System.currentTimeMillis()-time)/1000+"s");
                    System.out.println("length:"+target.length()/1024/1024+"MB");
                    long kbs=target.length()/(System.currentTimeMillis()-time);
                    System.out.println("spend："+kbs+"KB/S | "+kbs/1024+"MB/S");
                } catch (IOException e) {}
            }
        };
        t.start();
    }
    public void send(File file){
        send(file,"");
    }
    public void send(final File file,final String path){
        if(file.isDirectory()){
            for(File f:file.listFiles()){
                send(f,file.getName()+"\\\\");
            }
        }else{
            Thread t=new Thread(){
                public void run(){
                    int size=1*20;
                    while (bw==null){}
                    try {
                        System.out.println(file.length());
                        bw.write(Util.getNumBeforePoint(file.length()/size+1)+"\n");
                        bw.write(file.length()%size+"\n");
                        bw.write(path+file.getName()+"\n");
                        bw.flush();
                        br.read();
                        System.out.println(file.length());
                        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
                        byte[] b=new byte[size];
                        int read;
                        while((read=fis.read(b))!=-1){
                            bos.write(b,0,read);
                            bos.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        }
    }
    private void setConnected(boolean connected) {
        isConnected = connected;
    }
    public void shutdown(){
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            socket=null;
            bis=null;
            bos=null;
            br=null;
            bw=null;
            isConnected=false;
        } catch (IOException e) {}
    }
    private boolean isConnected=false;
    public boolean isConnected() {
        return isConnected;
    }
    public TCPBase(){}
    public TCPBase(Socket socket){
        setSocket(socket);
    }
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        try {
            this.socket = socket;
            setBos(new BufferedOutputStream(socket.getOutputStream()));
            setBis(new BufferedInputStream(socket.getInputStream()));
            setBr(new BufferedReader(new InputStreamReader(socket.getInputStream())));
            setBw(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            setConnected(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private Socket socket;
    public BufferedInputStream getBis() {
        return bis;
    }
    public void setBis(BufferedInputStream bis) {
        this.bis = bis;
    }
    public BufferedOutputStream getBos() {
        return bos;
    }
    public void setBos(BufferedOutputStream bos) {
        this.bos = bos;
    }
    public BufferedReader getBr() {
        return br;
    }
    public void setBr(BufferedReader br) {
        this.br = br;
    }
    public BufferedWriter getBw() {
        return bw;
    }
    public void setBw(BufferedWriter bw) {
        this.bw = bw;
    }
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private BufferedReader br;
    private BufferedWriter bw;
}
