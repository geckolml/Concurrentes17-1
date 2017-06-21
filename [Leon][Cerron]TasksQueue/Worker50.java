
import java.util.Scanner;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;
import java.io.*;
class Worker50{
    TCPWorker50 mTcpWorker;
    Scanner sc;

    // Vector de resultados de la integracion
    static String addr="";

    double[] rpta = new double[1000]; // maximo 1000 hilos

    public static void main(String[] args)  {
        Worker50 objcli = new Worker50();
        //addr = "192.168.0.110";//args[0]"";
        addr="127.0.0.1";
        //addr="10.0.2.15";
        objcli.iniciar();
    }
    void iniciar(){
       new Thread(
            new Runnable() {

                @Override
                public void run() {
                    mTcpWorker = new TCPWorker50(addr,
                        new TCPWorker50.OnMessageReceived(){
                            @Override
                            public void messageReceived(String message){
                                WorkerRecibe(message);
                            }
                        }
                    );
                    mTcpWorker.run();
                }
            }
        ).start();
        //---------------------------

        String salir = "n";
        sc = new Scanner(System.in);
        System.out.println("Worker bandera 01");
        while( !salir.equals("s")){
            salir = sc.nextLine();
            WorkerEnvia(salir);
        }
        System.out.println("Worker bandera 02");

    }
    void WorkerRecibe(String llego){

        System.out.println("WORKER50 El mensaje::" + llego);

        if ( llego.trim().contains("aaa")){

                String[] aux = llego.split(";");
                for(int i = 0; i < aux.length; i++){
                    System.out.println("aux es: " + aux[i]);
                }
                int id = 0, n = 0, H = 0;
                double A = 0.0, B = 0.0;
                String datos[] = llego.split("\\s+");

                System.out.println("TCPWorker recibo trabajo:");
                System.out.println("Clave verificada...");
               // System.out.println("0 " + datos[0]);
               // System.out.println("1 " + datos[1]);
               // System.out.println("2 " + datos[2]);
               // System.out.println("3 " + datos[3]);


                id = Integer.parseInt(aux[1])+1;
                //n = Integer.parseInt(aux[1]);
                n = 4;
                A = Double.parseDouble(datos[1]);
                B = Double.parseDouble(datos[2]);
                H = Integer.parseInt(datos[3].split(";")[0]);
                long time1 = System.currentTimeMillis();
                double answer = procesoHilos(A, B, H, id, n);
                long totalTime = System.currentTimeMillis() - time1;
                System.out.println("El Tiempo es: " + totalTime + " milisegundos.");
                WorkerEnvia("La Respuesta es :" + id + ":" + answer);
                System.out.println("La respuesta es: " + answer);
        }
    }

    public double procesoHilos(double A, double B, int H, int idWorker, int totalWorkers){
	final int TRABAJO_COLA = 10000000;
        //hilo[] hiloWork = new hilo[H]; // Vector de Hilos
        //Thread[] HILOS = new Thread[H];
        System.out.println(idWorker + " y " + totalWorkers );
        double Inf = A + ((B - A)*(double)(idWorker-1))/(double)totalWorkers;
        double Max = A + ((B - A)*(double)(idWorker))/(double)totalWorkers;
        System.out.println("Limite Inferior : " + Inf + " Limite Superior : " + Max);
        
	/*for (int i = 0; i < H; i++) {
            double a = Inf + ((double)i * (Max - Inf))/(double)H;
            double b = Inf + ((double)(i+1) * (Max - Inf))/(double)H;
            hiloWork[i] = new hilo(i, a, b, (int)B);
            Thread t = new Thread(hiloWork[i]);
            t.start();
            //HILOS[i] = new Thread(hiloWork[i]);
            try{
                t.join();
            }catch(Exception e){
                System.out.println("error:"+e.toString());
            }

        }
        */
        /*for (int i = 0; i < H; i++){
            try{
                HILOS[i].start();
            }catch(Exception e){
                System.out.println("error:"+e.toString());
            }
        }
        for (int i = 0; i < H; i++){
            try{
                HILOS[i].join();
           }catch(Exception e){
               System.out.println("error:"+e.toString());
           }
        }*/
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
        double total = 0;
        for (int i = 0; i < rpta.length; i++) {
            total += rpta[i];
        }
    	return total;
    }
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
            //int dx = 1; // Tamano del dx
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
    void WorkerEnvia(String envia){
        if (mTcpWorker != null) {
            mTcpWorker.sendMessage(envia);
        }
    }


	
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
