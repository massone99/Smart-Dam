package service;

class WaterRilevation {
	private double water;
	private long time;

	public WaterRilevation(double water, long time) {
		this.water = water;
		this.time = time;
	}
	
	public double getWater() {
		return water;
	}
	
	public long getTime() {
		return time;
	}
}
