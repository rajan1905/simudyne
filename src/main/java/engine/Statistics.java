package engine;

import entity.Agent;
import lombok.Getter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

@Getter
public class Statistics implements Runnable {

    private BlockingQueue<AgentYearResult> queue;

    public Statistics(){
        queue = new ArrayBlockingQueue<>(20);
    }

    @Override
    public void run() {
        while(true){
            try {
                AgentYearResult entry = queue.take();
                Agent agent = entry.getAgent();
                System.out.println("-----------------------------------------------------\n");
                System.out.println("Policy ID : "+agent.getPolicyId());
                System.out.println("\t Printing fifteen years status now.");
                AgentSimulationResult simulationResult = entry.getAgentSimulationResult();
                IntStream.range(1,16).forEach(i -> {
                    System.out.println("\t\t Year : "+i+" "+simulationResult.getSimulationResults().get(i));
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
