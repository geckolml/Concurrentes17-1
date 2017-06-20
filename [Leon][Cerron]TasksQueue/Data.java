
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Data {
    public static void main(String[] args) {
       Data da = new Data();
       da.inicio();
    }

    void inicio(){

        final int HILOS_DE_TRABAJO = 80;
        final int TRABAJO_COLA = 10000000;
        Scanner sc;

        System.out.println("1 "+ "");

        TasksQueue[]  WorTasks = new TasksQueue[HILOS_DE_TRABAJO+1];
        BlockingQueue<String> cola = new ArrayBlockingQueue<String>(TRABAJO_COLA);

        int max = 20;
        String val;
        
        System.out.println("2 "+ "");
        try{
            for (int i = 0; i < max; i++) {
                val = String.valueOf((int)(Math.random()*100000));
                cola.put(val);

            }
        }catch (Exception e){
            System.out.println("Err_132 "+e);
        }

        System.out.println("3  "+ "");
        for (int i = 1;  i <= HILOS_DE_TRABAJO ; i++) {
            WorTasks[i] = new TasksQueue(cola);
            Thread t= new Thread(WorTasks[i]);
            t.start();
        }
        System.out.println("4  ------------------------- "+ "");
    }

}
