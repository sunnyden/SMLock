#include"utilities.h"

void setup() {

  Serial.begin(9600);
  Serial1.begin(9600);
  Wire.begin();
}

void loop() {

  delay(20);
  char command[1000]="";
  int i=0;
  while(Serial1.available()>0){
   command[i]=Serial1.read();
   i++;
  }
  getCommand(command);

  delay(500);
}
