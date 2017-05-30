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

    private Socket worker;
    private TCPServerWorker50 tcpserverW;
    private int workerID;
    private boolean running = false;
    public PrintWriter mOut;
    public BufferedReader in;
    TCPServerWorker50.OnMessageReceived messageListenerW = null;
    private String message;
    TCPServerWorkerThread50[] work_amigos;

    public TCPServerWorkerThread50(Socket worker_, TCPServerWorker50 tcpserver_, int workerID_,TCPServerWorkerThread50[] work_ami_) {
        this.worker = worker_;
        this.tcpserverW = tcpserver_;
        this.workerID = workerID_;
        this.work_amigos = work_ami_;
    }

     public void trabajen(int cli){
         mOut.println("TRABAJAMOS ["+cli+"]...");
    }

    public void run() {
        running = true;
        try {
            try {
                boolean soycontador = false;
                mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(worker.getOutputStream())), true);
                System.out.println("TCP Server Worker"+ "C: Sent.");
                messageListenerW = tcpserverW.getMessageListener();
                in = new BufferedReader(new InputStreamReader(worker.getInputStream()));
                while (running) {

                    for(int i = 0; i < 10; i++){
                        if ( work_amigos[i] != null)
                            System.out.print("T");
                        else
                            System.out.print("F");
                    }
                    System.out.println();

                    message = in.readLine();

                    if (message != null && messageListenerW != null) {
                        messageListenerW.messageReceived(message);
                    }else{
                        //cli_amigos[clientID] = null; // Si esta muerto debe cambiar a null
                        for(int i = workerID; i < 9; i++){
                            work_amigos[i] = work_amigos[i+1];
                        }
                        tcpserverW.nrcli--;
                        break;
                    }

                    //Verifica quien envia trabajo y quien es el trabajador
                    if (workerID <= 1 && !soycontador){
                        String mitabla = "SoyClienteEnviaTrabajo";
                        mOut.println( mitabla + "soy el numero;" + workerID + ";" + tcpserverW.nrcli);
                        soycontador = true;
                    }else if ( !soycontador){
                        String mitabla = "SoyTrabajador";
                        mOut.println( mitabla + "soy el numero;" + workerID + ";" + tcpserverW.nrcli);
                        soycontador = true;

                    }
                    //El cliente 1 envia el comando "TRA"
                    if (workerID <= 1 && message.trim().contains("aaa")){

                        tcpserverW.sendMessageTCPServerWorker(message + " ;" + tcpserverW.nrcli);
                        System.out.println("Envio el mensaje trabajo: 100000 TRA BAJEN_SLAVE");
                    }
                    
                    message = null;
                }
                System.out.println("RESPONSE FROM CLIENT"+ "S: Received Message: '" + message + "'");
            } catch (Exception e) {
                System.out.println("TCP Server WORKER"+ "S: Error"+ e);
            } finally {
                worker.close();
            }

        } catch (Exception e) {
            System.out.println("TCP Server WORKER"+ "C: Error"+ e);
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
