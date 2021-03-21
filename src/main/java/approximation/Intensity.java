package approximation;

import java.util.ArrayList;
//
// класс аппроксиматор, правые прямоугольники
//
public class Intensity {

    int length; //длина массивов, на 1 больше чем точе разрыва
    ArrayList<Double> deltatimes = new ArrayList<>(); //промежутки времени для аппроксимации
    ArrayList<Range> intervals = new ArrayList<>(); //интервалы времени
    ArrayList<Double> intensities = new ArrayList<>(); //интенсивности на каждом из интервалов
    double average;
    Formula formula;
    double T;

    public Intensity(int numOfPoints, double T, Formula formula) {
        this.length = numOfPoints + 1;
        this.formula = formula;
        double delta = T / length;
        this.T = T;
        for (int i = 0; i < length; i++) {
            deltatimes.add(delta);
        }
        createIntervals();
        createIntensity();
        createAverage();
    }

    public double getT() {
        return T;
    }

    void createIntervals() {
        for (int i = 0; i < length; i++) {
            Range range;
            if (i == 0) {
                range = new Range(0, deltatimes.get(0));
            } else
                range = new Range(intervals.get(i - 1).getRight(), intervals.get(i - 1).getRight() + deltatimes.get(i));
            intervals.add(range);
        }
    }

    void createIntensity() {
        for (int i = 0; i < length; i++) {
            intensities.add(formula.f(intervals.get(i).getLeft()));
        }
    }

    void createAverage() {
        average = 0;
        for (int i = 0; i < intensities.size(); i++) {
            average += intensities.get(i);
        }
        average /= intensities.size();
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ArrayList<Double> getDeltatimes() {
        return deltatimes;
    }

    public void setDeltatimes(ArrayList<Double> deltatimes) {
        this.deltatimes = deltatimes;
    }

    public ArrayList<Range> getIntervals() {
        return intervals;
    }

    public void setIntervals(ArrayList<Range> intervals) {
        this.intervals = intervals;
    }

    public ArrayList<Double> getIntensities() {
        return intensities;
    }

    public void setIntensities(ArrayList<Double> intensities) {
        this.intensities = intensities;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public static void main(String[] args) {
        // тестовые данные
        Intensity intensity = new Intensity(9, 10, new Formula("x^2"));
        System.out.println("TIMES: " + intensity.getDeltatimes());
        System.out.println("INTERVALS: " + intensity.getIntervals());
        System.out.println("INTENSITIES: " + intensity.getIntensities());
        System.out.println("AVG: " + intensity.getAverage());
    }
}
