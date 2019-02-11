package engine;

import entity.Agent;
import entity.AgentBreed;
import entity.AgentYearResult;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

@Getter
public class Statistics implements Runnable {
    private BlockingQueue<Map<Agent,AgentSimulationResult>> queue;
    private List<Agent> breedCAgents;
    private List<Agent> breedNCAgents;

    public Statistics(){
        queue = new ArrayBlockingQueue<>(20);
        breedNCAgents = new ArrayList<>();
        breedCAgents = new ArrayList<>();
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
                printAgentBreed(breedCAgents, AgentBreed.BREED_C);
                printAgentBreed(breedNCAgents, AgentBreed.BREED_NC);

                calculateLostGainedRegainedForAgentBreedC(breedCAgents);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void printModelForEachAgent(Agent agent, AgentSimulationResult result){
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
                if(i==15) {
                    if (result.getSimulationResults().get(i).getAgentBreed() == AgentBreed.BREED_C) {
                        breedCAgents.add(result.getSimulationResults().get(i));
                    } else
                        breedNCAgents.add(result.getSimulationResults().get(i));
                }
            });
        }
        System.out.println("\t At the end of 15 years : "+agent +"\n");
    }

    private void printAgentBreed(List<Agent> agents, AgentBreed breed){
        System.out.println("\n\n\t List of Agent with AgentBreed : "+breed.name()+ " with count : "+agents.size());
        agents.stream()
                .forEach( b -> System.out.println("\t\t "+b));

    }

    private void calculateLostGainedRegainedForAgentBreedC(List<Agent> agents){

    }

}
