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
        agentInputQueue = new ArrayBlockingQueue<>(20);
        processingPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Integer.MAX_VALUE,
                10L, TimeUnit.SECONDS,
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
        private boolean breedCLost;
        private boolean breedCGained;
        private boolean wasBreedCLostInPast;

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

                double affinity = (agentForYear.getPaymentAtPurchase()/agentForYear.getAttributePrice())+
                        (random.nextFloat() * agentForYear.getAttributePromotions() * agentForYear.getInertiaForSwitch());
                if((agentForYear.getAgentBreed() == AgentBreed.BREED_C) &&
                        (affinity < (agentForYear.getSocialGrade() * agentForYear.getAttributeBrand()))){
                    if(agentForYear.getAgentBreed() != AgentBreed.BREED_NC){
                        breedCLost = true;
                        wasBreedCLostInPast = true;
                    }
                    agentForYear.setAgentBreed(AgentBreed.BREED_NC);
                }
                else if((agentForYear.getAgentBreed() == AgentBreed.BREED_NC) &&
                        (affinity < (agentForYear.getSocialGrade() * agentForYear.getAttributeBrand() * brandFactor))){
                    if(agentForYear.getAgentBreed() != AgentBreed.BREED_C){
                        breedCGained = true;
                    }
                    agentForYear.setAgentBreed(AgentBreed.BREED_C);
                }
                results.addResultForYear(year, agentForYear);
                if(breedCLost) { results.getAgentYearResult().breedCLost(); }
                if(breedCGained) { results.getAgentYearResult().breedCGained(); }
                if(wasBreedCLostInPast && breedCGained) {
                    results.getAgentYearResult().breedCRegained();
                    breedCLost = false;
                    wasBreedCLostInPast = false;
                }
                breedCLost = false;
                breedCGained = false;
                agent.setAge(agentForYear.getAge() + 1);
                agent.setAgentBreed(agentForYear.getAgentBreed());
            }
        }
    }
}
