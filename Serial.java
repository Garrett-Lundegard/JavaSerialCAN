import java.io.IOException;

import com.fazecast.jSerialComm.*;

public class Serial {

    private SerialPort selectedPort;

    // Event-based reading
    public void enableEventBasedReading() {
        if (selectedPort == null || !selectedPort.isOpen()) {
            System.out.println("No port selected or port is not open.");
            return;
        }

        // Add data listener for event-based reading
        selectedPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    byte[] buffer = new byte[selectedPort.bytesAvailable()];
                    int numBytes = selectedPort.readBytes(buffer, buffer.length);

                    if (numBytes > 0) {
                        String receivedData = new String(buffer, 0, numBytes);
                        System.out.println("Received: " + receivedData);
                    }
                }
            }
        });

        System.out.println("Event-based reading enabled.");
    }

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

    public boolean selectPort(String identifier) {
        SerialPort[] ports = SerialPort.getCommPorts();

        try {
            int portIndex = Integer.parseInt(identifier);
            if (portIndex >= 0 && portIndex < ports.length) {
                selectedPort = ports[portIndex];
            }
        } catch (NumberFormatException e) {
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

        if (!openPort()) {
            System.out.println("Failed to open port: " + selectedPort.getSystemPortName());
            return false;
        }

        System.out.println("Port selected and opened: " + selectedPort.getSystemPortName());
        return true;
    }

    private boolean openPort() {
        if (selectedPort == null) {
            System.out.println("No port selected to open.");
            return false;
        }

        if (selectedPort.openPort()) {
            selectedPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
            return true;
        } else {
            return false;
        }
    }

    public void closePort() {
        if (selectedPort != null && selectedPort.isOpen()) {
            selectedPort.closePort();
            System.out.println("Port closed: " + selectedPort.getSystemPortName());
        }
    }
    
    /**
     * Sends a command and reads the response from the device.
     *
     * @param command The command to send (e.g., "can send 8020 1c0110 \n").
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
            byte[] buffer = new byte[1024];
            int bytesRead;

            System.out.println("Waiting for response...");
            while ((selectedPort.bytesAvailable()) > 0) {
                bytesRead = selectedPort.getInputStream().read(buffer);
                
                String chunk = new String(buffer, 0, bytesRead);
                response.append(chunk);
                
                // Print and clear buffer if newline detected
                if (chunk.contains("\n")) {
                    System.out.print("Device output: " + response.toString());
                    response.setLength(0); // Clear buffer
                }
            
            
            }
            return response.toString();
        }
        catch (IOException e) {
            System.out.println("Error communicating with device: " + e.getMessage());
            return null;
        }
    }
}
