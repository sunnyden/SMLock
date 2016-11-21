import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
public class UploadUtil{
	public static void main(String[] args) {
		SerialPort serialPort = new SerialPort("COM3");//Will be change next time.
		try{
			serialPort.openPort();
			serialPort.setParams(1200, 8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.setDTR(false);
			serialPort.closePort();
		}catch(Exception e){
			System.out.println(e.toString());
		}

	}
}