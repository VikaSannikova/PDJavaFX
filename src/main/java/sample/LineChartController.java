package sample;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class LineChartController {

    XYChart.Series<Integer, Double> [] series;

    public XYChart.Series<Integer, Double>[] getSeries() {
        return series;
    }

    public void setSeries(XYChart.Series<Integer, Double>[] series) {
        this.series = series;
        for (XYChart.Series<Integer, Double> ser: series
             ) {
            stationarityChartId.getData().add(ser);
        }
//        stationarityChartId.getData().add(series);
    }

    @FXML
    LineChart<Integer, Double> stationarityChartId;

    @FXML
    void initialize() {
        stationarityChartId.setTitle("Визуализация достижения стационарности");
    }
}
