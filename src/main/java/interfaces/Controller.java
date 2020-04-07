package interfaces;

import FmiConnector.FmiWriter;

import java.util.List;

public interface Controller {
    public void performAction(FmiWriter fmiWriter, List<String> diagnosis);
}
