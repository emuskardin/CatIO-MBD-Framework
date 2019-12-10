package experiments;

import java.io.IOException;

public class ExpMain {
    public static void main(String[] args) throws NoSuchMethodException, IOException {
        //DAG dag = new DAG("src/main/java/experim/iscas7552.txt");
//        DAG dag = new DAG("src/main/java/experim/isccas_c17.txt");
//        dag.getLabels();
//        dag.parseInputOutputCNFNumbers("src/main/java/experim/iscas7552.cnf");
//        dag.createTC();
        LearningDriver learningDriver = new LearningDriver();
        learningDriver.quickSetup();
    }
}
