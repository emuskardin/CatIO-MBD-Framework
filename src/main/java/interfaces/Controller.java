package interfaces;

import FmiConnector.FmiConnector;

import java.util.List;

public interface Controller {
    /**
     * @param fmiConnector fmiConnector used to write(and read) from current simulation
     * @param diagnosis single diagnosis or signal used to determine which action to take
     * @return remaining time steps of the action
     */
    int performAction(FmiConnector fmiConnector, List<List<String>> diagnosis);
}
