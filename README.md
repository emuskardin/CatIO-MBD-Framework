# Framework for model based diagnosis
Framework for creating models for diagnosis, as well as testing their quality using co-simulation. 
Consisteny based and abductive diagnosis are possible.<br>

#Interaction with FMIs
FMI can either be standard test benches of form
```
model Testbench
    SUT sys;
equation
    if(time < t1) then 
        ....  // First inputs
    elsif (time >= t1andtime < t2) then 
        .... // Next inputs
    elsif 
        ....
    else
        ....
    end if;
end Testbench;
```
or input oriented models of form
```
model SUT
    input FaultTypeConnector modeAssigmenent_1;
    ...
    input FaultTypeConnector modeAssigmenent_n;
    RealInput modelRealValue_1;
    ...
    BooleanInput modelBooleanValue_1;
equation
    ...
end SUT;
```
Once test becnh or input model is created, its pathname is passed as 
```
public FmiMonitor(String pathToFmi);
```
to class FmiMonitor, which will read defined values from the running simulation.
If test bench with no inputs is given, diagnosis can be run directly on it, as test bench creates a simulation on the SUT.
If input oriented model is given, simulations need to be created in the framework and passed to diagnosis drivers. 

##ModelData
Model is described in data class ModelData.
Each model consist of 
* Components which are going to be read during simulation
* Mode Assigment Variables
* Parameters of the model
* Inputs of the model

Component is a data class representing each component in modelica model, and it consist of name, type and value.
```java
public class Component {
    private String name;
    private Type type;
    private Object value;

    public Component(String name, Type type);  
}

public enum Type {
    STRING,
    BOOLEAN,
    DOUBLE,
    INTEGER,
    ENUM
}

```
GUI form FmiDataExtractor can be used for easier extraction of data.
If values are provided to mode assigment variables, parameters and inputs mixed level covering array can be created for the model and used for automatic generation of abductive model.

##Simulation form
Simulation scenario is a Map<Integer, List<Component>>, where Integer is time step at which List<Component> values will be written to the simulation.
Scenario has to have key value 0, representing values which will be written at initial step to avoid undefined behaviour.

Simulations can also be created from JSON if form
```json
{
    "scenarioId": "first",
    "timeCompMap": {
      "0": [
        {
          "name": "leftFaultType",
          "type": "ENUM",
          "value": "ok"
        },
        {
          "name": "rightFaultType",
          "type": "ENUM",
          "value": "ok"
        },
        {
          "name": "rightWheelInput",
          "type": "DOUBLE",
          "value": "3"
        },
        {
          "name": "leftWheelInput",
          "type": "DOUBLE",
          "value": "3"
        }
      ],
      "5": [
        {
          "name": "leftFaultType",
          "type": "ENUM",
          "value": "faster"
        }
      ],
      "10": [
        {
          "name": "leftFaultType",
          "type": "ENUM",
          "value": "ok"
        }
      ]
    }
  }
```

##MLCA
MLCA class takes model data as constuctor. Following methods describe its use
```
    MLCA mlca = new MLCA(someModelData);
    \\ optional addition of constraints, relations or minimal number of correct components
    mlca.addRelationToGroup(mlca.get{params,inputs,modeAssigments}, 3);
    mlca.addConstraint("this is come contraint");
    mlca.numberOfCorrectComps(2); \\ or list of numbers 3, 4
    mlca.createTestSuite("testSuite.csv");
    List<List<Component>> simulationInputs = mlca.suitToSimulationInput("testSuite.csv");
```
#Consistency Based Diagnosis
Model
```
rightWheel & leftWheel.
!AbRightWheel & !AbLeftWheel.
rightWheel -> (!AbRightWheel -> rightNominal & rightFaster & rightSlower).
leftWheel -> (!AbLeftWheel -> leftNominal & leftFaster & leftSlower).
(rightNominal & leftNominal) -> straight.
(rightFaster & leftSlower) -> left.
(rightSlower & leftFaster) -> right.
```
Encoder
```java
public class BookCarEncoder implements Encoder {
    @Override
    public List<String> encodeObservation(Map<String, Object> obs){
        //...
        return obsrvationsInPropVars;
    }
}
```
Diagnosis TYPES
```java
public enum ConsistencyType{
    STEP, // returns diagnosis of observations at every time stpe
    PERSISTENT, // assumes fault are permanent and returns diagnosis at the end of simulation
    INTERMITTENT, // assumes fault are intermittent and returns diagnosis at the end of simulation
    STEP_INTERMITTENT // same as intermittent, but with reduced runtime due to different approach
}
```
Diagnosis Driver
```
FmiMonotor fmiMonitor = new FmiMonitor("pathToTestBench.fmi");
CbModel model = new CbModel("modelFile.txt");
ConsistencyDriver consistencyDriver = ConsistencyDriver.builder()
                .fmiMonitor(fmiMonitor)
                .model(model)
                .encoder(new BookCarEncoder())
                .modelData(md)
                .numberOfSteps(20)
                .simulationStepSize(1)
                .build();

consistencyDriver.runDiagnosis(ConsistencyType.INTERMITTENT);
```

