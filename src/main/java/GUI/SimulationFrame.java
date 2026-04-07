package GUI;

import BusinessLogic.SimulationManager;
import Model.Server;
import Model.Task;
import javax.swing.*;
import java.awt.*;
import java.util.List;


public class SimulationFrame extends JFrame {
    // Componente pentru configurare
    private final JTextField[] inputs = new JTextField[7];
    private final JButton btnStart = new JButton("PORNEȘTE SIMULAREA");

    // Componente pentru vizualizare
    private final QueueVisualizer visualizer = new QueueVisualizer();
    private final JTextArea statusArea = new JTextArea(12, 50);
    private final JLabel timeLabel = new JLabel("Timp de simulare: 0", SwingConstants.CENTER);

    public SimulationFrame() {
        this.setTitle("Managementul Cozilor - Interfață Vizuală");
        this.setSize(1000, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(10, 10));

        setupControlPanel();
        setupDisplayPanel();

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void setupControlPanel() {
        JPanel ctrl = new JPanel(new GridLayout(9, 1, 5, 5));
        ctrl.setBorder(BorderFactory.createTitledBorder("Setări Simulare"));

        String[] labels = {"Nr. Clienți (N):", "Nr. Cozi (Q):", "Timp Simulare:",
                "Sosire Min:", "Sosire Max:", "Service Min:", "Service Max:"};

        // Valori implicite conform Testului 1 [cite: 72]
        String[] defaults = {"4", "2", "60", "2", "30", "2", "4"};

        for (int i = 0; i < 7; i++) {
            ctrl.add(new JLabel(labels[i]));
            inputs[i] = new JTextField(defaults[i]);
            ctrl.add(inputs[i]);
        }

        btnStart.setBackground(new Color(70, 130, 180));
        btnStart.setForeground(Color.WHITE);
        btnStart.setFont(new Font("Arial", Font.BOLD, 12));
        btnStart.addActionListener(e -> startSimulation());
        ctrl.add(btnStart);

        this.add(ctrl, BorderLayout.WEST);
    }

    private void setupDisplayPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Zona de timp (Nord)
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        centerPanel.add(timeLabel, BorderLayout.NORTH);

        // Zona grafică cu buline (Centru)
        visualizer.setBackground(Color.WHITE);
        centerPanel.add(visualizer, BorderLayout.CENTER);

        // Zona de text pentru detalii ID/Timp (Sud)
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusArea.setBackground(new Color(245, 245, 245));
        JScrollPane scroll = new JScrollPane(statusArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Jurnal Evenimente (Real-time)"));
        centerPanel.add(scroll, BorderLayout.SOUTH);

        this.add(centerPanel, BorderLayout.CENTER);
    }

    private void startSimulation() {
        try {
            int n = Integer.parseInt(inputs[0].getText());
            int q = Integer.parseInt(inputs[1].getText());
            int tSim = Integer.parseInt(inputs[2].getText());
            int minA = Integer.parseInt(inputs[3].getText());
            int maxA = Integer.parseInt(inputs[4].getText());
            int minS = Integer.parseInt(inputs[5].getText());
            int maxS = Integer.parseInt(inputs[6].getText());

            btnStart.setEnabled(false);

            // Cream managerul și îi pasăm referința 'this' pentru update-uri [cite: 197]
            SimulationManager simManager = new SimulationManager(n, q, tSim, minA, maxA, minS, maxS, this);
            new Thread(simManager).start();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Eroare: Introduceți valori numerice valide!", "Eroare Validare", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateVisuals(int time, List<Server> servers, List<Task> generatedTasks, String textStatus) {
        SwingUtilities.invokeLater(() -> {
            timeLabel.setText("Momentul de timp: " + time);
            visualizer.updateData(servers, generatedTasks);
            statusArea.setText(textStatus);
        });
    }

    public void onFinish(String statistics) {
        SwingUtilities.invokeLater(() -> {
            btnStart.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Simularea s-a încheiat!\n" + statistics);
        });
    }

    public static void main(String[] args) {
        new SimulationFrame();
    }
}