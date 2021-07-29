package controller;

import javafx.scene.chart.XYChart;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WaterRilevation {
	private final double water;
	private final long time;

	public WaterRilevation(double water, long time) {
		this.water = water;
		this.time = time;
	}
	
	public XYChart.Data<String, Double> getXYChartData() {
		Date date = new Date(this.time);
		return new XYChart.Data<String, Double>(
				new SimpleDateFormat("HH-mm.ss").format(date),
				water
		);
	}
}
