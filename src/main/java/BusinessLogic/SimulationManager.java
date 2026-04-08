package BusinessLogic;

import GUI.SimulationFrame;
import Model.Server;
import Model.*;
import java.io.*;
import java.util.*;

public class SimulationManager implements Runnable {
    private int timeLimit, numClients;
    private Scheduler scheduler;
    private List<Task> generatedTasks;
    private SimulationFrame frame;

    public SimulationManager(int n, int q, int tLimit, int minA, int maxA, int minS, int maxS, SimulationFrame frame) {
        this.numClients = n;
        this.timeLimit = tLimit;
        this.frame = frame;
        this.scheduler = new Scheduler(q);
        this.scheduler.changeStrategy(SelectionPolicy.SHORTEST_TIME);
        generateRandomTasks(minA, maxA, minS, maxS);
    }

    private void generateRandomTasks(int minA, int maxA, int minS, int maxS) {
        generatedTasks = new ArrayList<>();
        Random r = new Random();
        for (int i = 1; i <= numClients; i++) {
            int arr = r.nextInt(maxA - minA + 1) + minA;
            int ser = r.nextInt(maxS - minS + 1) + minS;
            generatedTasks.add(new Task(i, arr, ser));
        }
        Collections.sort(generatedTasks);
    }

    @Override
    public void run() {
        int currentTime = 0;
        try (PrintWriter logFile = new PrintWriter(new FileWriter("simulation_log.txt"))) {
            while (currentTime <= timeLimit) {
                processArrivals(currentTime); 
                String status = getFullStatus(currentTime);
                logFile.println(status); 
                frame.updateVisuals(currentTime, scheduler.getServers(), generatedTasks, status);

                currentTime++;
                Thread.sleep(1000); 
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            frame.onFinish("Simulare incheiata.");
        }
    }

    private String getFullStatus(int time) {
        StringBuilder sb = new StringBuilder();
        sb.append("Timpul ").append(time).append("\n");
        sb.append("Clienti in asteptare: ");
        for (Task t : generatedTasks) {
            sb.append("(").append(t.getId()).append(", ")
                    .append(t.getArrivalTime()).append(", ")
                    .append(t.getServiceTime()).append(") ");
        }
        sb.append("\n");

        List<Server> servers = scheduler.getServers();
        for (int i = 0; i < servers.size(); i++) {
            sb.append("Coada ").append(i + 1).append(": ");
            Task[] tasks = servers.get(i).getTasks();
            if (tasks.length == 0) {
                sb.append("inchisa");
            } else {
                for (Task t : tasks) {
                    sb.append("(").append(t.getId()).append(", ")
                            .append(t.getArrivalTime()).append(", ")
                            .append(t.getServiceTime()).append(") ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    private void processArrivals(int currentTime) {
        Iterator<Task> iterator = generatedTasks.iterator();

        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getArrivalTime() == currentTime) {
                scheduler.dispatchTask(task);
                iterator.remove();
            }
        }
    }
}
