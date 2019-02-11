import csv.CsvReader;
import engine.ModelSimulatingEngine;
import engine.Statistics;
import entity.Agent;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Simulate {
    public static void main(String[] args) throws InterruptedException, TimeoutException, ExecutionException {
        ClassLoader classLoader = Simulate.class.getClassLoader();
        final File csvFile = new File(classLoader.getResource("csv/SimudynePlatformTestData.csv").getFile());

        Statistics statistics = new Statistics();
        ModelSimulatingEngine engine = new ModelSimulatingEngine(statistics.getQueue());
        new Thread(statistics).start();

        BlockingQueue<Agent> modelProcessingEngineQueue = engine.getAgentInputQueue();
        new Thread(new CsvReader(csvFile , modelProcessingEngineQueue)).start();

        engine.startProcessing();

    }
}
