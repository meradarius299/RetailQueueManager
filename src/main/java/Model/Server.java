package Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks = new LinkedBlockingQueue<>();
    private int waitingPeriod;
    private int servicePeriod;
    public void addTask(Task newTask) {
        tasks.add(newTask);
        waitingPeriod += newTask.getWaitingTime();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Task currentTask = tasks.peek();
                if (currentTask != null) {
                    Thread.sleep(1000);
                    int currentService = currentTask.getServiceTime();
                    if (currentService > 0) {
                        currentTask.setServiceTime(currentService - 1);
                        waitingPeriod -= currentTask.decrementWaitingTime();
                    }
                    if (currentTask.getServiceTime() == 0) tasks.poll();
                }
            } catch (InterruptedException e) { break; }
        }
    }

    public Task[] getTasks() { return tasks.toArray(new Task[0]); }
    public int getWaitingPeriod() { return waitingPeriod; }
}
