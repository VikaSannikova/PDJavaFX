package approximation;

import javafx.scene.layout.AnchorPane;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
        double diffArea  = this.getRealArea() - this.getMyArea();
        double height = Math.abs(diffArea / length);
        if ( diffArea >= 1) {

            for (int i = 0; i < intensities.size(); i++) {
                intensities.set(i, intensities.get(i) + height);
            }
        } else if (diffArea <= -1) {
            for (int i = 0; i < intensities.size(); i++) {
                intensities.set(i, intensities.get(i) - height);
            }
        }


//        if (this.getRealArea() - this.getMyArea() >= 1) {
//            double area = this.getRealArea() - this.getMyAreaWithoutLast();
//            intensities.set(length - 1, area/(T/length));
//        } else if (this.getRealArea() - this.getMyArea() <= -1) {
//            double area = this.getMyArea() - this.getRealArea();
//            intensities.set(0, intensities.get(0) - area/(T/length));
//        }
    }

    void createAverage() {
        average = 0;
        for (Double intensity : intensities) {
            average += intensity;
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

    public double getMyArea() {
        double result = 0.0;
        for (Double intensity : intensities) {
            result += intensity * (T / length);
        }
        return  result;
    }

    public double getMyAreaWithoutLast() {
        double result = 0.0;
        for (int i = 0; i < intensities.size() - 1; i++) {
            result += intensities.get(i) * (T / length);
        }
        return result;
    }

    public double getMyAreaWithoutFirst() {
        double result = 0.0;
        for (int i = 1; i < intensities.size(); i++) {
            result += intensities.get(i) * (T / length);
        }
        return result;
    }

    public double getMyAreaWithoutMax() {
        var max = Collections.max(intensities);
        return  getMyArea() - max * (T / length);
    }

    public double getMyFirstArea() {
        return intensities.get(0) * (T / length);
    }

    public double getRealArea(){
        SimpsonIntegrator si = new SimpsonIntegrator();
        return si.integrate(100, x -> formula.f(x) , 0, T);
    }

    public static void main(String[] args) {
        // тестовые данные
        Formula f = new Formula("x^2");
        Intensity intensity = new Intensity(9, 10, new Formula("sin(x)+100"));
        System.out.println("TIMES: " + intensity.getDeltatimes());
        System.out.println("INTERVALS: " + intensity.getIntervals());
        System.out.println("INTENSITIES: " + intensity.getIntensities());
        System.out.println("AVG: " + intensity.getAverage());
        System.out.println("MY MAX AREA: " + intensity.getMyArea());
        System.out.println("REAL MAX AREA: " + intensity.getRealArea());
    }
}
