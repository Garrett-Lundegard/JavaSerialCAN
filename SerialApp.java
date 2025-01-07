// File: SerialApp.java

public class SerialApp {
    public static void main(String[] args) {
        Serial serial = new Serial();

        // Step 1: Scan for ports
        serial.scanPorts();

        // Step 2: Select a port (change "COM10" to the desired port or ID)
        String targetPort = "COM10"; // You can also use an ID like "0", "1", etc.
        if (serial.selectPort(targetPort)) {
            System.out.println("Successfully selected the port.");
        } else {
            System.out.println("Failed to select the port.");
        }

        // Step 3: Get the selected port (if needed for further actions)
        if (serial.getSelectedPort() != null) {
            System.out.println("Currently selected port: " + serial.getSelectedPort().getSystemPortName());
        }
    }
}
