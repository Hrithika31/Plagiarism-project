import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SimplePlagiarismCheckerApp extends JFrame {

    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JFileChooser fileChooser;
    private File currentFile;

    public SimplePlagiarismCheckerApp() {
        setTitle("Simple Plagiarism Checker App");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout setup
        setLayout(new BorderLayout());

        // Buttons panel moved to top
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton btnNewFile = new JButton("New File");
        JButton btnOpenFile = new JButton("Open File");
        JButton btnCloseFile = new JButton("Close File");
        JButton btnPlagiarismCheck = new JButton("Plagiarism Check");
        JButton btnPlagiarismRemove = new JButton("Plagiarism Remove");
        JButton btnGrammarCheck = new JButton("Grammar Check");

        buttonPanel.add(btnNewFile);
        buttonPanel.add(btnOpenFile);
        buttonPanel.add(btnCloseFile);
        buttonPanel.add(btnPlagiarismCheck);
        buttonPanel.add(btnPlagiarismRemove);
        buttonPanel.add(btnGrammarCheck);

        add(buttonPanel, BorderLayout.NORTH);

        // Input text area below buttons
        inputTextArea = new JTextArea(10, 80);
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputScrollPane.setBorder(BorderFactory.createTitledBorder("Input Text"));
        add(inputScrollPane, BorderLayout.CENTER);

        // Output text area at the bottom half
        outputTextArea = new JTextArea(10, 80);
        outputTextArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("Output"));
        add(outputScrollPane, BorderLayout.SOUTH);

        fileChooser = new JFileChooser();

        // Button actions
        btnNewFile.addActionListener(e -> newFile());
        btnOpenFile.addActionListener(e -> openFile());
        btnCloseFile.addActionListener(e -> closeFile());
        btnPlagiarismCheck.addActionListener(e -> plagiarismCheck());
        btnPlagiarismRemove.addActionListener(e -> plagiarismRemove());
        btnGrammarCheck.addActionListener(e -> grammarCheck());
    }

    private void newFile() {
        inputTextArea.setText("");
        outputTextArea.setText("");
        currentFile = null;
        setTitle("Simple Plagiarism Checker App - New File");
    }

    private void openFile() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                inputTextArea.setText("");
                String line;
                while ((line = reader.readLine()) != null) {
                    inputTextArea.append(line + "\n");
                }
                outputTextArea.setText("");
                setTitle("Simple Plagiarism Checker App - " + currentFile.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage());
            }
        }
    }

    private void closeFile() {
        inputTextArea.setText("");
        outputTextArea.setText("");
        currentFile = null;
        setTitle("Simple PlagiarismCheckerApp");
    }

    private void plagiarismCheck() {
        String text = inputTextArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Input text is empty.");
            return;
        }
        outputTextArea.setText("Checking plagiarism...\n");

        // Call PlagiarismCheck.org API (example, replace with actual API details and key)
        String apiUrl = "https://api.plagiarismcheck.org/v1/check";
        String apiKey = "YOUR_API_KEY"; // Replace with your API key

        String response = callPlagiarismCheckApi(apiUrl, apiKey, text);
        outputTextArea.append(response);
    }

    private void plagiarismRemove() {
        String text = inputTextArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Input text is empty.");
            return;
        }
        outputTextArea.setText("Removing plagiarism...\n");

        // Call QuillBot paraphrasing API (example, replace with actual API details and key)
        String apiUrl = "https://api.quillbot.com/v1/paraphrase";
        String apiKey = "YOUR_API_KEY"; // Replace with your API key

        String response = callPlagiarismRemoveApi(apiUrl, apiKey, text);
        outputTextArea.append(response);
    }

    private void grammarCheck() {
        String text = inputTextArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Input text is empty.");
            return;
        }
        outputTextArea.setText("Checking grammar...\n");
        String response = callLanguageToolApi(text);
        String lineByLineSuggestions = parseGrammarSuggestionsLineByLine(response, text);
        outputTextArea.append(lineByLineSuggestions);
    }

    private String callPlagiarismCheckApi(String apiUrl, String apiKey, String text) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);

            String jsonInputString = "{\"text\": \"" + text.replace("\"", "\\\"") + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    StringBuilder response = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        response.append(scanner.nextLine()).append("\n");
                    }
                    return response.toString();
                }
            } else {
                return "Plagiarism Check API call failed with response code: " + responseCode;
            }
        } catch (Exception e) {
            return "Error calling Plagiarism Check API: " + e.getMessage();
        }
    }

    private String callPlagiarismRemoveApi(String apiUrl, String apiKey, String text) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);

            String jsonInputString = "{\"text\": \"" + text.replace("\"", "\\\"") + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    StringBuilder response = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        response.append(scanner.nextLine()).append("\n");
                    }
                    return response.toString();
                }
            } else {
                return "Plagiarism Remove API call failed with response code: " + responseCode;
            }
        } catch (Exception e) {
            return "Error calling Plagiarism Remove API: " + e.getMessage();
        }
    }

    private String callLanguageToolApi(String text) {
        try {
            String urlStr = "https://api.languagetool.org/v2/check";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postData = "text=" + URLEncoder.encode(text, StandardCharsets.UTF_8) + "&language=en-US";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    StringBuilder response = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        response.append(scanner.nextLine()).append("\n");
                    }
                    // Parse the JSON response to extract grammar suggestions
                    String jsonResponse = response.toString();
                    return jsonResponse;
                }
            } else {
                return "Grammar API call failed with response code: " + responseCode;
            }
        } catch (Exception e) {
            return "Error calling Grammar API: " + e.getMessage();
        }
    }

    private String parseGrammarSuggestionsLineByLine(String jsonResponse, String originalText) {
        StringBuilder suggestions = new StringBuilder();
        suggestions.append("Grammar Check Suggestions (Line by Line):\n\n");

        try {
            int matchesIndex = jsonResponse.indexOf("\"matches\":");
            if (matchesIndex == -1) {
                suggestions.append("No grammar issues found.\n");
                return suggestions.toString();
            }
            String matchesSub = jsonResponse.substring(matchesIndex);
            String[] parts = matchesSub.split("\\},\\{");
            if (parts.length <= 1) {
                suggestions.append("No grammar issues found.\n");
                return suggestions.toString();
            }

            // Split original text into lines
            String[] lines = originalText.split("\\r?\\n");

            // For each match, find the line number and append the issue
            for (String part : parts) {
                int messageIndex = part.indexOf("\"message\":\"");
                int offsetIndex = part.indexOf("\"offset\":");
                int lengthIndex = part.indexOf("\"length\":");
                if (messageIndex != -1 && offsetIndex != -1 && lengthIndex != -1) {
                    String message = part.substring(messageIndex + 10, part.indexOf("\"", messageIndex + 10));
                    int offset = Integer.parseInt(part.substring(offsetIndex + 9, part.indexOf(",", offsetIndex + 9)));
                    int length = Integer.parseInt(part.substring(lengthIndex + 9, part.indexOf(",", lengthIndex + 9)));

                    // Find line number by offset
                    int cumulativeLength = 0;
                    int lineNumber = 0;
                    for (int i = 0; i < lines.length; i++) {
                        cumulativeLength += lines[i].length() + 1; // +1 for newline
                        if (offset < cumulativeLength) {
                            lineNumber = i + 1;
                            break;
                        }
                    }

                    suggestions.append("Line ").append(lineNumber).append(": ").append(message).append("\n");
                }
            }
        } catch (Exception e) {
            suggestions.append("Error parsing grammar suggestions: ").append(e.getMessage()).append("\n");
        }
        return suggestions.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimplePlagiarismCheckerApp app = new SimplePlagiarismCheckerApp();
            app.setVisible(true);
        });
    }
}
