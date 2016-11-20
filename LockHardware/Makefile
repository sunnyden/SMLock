MCU = atmega328p
#avrg++ = "C:\Program Files (x86)\Arduino\hardware\tools\avr/bin/avr-gcc"
avrg++ = "avr-g++"
avrgcc = "avr-gcc"
avrar = "avr-ar"
INCLUDE = "-Ilib/"
ARGS = -c -g -Os -Wall -Wextra -std=gnu++11 -fno-exceptions -ffunction-sections -fdata-sections -fno-threadsafe-statics -MMD -mmcu=atmega32u4 -DF_CPU=16000000L -DARDUINO=10609 -DARDUINO_AVR_LEONARDO -DARDUINO_ARCH_AVR  -DUSB_VID=0x2341 -DUSB_PID=0x8036 '-DUSB_MANUFACTURER="Unknown"' '-DUSB_PRODUCT="Arduino Leonardo"'
C_FLAGS = -c -g -Os -Wall -Wextra -std=gnu11 -ffunction-sections -fdata-sections -MMD -mmcu=atmega32u4 -DF_CPU=16000000L -DARDUINO=10609 -DARDUINO_AVR_LEONARDO -DARDUINO_ARCH_AVR  -DUSB_VID=0x2341 -DUSB_PID=0x8036 '-DUSB_MANUFACTURER="Unknown"' '-DUSB_PRODUCT="Arduino Leonardo"'
ASM_ARGS = -c -g -x assembler-with-cpp -mmcu=atmega32u4 -DF_CPU=16000000L -DARDUINO=10609 -DARDUINO_AVR_LEONARDO -DARDUINO_ARCH_AVR  -DUSB_VID=0x2341 -DUSB_PID=0x8036 '-DUSB_MANUFACTURER="Unknown"' '-DUSB_PRODUCT="Arduino Leonardo"'
ELF_ARGS = -Wall -Wextra -Os -Wl,--gc-sections -mmcu=atmega32u4

AR_File = wiring_pulse.o WInterrupts.o hooks.o wiring.o wiring_analog.o\
wiring_digital.o wiring_pulse.o wiring_shift.o CDC.o HardwareSerial.o\
HardwareSerial0.o HardwareSerial1.o HardwareSerial2.o HardwareSerial3.o\
IPAddress.o PluggableUSB.o Print.o Stream.o Tone.o USBCore.o WMath.o\
WString.o abi.o main.o new.o

FILES=`ls *.o`

all:core.a DS3231.o Wire.o twi.o
	$(avrgcc) $(ELF_ARGS) -o "./out.elf" core.a DS3231.o Wire.o twi.o -Llib/
core.a:$(AR_File)
	@for target in $(AR_File); \
	do                        \
	$(avrar) rcs ./core.a $$target; \
	done
main.o: main.cpp 
	$(avrg++) $(ARGS) $(INCLUDE) main.cpp
wiring_pulse.o:
	$(avrgcc) $(ASM_ARGS) $(INCLUDE) lib/wiring_pulse.S
WInterrupts.o:
	$(avrgcc) $(C_FLAGS) $(INCLUDE) lib/WInterrupts.c
hooks.o:
	$(avrgcc) $(C_FLAGS) $(INCLUDE) lib/hooks.c
wiring.o:
	$(avrgcc) $(C_FLAGS) $(INCLUDE) lib/wiring.c
wiring_analog.o:
	$(avrgcc) $(C_FLAGS) $(INCLUDE) lib/wiring_analog.c
wiring_digital.o:
	$(avrgcc) $(C_FLAGS) $(INCLUDE) lib/wiring_digital.c
wiring_pulse.o:
	$(avrgcc) $(C_FLAGS) $(INCLUDE) lib/wiring_pulse.c
wiring_shift.o:
	$(avrgcc) $(C_FLAGS) $(INCLUDE) lib/wiring_shift.c
CDC.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/CDC.cpp
HardwareSerial.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/HardwareSerial.cpp
HardwareSerial0.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/HardwareSerial0.cpp
HardwareSerial1.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/HardwareSerial1.cpp
HardwareSerial2.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/HardwareSerial2.cpp
HardwareSerial3.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/HardwareSerial3.cpp
IPAddress.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/IPAddress.cpp
PluggableUSB.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/PluggableUSB.cpp
Print.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/Print.cpp
Stream.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/Stream.cpp
Tone.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/Tone.cpp
USBCore.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/USBCore.cpp
WMath.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/WMath.cpp
WString.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/WString.cpp
abi.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/abi.cpp
new.o:
	$(avrg++) $(ARGS) $(INCLUDE) lib/new.cpp
out.hex: out.elf
	avr-objcopy -j .text -j .data -O ihex out.elf out.hex
	
DS3231.o:
	$(avrg++) $(ARGS) $(INCLUDE) DS3231.cpp
Wire.o: 
	$(avrg++) $(ARGS) $(INCLUDE) wire/Wire.cpp
twi.o:
	$(avrgcc) $(C_FLAGS) $(INCLUDE) wire/utility/twi.c

clean:
	rm -rf *.o *.d out.elf out.hex
	
	
