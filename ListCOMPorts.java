import com.fazecast.jSerialComm.SerialPort;

public class ListCOMPorts {
    public static void main(String[] args) {
        SerialPort[] ports = SerialPort.getCommPorts();
        if (ports.length == 0) {
            System.out.println("No COM ports found.");
        } else {
            System.out.println("Available COM ports:");
            for (SerialPort port : ports) {
                System.out.println(port.getSystemPortName());
            }
        }
    }
}
