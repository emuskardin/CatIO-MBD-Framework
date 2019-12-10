package experiments;

import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealyBuilder;
import de.learnlib.api.SUL;
import de.learnlib.api.algorithm.LearningAlgorithm;
import de.learnlib.api.oracle.EquivalenceOracle;
import de.learnlib.api.query.DefaultQuery;
import de.learnlib.api.statistic.StatisticSUL;
import de.learnlib.drivers.reflect.MethodInput;
import de.learnlib.drivers.reflect.MethodOutput;
import de.learnlib.drivers.reflect.SimplePOJOTestDriver;
import de.learnlib.filter.cache.sul.SULCaches;
import de.learnlib.filter.statistic.sul.ResetCounterSUL;
import de.learnlib.oracle.equivalence.mealy.RandomWalkEQOracle;
import de.learnlib.oracle.membership.SULOracle;
import de.learnlib.util.Experiment;
import de.learnlib.util.statistics.SimpleProfiler;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.serialization.dot.GraphDOT;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Word;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Random;

@SuppressWarnings("Duplicates")
public class LearningDriver {
    private SimplePOJOTestDriver driver;
    private StatisticSUL<MethodInput, MethodOutput> statisticSul;
    private SUL<MethodInput, MethodOutput> effectiveSul;
    private LearningAlgorithm.MealyLearner<MethodInput, MethodOutput> learningAlgorithm;
    private EquivalenceOracle.MealyEquivalenceOracle<MethodInput, MethodOutput> equivalenceOracle;
    private Experiment.MealyExperiment<MethodInput, MethodOutput> experiment;
    private MealyMachine<?, MethodInput, ?, MethodOutput> mealyMachine;

    public LearningDriver() throws NoSuchMethodException {
        this.driver = new SimplePOJOTestDriver(Robot.class.getConstructor());
    }

    private void createLearningAlphablet() throws NoSuchMethodException {

        // create learning alphabet
        Method straight =      Robot.class.getMethod("driveStraight");
        Method left =     Robot.class.getMethod("driveLeft");
        Method right =      Robot.class.getMethod("driveRight");
        Method leftOk =    Robot.class.getMethod("leftOk");
        Method rightOK =   Robot.class.getMethod("rightOk");
        Method leftFaster =    Robot.class.getMethod("leftFaster");
        Method rightFaster =   Robot.class.getMethod("rightFaster");
        Method leftSlower =    Robot.class.getMethod("leftSlower");
        Method rightSlower =   Robot.class.getMethod("rightSlower");
        Method doStep =   Robot.class.getMethod("doStep");


        //inputs
        driver.addInput("straight", straight);
        driver.addInput("left", left);
        driver.addInput("right", right);
        driver.addInput("leftOk", leftOk);
        driver.addInput("rightOk", rightOK);
        driver.addInput("leftFaster", leftFaster);
        driver.addInput("rightFaster", rightFaster);
        driver.addInput("leftSlower", leftSlower);
        driver.addInput("rightSlower", rightSlower);
        driver.addInput("doStep", doStep);

    }

    private void instanceSUL(){
        statisticSul = new ResetCounterSUL<>("membership queries", driver);
        effectiveSul = statisticSul;
        effectiveSul = SULCaches.createCache(driver.getInputs(), effectiveSul);
    }

    private void createLearner(){
        SULOracle<MethodInput, MethodOutput> mqOracle = new SULOracle<>(effectiveSul);
        learningAlgorithm = new ExtensibleLStarMealyBuilder<MethodInput, MethodOutput>()
                .withAlphabet(driver.getInputs()) // input alphabet
                .withOracle(mqOracle) // membership oracle
                .create();
    }

    private void createEqivalanceOracle(){
        final double RESET_PROBABILITY = 0.05;
        final int MAX_STEPS = 10000;
        final int RANDOM_SEED = 46346293;
        equivalenceOracle = new RandomWalkEQOracle<>(driver, // system under learning
                RESET_PROBABILITY, // reset SUL w/ this probability before a step
                MAX_STEPS, // max steps (overall)
                false, // reset step count after counterexample
                new Random(RANDOM_SEED) // make results reproducible
        );
    }

    private void runExperiment(){
        experiment = new Experiment.MealyExperiment<>(learningAlgorithm, equivalenceOracle, driver.getInputs());
        experiment.setProfile(true);
        experiment.setLogModels(true);
        experiment.run();
        mealyMachine = experiment.getFinalHypothesis();
    }


    public void printResults() throws IOException {
        // profiling
        System.out.println(SimpleProfiler.getResults());

        // learning statistics
        System.out.println(experiment.getRounds().getSummary());
        System.out.println(statisticSul.getStatisticalData().getSummary());

        // model statistics
        System.out.println("States: " + mealyMachine.size());
        System.out.println("Sigma: " + driver.getInputs().size());

        System.out.println("Model: ");
        GraphDOT.write(mealyMachine, driver.getInputs(), System.out); // may throw IOException!
        Visualization.visualize(mealyMachine, driver.getInputs());
    }

    public DefaultQuery<MethodInput, Word<MethodOutput>> findCounterExamples(LearningDriver driver) throws NoSuchMethodException {
        createLearningAlphablet();
        instanceSUL();
        createLearner();
        createEqivalanceOracle();
        DefaultQuery<MethodInput, Word<MethodOutput>> cE =
                equivalenceOracle.findCounterExample(driver.getExperiment().getFinalHypothesis(), driver.getDriver().getInputs());
        if( cE != null) {
            System.out.println("CounterExample found");
        }
        return cE;
    }

    public void quickSetup() throws NoSuchMethodException, IOException {
        createLearningAlphablet();
        instanceSUL();
        createLearner();
        createEqivalanceOracle();
        runExperiment();
        printResults();
    }

    public SimplePOJOTestDriver getDriver() {
        return driver;
    }

    public Experiment.MealyExperiment<MethodInput, MethodOutput> getExperiment() {
        return experiment;
    }

    public MealyMachine<?, MethodInput, ?, MethodOutput> getMealyMachine() {
        return mealyMachine;
    }
}