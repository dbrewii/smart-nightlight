/*
  Blink

  Turns an LED on for one second, then off for one second, repeatedly.

  Most Arduinos have an on-board LED you can control. On the UNO, MEGA and ZERO
  it is attached to digital pin 13, on MKR1000 on pin 6. LED_BUILTIN is set to
  the correct LED pin independent of which board is used.
  If you want to know what pin the on-board LED is connected to on your Arduino
  model, check the Technical Specs of your board at:
  https://www.arduino.cc/en/Main/Products

  modified 8 May 2014
  by Scott Fitzgerald
  modified 2 Sep 2016
  by Arturo Guadalupi
  modified 8 Sep 2016
  by Colby Newman

  This example code is in the public domain.

  http://www.arduino.cc/en/Tutorial/Blink
*/

#include <SoftwareSerial.h> // use the software uart
#include <stdint.h>
SoftwareSerial bluetooth(4, 5); // RX, TX

unsigned long previousMillis = 0;        // will store last time
const long interval = 500;           // interval at which to delay
static uint32_t tmp; // increment this
String temp="";
// the setup function runs once when you press reset or power the board
void setup() {
  Serial.begin(115200);
  bluetooth.begin(115200);
  
  // initialize digital pin LED_BUILTIN as an output.
  pinMode(LED_BUILTIN, OUTPUT);
}

// the loop function runs over and over again forever
void loop() {

  digitalWrite(LED_BUILTIN,HIGH);
  delay(1000);
  digitalWrite(LED_BUILTIN,LOW);
  delay(1000);
  
if ((bluetooth.available())) { // check if anything in UART buffer
    //if(temp.startsWith("COLOR")){
      temp = bluetooth.read();
      writeString(temp);
    //}
    bluetooth.print("Printing 0x");
    bluetooth.println(temp);
}

bluetooth.print("Printing 0x");
bluetooth.println(temp);
    unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;
    bluetooth.print(tmp++); // print this to bluetooth module
  }
}

void writeString(String stringData) { // Used to serially push out a String with Serial.write()

  for (int i = 0; i < stringData.length(); i++)
  {
    Serial.write(stringData[i]);   // Push each char 1 by 1 on each loop pass
  }

}// end writeString
