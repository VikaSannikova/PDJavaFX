package sample;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class BarChartController {
    XYChart.Series<String, Double>[] series;

    public XYChart.Series<String , Double>[] getSeries() {
        return series;
    }

    public void setSeries(XYChart.Series<String , Double>[] series) {
        this.series = series;
        for (XYChart.Series<String , Double> ser: series) {
            barChartId.getData().add(ser);
        }
        for(Node n: barChartId.lookupAll(".default-color0.chart-bar")) {
            n.setStyle("-fx-bar-fill: green;");
        }
//        for(Node n: barChartId.lookupAll(".default-color1.chart-bar")) {
//            n.setStyle("-fx-bar-fill: green;");
//        }
        for (Node n : barChartId.lookupAll(".default-color0.charts-bar-symbol")) {
            n.setStyle("-fx-bar-fill: green;");
        }

    }

    @FXML
    private BarChart<String , Double> barChartId;

    @FXML
    void initialize() {
        barChartId.setTitle("Статистический ряд за целый зеленый свет");
        System.out.println(barChartId.getCategoryGap());
        barChartId.setCategoryGap(1);
//        for(Node n: barChartId.lookupAll(".default-color0.chart-bar")) {
//            n.setStyle("-fx-bar-fill: blue;");
//        }
//        for(Node n: barChartId.lookupAll(".default-color1.chart-bar")) {
//            n.setStyle("-fx-bar-fill: red;");
//        }

//        for(Node n: barChartId.lookupAll(".default-color0.charts-bar-symbol")) {
//            n.setStyle("-fx-bar-fill: blue;");
//        }
//        for(Node n: barChartId.lookupAll("default-color0.chart-bar-symbol")) {
//            n.setStyle("-fx-bar-fill: green;");
//        }
    }
}
