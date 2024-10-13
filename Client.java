package Merge;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String message="",w1="",w2="";
        final String serverAddress = "localhost";
        final int serverPort = 12345;
        try (
            Socket socket = new Socket(serverAddress, serverPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
        ) {
            System.out.print("Enter your name: ");
            String clientName = scanner.nextLine();
            out.println(clientName);

            while(true)
            {
                System.out.println("Enter y if you are the sender");
                String t = scanner.nextLine();
                if(t.equals("y"))
                {
                    System.out.print("Enter target client name and message (e.g., 'TargetClient: Hello'): ");
                    message = scanner.nextLine();
                    System.out.println("Enter 1 to send message.");
                    System.out.println("Enter 2 to send message with replacement.");
                    System.out.println("Enter 3 to count the words.");
                    System.out.println("Enter your choice: ");                    int choice = scanner.nextInt();
                    out.println(choice);
                    switch (choice) {
                    case 1:
                        out.println(message);
                        break;
                    case 2:
                        System.out.println("Enter the word to be replaced: ");
                        w1=scanner.next();
                        System.out.println("Enter the replaced word: ");
                        w2=scanner.next();

                        String newMessage = message.replaceAll(w1, w2);
                        out.println(newMessage);
                        break;
                    case 3:
                        break;
                
                    default:
                        break;
                }  
                }      
                else 
                {
                    String response = in.readLine();
                    System.out.println("Received message: " + response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

