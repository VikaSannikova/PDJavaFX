package approximation;

import java.math.BigDecimal;
import java.util.ArrayList;

public interface Distribution {

    void setA();

    double sum(ArrayList<Double> arr);

    int returnNum(double u, ArrayList<Range> ranges);

    void setP();

    double getU();

    ArrayList<Range> getIntervals();
}
