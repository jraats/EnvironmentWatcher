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
  // put your setup code here, to run once:
  #if DEBUG
    Serial.begin(9600);
  #endif
  selReg = 0;

  //Motor light (continue)
  pinMode(pwmLight, OUTPUT);
  servoLight.attach(pwmLight);
  servoLight.write(96);

  //Motor temp (stepper)
  pinMode(pwmTemp, OUTPUT);
  servoTemp.attach(pwmTemp);
  servoTemp.write(90);

  //i2c
  Wire.begin(SLAVE_ADDRESS);
  Wire.onReceive(receiveData);
  Wire.onRequest(sendData);

  #if DEBUG
    Serial.println("Ready!");
  #endif
}

void loop() {
  //ON
  /* servoLight.write(0);
  servoTemp.write(90);

  delay(1000);
  servoLight.write(96);
  servoTemp.write(0);
  delay(1000);*/
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
/*  Serial.println("Received");
  char rByte;
  while (Wire.available()) {
    //Serial.println("Enter WHile");
    rByte = Wire.read();
    //Serial.println(rByte, DEC);    
    if ((rByte & 0xF0) == 0xF0) {
      //Serial.println("It is a select servo");
      selectedServo = (rByte & 0xF);
      //Serial.println("Selected servo");
      //Serial.println(selectedServo, DEC);
    }
    else {
      //Serial.println("It is set value");
      //Serial.println("Selected value");
      //Serial.println(rByte, DEC);
      Servo* servo = getSelectedServo();
      if (servo) {
        servo->write(rByte);
      }
    }
  }*/
  //TEST
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
//  Serial.println("Ask for data");
//  Wire.write(data[selectedServo]);
  unsigned char angle = 0;
  Servo* servo = getSelectedServo();
  if (servo) {
    angle = servo->read();
  }
  Wire.write(angle);
}
