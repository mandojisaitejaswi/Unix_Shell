import java.io.*;
import java.util.*;

public class JShell {
    private static final String HISTORY_FILE = ".history";
    private static final int MAX_HISTORY_SIZE = 50;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> history = loadHistory();

        while (true) {
            System.out.print("JShell$ ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue; // Ignore empty input
            }

            history.add(input); // Add command to history
            if (history.size() > MAX_HISTORY_SIZE) {
                history.remove(0); // Limit history size
            }

            if (input.equalsIgnoreCase("exit")) {
                break; // Exit the shell
            }

            executeCommand(input);
        }

        saveHistory(history);
        scanner.close();
    }

    private static void executeCommand(String command) {
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", command);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();
            process.destroy();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing command: " + e.getMessage());
        }
    }

    private static List<String> loadHistory() {
        List<String> history = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                history.add(line);
            }
        } catch (IOException e) {
            // Ignore if history file doesn't exist
        }
        return history;
    }

    private static void saveHistory(List<String> history) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            for (String command : history) {
                writer.write(command);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving history: " + e.getMessage());
        }
    }
}