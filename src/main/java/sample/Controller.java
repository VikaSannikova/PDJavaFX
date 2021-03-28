package sample;

import approximation.Formula;
import approximation.Loop;
import approximation.Thread;
import com.gluonhq.charm.glisten.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Controller {
    ObservableList<Thread> threads = FXCollections.observableArrayList();
    double loopTime;
    int iterationCount;
    double oneRequestTime;

    @FXML
    private Label error_input_label;

    @FXML
    private TableView<Thread> threadsTable;

    @FXML
    private TableColumn<Thread, Integer> threadNumColId;

    @FXML
    private TableColumn<Thread, Integer> defaultQueueColId;

    @FXML
    private TableColumn<Thread, Double> lambdaColId;

    @FXML
    private TableColumn<Thread, Double> greenTimeColId;

    @FXML
    private TableColumn<Thread, Formula> muColId;

    @FXML
    private TableColumn<Thread, Double> yellowTimeColId;

    @FXML
    private Button addButtonId;

    @FXML
    private Button deleteButtonId;

    @FXML
    private Button startButtonId;

    @FXML
    private Button drawStatisticGraphicsButtonId;

    @FXML
    private Button statisticSeriesButtonId;

    @FXML
    private Button manyStatisticSeriesButtonId;

    @FXML
    private TextField loopTimeTfId;

    @FXML
    private TextField iterNumTfId;

    @FXML
    private TextField oneRequestTimeTfId;

    @FXML
    private Label stationaityId;

    @FXML
    private Label loopYellowCounId;

    @FXML
    private Label queuesId;

    @FXML
    void initialize() {
        threadNumColId.setCellValueFactory(new PropertyValueFactory<>("id"));
        defaultQueueColId.setCellValueFactory(new PropertyValueFactory<>("queue"));
        lambdaColId.setCellValueFactory(new PropertyValueFactory<>("lambda"));
        greenTimeColId.setCellValueFactory(new PropertyValueFactory<>("greenTime"));
        muColId.setCellValueFactory(new PropertyValueFactory<>("formula"));
        yellowTimeColId.setCellValueFactory(new PropertyValueFactory<>("yellowTime"));

        threads.add(new Thread(0,0,0.01,10, new Formula("5"),5, 0 ));
        threads.add(new Thread(1, 10, 0.3, 10, new Formula("-0.6x+7.4"), 5, 9));
        threads.add(new Thread(2, 40, 1, 10, new Formula("-0.6x+7.4"), 5, 9));
        threadsTable.setItems(threads);

        addButtonId.setOnAction(actionEvent -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Objects.requireNonNull(getClass().getClassLoader().getResource("newThread.fxml")));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            NewThreadController newThreadController = loader.getController();
            Parent root = loader.getRoot();
            Stage dialog = new Stage();
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.showAndWait();
            threads.add(newThreadController.getNew_thread());
            threadsTable.setItems(threads);
            startButtonId.setDisable(false);
        });

        final Loop[] loopGeneral = new Loop[1];
        startButtonId.setOnAction(actionEvent -> {
            loopTime = Double.parseDouble(loopTimeTfId.getText());
            iterationCount = Integer.parseInt(iterNumTfId.getText());
            oneRequestTime = Double.parseDouble(oneRequestTimeTfId.getText());

            loopGeneral[0] = new Loop( new ArrayList<Thread>(threadsTable.getItems()), loopTime, oneRequestTime);
            try {
                loopGeneral[0].
                        start(iterationCount);
            } catch (CloneNotSupportedException e1) {
                e1.printStackTrace();
            }
            String str = "";
            int iter = loopGeneral[0].getIter();
            int yellowIter = loopGeneral[0].getLoopIter();
            ArrayList<Double> avgQueues = loopGeneral[0].getAvgQueues();
            str = "Стационарность достигнута: " + Integer.toString(iter);
            stationaityId.setText(str);
            str = "Число заходов в петлю: " + Integer.toString(yellowIter);
            loopYellowCounId.setText(str);
            str = "Очереди: " + avgQueues;
            queuesId.setText(str);
            loopGeneral[0].check();
            drawStatisticGraphicsButtonId.setDisable(false);
            statisticSeriesButtonId.setDisable(false);
            manyStatisticSeriesButtonId.setDisable(false);
        });
        drawStatisticGraphicsButtonId.setOnAction( actionEvent -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Objects.requireNonNull(getClass().getClassLoader().getResource("linechart.fxml")));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int rowCount = threadsTable.getItems().size();
            XYChart.Series <Integer, Double> []series = new XYChart.Series[2 + rowCount];
            series[0] = new XYChart.Series <Integer, Double>();
            series[0].setName("Нулевая");
            series[1] = new XYChart.Series <Integer, Double>();
            series[1].setName("Наибольшая");
            for(int i = 2; i < series.length; i++){
                String str = (i-1) + " очередь";
                series[i] = new XYChart.Series <Integer, Double>();
                series[i].setName(str);
            }
            for(int i = 0; i < iterationCount; i++){
                series[0].getData().add(new XYChart.Data<Integer, Double>(i, loopGeneral[0].getSintZero().get(i)));
                series[1].getData().add(new XYChart.Data<Integer, Double>(i, loopGeneral[0].getSintInf().get(i)));
                for(int j = 2; j < series.length; j++){
                    series[j].getData().add(new XYChart.Data<Integer, Double>(i, loopGeneral[0].getThreadsQ().get(j-2).get(i)));
                }
            }
            LineChartController lineChartController = loader.getController();
            lineChartController.setSeries(series);

            Parent root = loader.getRoot();
            Dialog dialog = new Dialog();
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
            closeButton.managedProperty().bind(closeButton.visibleProperty());
            closeButton.setVisible(false);
            dialog.show();
        });

        statisticSeriesButtonId.setOnAction(actionEvent -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Objects.requireNonNull(getClass().getClassLoader().getResource("barchart.fxml")));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int rowCount = threadsTable.getItems().size();
            XYChart.Series<String , Double>[] series = new XYChart.Series[rowCount-1];
            for(int k = 1; k < threadsTable.getItems().size(); k++){
                series[k-1] = new XYChart.Series <String , Double>();
                series[k-1].setName(String.valueOf(k));
                for(int i = 0; i < loopGeneral[0].getStats().get(k).size();i++) {
                    String str = String.valueOf(i);
                    series[k-1].getData().add(new XYChart.Data<String, Double>(str, loopGeneral[0].getStats().get(k).get(i)));
                }
            }

            BarChartController barChartController = loader.getController();
            barChartController.setSeries(series);


            Parent root = loader.getRoot();
            Dialog dialog = new Dialog();
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
            closeButton.managedProperty().bind(closeButton.visibleProperty());
            closeButton.setVisible(false);
            dialog.show();




        });

    }

































}
