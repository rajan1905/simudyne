package engine;

import entity.Agent;
import entity.AgentBreed;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Getter
@Setter
public class ModelSimulatingEngine {
    private BlockingQueue<Agent> agentInputQueue;
    private double brand_factor;
    private ThreadPoolExecutor processingPool;
    private Map<Agent,AgentSimulationResult> results;
    private long agentsReceived;

    public ModelSimulatingEngine(){
        agentInputQueue = new ArrayBlockingQueue<>(40);
        processingPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        brand_factor = 0.1 + new Random().nextDouble()*(2.8);
        results = new ConcurrentHashMap<>();
    }

    public void start() throws InterruptedException, TimeoutException, ExecutionException {
        while (true) {
            Agent agent = agentInputQueue.take();
            results.put(agent, new AgentSimulationResult());
            Future task = processingPool.submit(new ProcessAgent(brand_factor,
                    agent, results.get(agent)));
            task.get();
            if(processingPool.getCompletedTaskCount() == agentsReceived) break;
        }

        // If we are here it means input feed is done. So, let's send everything to Statistics module
        // Check nothing executing in thread Pool and post results further
        processingPool.execute(new Statistics(results));
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
            boolean breedCLost = false;
            boolean breedCGained = false;

            if(!agent.isAutoRenew()){
                agentForYear.setAge(agentForYear.getAge() + 1);

                double affinity = (agent.getPaymentAtPurchase()/agent.getAttributePrice())+
                        (random.nextFloat() * agent.getAttributePromotions() * agent.getInertiaForSwitch());
                if((agent.getAgentBreed() == AgentBreed.BREED_C) &&
                        (affinity < (agent.getSocialGrade() * agent.getAttributeBrand()))){
                    if(agentForYear.getAgentBreed() != AgentBreed.BREED_NC){
                        breedCLost = true;
                    }
                    agentForYear.setAgentBreed(AgentBreed.BREED_NC);
                    agent.setAgentBreed(AgentBreed.BREED_NC);
                }
                else if((agent.getAgentBreed() == AgentBreed.BREED_NC) &&
                        (affinity < (agent.getSocialGrade() * agent.getAttributeBrand() * brandFactor))){
                    if(agentForYear.getAgentBreed() != AgentBreed.BREED_C){
                        breedCGained = true;
                    }
                    agentForYear.setAgentBreed(AgentBreed.BREED_C);
                    agent.setAgentBreed(AgentBreed.BREED_C);
                }
                results.addResultForYear(year, agentForYear);
                if(breedCLost) { results.getAgentYearResult().breedCLost(); }
                if(breedCGained) { results.getAgentYearResult().breedCGained(); }
                if(breedCLost && breedCGained) { results.getAgentYearResult().breedCRegained(); }
            }
        }
    }
}
