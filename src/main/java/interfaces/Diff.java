package interfaces;

import FmiConnector.Component;

import java.util.List;

@FunctionalInterface
public interface Diff {
    public String encodeDiff(List<Component> corr, List<Component> faulty);
}
