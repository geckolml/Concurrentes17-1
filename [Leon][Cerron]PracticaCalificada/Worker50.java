
import java.util.Scanner;

class Worker50{
    TCPWorker50 mTcpWorker;
    Scanner sc;

    // Vector de resultados de la integracion
    static String addr="";

    double[] rpta = new double[1000]; // maximo 1000 hilos

    public static void main(String[] args)  {
        Worker50 objcli = new Worker50();
        //addr = "192.168.0.110";//args[0]"";
        //addr="127.0.0.1";
        addr="10.0.2.15";
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
                n = 3;
                // Solo 3 trabajadores 
                A = Double.parseDouble(datos[1]);
                B = Double.parseDouble(datos[2]);
                H = Integer.parseInt(datos[3].split(";")[0]);
                long time1 = System.currentTimeMillis();
                double answer = procesoHilos(A, B, H, id, n);
                long totalTime = System.currentTimeMillis() - time1;
                System.out.println("El Tiempo es: " + totalTime + " milisegundos.");
                //WorkerEnvia("La Respuesta es :" + id + ":" + answer);
        }
    }

    public double procesoHilos(Double A, Double B, int H, int idWorker, int totalWorkers){

        hilo[] hiloWork = new hilo[H]; // Vector de Hilos
        System.out.println(idWorker + " y " + totalWorkers );
        double Inf = A + ((B - A)*(double)(idWorker-1))/(double)totalWorkers;
        double Max = A + ((B - A)*(double)(idWorker))/(double)totalWorkers;
        System.out.println("Limite Inferior : " + Inf + " Limite Superior : " + Max);
        for (int i = 0; i < H; i++) {
            double a = Inf + ((double)i * (Max - Inf))/(double)H;
            double b = Inf + ((double)(i+1) * (Max - Inf))/(double)H;
            hiloWork[i] = new hilo(i,a,b);
            Thread t = new Thread(hiloWork[i]);
            t.start();
//            try{
//                t.join();
//            }catch(Exception e){
//                System.out.println("error:"+e.toString());
//            }

        }

        for (int i = 0; i < H; i++){
             try{
                hiloWork[i].join();
            }catch(Exception e){
                System.out.println("error:"+e.toString());
            }
        }
        double total = 0;
        for (int i = 0; i < rpta.length; i++) {
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
            for (double i = a; i < b; i+=dx) {
                sum += (f((i+i+dx)/2.0))*(dx); // Metodo Trapecio
                //sum += (f(i)*dx); //Metodo Rectangulos
            }
            rpta[id] = sum;
        }
        public double f(double x){
            return (Math.sin(x)); // Colocar cualquier funcion
        }
    }
    void WorkerEnvia(String envia){
        if (mTcpWorker != null) {
            mTcpWorker.sendMessage(envia);
        }
    }
}
