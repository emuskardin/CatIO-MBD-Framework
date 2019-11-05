package abductive;

import java.util.List;
import java.util.Map;

public interface AbModelEncoderContract {
    AbductiveModel model = new AbductiveModel();

    void constructModel();

    List<String> encodeObservations(Map<String, Object> obs);

    default AbductiveModel getModel() { return model; }

}
