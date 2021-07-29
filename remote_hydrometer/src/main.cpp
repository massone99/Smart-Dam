#include <Arduino.h>
#include <NewPing.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>

#define TRIGPIN 5
#define ECHOPIN 4
#define MAXDIST 200
#define D1 100
#define D2 40
#define DELTAD 4
#define DELTAL 4
#define F1 10000

/* wifi network name */
char *ssidName = "Pixel";
/* WPA2 PSK password */
char *pwd = "lollomas";
/* service IP address */
char *address = "http://c252913b0545.ngrok.io";

HTTPClient http;
WiFiClient client;

NewPing sonar(TRIGPIN, ECHOPIN, MAXDIST);

void setup()
{
  Serial.begin(9600);
  WiFi.begin(ssidName, pwd);
  Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println("Connected: \n local IP: " + WiFi.localIP().toString());
}

int sendStatus(String address, String status) {
  http.begin(client, address + "/api/status");
  http.addHeader("Content-Type", "application/json");

  String msg =
      String("{ \"status\": \"") + status + "\" }";
  int retCode = http.POST(msg);
  http.end();

  Serial.println(msg);

  // String payload = http.getString();
  // Serial.println(payload);
  return retCode;
}

int sendData(String address, float value)
{

  http.begin(client, address + "/api/data");
  http.addHeader("Content-Type", "application/json");
  String sclup =
      String("{ \"value\": ") + String(value) +
      ", \"timestamp\": \"" + 12 + "\" }";

  String msg =
      String("{ \"water\": ") + String(value) + " }";
  int retCode = http.POST(msg);
  Serial.println(msg);
  http.end();

  // String payload = http.getString();
  // Serial.println(payload);
  return retCode;
}

void loop()
{
  if (WiFi.status() == WL_CONNECTED)
  {

    /* read sensor */
    float distance = (float)sonar.ping_cm();

    if (distance < D1 && distance > D2)
    {
      /* stato PRE-ALLARME */

      /* send data */
      Serial.print("sending distance data... \n");
      Serial.println(distance);
      sendStatus(address, "Pre-alarm");
      int code = sendData(address, distance);

      Serial.println("Code: " + String(code));

      /* log result */
      if (code == 200)
      {
        Serial.println("ok \n");
      }
      else
      {
        Serial.println("error \n");
      }
    } else if (distance < D2) {
      /* stato ALLARME */

      /* send data */
      sendStatus(address, "Alarm");
      int code = sendData(address, distance);

      Serial.println("Code: " + String(code));

      // /* log result */
      // if (code == 200)
      // {
      //   Serial.println("ok \n");
      // }
      // else
      // {
      //   Serial.println("error \n");
      // }
    } else {
      /* stato NORMALE */
      sendStatus(address, "Normal");
    }
  }
  else
  {
    Serial.println("Error in WiFi connection \n");
  }

  delay(F1 / 2);
}