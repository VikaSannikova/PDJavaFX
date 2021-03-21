package sample;

import approximation.Formula;
import approximation.Thread;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class NewThreadController {

    private Thread new_thread;


    @FXML
    private Button addThreadButtonId;

    @FXML
    private TextField threadNumberTfId;

    @FXML
    private TextField queueTfId;

    @FXML
    private TextField lambdaTfId;

    @FXML
    private TextField greenTimeTfId;

    @FXML
    private TextField muTfId;

    @FXML
    private TextField yellowTimeTfId;

    @FXML
    void initialize() {
        addThreadButtonId.setOnAction(actionEvent -> {
            int id = Integer.parseInt(threadNumberTfId.getText());
            int queue  = Integer.parseInt(queueTfId.getText());
            double lambda = Integer.parseInt(lambdaTfId.getText());
            double greenTime = Double.parseDouble(greenTimeTfId.getText());
            Formula formula = new Formula(muTfId.getText());
            double yellowTime = Double.parseDouble(yellowTimeTfId.getText());
            int numOfPoints = 9;
            new_thread = new Thread(id, queue, lambda, greenTime, formula, yellowTime, numOfPoints);
            System.out.println("добавлен!");
            Stage stage = (Stage) addThreadButtonId.getScene().getWindow();
            stage.close();
        });
    }

    public Thread getNew_thread() {
        return new_thread;
    }

}
