import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPServerClient50 {
    private String message;

    int nrcli = 0;

    public static final int SERVERPORT = 5555;
    private OnMessageReceived messageListener = null;
    private boolean running = false;
    TCPServerClientThread50[] sendclis = new TCPServerClientThread50[10];

    PrintWriter mOut;
    BufferedReader in;

    ServerSocket serverSocket;

    //el constructor pide una interface OnMessageReceived
    public TCPServerClient50(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }

    public OnMessageReceived getMessageListener(){
        return this.messageListener;
    }

    public void sendMessageTCPServer(String message){
        for (int i = 1; i <= nrcli; i++) {
            sendclis[i].sendMessage(message);
            System.out.println("ENVIANDO A SERVER " + (i));
        }
    }

    public void sendMessageTCPServerClient(String message){
        for (int i = 1; i <= nrcli; i++) {
            //sendclis[i].sendMessage(message + ";" + (i));
            System.out.println("ENVIANDO A SERVER " + (i));
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
                sendclis[nrcli] = new TCPServerClientThread50(client,this,nrcli,sendclis);
                Thread t = new Thread(sendclis[nrcli]);
                t.start();
                System.out.println("Nuevo conectado:"+ nrcli+" cliente conectados");

            }

        }catch( Exception e){
            System.out.println("Error"+e.getMessage());
        }finally{

        }
    }
    public  TCPServerClientThread50[] getClients(){
        return sendclis;
    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
