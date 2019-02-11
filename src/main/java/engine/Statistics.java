package engine;

import entity.Agent;
import entity.AgentYearResult;
import lombok.Getter;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

@Getter
public class Statistics implements Runnable {
    private BlockingQueue<Map<Agent,AgentSimulationResult>> queue;
    private long breedCAgents;
    private long breedNCAgents;

    public Statistics(){
        queue = new ArrayBlockingQueue<>(20);
    }

    @Override
    public void run() {
        while(true){
            try {
                Map<Agent,AgentSimulationResult> results = queue.take();
                Iterator<Agent> iterator = results.keySet().iterator();

                while(iterator.hasNext()){
                    Agent agent = iterator.next();
                    printModelForEachAgent(agent, results.get(agent));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printModelForEachAgent(Agent agent, AgentSimulationResult result){
        System.out.println("-----------------------------------------------------\n");
        System.out.println("Policy ID : "+agent.getPolicyId());
        if(result.getSimulationResults().isEmpty()){
            System.out.println("\t Due to Auto-Renew, not processed for 15 years");
            System.out.println("\t Agent details : " + agent);
        }
        else{
            System.out.println("\t Printing fifteen years status now.");
            IntStream.range(1,16).forEach(i -> {
                System.out.println("\t\t Year : "+i+" "+result.getSimulationResults().get(i));
            });
        }
    }
}
