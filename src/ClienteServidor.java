import java.rmi.Remote;

public interface ClienteServidor extends Remote {

    // El cliente puede mandar un array para ser ordenado al servidor, donde mandara el arrray desordenado
    // y si sera de manera Secuencial, ForkJoin y ExecutorService como un string
    void enviarArray(String tipoOrdenamiento, long startTime, long startTotalTime) throws java.rmi.RemoteException;

    // El server nos regresa una parte del array y nosostros lo ordenamos con el metodo que se nos indico
    void recibirOrdenarParteArray(int[] array, String tipoOrdenamiento) throws java.rmi.RemoteException;

    // El server nos regresa el array ordenado y su tiempo de ejecucion
    void recibirArrayFinal(int[] array, String tipo, long startTime, long startTotalTime, String name) throws java.rmi.RemoteException;

    void mandarArrayParaUnir(int[] array) throws java.rmi.RemoteException;
}
