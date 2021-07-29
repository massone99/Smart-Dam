package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/*
 * Data Service as a vertx event-loop 
 */
public class HttpVerticle extends AbstractVerticle {

	private int port;
	private ArrayList<WaterRilevation> values;
	private final int MAX_SIZE = 1000;
	private final String DATA_PATH = "D:\\E-Docu\\Uni\\anno3\\primo sem\\internet_of_things\\dam_service\\src\\main\\resources\\ril.json";

	private String lastStatus;
	private float lastWaterLevel;

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public HttpVerticle(int port) {
		this.values = new ArrayList<>();
		this.port = port;
	}

	@Override
	public void start() {		
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.post("/api/data").handler(this::handleAddNewData);
		router.post("/api/status").handler(this::handleAddNewStatus);
		router.get("/api/data").handler(this::handleGetData);
		router.get("/api/status").handler(this::handleGetStatus);
		router.get("/api/water").handler(this::handleGetWater);
		vertx
			.createHttpServer()
			.requestHandler(router)
			.listen(port);

		log("Service ready.");
	}

	private void handleAddNewStatus(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		// log("new msg "+routingContext.getBodyAsString());

		JsonObject res = routingContext.getBodyAsJson();
		if (res == null) {
			sendError(400, response);
		} else {
			this.lastStatus = res.getString("status");
			response.setStatusCode(200).end();
			log("New status: " + this.lastStatus);
		}
	}

	private void handleAddNewData(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		// log("new msg "+routingContext.getBodyAsString());

		JsonObject res = routingContext.getBodyAsJson();

		if (res == null) {
			sendError(400, response);
		} else {
			float waterLevel = res.getFloat("water");
			long time = System.currentTimeMillis();

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			File file = new File(DATA_PATH);


			WaterRilevation ril = new WaterRilevation(waterLevel, time);


			List<WaterRilevation> rilevations = new LinkedList<>();

			if (file.exists()){
				try {
					JsonReader reader = new JsonReader(new FileReader(file));
					rilevations = gson.fromJson(reader, new TypeToken<List<WaterRilevation>>() {}.getType());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				if (rilevations == null) {
					rilevations = new LinkedList<>();
				}
				rilevations.add(ril);
			} else {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				rilevations = new LinkedList<>();
				rilevations.add(ril);
			}

			try {
				FileWriter writer = new FileWriter(DATA_PATH);
				gson.toJson(rilevations, writer);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			log("New water level: " + waterLevel + " Timestamp: " + new Date(time));

			this.lastWaterLevel = waterLevel;
			try
			 {
				JsonReader reader = new JsonReader(new FileReader(file));
				rilevations = gson.fromJson(reader, new TypeToken<List<WaterRilevation>>() {}.getType());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			response.setStatusCode(200).end();
		}
		
	}

	private void handleGetStatus(RoutingContext routingContext) {
		System.out.println("GET (status) request received");
		String data = gson.toJson(lastStatus);
		routingContext
				.response()
				.putHeader("content-type", "application/json")
				.end(data);
	}

	private void handleGetWater(RoutingContext routingContext) {
		System.out.println("GET (water) request received");
		String data = gson.toJson(lastWaterLevel);
		routingContext
				.response()
				.putHeader("content-type", "application/json")
				.end(data);
	}
	
	private void handleGetData(RoutingContext routingContext) {

		System.out.println("GET (data) request received");


		try {
			this.values = gson.fromJson(new FileReader(DATA_PATH), new TypeToken<List<WaterRilevation>>() {}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String data = gson.toJson(this.values);
		routingContext
				.response()
				.putHeader("content-type", "application/json")
				.end(data);
	}
	
	private void sendError(int statusCode, HttpServerResponse response) {
		response.setStatusCode(statusCode).end();
	}

	private void log(String msg) {
		System.out.println("[DATA SERVICE] "+msg);
	}
}