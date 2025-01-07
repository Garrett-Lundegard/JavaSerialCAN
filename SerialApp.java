public class SerialApp {
    public static void main(String[] args) {
        Serial serial = new Serial();

        // Step 1: Scan for ports
        serial.scanPorts();

        // Step 2: Select the target port
        String targetPort = "COM10"; // Adjust based on your setup
        if (serial.selectPort(targetPort)) {
            System.out.println("Successfully selected the port.");
        } else {
            System.out.println("Failed to select the port.");
            return;
        }

        // Step 3: Send command to query Input Power register
        String command = "conf enumerate \n";//"can send 8020 171019131102 \n"; // Replace with the appropriate command
        String response = serial.sendCommand(command);

        // Step 4: Print the response
        if (response != null) {
            System.out.println("Device response: " + response);
        } else {
            System.out.println("Failed to get a response from the device.");
        }

        // Step 5: Close the port
        serial.closePort();
    }
}
