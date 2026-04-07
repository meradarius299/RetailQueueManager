package BusinessLogic;
import Model.*;
import java.util.*;

public class Scheduler {
    private List<Server> servers;
    private Strategy strategy;

    public Scheduler(int maxNoServers) {
        this.servers = new ArrayList<>();
        for (int i = 0; i < maxNoServers; i++) {
            Server s = new Server();
            servers.add(s);
            new Thread(s).start();
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        if (policy == SelectionPolicy.SHORTEST_QUEUE)
            strategy = new ConcreteStrategyQueue();
        if (policy == SelectionPolicy.SHORTEST_TIME)
            strategy = new ConcreteStrategyTime();
    }

    public void dispatchTask(Task t) {
        strategy.addTask(servers, t);
    }
    public List<Server> getServers() {
        return servers;
    }
}

enum SelectionPolicy {
    SHORTEST_QUEUE, SHORTEST_TIME
}