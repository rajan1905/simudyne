package engine;

import entity.Agent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AgentYearResult {
    private Agent agent;
    private AgentSimulationResult agentSimulationResult;
}
