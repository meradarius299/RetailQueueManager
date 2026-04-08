package GUI;

import Model.Server;
import Model.Task;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QueueVisualizer extends JPanel {
    private List<Task> waitingTasks;
    private List<Server> servers;

    public void updateData(List<Server> servers, List<Task> generatedTasks) {
        this.servers = servers;
        this.waitingTasks = (generatedTasks != null) ? new ArrayList<>(generatedTasks) : new ArrayList<>();
        this.repaint(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.drawString("Clienți în așteptare (Waiting Room):", 20, 20);

        int startX = 20;
        if (waitingTasks != null) {
            for (Task t : waitingTasks) {
                g.setColor(Color.BLUE);
                g.fillOval(startX, 30, 25, 25);
                g.setColor(Color.WHITE);
                g.drawString(String.valueOf(t.getId()), startX + 5, 48);
                startX += 30;
            }
        }
        if (servers != null) {
            int y = 100;
            for (Server s : servers) {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(20, y, 40, 40);

                int x = 70;
                for (Task t : s.getTasks()) {
                    g.setColor(Color.GREEN);
                    g.fillOval(x, y + 5, 30, 30);
                    x += 35;
                }
                y += 60;
            }
        }
    }
}
