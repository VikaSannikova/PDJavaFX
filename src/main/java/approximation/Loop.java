package approximation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Loop {

    ArrayList<Thread> threads; // потоки
    Double loopTime; //время петли
    Double param;
    ArrayList<Double> avgQueues = new ArrayList<>(); //средние очереди по потокам
    ArrayList<String> avgTimes = new ArrayList<>(); //средние времена по потокам
    int iter; //итерация переходного процесса
    int loopIter; //число заходов в петлю
    ArrayList<ArrayList<Double>> stats = new ArrayList<>();
    ArrayList<ArrayList<Double>> deltaStats = new ArrayList<>();
    ArrayList<ArrayList<Double>> allDeltaThreadStat = new ArrayList<>();
    ArrayList<Double> sintZero = new ArrayList<>();
    ArrayList<Double> sintInf = new ArrayList<>();
    ArrayList<ArrayList<Double>> threadsQ = new ArrayList<>(); //для динамики средних очередей по итерациям


    public Loop(ArrayList<Thread> threads, Double loopTime, double timeForOne) {
        System.out.println("\nСТАРТ ИНИЦИАЛИЗАЦИИ");
        this.threads = threads;
        for (Thread thread : this.threads) {
            thread.setTimeForOne(timeForOne);
        }
        this.loopTime = loopTime;
        this.param = 0.0;
        for (int i = 0; i < threads.size(); i++) { // средние очереди по потокам 0
            this.avgQueues.add(0.0);
            this.avgTimes.add("");
        }
        this.iter = 0;
        this.loopIter = 0;
        System.out.println("Количество массивов " + threads.size());
        System.out.println("Их длнина равна максимальному числу заявок, котовые могут прийти за ЗС по каждому из потоков.");
        System.out.println("+1 будет добавляться в соответствующий элемент, если пришло кол-во заявок равное индексу");
        System.out.println("Данные необходимы для подсчта статистики и вывода статистических рядов");
        for (int i = 0; i < threads.size(); i++) { // заполняем 0 массивы, длина которых равна максимальному числу заявок
            ArrayList<Double> arr = new ArrayList<>();
            for (int j = 0; j <= threads.get(i).getMaxDoneApps(); j++) {
                arr.add(0.0);
            }
            System.out.println(arr);
            stats.add(arr);
        }
        System.out.println("Теперь заведем массиы для 'дельт', получеснных в ходе аппроксимации");
        System.out.println("Длина каждого из массивов равна кол-ву максимально возможному числу обслуженных заявок за дробленый отрезок");
        System.out.println("Первый поток приоритетный, с постоянной интенсивностью обслуживание - не аппроксимируем");
        for (int i = 0; i < threads.size(); i++) {
            for (int j = 0; j < threads.get(i).getMaxDoneAppsDeltas().size(); j++) {
                ArrayList<Double> arr = new ArrayList<>();
                for (int k = 0; k <= threads.get(i).getMaxDoneAppsDeltas().get(j); k++) {
                    arr.add(0.0);
                }
                System.out.println("Для " + i + " потока за " + j + "  дельту " + arr);
                deltaStats.add(arr); // это массивы, длина которых равна максимально мозможному числу заявок обслуженных на маленьком отрезке
            }
        }
    }

    public ArrayList<Thread> getThreads() {
        return threads;
    }

    public Double getLoopTime() {
        return loopTime;
    }

    public Double getParam() {
        return param;
    }

    public ArrayList<Double> getAvgQueues() {
        return avgQueues;
    }

    public void setAvgQueues(ArrayList<Double> avgQueues) {
        this.avgQueues = avgQueues;
    }

    public ArrayList<String> getAvgTimes() {
        return avgTimes;
    }

    public int getIter() {
        return iter;
    }

    public int getLoopIter() {
        return loopIter;
    }

    public ArrayList<ArrayList<Double>> getStats() {
        return stats;
    }

    public ArrayList<Double> getSintZero() {
        return sintZero;
    }

    public void setSintZero(ArrayList<Double> sintZero) {
        this.sintZero = sintZero;
    }

    public ArrayList<Double> getSintInf() {
        return sintInf;
    }

    public void setSintInf(ArrayList<Double> sintInf) {
        this.sintInf = sintInf;
    }

    public void setStats(ArrayList<ArrayList<Double>> stats) {
        this.stats = stats;
    }

    public ArrayList<ArrayList<Double>> getDeltaStats() {
        return deltaStats;
    }

    public void setDeltaStats(ArrayList<ArrayList<Double>> deltaStats) {
        this.deltaStats = deltaStats;
    }

    public ArrayList<ArrayList<Double>> getAllDeltaThreadStat() {
        return allDeltaThreadStat;
    }

    public void setAllDeltaThreadStat(ArrayList<ArrayList<Double>> allDeltaThreadStat) {
        this.allDeltaThreadStat = allDeltaThreadStat;
    }



    public void start(int numOfIterations) throws CloneNotSupportedException {
        boolean flag = false;
        System.out.print("Начальные очереди по потокам: ");
        for (Thread th : threads) { // выводим начальные очереди
            System.out.print (th.getQueue() +" ");
        }
        System.out.println();
        System.out.print("Начальные очереди по ZERO потокам: ");

        ArrayList<Thread> threadsZero = new ArrayList<>(); //начальные очереди для zero(запуск с 0 очередями)
        for (Thread th : threads) {
            threadsZero.add((Thread) th.clone());
        }
        for (Thread th : threadsZero) {
            th.setZeroQueue();
            th.getAll_requests().clear();
            System.out.print (th.getQueue() +" ");
        } // статистики, размер 3, длина каждого равна максимальному числу обслуженных заявок
        System.out.println();

        Integer[] arr = new Integer[threads.size()]; //считает суммму по полным очередям
        Integer[] arrZero = new Integer[threadsZero.size()]; //считает сумму по нулевым очередям

        ArrayList<Double> greenTimes = new ArrayList<>(); //зеленые света
        ArrayList<Double> yellowTimes = new ArrayList<>(); // желтые света
        for (int i = 0; i < threads.size(); i++) {
            arr[i] = threads.get(i).getQueue(); //забиваем начальные очереди
            arrZero[i] = threadsZero.get(i).getQueue(); //забиваем начальные очереди
            greenTimes.add(threads.get(i).getGreenTime()); //забиваем зеленый свет
            yellowTimes.add(threads.get(i).getYellowTime()); // забиваем переналадки
            threadsQ.add(new ArrayList<Double>()); // будет 3 массива
        }
        System.out.println("Текущие очереди по потокам " + Arrays.toString(arr));
        System.out.println("Текущие очереди по ZERO потокам " + Arrays.toString(arrZero));
        System.out.println("ЗС для потоков "+ greenTimes);
        System.out.println("ВП для потoков "+ yellowTimes);
        System.out.println("******************************************************");
        int numOfThreads = threads.size();
        for (int p = 0; p < numOfIterations; p++) {
            int k = 0;
            //работа до последнего зеленого света
            for (int i = 0; i < numOfThreads - 1; i++) {
                for (int j = 0; j < numOfThreads; j++) {
                    if (j == k) {
                        System.out.println("РАБОТА В ЗС " + i + " ZERO ПОТОКА " + j +" :");
                        threadsZero.get(j).createQueue();
                        System.out.println("-----");
                        System.out.println("РАБОТА В ЗС " + i + " ПОТОКА " + j +" :");
                        threads.get(j).createQueue();
                        for (int r = 0; r < threads.get(j).realDoneAppsStats.length; r++) {
                            if (j == 0) { // добавляем в статистику за каждый маленький промежуток времени
                                deltaStats.get(j).set(threads.get(j).realDoneAppsStats[r], deltaStats.get(j).get(threads.get(j).realDoneAppsStats[r]) + 1.0); // единичку ставит на числе заявок
                            }
                            if (j > 0) {
                                deltaStats.get(j * (threads.get(j).getNumOfPoints() + 1) - 9 + r).set(threads.get(j).realDoneAppsStats[r], deltaStats.get(j * (threads.get(j).getNumOfPoints() + 1) - 9 + r).get(threads.get(j).realDoneAppsStats[r]) + 1.0);
                            }
                        }
                        // добавляем в статистику за большой промежуток времени
                        stats.get(j).set(threads.get(j).realDoneApps, stats.get(j).get(threads.get(j).realDoneApps) + 1.0);
                        System.out.println("Очередь " + j + " потока ZERO = " + threadsZero.get(j).getQueue() + " после ЗС " + i);
                        System.out.println("Очередь " + j + " потока = " + threads.get(j).getQueue() + " после ЗС " + i);
                    } else { //блок для работы потоков у которых сейчас не их  ЗС
                        System.out.println("РАБОТА В ЗС " + i + " ZERO ПОТОКА " + j +" :");
                        threadsZero.get(j).setCurrentTime(greenTimes.get(i)); // обслуживание за ЗС другого потока
                        threadsZero.get(j).createQueueWithoutService();
                        System.out.println("РАБОТА В ЗС " + i + " ПОТОКА " + j +" :");
                        threads.get(j).setCurrentTime(greenTimes.get(i));
                        threads.get(j).createQueueWithoutService();
                        System.out.println("ОЧЕРЕДЬ " + j + " потока ZERO = " + threadsZero.get(j).getQueue() + " после зеленого света " + i);
                        System.out.println("ОЧЕРЕДЬ " + j + " потока      = " + threads.get(j).getQueue() + " после зеленого света " + i);
                    }
                    //работа за время переналадок
                    System.out.println("-----------------");
                    System.out.println("РАБОТА В ВП " + i + " ZERO ПОТОКА " + j +" :");
                    threadsZero.get(j).setCurrentTime(yellowTimes.get(i));
                    threadsZero.get(j).createQueueWithoutService();
                    System.out.println("-----");
                    System.out.println("РАБОТА В ВП " + i + " ПОТОКА " + j +" :");
                    this.threads.get(j).setCurrentTime(yellowTimes.get(i));
                    this.threads.get(j).createQueueWithoutService();
                    System.out.println("ОЧЕРЕДЬ " + j + " потока ZERO = " + threadsZero.get(j).getQueue() + " после ВП " + i);
                    System.out.println("ОЧЕРЕДЬ " + j + " потока      = " + threads.get(j).getQueue() + " после ВП " + i);
                    System.out.println("-----------------");
                }
                k++;
            }
            //работа за последний зеленый свет
            for (int i = 0; i < numOfThreads - 1; i++) {
                System.out.println("РАБОТА В ЗС " + (numOfThreads - 1) + " ZERO ПОТОКА " + i +" :");
                threadsZero.get(i).setCurrentTime(greenTimes.get(numOfThreads - 1));
                threadsZero.get(i).createQueueWithoutService();
                System.out.println("-----");
                System.out.println("РАБОТА В ЗС " + (numOfThreads - 1) + " ПОТОКА " + i +" :");
                threads.get(i).setCurrentTime(greenTimes.get(numOfThreads - 1));
                threads.get(i).createQueueWithoutService();
                System.out.println("Очередь " + i + " потока ZERO = " + threadsZero.get(i).getQueue() + " после ЗС " + (numOfThreads - 1));
                System.out.println("Очередь " + i + " потока      = " + threads.get(i).getQueue() + " после ЗС " + (numOfThreads - 1));
                System.out.println("-----------------");
            }
            System.out.println("РАБОТА В ЗС " + (numOfThreads - 1) + " ZERO ПОТОКА " + (numOfThreads - 1) +" :");
            threadsZero.get(numOfThreads - 1).createQueue();
            System.out.println("-----");
            System.out.println("РАБОТА В ЗС " + (numOfThreads - 1) + " ПОТОКА " + (numOfThreads - 1) +" :");
            threads.get(numOfThreads - 1).createQueue();
            for (int r = 0; r < threads.get(numOfThreads - 1).realDoneAppsStats.length; r++) {
                deltaStats.get((numOfThreads - 1) * (threads.get(numOfThreads - 1).getNumOfPoints() + 1) - 9 + r).set(threads.get(numOfThreads - 1).realDoneAppsStats[r], deltaStats.get((numOfThreads - 1) * (threads.get(numOfThreads - 1).getNumOfPoints() + 1) - 9 + r).get(threads.get(numOfThreads - 1).realDoneAppsStats[r]) + 1.0);
            }
            //Добавка в статистику за ЗС последнего потока
            stats.get(numOfThreads - 1).set(threads.get(numOfThreads - 1).realDoneApps, stats.get(numOfThreads - 1).get(threads.get(numOfThreads - 1).realDoneApps) + 1.0);
            System.out.println("Очередь " + (numOfThreads - 1) + " потока ZERO = " + threadsZero.get(numOfThreads - 1).getQueue() + " после ЗС света " + (numOfThreads - 1));
            System.out.println("Очередь " + (numOfThreads - 1) + " потока      = " + threads.get(numOfThreads - 1).getQueue() + " после ЗС света " + (numOfThreads - 1));
            System.out.println("-----------------");
            //работа за петлю
            int s = 0;
            System.out.println("--- ПЕТЛЯ ZERO ---");
            while (threadsZero.get(0).getQueue() == 0) {
                for (int i = 0; i < numOfThreads - 1; i++) {
                    System.out.println("    РАБОТА В ПЕТЛЮ " + s + " ZERO ПОТОКА " + i +" :");
                    threadsZero.get(i).setCurrentTime(getLoopTime());
                    threadsZero.get(i).createQueueWithoutService();
                    System.out.println("Очередь ZERO потока " + i + " = " + threadsZero.get(i).getQueue() + " после петли " + s);
                    System.out.println("-----------------");
                }
                System.out.println("    РАБОТА В ПЕТЛЮ " + s + " ZERO ПОТОКА " + (numOfThreads - 1) +" :");
                threadsZero.get(numOfThreads - 1).setCurrentTime(getLoopTime()); //обслуживание последнего потока за время переналадки, дробим ли мы петлю
                threadsZero.get(numOfThreads - 1).createQueueInLoop();
                System.out.println("Очередь " + (numOfThreads - 1) + " ZERO потока = " + threadsZero.get(numOfThreads - 1).getQueue() + " после петли " + s);
                s++;
                System.out.println("-----------------");
            }
            s = 0;
            System.out.println("--- ПЕТЛЯ ---");
            while (threads.get(0).getQueue() == 0) {
                loopIter++;
                for (int i = 0; i < numOfThreads - 1; i++) {
                    System.out.println("    РАБОТА В ПЕТЛЮ " + s + " ПОТОКА " + i +" :");
                    this.threads.get(i).setCurrentTime(getLoopTime());
                    this.threads.get(i).createQueueWithoutService();
                    System.out.println("Очередь потока " + i + " = " + threads.get(i).getQueue() + " после петли " + s);
                    System.out.println("-----------------");
                }
                System.out.println("    РАБОТА В ПЕТЛЮ " + s + " ПОТОКА " + (numOfThreads - 1) +" :");
                this.threads.get(numOfThreads - 1).setCurrentTime(getLoopTime());
                this.threads.get(numOfThreads - 1).createQueueInLoop();
                System.out.println("Очередь " + (numOfThreads - 1) + " потока = " + threads.get(numOfThreads - 1).getQueue() + " после петли " + s);
                s++;
                System.out.println("-----------------");
            }
            //работа за последний красный свет
            for (int i = 0; i < numOfThreads; i++) {
                System.out.println("РАБОТА В ВП " + (numOfThreads - 1) + " ZERO ПОТОКА " + i +" :");
                threadsZero.get(i).setCurrentTime(yellowTimes.get(numOfThreads - 1));
                threadsZero.get(i).createQueueWithoutService();
                System.out.println("-----");
                System.out.println("РАБОТА В ВП " + (numOfThreads - 1) + " ПОТОКА " + i +" :");
                this.threads.get(i).setCurrentTime(yellowTimes.get(numOfThreads - 1));
                this.threads.get(i).createQueueWithoutService();
                System.out.println("Очередь " + i + " потока ZERO = " + threadsZero.get(i).getQueue() + " после желтого света " + (numOfThreads - 1));
                System.out.println("Очередь " + i + " потока = " + threads.get(i).getQueue() + " после желтого света " + (numOfThreads - 1));
                System.out.println("-----------------");
            }
            System.out.print("Очереди на новое начало цикла ZERO: ");
            for (int i = 0; i < numOfThreads; i++) {
                System.out.print(threadsZero.get(i).getQueue() + ", ");
            }
            System.out.print("\nОчереди на новое начало цикла     : ");
            for (Thread thread : threads) {
                System.out.print(thread.getQueue() + ", ");
            }
            System.out.println();
            for (int i = 0; i < numOfThreads; i++) { // замеряем очередь на начало  нового цикла?
                arr[i] += threads.get(i).getQueue(); //добавляем очередь на начало зс0
                arrZero[i] += threadsZero.get(i).getQueue(); //добавляем очередь на начало зс0
            }
            System.out.println("Сумма очередей ZERO по каждому из потоков на конец цикла " + p +": " + Arrays.toString(arrZero));
            System.out.println("Сумма очередей по каждому из потоков на конец цикла " + p + "     : " + Arrays.toString(arr));

            double param = 0.0;
            double paramZero = 0.0;
            double sumLambda = 0.0;
            for (int i = 0; i < numOfThreads; i++) {
                param += threads.get(i).getLambda() * arr[i] / (p + 1);
                paramZero += threadsZero.get(i).getLambda() * arrZero[i] / (p + 1);
                sumLambda += threads.get(i).getLambda();
                threadsQ.get(i).add((double) (arr[i] / (p + 1))); //рассчитываем среднюю очередь
            }
            paramZero /= sumLambda;
            sintZero.add(paramZero);
            param /= sumLambda;
            sintInf.add(param);
            System.out.println("ВЫВОД ПАРАМЕТРА");
            System.out.println(param + ", " + paramZero + ", " + (p + 1));
            System.out.println(Math.abs(param - paramZero) + " ? " + paramZero * 0.05); // отслеживание, подстановка 0.00000001, нарастание могло пройти
            if (p + 1 >= numOfIterations)
                System.out.println("Стационарность не была достигнута за " + numOfIterations + " итераций");
            else if ((Math.abs(param - paramZero) < param * 0.05) && (flag == false)) {
                iter = p + 1;
                flag = true;
            }

        }
        System.out.println("=======================================");
        System.out.println("ПО ПОТОКАМ НА КОНЕЦ РАБОТЫ ОУ В СУММЕ БЫЛИ ОЧЕРЕДИ: " + Arrays.toString(arr));
        for (int i = 0; i < numOfThreads; i++) {
            this.avgQueues.set(i, (double) (arr[i] / numOfIterations));
            this.avgTimes.set(i, new DecimalFormat("#0.0000").format(threads.get(i).getAvgTime()));
        }
        System.out.println("СРЕДНЯЯ ОЧЕРЕДЬ ЗА " + numOfIterations + " ИТЕРИЦИЙ: " + avgQueues);
        System.out.println("СРЕДНЕЕ ВРЕМЯ ЗА" + numOfIterations + "ИТЕРАЦИЙ: " + avgTimes);

        double sumLambda = 0.0;
        for (int i = 0; i < numOfThreads; i++) {
            param += threads.get(i).getLambda() * arr[i] / numOfIterations;
            sumLambda += threads.get(i).getLambda();
        }
        param /= sumLambda; //синтетическая характеристика для сравнивания
        System.out.println("Стационарность была достигнута на " + iter + " итерации");
        System.out.println("Число заходов в петлю " + loopIter);

        System.out.println("по дельтам вывод обслуженных:");
        for(Thread th: threads){
            System.out.println(Arrays.toString(th.getRealDoneAppsStats()));
        }




        System.out.println("по дельтам вывод обслуженных за итерации:");
        for (Thread th: threads){
            for (int i = 0; i<th.getNumOfPoints()+1;i++){
                th.getRealDoneAppsStats()[i]/=numOfIterations;
            }
            System.out.println(Arrays.toString(th.getRealDoneAppsStats()));
        }
        for (ArrayList<Double> elem : deltaStats) {
            for (int i = 0; i < elem.size(); i++) {
                elem.set(i, elem.get(i) / numOfIterations); // делим для получения частот(вероятностей)
            }
        }

        for (ArrayList<Double> elem : stats) {
            System.out.println(elem);
        }
        for (ArrayList<Double> elem : stats) {
            for (int i = 0; i < elem.size(); i++) {
                elem.set(i, elem.get(i) / numOfIterations);
            }
        }
        System.out.println("За " + numOfIterations + " получили статистический ряд: ");
        for (ArrayList<Double> elem : stats) {
            System.out.println(elem);
        }
        System.out.println("Ряды по каждому дальта: ");
        for (ArrayList<Double> elem : deltaStats) {
            System.out.println(elem);
        }
        System.out.println("Вывод всех штук: ");
        for (int i = 0; i < numOfThreads; i++) {
            ArrayList<Double> tmp = new ArrayList<>();
            if (i == 0) {
                tmp.addAll(deltaStats.get(0));
            }
            if (i > 0) {
                tmp = new ArrayList<>();
                for (int j = i * 10 - 9; j < (i + 1) * 10 - 9; j++) {
                    tmp.addAll(deltaStats.get(j));
                }
            }
            // данные для склейки за дельты
            allDeltaThreadStat.add(tmp);
        }

    }

    public void check() { //todo спросить на счет проверки, не меняются ли условия
        System.out.println();
        double sumTime = 0;
        for (Thread thr : threads) {
            sumTime += (thr.getGreenTime() + thr.getYellowTime());
        }
        for (int i = 0; i < threads.size(); i++) {
            if (threads.get(i).getLambda() * sumTime - threads.get(i).getAvgIntens() * threads.get(i).getGreenTime() < 0) { // умножаем на ЗС от потока
                System.out.println(threads.get(i).getLambda() + "*" + sumTime + "-" + threads.get(i).getAvgIntens() + "*" + threads.get(i).getGreenTime());
                System.out.println("Thread " + i + " is valid");
            }
        }
    }

    public ArrayList<ArrayList<Double>> getThreadsQ() {
        return threadsQ;
    }

    public void setThreadsQ(ArrayList<ArrayList<Double>> threadsQ) {
        this.threadsQ = threadsQ;
    }

    public static void main(String[] args) throws CloneNotSupportedException {
        List<Thread> list = new ArrayList<>();
        list.add(new Thread(0, 1, 0.01, 10.0, new Formula("5"), 5, 0));
        list.add(new Thread(1, 10, 0.3, 10.0, new Formula("-0.6x+7.4"), 5, 9));
        list.add(new Thread(2, 30, 1.0, 10.0, new Formula("-0.6x+7.4"), 5, 9));
        double tfo = 0.1;
        Loop loop = new Loop((ArrayList<Thread>) list, 10.0, tfo);
        loop.start(2);
        loop.check();
        System.out.println("Частоты для дельт");
        for (ArrayList<Double> arr : loop.deltaStats) {
            System.out.println(arr);
        }
        System.out.println(loop.threadsQ.get(0));
        System.out.println(loop.threadsQ.get(1));
        System.out.println(loop.threadsQ.get(2));
    }
}

