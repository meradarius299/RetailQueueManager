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
        // Specificăm numele fișierului pentru jurnal conform cerinței 5
        try (PrintWriter logFile = new PrintWriter(new FileWriter("simulation_log.txt"))) {
            while (currentTime <= timeLimit) {
                processArrivals(currentTime); //

                // Generăm textul pentru Jurnalul de evenimente (Cerința 5)
                String status = getFullStatus(currentTime);
                logFile.println(status); // Scriem în fișier

                // TRIMITEM DATELE CĂTRE ECRAN
                // Trebuie să pasăm: timpul, serverele, lista de așteptare și textul
                frame.updateVisuals(currentTime, scheduler.getServers(), generatedTasks, status);

                currentTime++;
                Thread.sleep(1000); // Ritmul de o secundă
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            frame.onFinish("Simulare încheiată."); //
        }
    }

    private String getFullStatus(int time) {
        StringBuilder sb = new StringBuilder();
        sb.append("Timpul ").append(time).append("\n");
        sb.append("Clienți în așteptare: ");
        for (Task t : generatedTasks) {
            // Format: (ID, Sosire, Serviciu)
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
                sb.append("închisă");
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
        // Folosim un Iterator pentru a putea șterge în siguranță elementele din listă în timp ce o parcurgem
        Iterator<Task> iterator = generatedTasks.iterator();

        while (iterator.hasNext()) {
            Task task = iterator.next();

            // Verificăm dacă timpul de sosire al clientului coincide cu timpul curent al simulării [cite: 20, 21]
            if (task.getArrivalTime() == currentTime) {
                // Trimitem task-ul către scheduler pentru a fi repartizat unei cozi [cite: 52, 377]
                scheduler.dispatchTask(task);

                // Ștergem clientul din lista de așteptare deoarece a intrat în sistem [cite: 378]
                iterator.remove();
            }
        }
    }


}