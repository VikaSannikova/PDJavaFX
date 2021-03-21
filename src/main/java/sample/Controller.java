package sample;

import approximation.Formula;
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
import java.util.Objects;

public class Controller {
    ObservableList<Thread> threads = FXCollections.observableArrayList();;

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
    }

}
