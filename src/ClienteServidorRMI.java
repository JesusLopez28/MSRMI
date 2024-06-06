import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClienteServidorRMI {
    public static void main(String[] args) {
        try {
            String nombre = JOptionPane.showInputDialog("Introduce tu nombre: ");
            String nom = nombre;
            Registry rmii = LocateRegistry.getRegistry("localhost", 1099);
            ServidorPrincipal servidor = (ServidorPrincipal) rmii.lookup("MergeSortRMI");
            new Thread(new implementacionClienteServidor(nom, servidor)).start();
        } catch (Exception e) {
            System.out.println("Excepcion en clienteRMI: " + e);
            e.printStackTrace();
        }
    }
}
