package Model;

public class Task implements Comparable<Task> {
    private int id;
    private int arrivalTime;
    private int serviceTime;

    public Task(int id, int arrivalTime, int serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }
    public int getServiceTime() {
        return serviceTime;
    }
    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }
    public int getId() {
        return id;
    }

    public int getWaitingTime() {
        return serviceTime - arrivalTime;
    }

    public void addWaitingTime(int waitingTime) {
        serviceTime += waitingTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int decrementWaitingTime() {
        return --serviceTime;
    }

    @Override
    public String toString() {
        return "(" + id + ", " + arrivalTime + ", " + serviceTime + ")";
    }

    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.arrivalTime, other.arrivalTime);
    }
}