/**
 * Simple routine that performs the following:
 *  1. Configures the software UART on pins 2 and 4 (RX,TX)
 *  2. Increments a 32-bit variable every 500ms
 *  4. If it receives a '1' character from bluetooth, it toggles an LED
 *     
 *  @author Justin Bauer - mcuhq.com
 *  @date 4.24.2016
 */

#include <SoftwareSerial.h> // use the software uart
SoftwareSerial bluetooth(2, 4); // RX, TX

unsigned long previousMillis = 0;        // will store last time
const long interval = 500;           // interval at which to delay
static uint32_t tmp; // increment this

void setup() {
  pinMode(LED_BUILTIN, OUTPUT); // for LED status
  bluetooth.begin(9600); // start the bluetooth uart at 9600 which is its default
  delay(200); // wait for voltage stabilize
  bluetooth.print("Asswipe"); // place your name in here to configure the bluetooth name.
                                       // will require reboot for settings to take affect. 
  delay(3000); // wait for settings to take affect. 
}

void loop() {
  if (bluetooth.available()) { // check if anything in UART buffer
    //if(bluetooth.read() == '1'){ // did we receive this character?
    //   digitalWrite(LED_BUILTIN,HIGH); // if so, toggle the onboard LED
       
    //}
    //else{
    //   digitalWrite(LED_BUILTIN,LOW); // if so, toggle the onboard LED

    //}
  }
  
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;
    bluetooth.print(tmp++); // print this to bluetooth module
  }

}
