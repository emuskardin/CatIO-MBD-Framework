package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Fault{
    Double startTime;
    Double duration;
    String compName;
    Integer faultIndex;
}
