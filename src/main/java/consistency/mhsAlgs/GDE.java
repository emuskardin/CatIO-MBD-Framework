package consistency.mhsAlgs;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class GDE {
    Set<Set<String>> noGoods;
    public List<Set<String>> getMUS(){
        List<Set<String >> mhs = new ArrayList<>();
        for(Set<String> conflict : noGoods){
            if (mhs.isEmpty())
                mhs.add(conflict);

        }
        return null;
    }
}
