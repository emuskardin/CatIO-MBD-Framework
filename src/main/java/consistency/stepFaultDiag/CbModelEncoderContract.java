package consistency.stepFaultDiag;

import org.logicng.io.parsers.ParserException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CbModelEncoderContract {
    CbModel model = new CbModel();

    // Construct the model which will be used for CBD
    void constructModel() throws IOException, ParserException;
    // Encode observations and return list of their representation
    List<String> encodeObservation(Map<String, Object> obs);

    // Default getters
    default CbModel getModel(){ return this.model; }
    default void clearModel() { model.clearModel();}
}
