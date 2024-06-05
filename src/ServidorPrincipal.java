import java.rmi.Remote;

public interface ServidorPrincipal extends Remote{
    // Registrar cliente
    void registrarCliente(ClienteServidor cliente) throws java.rmi.RemoteException;

    // El server recibe el array y lo distribuye a los clientes-server para que lo ordenen
    void recibirArray(int[] array, String tipoOrdenamiento) throws java.rmi.RemoteException;

    // El server recibe los arrays ordenados y los junta en uno solo
    void recibirArraysOrdenados(int[] array, String tipoOrdenamiento) throws java.rmi.RemoteException;

    // El server nos regresa el array ordenado y su tiempo de ejecucion
    void enviarArrayOrdenado(int[] array, long tiempoEjecucion) throws java.rmi.RemoteException;
}
