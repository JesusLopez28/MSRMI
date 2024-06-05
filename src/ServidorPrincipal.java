import java.rmi.Remote;

public interface ServidorPrincipal extends Remote{
    // Registrar cliente
    void registrarCliente(ClienteServidor cliente) throws java.rmi.RemoteException;

    // El server recibe el array y lo distribuye a los clientes-server para que lo ordenen
    void recibirArray(String tipoOrdenamiento, long startTime, long startTotalTime) throws java.rmi.RemoteException;

    // El server recibe los arrays ordenados y los junta en uno solo
    void recibirArraysOrdenados(int[] array, String tipoOrdenamiento) throws java.rmi.RemoteException;

    // El server nos regresa el array ordenado y su tiempo de ejecucion
    void enviarArrayOrdenado(int[] array) throws java.rmi.RemoteException;

    // Recibir array de un cliente pero que se vaya uniendo
    void recibirArrayParaUnir(int[] array) throws java.rmi.RemoteException;
}
