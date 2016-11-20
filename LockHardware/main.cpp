
#include"lib/Arduino.h" 
#include"utilities.h"

// Declared weak in Arduino.h to allow user redefinitions.
int atexit(void (* /*func*/ )()) { return 0; }

// Weak empty variant initialization function.
// May be redefined by variant files.
void initVariant() __attribute__((weak));
void initVariant() { }

void setupUSB() __attribute__((weak));
void setupUSB() { }

int main(void)
{
	init();

	initVariant();

#if defined(USBCON)
	USBDevice.attach();
#endif
	
	Serial.begin(9600);
  	Serial1.begin(9600);
  	Wire.begin();
    
	for (;;) {
		delay(20);
		char command[1000]="";
		int i=0;
		while(Serial1.available()>0){
		command[i]=Serial1.read();
		i++;
		}
		getCommand(command);
		
		delay(500);
		if (serialEventRun) serialEventRun();
	}
        
	return 0;
}
