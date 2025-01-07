// File: Serial.java

import com.fazecast.jSerialComm.SerialPort;

/**
 * Serial class for managing serial port operations.
 */
public class Serial {

    private SerialPort selectedPort; // Stores the selected port

    /**
     * Scans and lists all available serial ports.
     */
    public void scanPorts() {
        SerialPort[] ports = SerialPort.getCommPorts();
        if (ports.length == 0) {
            System.out.println("No COM ports found.");
        } else {
            System.out.println("Available COM ports:");
            for (int i = 0; i < ports.length; i++) {
                System.out.printf("[%d] %s\n", i, ports[i].getSystemPortName());
            }
        }
    }

    /**
     * Selects a serial port by its name or ID.
     * 
     * @param identifier The port name (e.g., "COM10") or ID (index from the list).
     * @return True if the port is successfully selected, otherwise false.
     */
    public boolean selectPort(String identifier) {
        SerialPort[] ports = SerialPort.getCommPorts();

        try {
            int portIndex = Integer.parseInt(identifier);
            if (portIndex >= 0 && portIndex < ports.length) {
                selectedPort = ports[portIndex];
                System.out.println("Selected port by ID: " + selectedPort.getSystemPortName());
                return true;
            }
        } catch (NumberFormatException e) {
            // Ignore, it means the identifier is not numeric
        }

        for (SerialPort port : ports) {
            if (port.getSystemPortName().equalsIgnoreCase(identifier)) {
                selectedPort = port;
                System.out.println("Selected port by name: " + selectedPort.getSystemPortName());
                return true;
            }
        }

        System.out.println("Port not found: " + identifier);
        return false;
    }

    /**
     * Gets the currently selected port.
     * 
     * @return The selected SerialPort object or null if none is selected.
     */
    public SerialPort getSelectedPort() {
        return selectedPort;
    }
}
