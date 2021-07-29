package controller;

import application.Main;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class HttpClient {

	private final String host = "localhost";
	private final int port = 8080;

	private final Vertx vertx = Vertx.vertx();
	private final WebClient client = WebClient.create(vertx);

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static final String DATA_PATH = "D:\\E-Docu\\Uni\\anno3\\primo sem\\internet_of_things\\dam_dashboard\\src\\main\\resources\\ril.json";

	public void getStatus() {
		client
				.get(port, host, "/api/status")
				.send()
				.onSuccess(res -> {
					try {
						JsonPrimitive obj = gson.fromJson(
								res.bodyAsString(),
								JsonPrimitive.class
						);
						Main.STATUS = obj.getAsString();
					} catch (JsonSyntaxException e) {
						System.out.println("Null JSON received. Communication error");
					}
				})
				.onFailure(err -> {
					System.out.println("Error message: " + err.getMessage());
				});
	}

	public void getData() {
		File file = new File(DATA_PATH);
		client
				.get(port, host, "/api/data")
				.send()
				.onSuccess(res -> {
					List<WaterRilevation> rilevations = gson.fromJson(
							res.bodyAsString(),
							new TypeToken<List<WaterRilevation>>() {}.getType()
					);

					if (!file.exists()){
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					try {
						FileWriter writer = new FileWriter(DATA_PATH);
						gson.toJson(rilevations, writer);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// DEBUG
					// System.out.println(gson.toJson(rilevations));
				})
				.onFailure(err -> {
					System.out.println("Error message: " + err.getMessage());
				});
	}
}