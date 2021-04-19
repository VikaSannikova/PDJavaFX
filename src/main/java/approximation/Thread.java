package approximation;


import java.util.ArrayList;
import java.util.LinkedList;

public class Thread implements Cloneable {
    int id; // номер потока
    int queue; // очередь по этому потоку
    //ArrayList<Integer> queues = new ArrayList<>();
    double lambda; // интенсивность поступления
    double greenTime; // время обслуживания

    Formula formula; // формула, интенсивность обслуживания
    double yellowTime; // время переналадки

    double currentTime;
    //Expression expression;
    int numOfPoints; //количество точек разрыва
    double avgIntens; //средняя интенсивность обслуживания
    int realDoneApps; // реальное число обслуженных заявок
    int maxDoneApps; // максимальное число, которое может быть обслужено
    int[] realDoneAppsStats; // статистика??? изначально 0, размер зависит от числа точек разрыва
    double timeForOne; // время на обслуживание одной заявки
    LinkedList<Request> all_requests;
    double allTimeServedRequests;
    double avgTime = 0.0;
    int all_requests_in_system;

    public LinkedList<Request> getAll_requests() {
        return all_requests;
    }

    public void setAll_requests(LinkedList<Request> all_requests) {
        this.all_requests = all_requests;
    }

    public Thread(int id, int queue, double lambda, double greenTime, Formula formula, double yellowTime, int numOfPoints) {
        this.id = id;
        this.queue = queue;
        this.lambda = lambda;
        this.greenTime = greenTime;
        this.formula = formula;
        this.yellowTime = yellowTime;
        this.currentTime = 0.0;
        this.numOfPoints = numOfPoints;
        realDoneAppsStats = new int[numOfPoints + 1];
        for (int i = 0; i < numOfPoints + 1; i++) {
            realDoneAppsStats[i] = 0;
        }
        all_requests = new LinkedList<>();
        for (int i = 0; i < this.queue; i++) { // время пребывания в очереди у изначальных заявок = 0
            all_requests.add(new Request(0.0));
        }
        System.out.println("начальное время заявок, находящихся в очереди на старте в потоке " + this.id + ": " + all_requests);
        this.allTimeServedRequests = 0.0;
        this.all_requests_in_system = 0;
        this.avgTime = 0.0;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Thread clone = (Thread) super.clone();
        clone.realDoneAppsStats = (int[]) realDoneAppsStats.clone();
        clone.all_requests = (LinkedList<Request>) all_requests.clone();
        return clone;
    }

    public double getTimeForOne() {
        return timeForOne;
    }

    public double getAllTimeServedRequests() {
        return allTimeServedRequests;
    }

    public void setAllTimeServedRequests(double allTimeServedRequests) {
        this.allTimeServedRequests = allTimeServedRequests;
    }

    public double getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(double avgTime) {
        this.avgTime = avgTime;
    }

    public int getAll_requests_in_system() {
        return all_requests_in_system;
    }

