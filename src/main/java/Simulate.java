import agentinput.CsvReader;
import engine.ModelSimulatingEngine;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Simulate {
    public static void main(String[] args) throws InterruptedException, TimeoutException, ExecutionException {
        ClassLoader classLoader = Simulate.class.getClassLoader();
        final File csvFile = new File(classLoader.getResource("csv/SimudynePlatformTestData.csv").getFile());

        ModelSimulatingEngine engine = new ModelSimulatingEngine();
        new Thread(new CsvReader(csvFile , engine)).start();

        engine.start();
    }
}
