
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Scanner;
import java.util.concurrent.locks.*;

// Cliente(5555) -> Servidor -> Worker(4444)

public class Servidor50 {
   TCPServerClient50 mTcpServerClient;
   TCPServerWorker50 mTcpServerWorker;
   Scanner sc;

   double A, B;
   int H;

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
            String[] aux = llego.split(" ");
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
            System.out.println("MENSAJE QUE RECIBE SERVIDOR DE CLIENTES : "+llego+"\n Enviando a Workers...");
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
       }else
            System.out.println("SERVIDOR40 El mensaje:" + llego);
   }

   void ServidorEnvia(String envia){
        if (mTcpServerWorker != null) {
            // cambio ultimo1
            mTcpServerWorker.sendMessageTCPServer(envia);
            //mTcpServerWorker.sendMessageTCPServerWorker(envia);
        }
   }

   public double procesoHilos(Double A, Double B, int H, int idCliente, int totalClientes){

       hilo[] hiloWork = new hilo[H]; // Vector de Hilos
       System.out.println(idCliente + " y " + totalClientes );
       double Inf = A + ((B - A)*(double)(idCliente-1))/(double)totalClientes;
       double Max = A + ((B - A)*(double)(idCliente))/(double)totalClientes;
       System.out.println("Limite Inferior : " + Inf + " Limite Superior : " + Max);
       for (int i = 0; i < H; i++) {
           double a = Inf + ((double)i * (Max - Inf))/(double)H;
           double b = Inf + ((double)(i+1) * (Max - Inf))/(double)H;
           hiloWork[i] = new hilo(i,a,b);
           Thread t = new Thread(hiloWork[i]);
           t.start();
            try{
                t.join();
            }catch(Exception e){
                System.out.println("error:"+e.toString());
            }

       }

       /*for (int i = 0; i < H; i++){
            try{
               hiloWork[i].join();
           }catch(Exception e){
               System.out.println("error:"+e.toString());
           }
       }*/
       double total = 0;
        for (int i = 0; i < rpta.length; i++){
            total += rpta[i];
        }
        return total;
   }
   class hilo extends Thread{
       double a, b, sum = .0;
       int id;
       public hilo(int id_, double a_, double b_){
           a = a_;
           b = b_;
           id = id_;
       }
       public void run(){
           double dx = 0.01; // Tamano del dx
           for (double i = a; i <= b; i+=dx) {
               sum += (f((i+i+dx)/2.0))*(dx); // Metodo Trapecio
               //sum += (f(i)*dx); //Metodo Rectangulos
           }
           rpta[id] = sum;
       }
       public double f(double x){
           return (x); // Colocar cualquier funcion
       }
   }



}
