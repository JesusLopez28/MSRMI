import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorPrincipalRMI {
    public static void main(String[] args) {
        try {
            Registry rmi = LocateRegistry.createRegistry(1099);
            rmi.rebind("MergeSortRMI", new implementacionServidorPrincipal());
            System.out.println("Servidor Principal RMI listo");
        } catch (Exception e) {
            System.out.println("Excepcion en ClienteServidorRMI: " + e);
            e.printStackTrace();
        }
    }
}
