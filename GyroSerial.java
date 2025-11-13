import com.fazecast.jSerialComm.*;
import java.util.Scanner;

public class GyroSerial {

    private SerialPort serialPort;
    private Scanner scanner;

    /**
     * Constructor: if portName is null, automatically uses the first available port.
     * @param portName The serial port name (e.g., "/dev/ttyUSB0"), or null to auto-select
     */
    public GyroSerial(String portName) {
        if (portName == null) {
            SerialPort[] ports = SerialPort.getCommPorts();
            if (ports.length == 0) {
                System.out.println("No serial ports found!");
                return;
            }
            serialPort = ports[0];  // pick first available
            System.out.println("Auto-selected port: " + serialPort.getSystemPortName());
        } else {
            serialPort = SerialPort.getCommPort(portName);
        }

        // Configure serial port
        serialPort.setBaudRate(9600); // change to match your device
        serialPort.setNumDataBits(8);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);

        if (serialPort.openPort()) {
            System.out.println("Serial port opened successfully.");
            scanner = new Scanner(serialPort.getInputStream());
        } else {
            System.out.println("Failed to open serial port: " + serialPort.getSystemPortName());
        }
    }

    /**
     * Reads a single line from the gyro serial device and returns it.
     * Expected format from device: "X:0.12,Y:-0.34,Z:1.23"
     * @return String line of data, or null if none available
     */
    public String readLine() {
        if (scanner != null && scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null;
    }

    /**
     * Close the serial port when done
     */
    public void close() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            System.out.println("Serial port closed.");
        }
    }

    // --- Example usage ---
    public static void main(String[] args) {
        GyroSerial gyro = new GyroSerial(null); // auto-select port

        if (gyro.serialPort == null || !gyro.serialPort.isOpen()) {
            System.out.println("Exiting: no port open.");
            return;
        }

        System.out.println("Reading gyro data... Press Ctrl+C to stop.");

        while (true) {
            String line = gyro.readLine();
            if (line != null) {
                System.out.println(line);
            }
            try {
                Thread.sleep(50); // adjust to match device sending rate
            } catch (InterruptedException e) {
                break;
            }
        }

        gyro.close();
    }
}

