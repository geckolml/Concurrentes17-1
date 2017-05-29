/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//import clase5.TCPServer50.OnMessageReceived;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.net.Socket;


public class TCPServerWorkerThread50 extends Thread{

    private Socket client;
    private TCPServerWorker50 tcpserver;
    private int clientID;
    private boolean running = false;
    public PrintWriter mOut;
    public BufferedReader in;
    TCPServerWorker50.OnMessageReceived messageListener = null;
    private String message;
    TCPServerWorkerThread50[] cli_amigos;

    public TCPServerWorkerThread50(Socket client_, TCPServerWorker50 tcpserver_, int clientID_,TCPServerWorkerThread50[] cli_ami_) {
        this.client = client_;
        this.tcpserver = tcpserver_;
        this.clientID = clientID_;
        this.cli_amigos = cli_ami_;
    }

     public void trabajen(int cli){
         mOut.println("TRABAJAMOS ["+cli+"]...");
    }

    public void run() {
        running = true;
        try {
            try {
                boolean soycontador = false;
                mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                System.out.println("TCP Server Worker"+ "C: Sent.");
                messageListener = tcpserver.getMessageListener();
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                while (running) {

                    for(int i = 0; i < 10; i++){
                        if ( cli_amigos[i] != null)
                            System.out.print("T");
                        else
                            System.out.print("F");
                    }
                    System.out.println();

                    message = in.readLine();

                    if (message != null && messageListener != null) {
                        messageListener.messageReceived(message);
                    }else{
                        //cli_amigos[clientID] = null; // Si esta muerto debe cambiar a null
                        for(int i = clientID; i < 9; i++){
                            cli_amigos[i] = cli_amigos[i+1];
                        }
                        tcpserver.nrcli--;
                        break;
                    }

                    //Verifica quien envia trabajo y quien es el trabajador
                    if (clientID <= 1 && !soycontador){
                        String mitabla = "SoyClienteEnviaTrabajo";
                        mOut.println( mitabla + "soy el numero;" + clientID + ";" + tcpserver.nrcli);
                        soycontador = true;
                    }else if ( !soycontador){
                        String mitabla = "SoyTrabajador";
                        mOut.println( mitabla + "soy el numero;" + clientID + ";" + tcpserver.nrcli);
                        soycontador = true;

                    }
                    //El cliente 1 envia el comando "TRA"
                    if (clientID <= 1 && message.trim().contains("aaa")){

                        tcpserver.sendMessageTCPServerWorker(message + " ;" + tcpserver.nrcli);
                        System.out.println("Envio el mensaje trabajo: 100000 TRA BAJEN_SLAVE");
                    }

                    message = null;
                }
                System.out.println("RESPONSE FROM CLIENT"+ "S: Received Message: '" + message + "'");
            } catch (Exception e) {
                System.out.println("TCP Server"+ "S: Error"+ e);
            } finally {
                client.close();
            }

        } catch (Exception e) {
            System.out.println("TCP Server"+ "C: Error"+ e);
        }
    }

    public void stopWorker(){
        running = false;
    }

    public void sendMessage(String message){//funcion de trabajo
        if (mOut != null && !mOut.checkError()) {
            mOut.println( message);
            mOut.flush();
        }
    }

}
