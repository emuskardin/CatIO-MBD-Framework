package interfaces;

import FmiConnector.FmiConnector;

import java.util.List;

public interface Controller {
    void performAction(FmiConnector fmiConnector, List<String> diagnosis);
}
