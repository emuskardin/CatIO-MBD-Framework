package consistency.SatSolver;

import consistency.stepFaultDiag.CbModel;
import lombok.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class PicoSAT {
    private String filename;
    BufferedWriter fr;
    CbModel cbModel;
    File file;

    private void addProblemLine(int numClauses, int numVars) throws IOException {
        fr.write("p cnf " + numVars + " " +  numClauses);
        fr.newLine();
    }

    private void addClause(int[] clause) throws IOException {
        StringBuilder sb = new StringBuilder();
        for(int i : clause)
            sb.append(i).append(" ");
        sb.append(0);
        fr.write(sb.toString());
        fr.newLine();
    }

    public PicoSAT(String pathToFile) throws IOException {
        filename = pathToFile;
        file = new File(filename);
        fr = new BufferedWriter(new FileWriter(file));
    }

    public void writeModelAndObsToFile(CbModel model, List<Integer> obs) throws IOException {
        this.cbModel = model;
        addProblemLine(model.getWorkingModel().size() + obs.size(), model.getNumOfDistinct());
        for(int[] clause : model.modelToIntArr())
            addClause(clause);
        for(Integer ob: obs)
            addClause(new int[]{ob});
        fr.close();
    }

    public List<Integer> getMUS() throws IOException {
        List<Integer> mhs = new ArrayList<>();
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"lib/picomus", filename};
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        // Read the output from the command
        String s;
        while ((s = stdInput.readLine()) != null) {
            String[] line = s.split(" ");
            if(line[0].equals("c") || line[1] == null)
                continue;
            if(line[0].equals("s") && !line[1].equals("UNSATISFIABLE"))
                return mhs;
            if (line[1].equals("UNSATISFIABLE"))
                continue;

            int var = Integer.parseInt(line[1]);
            if(var > cbModel.getWorkingModel().size() || var == 0)
                continue;
            if(cbModel.getAbPredicates().contains(cbModel.getWorkingModel().get(var - 1).get(0)))
                mhs.add(var - 1);
        }
        file.deleteOnExit();
        return mhs;
    }

}
