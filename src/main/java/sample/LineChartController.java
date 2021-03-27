package sample;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class LineChartController {

    XYChart.Series<Integer, Integer> series;

    public XYChart.Series<Integer, Integer> getSeries() {
        return series;
    }

    public void setSeries(XYChart.Series<Integer, Integer> series) {
        this.series = series;
        stationarityChartId.getData().add(series);
    }

    @FXML
    LineChart<Integer, Integer> stationarityChartId;

    @FXML
    void initialize() {
        stationarityChartId.setTitle("LineChart");
    }
}
