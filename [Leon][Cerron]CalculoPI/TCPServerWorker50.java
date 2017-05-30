import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPServerWorker50 {
    private String message;

    int nrcli = 0;

    public static final int SERVERPORT = 4444;
    private OnMessageReceived messageListener = null;
    private boolean running = false;
    TCPServerWorkerThread50[] sendworks = new TCPServerWorkerThread50[10];

    PrintWriter mOut;
    BufferedReader in;

    ServerSocket serverSocket;

    //el constructor pide una interface OnMessageReceived
    public TCPServerWorker50(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }

    public OnMessageReceived getMessageListener(){
        return this.messageListener;
    }

    public void sendMessageTCPServer(String message){
        for (int i = 1; i <= nrcli; i++) {
            sendworks[i].sendMessage(message);
            System.out.println("ENVIANDO A Worker " + (i));
        }
    }

    public void sendMessageTCPServerWorker(String message){
        for (int i = 1; i <= nrcli; i++) {
            sendworks[i].sendMessage(message + ";" + (i));
            System.out.println("ENVIANDO A Worker " + (i));
        }
    }

    public void run(){
        running = true;
        try{
            System.out.println("TCP Server"+"S : Connecting...");
            serverSocket = new ServerSocket(SERVERPORT);

            while(running){
                Socket client = serverSocket.accept();
                System.out.println("TCP Server"+"S: Receiving...");
                nrcli++;
                System.out.println("Engendrado " + nrcli);//////////////////////////////////////////////////
                sendworks[nrcli] = new TCPServerWorkerThread50(client,this,nrcli,sendworks);
                Thread t = new Thread(sendworks[nrcli]);
                t.start();
                System.out.println("Nuevo conectado:"+ nrcli+" cliente conectados");

            }

        }catch( Exception e){
            System.out.println("Error"+e.getMessage());
        }finally{

        }
    }
    public  TCPServerWorkerThread50[] getWorkers(){
        return sendworks;
    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
