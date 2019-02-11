package csv;

import entity.Agent;
import entity.AgentBreed;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class CsvReader implements Runnable {
    private static final String COMMA_DELIMITER = ",";
    BlockingQueue<Agent> engineQueue;
    File inputFile;
    private boolean escapeHeader = true;

    public CsvReader(File inputFile,
                     BlockingQueue<Agent> engineQueue){
        this.inputFile = inputFile;
        this.engineQueue = engineQueue;
    }

    public void processCSV(){
        try (Scanner scanner = new Scanner(inputFile);) {
            while (scanner.hasNextLine()) {
                if(escapeHeader){
                    escapeHeader = false;
                    scanner.nextLine();
                    continue;
                }
                Agent agent = getAgentFromRecord(scanner.nextLine());
                engineQueue.put(agent);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Agent getAgentFromRecord(String line) {
        Agent agent = new Agent();
        String[] agentString = line.split(COMMA_DELIMITER);

        agent.setAgentBreed(agentString[0].equals("BREED_C")? AgentBreed.BREED_C:AgentBreed.BREED_NC);
        agent.setPolicyId(agentString[1].trim());
        agent.setAge(Integer.parseInt(agentString[2].trim()));
        agent.setSocialGrade(Integer.parseInt(agentString[3].trim()));
        agent.setPaymentAtPurchase(Integer.parseInt(agentString[4].trim()));
        agent.setAttributeBrand(Double.parseDouble(agentString[5].trim()));
        agent.setAttributePrice(Double.parseDouble(agentString[6].trim()));
        agent.setAttributePromotions(Double.parseDouble(agentString[7].trim()));
        agent.setAutoRenew(agentString[8].trim().equals("0")?false:true);
        agent.setInertiaForSwitch(Integer.parseInt(agentString[9].trim()));

        return agent;
    }

    @Override
    public void run() {
        processCSV();
    }
}
