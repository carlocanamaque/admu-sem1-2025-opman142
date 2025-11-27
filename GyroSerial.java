import com.fazecast.jSerialComm.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class GyroSerial {

    SerialPort serialPort;
    private BufferedReader reader;

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
        serialPort.setBaudRate(9600); // must match Arduino
        serialPort.setNumDataBits(8);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);

        if (serialPort.openPort()) {
            System.out.println("Serial port opened successfully.");
            // Set read timeout to avoid SerialPortTimeoutException
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
            reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        } else {
            System.out.println("Failed to open serial port: " + serialPort.getSystemPortName());
        }
    }

    /**
     * Reads a single line from the gyro serial device (blocking, with timeout)
     * @return String line of data, or null if timeout or error
     */
    public String readLine() {
        if (reader != null) {
            try {
                return reader.readLine(); // blocks until a line or timeout occurs
            } catch (IOException e) {
                System.out.println("Serial read error: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Close the serial port when done
     */
    public void close() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                System.out.println(line); // X,Y lines from Arduino
            } else {
                // No line received within timeout; can log or ignore
                System.out.println("No data received (timeout).");
            }
        }
    }
}

