import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class implementacionClienteServidor extends UnicastRemoteObject implements ClienteServidor, Runnable, ActionListener {
    public ServidorPrincipal servidor;
    public String nombre = null;
    public final MergeSort mergeSort;
    private JTextArea originalTextArea;
    private JTextArea ordenadoTextArea;
    private JTextField tamano;
    private JButton secuencialButton;
    private JButton forkJoinButton;
    private JButton executorServiceButton;
    private JTextField tiempoSecuencial;
    private JTextField tiempoForkJoin;
    private JTextField tiempoExecutorService;
    private JTextField tiempoTotalSecuencial;
    private JTextField tiempoTotalForkJoin;
    private JTextField tiempoTotalExecutorService;

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
        IUFrame();
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

    public void IUFrame() {
        JFrame frame = new JFrame(nombre);
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        frame.getContentPane().add(panel);

        Panel textAreasPanel = new Panel(new GridLayout(1, 2, 10, 10));
        panel.add(textAreasPanel, BorderLayout.CENTER);

        originalTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        originalTextArea.setLineWrap(true);
        originalTextArea.setWrapStyleWord(true);
        JScrollPane originalScrollPane = new JScrollPane(originalTextArea);
        textAreasPanel.add(originalScrollPane);

        ordenadoTextArea = new JTextArea();
        ordenadoTextArea.setEditable(false);
        ordenadoTextArea.setLineWrap(true);
        ordenadoTextArea.setWrapStyleWord(true);
        JScrollPane ordenadoScrollPane = new JScrollPane(ordenadoTextArea);
        textAreasPanel.add(ordenadoScrollPane);

        JPanel inputPanel = new JPanel(new FlowLayout());
        panel.add(inputPanel, BorderLayout.NORTH);

        JLabel sizeLabel = new JLabel("Tamaño:");
        inputPanel.add(sizeLabel);

        tamano = new JTextField(10);
        tamano.setText("0");
        inputPanel.add(tamano);

        JButton nuevoButton = new JButton("Nuevo");
        nuevoButton.addActionListener(this);
        inputPanel.add(nuevoButton);

        secuencialButton = new JButton("Secuencial");
        secuencialButton.addActionListener(this);
        inputPanel.add(secuencialButton);
        secuencialButton.setEnabled(false);

        forkJoinButton = new JButton("ForkJoin");
        forkJoinButton.addActionListener(this);
        inputPanel.add(forkJoinButton);
        forkJoinButton.setEnabled(false);

        executorServiceButton = new JButton("ExecutorService");
        executorServiceButton.addActionListener(this);
        inputPanel.add(executorServiceButton);
        executorServiceButton.setEnabled(false);

        JButton limpiarButton = new JButton("Limpiar");
        limpiarButton.addActionListener(this);
        inputPanel.add(limpiarButton);

        JPanel tiempoPanel = new JPanel(new GridLayout(2, 3, 10, 10)); // GridLayout con 2 filas y 3 columnas
        panel.add(tiempoPanel, BorderLayout.SOUTH);

        JLabel tiempoSecuencialLabel = new JLabel("Proceso Secuencial:");
        tiempoPanel.add(tiempoSecuencialLabel);

        tiempoSecuencial = new JTextField(10);
        tiempoSecuencial.setEditable(false);
        tiempoPanel.add(tiempoSecuencial);

        JLabel tiempoForkJoinLabel = new JLabel("Proceso ForkJoin:");
        tiempoPanel.add(tiempoForkJoinLabel);

        tiempoForkJoin = new JTextField(10);
        tiempoForkJoin.setEditable(false);
        tiempoPanel.add(tiempoForkJoin);

        JLabel tiempoExecutorServiceLabel = new JLabel("Proceso ExecutorService:");
        tiempoPanel.add(tiempoExecutorServiceLabel);

        tiempoExecutorService = new JTextField(10);
        tiempoExecutorService.setEditable(false);
        tiempoPanel.add(tiempoExecutorService);

        JLabel tiempoTotalSecuencialLabel = new JLabel("Total Secuencial:");
        tiempoPanel.add(tiempoTotalSecuencialLabel);

        tiempoTotalSecuencial = new JTextField(10);
        tiempoTotalSecuencial.setEditable(false);
        tiempoPanel.add(tiempoTotalSecuencial);

        JLabel tiempoTotalForkJoinLabel = new JLabel("Total ForkJoin:");
        tiempoPanel.add(tiempoTotalForkJoinLabel);

        tiempoTotalForkJoin = new JTextField(10);
        tiempoTotalForkJoin.setEditable(false);
        tiempoPanel.add(tiempoTotalForkJoin);

        JLabel tiempoTotalExecutorServiceLabel = new JLabel("Total ExecutorService:");
        tiempoPanel.add(tiempoTotalExecutorServiceLabel);

        tiempoTotalExecutorService = new JTextField(10);
        tiempoTotalExecutorService.setEditable(false);
        tiempoPanel.add(tiempoTotalExecutorService);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

}
