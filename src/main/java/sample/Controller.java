package sample;

import approximation.Formula;
import approximation.Loop;
import approximation.Thread;
import com.gluonhq.charm.glisten.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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
        });

        final Loop[] loopGeneral = new Loop[1];
        startButtonId.setOnAction(actionEvent -> {
            loopTime = Double.parseDouble(loopTimeTfId.getText());
            iterationCount = Integer.parseInt(iterNumTfId.getText());
            oneRequestTime = Double.parseDouble(oneRequestTimeTfId.getText());

            loopGeneral[0] = new Loop( new ArrayList<Thread>(threadsTable.getItems()), loopTime, oneRequestTime);
            try {
                loopGeneral[0].start(iterationCount);
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

        });
    }

}
