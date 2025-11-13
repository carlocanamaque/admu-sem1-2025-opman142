import com.fazecast.jSerialComm.*;

public class ListPorts {
    public static void main(String[] args) {
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("Available serial ports:");
        for (SerialPort p : ports) {
            System.out.println(" - " + p.getSystemPortName() + " | " + p.getDescriptivePortName());
        }
    }
}

