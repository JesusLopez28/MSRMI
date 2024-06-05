import javax.swing.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class implementacionServidorPrincipal extends UnicastRemoteObject implements ServidorPrincipal {
    public ArrayList<ClienteServidor> clientes;
    public final MergeSort mergeSort;
    public int[] arrayReal;
    public String name;
    public int[] arrayAux;
    public String tipoOrdenamiento;
    public int n;
    public int m;
    public int veces = 0;
    private int[] arrayOrdenado;
    private long startTime;
    private long startTotalTime;

    public implementacionServidorPrincipal() throws java.rmi.RemoteException {
        super();
        clientes = new ArrayList<ClienteServidor>();
        mergeSort = new MergeSort();
    }

    @Override
    public void registrarCliente(ClienteServidor cliente) throws java.rmi.RemoteException {
        clientes.add(cliente);
    }

    @Override
    public void recibirArray(String tipoOrdenamiento, long startTime, long startTotalTime, String name) throws java.rmi.RemoteException {
        this.tipoOrdenamiento = tipoOrdenamiento;
        this.n = arrayAux.length;
        this.m = n / clientes.size();
        this.startTime = startTime;
        this.startTotalTime = startTotalTime;
        this.name = name;

        int inicio = 0;

        for (int i = 0; i < clientes.size(); i++) {
            int fin = (i == clientes.size() - 1) ? n : inicio + m;

            int[] parteArray = new int[fin - inicio];
            for (int j = inicio; j < fin; j++) {
                parteArray[j - inicio] = arrayAux[j];
            }

            clientes.get(i).recibirOrdenarParteArray(parteArray, tipoOrdenamiento);
            inicio = fin;
        }
    }

    @Override
    public void recibirArraysOrdenados(int[] array, String tipoOrdenamiento) throws java.rmi.RemoteException {
        if (arrayOrdenado == null) {
            arrayOrdenado = new int[n];
        }

        int m = n / clientes.size();

        int inicio = veces * m;
        int fin = inicio + array.length;

        for (int i = 0; i < array.length; i++) {
            arrayOrdenado[inicio + i] = array[i];
        }

        veces++;

        if (veces == clientes.size()) {
            veces = 0;
            if (tipoOrdenamiento.equals("secuencial")) {
                mergeSort.sort(arrayOrdenado, 0, arrayOrdenado.length - 1);
            } else if (tipoOrdenamiento.equals("forkjoin")) {
                ForkJoinPool pool = new ForkJoinPool();
                pool.invoke(new ForkJoinOperator(arrayOrdenado, 0, arrayOrdenado.length - 1, mergeSort));
            } else if (tipoOrdenamiento.equals("executorservice")) {
                ExecutorServiceOperator executorService = new ExecutorServiceOperator(mergeSort);
                executorService.sortWithExecutorService(arrayOrdenado);
            }
            enviarArrayOrdenado(arrayOrdenado);
        }
    }

    @Override
    public void enviarArrayOrdenado(int[] array) throws java.rmi.RemoteException {
        for (ClienteServidor cliente : clientes) {
            cliente.recibirArrayFinal(array, tipoOrdenamiento, startTime, startTotalTime, name);
        }
        arrayOrdenado = null;
        arrayAux = null;
        arrayReal = null;
    }

    @Override
    public void recibirArrayParaUnir(int[] array) throws java.rmi.RemoteException {

        if (arrayAux == null) {
            arrayAux = array;
        } else {
            int length = arrayAux.length + array.length;
            int[] arrayReal = new int[length];
            int i = 0;
            for (int j = 0; j < arrayAux.length; j++) {
                arrayReal[i] = arrayAux[j];
                i++;
            }
            for (int j = 0; j < array.length; j++) {
                arrayReal[i] = array[j];
                i++;
            }
            arrayAux = arrayReal;
        }
    }
}
