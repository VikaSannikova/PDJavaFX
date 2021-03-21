package approximation;

public class Request {
    double time;

    public Request(double time) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void addTime(double time) {
        this.time += time;
    }

    @Override
    public String toString() {
        return String.valueOf(time);
    }
}
