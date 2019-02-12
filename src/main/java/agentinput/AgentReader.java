package agentinput;

public interface AgentReader {
    void processInput();
    void tellEngineAboutAgentCreated(long count);
}
