package engine;

import entity.Agent;
import entity.AgentBreed;
import entity.AgentYearResult;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class ModelSimulatingEngine {
    private BlockingQueue<Agent> agentInputQueue;
    private double brand_factor;
    private ThreadPoolExecutor processingPool;
    private BlockingQueue<Map<Agent,AgentSimulationResult>> outputQueue;
    private Map<Agent,AgentSimulationResult> results;
    private boolean inputFeedCompleted;

    public ModelSimulatingEngine(BlockingQueue<Map<Agent,AgentSimulationResult>> outputQueue){
        agentInputQueue = new ArrayBlockingQueue<>(20);
        processingPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        brand_factor = 0.1 + new Random().nextDouble()*(2.8);
        results = new ConcurrentHashMap<>();
        this.outputQueue = outputQueue;
    }

    private void processAgent(Agent agent) throws InterruptedException, ExecutionException, TimeoutException {

    }

    public void start() throws InterruptedException, TimeoutException, ExecutionException {
        List<CompletableFuture<Void>> allTasks = new ArrayList<>();
        while(true && !isInputFeedCompleted()){
            Agent agent = agentInputQueue.take();
            results.put(agent, new AgentSimulationResult());
            Future task = processingPool.submit(new ProcessAgent(brand_factor,
                    agent, results.get(agent)));
            task.get(1, TimeUnit.SECONDS);
        }

        // If we are here it means input feed is done. So, let's send everything to Statistics module
        // Check nothing executing in thread Pool and post results further

        System.out.println(results.size());
        outputQueue.put(results);
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
    public void setInputFeedCompleted(){inputFeedCompleted = true;}
}
