package engine;

import entity.Agent;
import entity.AgentBreed;
import lombok.Getter;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Getter
public class ModelSimulatingEngine {
    private BlockingQueue<Agent> agentInputQueue;
    private double brand_factor;
    private ExecutorService processingPool;
    private BlockingQueue<AgentYearResult> outputQueue;
    private Map<Agent,AgentSimulationResult> results;

    public ModelSimulatingEngine(BlockingQueue<AgentYearResult> outputQueue){
        agentInputQueue = new ArrayBlockingQueue<>(20);
        processingPool = Executors.newCachedThreadPool();
        brand_factor = 0.1 + new Random().nextDouble()*(2.8);
        results = new ConcurrentHashMap<>();
        this.outputQueue = outputQueue;
    }

    private void processAgent(Agent agent) throws InterruptedException, ExecutionException, TimeoutException {
        results.put(agent, new AgentSimulationResult());
        Future task = processingPool.submit(new ProcessAgent(brand_factor,
                agent, results.get(agent)));
        task.get();
        outputQueue.put(new AgentYearResult(agent, results.get(agent)));
    }

    public void startProcessing() throws InterruptedException, TimeoutException, ExecutionException {
        while(true){
            processAgent(agentInputQueue.take());
        }
    }

    class ProcessAgent implements Runnable{
        private double brandFactor;
        private Agent agent;
        private AgentSimulationResult results;
        private Random random;

        public ProcessAgent(double brandFactor,
                            Agent agent,
                            AgentSimulationResult results){
            this.brandFactor = brandFactor;
            this.agent = agent;
            this.results = results;
            random = new Random();
        }

        @Override
        public void run() {
            IntStream.range(1,16).forEach(this::processForYear);
        }

        private void processForYear(int year){
            Agent agentForYear = agent.clone();

            if(!agent.isAutoRenew()){
                double affinity = (agent.getPaymentAtPurchase()/agent.getAttributePrice())+
                        (random.nextFloat() * agent.getAttributePromotions() * agent.getInertiaForSwitch());
                if((agent.getAgentBreed() == AgentBreed.BREED_C) &&
                        (affinity < (agent.getSocialGrade() * agent.getAttributeBrand()))){
                    agentForYear.setAgentBreed(AgentBreed.BREED_NC);
                    agent.setAgentBreed(AgentBreed.BREED_NC);
                }
                else if((agent.getAgentBreed() == AgentBreed.BREED_NC) &&
                        (affinity < (agent.getSocialGrade() * agent.getAttributeBrand() * brandFactor))){
                    agentForYear.setAgentBreed(AgentBreed.BREED_C);
                    agent.setAgentBreed(AgentBreed.BREED_C);
                }
                results.addResultForYear(year, agentForYear);
            }
        }
    }

    public boolean isQueueProcessed(){
        return agentInputQueue.isEmpty() ? true : false;
    }
}
