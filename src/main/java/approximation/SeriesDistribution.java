package approximation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

public class SeriesDistribution implements Distribution {
    ArrayList<Integer> a = new ArrayList<>();
    ArrayList<Range> intervals = new ArrayList<>();
    ArrayList<Double> p = new ArrayList<>();
    double u;

    public SeriesDistribution() {
        setU();
        setA();
        setP();
        setIntervals();
    }

    public ArrayList<Integer> getA() {
        return a;
    }

    public ArrayList<Range> getIntervals() {
        return intervals;
    }

    public ArrayList<Double> getP() {
        return p;
    }

    public double sum (ArrayList<Double> arr){
        double sum = 0;
        for(int i = 0; i < arr.size(); i++){
            sum += arr.get(i);
        }
        return sum;
    }

    public void setA() {
        for(int i = 0; i < 36; i++){
            a.add(i);
        }
    }

    public void setIntervals(){
        intervals = new ArrayList<>(a.size());
        Range range = new Range(0, p.get(0));
        intervals.add(range);
        for (int i = 1; i < a.size(); i++) {
            BigDecimal left = BigDecimal.valueOf(intervals.get(i - 1).getRight());
            BigDecimal _p = BigDecimal.valueOf(p.get(i));
            BigDecimal right = left.subtract(_p.negate());
            intervals.add(new Range(left.doubleValue(), right.doubleValue()));
        }
    }

    public void setP() {
        Random randNumber = new Random();
        int size = 200;
        int[] series = new int[size];
        for (int i = 0; i < size; i++) {
            series[i] = randNumber.nextInt(a.size());
        }
        int[] count = new int[a.size()];
        for (int i = 0; i < 200; i++) {
            count[series[i]] += 1;
        }
        for (int i = 0; i < a.size(); i++) {
            p.add(i, (double) count[i] / size);
        }
    }

    public double getU() {
        return u;
    }

    public void setU() {
        this.u = Math.random();
    }

    public int returnNum(double u, ArrayList<Range> ranges){
        for (int i = 0; i < ranges.size(); i++) {
            if (u > ranges.get(i).getLeft() && u <= ranges.get(i).getRight()) { //равенство с 1 из сторон!!!!
                return a.get(i);
            }
        }
        return 100;
    }

    public static void main(String[] args) {

        SeriesDistribution sd;
        sd = new SeriesDistribution();

        System.out.println("Сгенерированное число из [0,1]: " + sd.getU());
        System.out.println("А: " + sd.getA());
        System.out.println("P: " + sd.getP());
        System.out.println("Интервалы: " + sd.getIntervals());
        System.out.println("Число заявок: " + sd.returnNum(sd.u, sd.intervals));

    }
}