    public void setAll_requests_in_system(int all_requests_in_system) {
        this.all_requests_in_system = all_requests_in_system;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getYellowTime() {
        return yellowTime;
    }

    public void setYellowTime(double yellowTime) {
        this.yellowTime = yellowTime;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }

    public void setGreenTime(double greenTime) {
        this.greenTime = greenTime;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public void setNumOfPoints(int numOfPoints) {
        this.numOfPoints = numOfPoints;
    }

    public int getQueue() {
        return queue;
    }

    public double getGreenTime() {
        return greenTime;
    }

    public double getLambda() {
        return lambda;
    }

    public Formula getFormula() {
        return formula;
    }

    public int getNumOfPoints() {
        return numOfPoints;
    }

    public double getAvgIntens() {
        return avgIntens;
    }

    public void setAvgIntens(double avgIntens) {
        this.avgIntens = avgIntens;
    }

    public int getRealDoneApps() {
        return realDoneApps;
    }

    public void setRealDoneApps(int realDoneApps) {
        this.realDoneApps = realDoneApps;
    }

    public void setZeroQueue() {
        setQueue(0);
    }

    public ArrayList<Integer> getMaxDoneAppsDeltas() { //возвращает за каждый маленький дельта какое максимальное число заявок мб обслужено
        ArrayList<Integer> maxDoneAppsDeltas = new ArrayList<>(); //за каждый дальта t хранит максимально вохможное число обслуженных заявок
        Intensity intensity = new Intensity(getNumOfPoints(), getGreenTime(), getFormula());
        for (int i = 1; i < intensity.getIntervals().size() + 1; i++) {
            maxDoneAppsDeltas.add((int) (intensity.getIntervals().get(i - 1).getLength() * intensity.getIntensities().get(i - 1)));
        }
        return maxDoneAppsDeltas;
    }


    public Integer getMaxDoneApps() { //максимальное возможное число обслуженных заявок
        ArrayList<Integer> maxDoneApps = new ArrayList<>(); //ха каждый дальта t хранит максимально вохможное число обслуженных заявок
        Intensity intensity = new Intensity(getNumOfPoints(), getGreenTime(), getFormula());
        for (int i = 1; i < intensity.getIntervals().size() + 1; i++) {
            maxDoneApps.add((int) (intensity.getIntervals().get(i - 1).getLength() * intensity.getIntensities().get(i - 1)));
        }
        this.maxDoneApps = 0;
        for (int elem : maxDoneApps) {
            this.maxDoneApps += elem;
        }
        //System.out.println("Могло быть обслужено " + this.maxDoneApps +" заявок");
        return this.maxDoneApps;
    }

    public void setMaxDoneApps(int maxDoneApps) {
        this.maxDoneApps = maxDoneApps;
    }

    public int[] getRealDoneAppsStats() {
        return realDoneAppsStats;
    }

    public void setRealDoneAppsStats(int[] realDoneAppsStats) {
        this.realDoneAppsStats = realDoneAppsStats;
    }

    public void setTimeForOne(double timeForOne) {
        this.timeForOne = timeForOne;
    }


    public void createQueue() { //результат обсдуживания за зеленый свет
        Intensity intensity = new Intensity(getNumOfPoints(), getGreenTime(), getFormula());
        avgIntens = intensity.getAverage();
        ArrayList<Integer> queues = new ArrayList<>();
        ArrayList<Integer> realDoneApps = new ArrayList<>(); //будет хранить за каждый дельта t реально обслуженные заявки

        ArrayList<Integer> maxDoneApps = new ArrayList<>(); //за каждый дальта t хранит максимально вохможное число обслуженных заявок
        queues.add(getQueue());
        double TIME = 0.0;
        for (int i = 1; i < intensity.getIntervals().size() + 1; i++) {
            Distribution pd = new PoissonDistribution(getLambda(), intensity.getIntervals().get(i - 1).getLength());
            try {
                timeForOne = 1/intensity.intensities.get(i-1); // в числителе стоит длина маленького интервала?
                System.out.println("Время на одну заявку на " + (i - 1) + " интервале " + timeForOne);
            }  catch (Exception ex){
                timeForOne = 0;
            }
            int maxnum = (int) (intensity.getIntervals().get(i - 1).getLength() / timeForOne); //сколько может обслужиться по максимуму с явным вр обсл
            queues.add(Math.max(0,
                    queues.get(i - 1) + pd.returnNum(pd.getU(), pd.getIntervals()) -
                            Math.min((int) (intensity.getIntervals().get(i - 1).getLength() * intensity.getIntensities().get(i - 1)), maxnum)));
            for (int j = 0; j < queues.get(i - 1); j++) {
                all_requests.get(j).addTime(intensity.getIntervals().get(i - 1).getLength()); //TODO заменить на время маленького участка
            }
            System.out.println("Время проведенное старыми заявками потока " + this.id + " в системе на конец ЗС     " + all_requests); //время пребывания
            for (int j = 0; j < pd.returnNum(pd.getU(), pd.getIntervals()); j++) {
                all_requests.add(new Request(intensity.getIntervals().get(i - 1).getLength())); // TODO заменить на время маленького участка
            }
            System.out.println("Время проведенное всеми(старые + новые) заявками потока " + this.id + " на конец ЗС " + all_requests); // время пребывания
            realDoneApps.add(Math.min(queues.get(i - 1) + pd.returnNum(pd.getU(), pd.getIntervals()),
                    Math.min((int) (intensity.getIntervals().get(i - 1).getLength() * intensity.getIntensities().get(i - 1)), maxnum)));
            for (int j = 0; j < realDoneApps.get(i - 1); j++) {
                TIME += all_requests.remove().getTime();
            }
            System.out.println("После обслуживания в системе остались заявки с такими временами " + all_requests); // время пребывания
            realDoneAppsStats[i - 1] = 0;
            realDoneAppsStats[i - 1] = realDoneApps.get(i - 1); //+=

            maxDoneApps.add(Math.min((int) (intensity.getIntervals().get(i - 1).getLength() * intensity.getIntensities().get(i - 1)), maxnum));
            System.out.println(queues.get(i - 1) + "+" + pd.returnNum(pd.getU(), pd.getIntervals()) + "-" + Math.min((int) (intensity.getIntervals().get(i - 1).getLength() * intensity.getIntensities().get(i - 1)), maxnum));
        }
        this.queue = queues.get(queues.size() - 1);
        this.realDoneApps = 0;
        for (int elem : realDoneApps) {
            this.realDoneApps += elem;
        }
        this.maxDoneApps = 0;
        for (int elem : maxDoneApps) {

            this.maxDoneApps += elem;
        }
        System.out.println("Реально обслуженные заявки по дельтам: " + realDoneApps) ;
        System.out.println("Обслужилось " + this.realDoneApps + " заявок");
        System.out.println("Могло быть обслужено " + this.maxDoneApps + " заявок");
        System.out.println("Общее время заявок которые покинули сисему: " + TIME);
        System.out.println("Всего(покинули + еще в очереди) в ОУ было замечено заявок: " + (this.realDoneApps + this.queue));
        allTimeServedRequests +=TIME; // время тех, что вышли из системы
        all_requests_in_system += this.realDoneApps; //делить на обслуженные
        if (this.all_requests_in_system != 0) {
            avgTime = this.allTimeServedRequests /(this.all_requests_in_system);
        } else {
            avgTime = 0;
        }
        System.out.println("Среднее время пребывания заявок по потоку: " + avgTime);
    }


    public void createQueueWithoutService() { //результат обслуживание за желтый всет
        Distribution pd = new PoissonDistribution(getLambda(), getCurrentTime()); // при loop мы устанавливаем в кач-ве ЗС следующий желтый.
        for (Request request : all_requests) {
            request.addTime(getCurrentTime());
        }
        System.out.println("Время проведенное старыми заявками потока " + this.id + " в системе на конец этапа без обслуживания     " + all_requests);
        for (int i = 0; i < pd.returnNum(pd.getU(), pd.getIntervals()); i++) {
            all_requests.add(new Request(getCurrentTime()));
        }
        System.out.println("Время проведенное всеми(старые + новые) заявками потока " + this.id + " на конец этапа без обслеживания " + all_requests);
        System.out.print(getQueue() + "+" + pd.returnNum(pd.getU(), pd.getIntervals()) + "=");
        setQueue(getQueue() + pd.returnNum(pd.getU(), pd.getIntervals()));
        System.out.println(getQueue());
    }

    public void createQueueInLoop() {
        Distribution pd = new PoissonDistribution(getLambda(), getCurrentTime()); // при loop мы устанавливаем в кач-ве ЗС следующий желтый.
        try {
            timeForOne = 1/avgIntens; // в числителе стоит длина маленького интервала?
            System.out.println("Время на одну заявку " + timeForOne);
        }  catch (Exception ex){
            timeForOne = 0;
        }
        double intensity = getAvgIntens();
        ArrayList<Integer> realDoneApps = new ArrayList<>();
        ArrayList<Integer> maxDoneApps = new ArrayList<>();
        int maxnum = (int) (getCurrentTime() / timeForOne); //сколько может обслужиться по максимуму с явным вр обсл
        int queue  = Math.max(0, this.getQueue() + pd.returnNum(pd.getU(), pd.getIntervals()) - Math.min((int)(getCurrentTime() * intensity), maxnum));

        for (int i = 0; i < getQueue(); i++) {
            all_requests.get(i).addTime(getCurrentTime());
        }
        System.out.println("в конце этапа старые " + all_requests);
        for (int i = 0; i < pd.returnNum(pd.getU(), pd.getIntervals()); i++) {
            all_requests.add(new Request(getCurrentTime()));
        }
        System.out.println("в конце этапа новые " + all_requests);
        realDoneApps.add(Math.min(this.getQueue() + pd.returnNum(pd.getU(), pd.getIntervals()),
                Math.min((int) (getCurrentTime() * intensity), maxnum)));
        double TIME = 0.0;
        for (int j = 0; j < realDoneApps.get(0); j++) {
            TIME += all_requests.remove().getTime();
        }
        System.out.println("В конце этапа после обслуживания " + all_requests); // время пребывания
        //realDoneAppsStats[i - 1] = 0;
        //realDoneAppsStats[i - 1] = realDoneApps.get(i - 1); //+=

        maxDoneApps.add(Math.min((int) (getCurrentTime() * intensity), maxnum));
        System.out.println(queue + "+" + pd.returnNum(pd.getU(), pd.getIntervals()) + "-" + Math.min((int) (getCurrentTime() * intensity), maxnum));
        this.queue = queue;
        this.realDoneApps = 0;
        for (int elem : realDoneApps) {
            this.realDoneApps += elem;
        }
        this.maxDoneApps = 0;
        for (int elem : maxDoneApps) {
            this.maxDoneApps += elem;
        }
        System.out.println("Реально обслуженные заявки по дельтам: ");
        System.out.println(realDoneApps);
        System.out.println("Обслужилось " + this.realDoneApps + " заявок");
        System.out.println("Могло быть обслужено " + this.maxDoneApps + " заявок");
        System.out.println("Общее время: " + TIME);
        System.out.println("Всего в ОУ было замечено заявок: " + (this.realDoneApps + this.queue));
        allTimeServedRequests +=TIME; // время тех, что вышли из системы
        all_requests_in_system += this.realDoneApps; //делить на обслуженные
        avgTime = this.allTimeServedRequests /(this.all_requests_in_system);
        System.out.println("Среднее время пребывания заявок по потоку: " + avgTime);
    }


    public static void main(String[] args) {
        Formula formula = new Formula("x^2");
        Thread thread = new Thread(1, 2, 1, 10, formula, 10, 9);
        thread.createQueue();
        thread.setCurrentTime(thread.getYellowTime());
        thread.createQueueWithoutService();
        thread.createQueue();
        //System.out.println("QUEUES: " + thread.getQueues());
        System.out.println("FINAL QUEUE: " + thread.queue);
        System.out.println("AVG INTENSITY: " + thread.avgIntens);
        System.out.println("В сумме " + thread.realDoneApps);

        System.out.println("_______________________________");
        Thread thread1 = new Thread(2, 2, 1, 10, new Formula("x"), 10, 9);
        thread1.createQueue();
        System.out.println("FINAL: " + thread1.queue);
        System.out.println("AVG INTENSITY: " + thread1.avgIntens);
        System.out.println("В сумме " + thread1.realDoneApps);


    }

}

