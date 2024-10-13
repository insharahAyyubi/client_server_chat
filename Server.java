package Merge;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static Map<String, Socket> clients = new HashMap<>();

    public static void main(String[] args) {
        final int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        private static String censorInaptWords(String input) {
            String[] inappropriateWords = { "kill", "bully", "idiot", "moron" };

            for (String word : inappropriateWords) {
                int len = word.length();
                String ans = "";
                for (int i = 0; i < len; i++) {
                    ans += '#';
                }
                // Alternative using replace
                input = input.replace(word, ans);

                // input = input.replaceAll("(?i)\\b" + word + "\\b", ans);
            }
            return input;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Read the client name
                clientName = in.readLine();
                clients.put(clientName, socket);
                System.out.println(clientName + " connected.");

                String clientFilename = clientName + ".txt";

                // Create a separate file for each client
                FileWriter fileWriter = new FileWriter(clientFilename, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                String message;
                String choice = in.readLine();
                System.out.println(choice);
                if (choice.compareTo("3") == 0) {
                    int wordCount = countWordsInFile(clientFilename);
                    System.out.println("Word count for client " + clientName + ": " + wordCount);

                    // Send the word count to the respective client
                    out.println("Total words in your file: " + wordCount);
                } else {
                    while ((message = in.readLine()) != null) {
                        if (clients.containsKey(clientName)) {
                            String[] parts = message.split(":");
                            String targetClientName = parts[0];
                            String msg = parts[1];

                            bufferedWriter.write(msg); // write in file
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            System.out.println("Client's message received and stored in file: " + clientFilename);

                            String finalMsg = censorInaptWords(msg);

                            System.out.print("Message sent from " + clientName);
                            System.out.print(" to " + targetClientName + ":" + finalMsg);
                            System.out.println();

                            Socket targetSocket = clients.get(targetClientName);
                            System.out.println(targetSocket);
                            if (targetSocket != null) {
                                PrintWriter targetOut = new PrintWriter(targetSocket.getOutputStream(), true);
                                targetOut.println(clientName + ": " + finalMsg);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private int countWordsInFile(String filename) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            int wordCount = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                wordCount += words.length;
            }

            reader.close();
            return wordCount;
        }
    }
}
