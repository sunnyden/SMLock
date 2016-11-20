all:
	blink.o
blink.o:
	main.cpp 
	avr-g++ -mmcu=atmega328p -Wall -Os -o out.elf main.cpp
	avr-objcopy -j .text -j .data -O ihex out.elf out.hex
clean:
	rm -rf *.o out.elf out.hex