#include <BLEAttribute.h>
#include <BLECentral.h>
#include <BLECharacteristic.h>
#include <BLECommon.h>
#include <BLEDescriptor.h>
#include <BLEPeripheral.h>
#include <BLEService.h>
#include <BLETypedCharacteristic.h>
#include <BLETypedCharacteristics.h>
#include <BLEUuid.h>
#include <CurieBle.h>

#include <Wire.h>
#include "rgb_lcd.h"

rgb_lcd lcd;

const int colorR = 255;
const int colorG = 0;
const int colorB = 0;

char *line1_buffer = new char[17];
char *line2_buffer = new char[17];
char setup_message1[] = "                ";
char setup_message2[] = "                ";
const unsigned char buffer_length = 0x10;
const unsigned char characteristic_length = 0x20;

BLEPeripheral perf;
BLEService _service("4d8dd3ab-3844-4e7d-86ff-4a2aa8e7f132");
BLECharacteristic message1("4d8dd3ab-3844-4e7d-86ff-4a2aa8e7f132", BLEWrite, characteristic_length);
BLECharacteristic message2("4d8dd3ab-3844-4e7d-86ff-4a2aa8e7f133", BLEWrite, characteristic_length);

void setup() {
  Serial.begin(9600);

  line1_buffer[16] = 0;
  line2_buffer[16] = 0;
  
  //LCD Setup
  lcd.begin(16,2);
  lcd.setRGB(colorR, colorG, colorB);
  lcd.setCursor(0,0);
  lcd.print(setup_message1);
  lcd.setCursor(0,1);
  lcd.print(setup_message2);
  
  //BLE Setup
  perf.setLocalName("LCDDISPLAY");
  perf.setAdvertisedServiceUuid(_service.uuid());
  perf.addAttribute(_service);
  perf.addAttribute(message1);
  perf.addAttribute(message2);
  
  perf.setEventHandler(BLEConnected, blePeripheralConnectHandler);
  perf.setEventHandler(BLEDisconnected, blePeripheralDisconnectHandler);
  
  message1.setEventHandler(BLEWritten, messageOneWritten);

  message2.setEventHandler(BLEWritten, messageTwoWritten);

  perf.begin();
  delay(1000);


}

void loop() {
  // put your main code here, to run repeatedly:
  perf.poll();
}

void blePeripheralConnectHandler(BLECentral& central) {
  // central connected event handler
  Serial.print("Connected event, central: ");
  Serial.println(central.address());
}

void blePeripheralDisconnectHandler(BLECentral& central) {
  // central disconnected event handler
  Serial.print("Disconnected event, central: ");
  Serial.println(central.address());
}

void messageOneWritten(BLECentral& central, BLECharacteristic& characteristic) {
  Serial.print("Message1 event, written: ");
  memcpy(line1_buffer, message1.value(), buffer_length);
  Serial.println(*(message1.value()));
  Serial.println("Message length: ");
  Serial.println(message1.valueLength());

  lcd.setCursor(0,0);
  lcd.print(line1_buffer);
  
}

void messageTwoWritten(BLECentral& central, BLECharacteristic& characteristic) {
  
  memcpy(line2_buffer, message2.value(), buffer_length);
  Serial.print("Message2 event, written: ");
  Serial.println(*(message2.value()));
  Serial.println("Message length: ");
  Serial.println(message2.valueLength());

  lcd.setCursor(0,1);
  lcd.print(line2_buffer);
  
}

