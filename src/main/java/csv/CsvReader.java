package csv;

import entity.Agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class CsvReader implements Runnable {
    private static final String COMMA_DELIMITER = ",";
    BlockingQueue<Agent> engineQueue;
    File inputFile;

    public CsvReader(File inputFile,
                     BlockingQueue<Agent> engineQueue){
        this.inputFile = inputFile;
        this.engineQueue = engineQueue;
    }

    public void processCSV(File csv){
        try (Scanner scanner = new Scanner(csv);) {
            while (scanner.hasNextLine()) {
                Agent agent = getAgentFromRecord(scanner.nextLine()));
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
        return agent;
    }

    @Override
    public void run() {

    }
}
