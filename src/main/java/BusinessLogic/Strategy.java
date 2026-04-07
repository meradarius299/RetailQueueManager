package BusinessLogic;
import Model.*;
import java.util.List;

public interface Strategy {
    public void addTask(List<Server> servers, Task t);
}

class ConcreteStrategyTime implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task t) {
        Server best = servers.get(0);
        for (Server s : servers) {
            if (s.getWaitingPeriod() < best.getWaitingPeriod()) best = s;
        }
        best.addTask(t);
    }
}

class ConcreteStrategyQueue implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task t) {
        Server best = servers.get(0);
        for (Server s : servers) {
            if (s.getTasks().length < best.getTasks().length) best = s;
        }
        best.addTask(t);
    }
}