
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Scanner;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;
import java.io.*;

// Cliente(5555) -> Servidor -> Worker(4444)

public class Servidor50 {
   TCPServerClient50 mTcpServerClient;
   TCPServerWorker50 mTcpServerWorker;
   Scanner sc;

   int N_NODOS=4;
   int n_cont=0;

   double resultadoPi=0.0;

   double A, B;
   int H;
    String[] aux;
   private Lock bloqueo = new ReentrantLock();

   double[] rpta = new double[1000];

   public static void main(String[] args) {
       Servidor50 objser = new Servidor50();
       objser.iniciar();
   }
   void iniciar(){


     // TCP Workers Puerto 4444
        bloqueo.lock();

       Thread Work =new Thread(
             new Runnable() {

                @Override
                public void run() {
                      mTcpServerWorker = new TCPServerWorker50(
                        new TCPServerWorker50.OnMessageReceived(){
                            @Override
                            public void messageReceived(String message){
                                ServidorWorkerRecibe(message);
                            }
                        }
                    );
                    mTcpServerWorker.run();
                }
            }
        );


        // TCP Clientes Puerto 5555
        Thread Cli =new Thread(
             new Runnable() {

                 @Override
                 public void run() {
                       mTcpServerClient = new TCPServerClient50(
                         new TCPServerClient50.OnMessageReceived(){
                             @Override
                             public void messageReceived(String message){
                                 ServidorClientRecibe(message);
                             }
                         }
                     );
                     mTcpServerClient.run();
                 }
             }
         );

         Work.start();
         Cli.start();
        //-----------------



        ///---
        String salir = "n";
        sc = new Scanner(System.in);
        System.out.println("Servidor bandera 01");
        while( !salir.equals("s")){
            salir = sc.nextLine();
            ServidorEnvia(salir);
            //System.out.println("Renzo burro!");
       }
       System.out.println("Servidor bandera 02");
   }
   void ServidorClientRecibe(String llego){
        resultadoPi=0.0;
       /*String[] aux = llego.split(":");

       if(aux.length > 1){
            mTcpServerWorker.sendMessageTCPServerWorker("aaa 0 1000 5 ;1;1");
            int id = Integer.parseInt(aux[1]);
            double send = Double.parseDouble(aux[2]);
            NumberFormat formatter = new DecimalFormat("#.#################");
            System.out.println("SERVIDOR50 envia mensaje:: 0;4 ");
            double answer = procesoHilos(0.0, 100000000.0, 5, 1, 4);
            System.out.println("La respues de " + id + " es: " + formatter.format(send));
       }else
            System.out.println("SERVIDOR40 El mensaje:" + llego);

            */

        Thread aux2 =new Thread(
             new Runnable() {

                 @Override
                 public void run() {
                       aux = llego.split(" ");
                        A = Double.parseDouble(aux[1]);
                        B = Double.parseDouble(aux[2]);
                        H = Integer.parseInt(aux[3]);
                        double answer = procesoHilos(A, B, H, 1, 4);
                        System.out.println("A:"+A);
                        System.out.println("B:"+B);
                        System.out.println("H:"+H);
                        System.out.println("La respuesta procesado en el master es "+answer);
                        resultadoPi+=answer;
                        n_cont++;
                        //System.out.println("El mensaje llego es:"+llego);
                        System.out.println("MENSAJE QUE RECIBE SERVIDOR DE CLIENTES : "+llego+"\n Enviando a Workers...");

                 }
             }
         );
        aux2.start();

            /*String[] aux = llego.split(" ");
            double A, B;
            int H;
            A = Double.parseDouble(aux[1]);
            B = Double.parseDouble(aux[2]);
            H = Integer.parseInt(aux[3]);
            double answer = procesoHilos(A, B, H, 1, 4);
            System.out.println("A:"+A);
            System.out.println("B:"+B);
            System.out.println("H:"+H);
            System.out.println("La respuesta procesado en el master es "+answer);
            //System.out.println("El mensaje llego es:"+llego);
            System.out.println("MENSAJE QUE RECIBE SERVIDOR DE CLIENTES : "+llego+"\n Enviando a Workers...");*/
            mTcpServerWorker.sendMessageTCPServerWorker(llego);


   }

   void ServidorWorkerRecibe(String llego){
       String[] aux = llego.split(":");
       System.out.println("MENSAJE QUE RECIBE SERVIDOR DE WORKERS : "+llego);
       if(aux.length > 1){
            //mTcpServerWorker.sendMessageTCPServerWorker("aaa 0 1000 5 ;1;1");
            int id = Integer.parseInt(aux[1]);
            double send = Double.parseDouble(aux[2]);
            NumberFormat formatter = new DecimalFormat("#.#################");
            System.out.println("SERVIDOR50 envia mensaje:: 0;4 ");

            System.out.println("La respuesta de " + id + " es: " + formatter.format(send));
            resultadoPi+=send;
            n_cont++;
       }else
            System.out.println("SERVIDOR40 El mensaje:" + llego);

      if(n_cont == N_NODOS){
        mTcpServerClient.sendMessageTCPServer("bbb "+String.valueOf(resultadoPi));
        n_cont=0;
      }
   }

