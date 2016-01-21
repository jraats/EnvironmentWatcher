#include <Servo.h>
#include <Wire.h>

#define DEBUG 0
#define SLAVE_ADDRESS 0x04
#define pwmTemp 3
#define pwmLight 5
Servo servoTemp;
Servo servoLight;
char selReg;

void setup() {
  #if DEBUG
    Serial.begin(9600);
  #endif
  selReg = 0;

  //Motor light (continue)
  pinMode(pwmLight, OUTPUT);
  servoLight.attach(pwmLight);
  servoLight.write(90);

  //Motor temp (stepper)
  pinMode(pwmTemp, OUTPUT);
  servoTemp.attach(pwmTemp);
  servoTemp.write(90);

  //i2c
  Wire.begin(SLAVE_ADDRESS);
  Wire.setClock(100000L);
  Wire.onReceive(receiveData);
  Wire.onRequest(sendData);

  #if DEBUG
    Serial.println("Ready!");
  #endif
}

void loop() {
  delay(100);
}

Servo* getSelectedServo() {
  Servo* servo = NULL;
  switch (selReg) {
    case pwmTemp:
        servo = &servoTemp;
      break;
    case pwmLight:
      servo = &servoLight;
      break;
  }
  return servo;
}

// callback for received data
void receiveData(int byteCount) {
  bool setReg = true;
   while (Wire.available()) {
     unsigned char data = Wire.read();
     if(setReg) {
      selReg = data;
       setReg = false;
     }
     else {
       Servo* servo = getSelectedServo();
        if (servo) {
          servo->write(data);
        }
       setReg = true;
     }
   } 
}

// callback for sending data
void sendData() {
  unsigned char angle = 0;
  Servo* servo = getSelectedServo();
  if (servo) {
    angle = servo->read();
  }
  Wire.write(angle);
}
