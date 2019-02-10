import csv.CsvReader;
import engine.ModelSimulatingEngine;
import entity.Agent;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public class Simulate {
    public static void main(String[] args){
        ClassLoader classLoader = Simulate.class.getClassLoader();
        final File csvFile = new File(classLoader.getResource("csv/SimudynePlatformTestData.csv").getFile());

        ModelSimulatingEngine engine = new ModelSimulatingEngine();

        BlockingQueue<Agent> modelProcessingEngineQueue = engine.getAgentInputQueue();
        new Thread(new CsvReader(csvFile , modelProcessingEngineQueue)).start();



    }
}