   void ServidorEnvia(String envia){
        if (mTcpServerWorker != null) {
            // cambio ultimo1
            mTcpServerWorker.sendMessageTCPServer(envia);
            //mTcpServerWorker.sendMessageTCPServerWorker(envia);
        }
   }

   public double procesoHilos(double A, double B, int H, int idCliente, int totalClientes){
       final int TRABAJO_COLA = 10000000;
       //hilo[] hiloWork = new hilo[H]; // Vector de Hilos
       System.out.println(idCliente + " y " + totalClientes );//
       double Inf = A + ((B - A)*(double)(idCliente-1))/(double)totalClientes; // Balanceo para cada nodo
       double Max = A + ((B - A)*(double)(idCliente))/(double)totalClientes;
       System.out.println("Limite Inferior : " + Inf + " Limite Superior : " + Max);

       TasksQueue[]  WorTasks = new TasksQueue[H+1];
       BlockingQueue<String> cola = new ArrayBlockingQueue<String>(TRABAJO_COLA);

       try{
             for (int i = 0; i < H; i++) { // Balanceo para cada nhilos
                 double a = Inf + ((double)i * (Max - Inf))/(double)H;
                 double b = Inf + ((double)(i+1) * (Max - Inf))/(double)H;
                 //hiloWork[i] = new hilo(i,a,b,(int)B) ;
                 //Thread t = new Thread(hiloWork[i]);
                 String s = String.valueOf(i)+"_"+String.valueOf(a) + "_"+String.valueOf(b)+ "_"+String.valueOf(B); // i a b
                 cola.put(s);
             }
     }catch( Exception e){
       System.out.println("Error producido por George ! ");

     }

     System.out.println("3  "+ "");
        for (int i = 1;  i <= H ; i++) {
            WorTasks[i] = new TasksQueue(cola);
            Thread t= new Thread(WorTasks[i]);
            t.start();
        }
/*
       for (int i = 0; i < H; i++){
            try{
               hiloWork[i].join();
           }catch(Exception e){
               System.out.println("error:"+e.toString());
           }
       }

       */
       double total = 0;
        for (int i = 0; i < rpta.length; i++){
            total += rpta[i];
        }
        return total;
   }

/*
   class hilo extends Thread{
       double a, b, sum = .0;
       int id, N;
       public hilo(int id_, double a_, double b_, int _N){
           a = a_;
           b = b_;
           id = id_;
	   N = _N;
       }
       public void run(){
           //double dx = 1; // Tamano del dx
           for (int i = (int)a; i <= (int)b; i++) {
               sum += f(i, N); // Metodo Trapecio
               //sum += (f(i)*dx); //Metodo Rectangulos
           }
           rpta[id] = sum;
       }
        public double f (int i, int N){ // N = B
             double h = 1.0 / N;
             double x = h * ((double)i - 0.5);
             return (4.0/(1.0 + x*x)); // Colocar cualquier funcion
         }

   }
*/
   public class TasksQueue implements Runnable{
       private BlockingQueue<String> cola;

       public  TasksQueue(BlockingQueue<String> cola_){
           this.cola = cola_;
       }
       public void taskuno(String llego) throws IOException{
           String[] aux = llego.split("_");
           double sum = 0;

           System.out.println("WorkerTask153 recibe el mensaje::" + llego);
           double parc_rpta = 0;
           int it = Integer.parseInt(aux[0]);
           double a = Double.parseDouble(aux[1]);
           double b =Double.parseDouble(aux[2]);
           double n_part = Double.parseDouble(aux[3]);
   //        int dat = Integer.parseInt(llego);   ///LLEGA LA TAREA "dat" UN ENTERO
            int part = (int)n_part;

        for (int i = (int)a; i <= (int)b; i++) {
            sum += funcion(i, part); // Metodo Trapecio
              //sum += (f(i)*dx); //Metodo Rectangulos
          }
          rpta[it] = sum;


           //parc_rpta = funcion(dat);                          ///REALIZA LA FUNCION
           //System.out.println("WORKER resulado:" + rpta);
           //rpta[it] = parc_rpta;
       }
       String leoWor;
       public void run(){
           int cont = 0;
           try{

           while(true){
               if ( (leoWor = cola.take()) != null ){
                   System.out.println("tarea:" + cont + "  es:" + leoWor);
                   taskuno(leoWor);
                   cont++;
               }
           }

           }catch ( IOException e){
               System.out.println("error errorr T:" + e.toString());
           }catch(InterruptedException e){
               System.out.println("error errorr Y:"+e.toString());
           }

       }

   /*
       double funcion(int fin){
           double sum = 0;
           for(int j = 0; j<=fin;j++ ){
               sum = sum + Math.sin(j*Math.random());
           }

           return sum;
       }*/


     public double funcion (int i,int N){ // N = B
                double h = 1.0 / N;
                double x = h * ((double)i - 0.5);
                return (4.0/(1.0 + x*x)); // Colocar cualquier funcion
            }
   }




}
