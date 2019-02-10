package engine;

import entity.Agent;
import lombok.Getter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Getter
public class ModelSimulatingEngine implements Runnable{
    private BlockingQueue<Agent> agentInputQueue;

    public ModelSimulatingEngine(){
        agentInputQueue = new ArrayBlockingQueue<>(20);
    }


    @Override
    public void run() {

    }
}
