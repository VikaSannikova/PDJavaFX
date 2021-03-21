package sample;

import approximation.Thread;
import com.gluonhq.charm.glisten.control.TextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Controller {
    public Thread thread;

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @FXML
    private Label error_input_label;

    @FXML
    private TableColumn<?, ?> threadNumColId;

    @FXML
    private TableColumn<?, ?> defaultQueueColId;

    @FXML
    private TableColumn<?, ?> lambdaColId;

    @FXML
    private TableColumn<?, ?> greenTimeColId;

    @FXML
    private TableColumn<?, ?> muColId;

    @FXML
    private TableColumn<?, ?> yellowTimeColId;

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

            System.out.println(newThreadController.getNew_thread().getQueue());
        });
    }

}
