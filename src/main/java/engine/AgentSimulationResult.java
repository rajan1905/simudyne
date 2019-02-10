package engine;

import java.util.Map;
import java.util.TreeMap;

public class AgentSimulationResult {
    private int agentId;
    private Map<Integer,YearResult> simulationResults;

    public AgentSimulationResult(int agentId){
        this.agentId = agentId;
        simulationResults = new TreeMap<>();
    }

    public synchronized void addResultForYear(int year, YearResult result){
        simulationResults.put(year,result);
    }
}
