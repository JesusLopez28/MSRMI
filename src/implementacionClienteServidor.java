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
    private int[] firstArray;
    private String tipoOrdenamiento;
    private long startTime;
    private long endTime;
    private long startTotalTime;
    private long endTotalTime;

    public implementacionClienteServidor(String nombre, ServidorPrincipal servidor) throws RemoteException {
        this.nombre = nombre;
        this.servidor = servidor;
        servidor.registrarCliente(this);
        originalTextArea = new JTextArea();
        mergeSort = new MergeSort();
        mergeSort.setOriginalTextArea(originalTextArea);
    }

    @Override
    public void enviarArray(String tipoOrdenamiento, long startTime, long startTotalTime) throws RemoteException {
        servidor.recibirArray(tipoOrdenamiento, startTime, startTotalTime, nombre);
    }

    @Override
    public void mandarArrayParaUnir(int[] array) throws RemoteException {
        servidor.recibirArrayParaUnir(array);
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
    public void recibirArrayFinal(int[] array, String tipo, long startTime, long startTotalTime, String name) throws RemoteException {
        if(!name.equals(nombre)){
            return;
        }
        endTime = System.nanoTime();
        long duration = (endTime - startTime);
        double milliseconds = (double) duration / 1_000_000.0;

        ordenadoTextArea.setText(mergeSort.printArray(array));

        endTotalTime = System.nanoTime();
        long durationTotal = (endTotalTime - startTotalTime) + duration;
        double millisecondsTotal = (double) durationTotal / 1_000_000.0;

        if (tipo.equals("secuencial")) {
            tiempoSecuencial.setText(milliseconds + " ms");
            tiempoTotalSecuencial.setText(millisecondsTotal + " ms");
        } else if (tipo.equals("forkjoin")) {
            tiempoForkJoin.setText(milliseconds + " ms");
            tiempoTotalForkJoin.setText(millisecondsTotal + " ms");
        } else if (tipo.equals("executorservice")) {
            tiempoExecutorService.setText(milliseconds + " ms");
            tiempoTotalExecutorService.setText(millisecondsTotal + " ms");
        }
    }

    @Override
    public void run() {
        IUFrame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Secuencial")) {
            tipoOrdenamiento = "secuencial";
            startTime = System.nanoTime();
            startTotalTime = startTime;
            try {
                enviarArray(tipoOrdenamiento, startTime, startTotalTime);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } else if (e.getActionCommand().equals("ForkJoin")) {
            tipoOrdenamiento = "forkjoin";
            startTime = System.nanoTime();
            startTotalTime = startTime;
            try {
                enviarArray(tipoOrdenamiento, startTime, startTotalTime);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } else if (e.getActionCommand().equals("ExecutorService")) {
            tipoOrdenamiento = "executorservice";
            startTime = System.nanoTime();
            startTotalTime = startTime;
            try {
                enviarArray(tipoOrdenamiento, startTime, startTotalTime);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } else if (e.getActionCommand().equals("Nuevo")) {
            int n = Integer.parseInt(tamano.getText());
            if (n > 0) {
                firstArray = mergeSort.generateRandomArray(n);
                try {
                    mandarArrayParaUnir(firstArray);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
                secuencialButton.setEnabled(true);
                forkJoinButton.setEnabled(true);
                executorServiceButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(null, "El tamaño debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                secuencialButton.setEnabled(false);
                forkJoinButton.setEnabled(false);
                executorServiceButton.setEnabled(false);
            }
        } else if (e.getActionCommand().equals("Limpiar")) {
            originalTextArea.setText("");
            ordenadoTextArea.setText("");
            tamano.setText("0");
            secuencialButton.setEnabled(false);
            forkJoinButton.setEnabled(false);
            executorServiceButton.setEnabled(false);
            tiempoSecuencial.setText("");
            tiempoForkJoin.setText("");
            tiempoExecutorService.setText("");
            tiempoTotalSecuencial.setText("");
            tiempoTotalForkJoin.setText("");
            tiempoTotalExecutorService.setText("");
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

}
