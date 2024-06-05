import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class implementacionClienteServidor extends UnicastRemoteObject implements ClienteServidor, Runnable {
    public ServidorPrincipal servidor;
    public String nombre = null;
    public final MergeSort mergeSort;

    public implementacionClienteServidor(String nombre, ServidorPrincipal servidor) throws RemoteException {
        this.nombre = nombre;
        this.servidor = servidor;
        servidor.registrarCliente(this);
        mergeSort = new MergeSort();
    }

    @Override
    public void enviarArray(int[] array, String tipoOrdenamiento) throws RemoteException {
        servidor.recibirArray(array, tipoOrdenamiento);
    }

    @Override
    public void recibirOrdenarParteArray(int[] array, String tipoOrdenamiento) throws RemoteException {
        int[] sortedArray = new int[0];
        if (tipoOrdenamiento.equals("secuencial")) {
            mergeSort.sort(array, 0, array.length - 1);
            sortedArray = array;
        } else if (tipoOrdenamiento.equals("forkjoin")) {
            ForkJoinPool pool = new ForkJoinPool();
            sortedArray = pool.invoke(new ForkJoinOperator(array, 0, array.length - 1, mergeSort));
        } else if (tipoOrdenamiento.equals("executorservice")) {
            ExecutorServiceOperator executorService = new ExecutorServiceOperator(mergeSort);
            executorService.sortWithExecutorService(array);
            sortedArray = array;
        }
        servidor.recibirArraysOrdenados(sortedArray, tipoOrdenamiento);
    }

    @Override
    public void recibirArrayFinal(int[] array, long tiempoEjecucion) throws RemoteException {
        System.out.println("Array ordenado: ");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println("\nTiempo de ejecucion: " + tiempoEjecucion + " ms");
    }

    @Override
    public void run() {
        while (true) {
            Scanner s = new Scanner(System.in);
            System.out.println("Digite el tamaño del array: ");
            int n = s.nextInt();
            int[] array = new int[n];
            System.out.println("Digite los elementos del array: ");
            for (int i = 0; i < n; i++) {
                array[i] = s.nextInt();
            }
            System.out.println("Digite el tipo de ordenamiento (secuencial, forkjoin, executorservice): ");
            String tipoOrdenamiento = s.next();
            try {
                enviarArray(array, tipoOrdenamiento);
            } catch (RemoteException e) {
                System.out.println("Excepcion en implementacionClienteServidor: " + e);
                e.printStackTrace();
            }
        }
    }

}
