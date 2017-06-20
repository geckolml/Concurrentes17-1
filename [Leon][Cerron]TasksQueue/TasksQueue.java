
import java.util.concurrent.*;
import java.io.*;

public class TasksQueue implements Runnable{
    private BlockingQueue<String> cola;

    public  TasksQueue(BlockingQueue<String> cola_){
        this.cola = cola_;
    }
    public void taskuno(String llego) throws IOException{
        String[] aux = llego.split(":");
        System.out.println("WorkerTask153 recibe el mensaje::" + llego);
        double rpta = 0;
        int it = aux[0];
        int n_part = aux[1];
//        int dat = Integer.parseInt(llego);   ///LLEGA LA TAREA "dat" UN ENTERO
        rpta = funcion(dat);                          ///REALIZA LA FUNCION
        System.out.println("WORKER resulado:" + rpta);
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


  public double funcion (int i, int N){ // N = B
             double h = 1.0 / N;
             double x = h * ((double)i - 0.5);
             return (4.0/(1.0 + x*x)); // Colocar cualquier funcion
         }



}
