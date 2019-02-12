package engine;

import entity.Agent;
import entity.AgentYearResult;
import lombok.Getter;

import java.util.Map;
import java.util.TreeMap;

@Getter
public class AgentSimulationResult {
    private Map<Integer ,Agent> simulationResults;
    private AgentYearResult agentYearResult;

    public AgentSimulationResult(){
        agentYearResult = new AgentYearResult();
        simulationResults = new TreeMap<>();
    }

    public synchronized void addResultForYear(int year ,Agent agentForYear){
        simulationResults.put(year ,agentForYear);
    }
}
