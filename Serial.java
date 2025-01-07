// Refactored Serial.java

import java.io.IOException;
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
            // Check if identifier is numeric (for ID selection)
            int portIndex = Integer.parseInt(identifier);
            if (portIndex >= 0 && portIndex < ports.length) {
                selectedPort = ports[portIndex];
            }
        } catch (NumberFormatException e) {
            // If identifier is not numeric, match by name
            for (SerialPort port : ports) {
                if (port.getSystemPortName().equalsIgnoreCase(identifier)) {
                    selectedPort = port;
                    break;
                }
            }
        }

        if (selectedPort == null) {
            System.out.println("Port not found: " + identifier);
            return false;
        }

        // Open the selected port
        if (!openPort()) {
            System.out.println("Failed to open port: " + selectedPort.getSystemPortName());
            return false;
        }

        System.out.println("Port selected and opened: " + selectedPort.getSystemPortName());
        return true;
    }

    /**
     * Gets the currently selected port.
     * 
     * @return The selected SerialPort object or null if none is selected.
     */
    public SerialPort getSelectedPort() {
        return selectedPort;
    }

    /**
     * Opens the currently selected serial port.
     * 
     * @return True if the port was successfully opened, otherwise false.
     */
    private boolean openPort() {
        if (selectedPort == null) {
            System.out.println("No port selected to open.");
            return false;
        }

        if (selectedPort.openPort()) {
            selectedPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 1000);
            System.out.println("Port opened: " + selectedPort.getSystemPortName());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Closes the currently selected serial port.
     */
    public void closePort() {
        if (selectedPort != null && selectedPort.isOpen()) {
            selectedPort.closePort();
            System.out.println("Port closed: " + selectedPort.getSystemPortName());
        }
    }

    /**
     * Sends a command and reads the complete response from the device.
     *
     * @param command The command to send (e.g., "can send 8020 171019131102 \n").
     * @return The full response from the device as a String, or null if an error occurs.
     */
    public String sendCommand(String command) {
        if (selectedPort == null || !selectedPort.isOpen()) {
            System.out.println("No port selected or port is not open.");
            return null;
        }

        try {
            // Write the command to the serial port
            selectedPort.getOutputStream().write(command.getBytes());
            selectedPort.getOutputStream().flush();
            System.out.println("Command sent: " + command.trim());

            // Read the response from the serial port
            StringBuilder response = new StringBuilder();
            byte[] buffer = new byte[1024]; // Buffer to store read data
            int bytesRead;

            while ((bytesRead = selectedPort.getInputStream().read(buffer)) > 0) {
                response.append(new String(buffer, 0, bytesRead).trim());

                // If the response ends with a newline, assume it's complete
                if (response.toString().endsWith("\n")) {
                    break;
                }
            }

            // Return the complete response
            if (response.length() > 0) {
                System.out.println("Device response: " + response.toString());
                return response.toString();
            } else {
                System.out.println("No response received.");
                return null;
            }

        } catch (IOException e) {
            System.out.println("Error communicating with device: " + e.getMessage());
            return null;
        }
    }
}
