package fr.archilog.mediatheque.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class EmpruntClient {
    private static void afficherReponse(String response) {
        if (response != null && response.contains(": ")) {
            String[] parts = response.split(": ", 2);
            System.out.println("Statut : " + parts[0]);
            System.out.println("Message : " + parts[1]);
        } else {
            System.out.println("Réponse serveur : " + response);
        }
    }

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 4000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            // 1. Recevoir message d'accueil
            String welcome = in.readLine();
            System.out.println("\n=== Service d'Emprunt ===");
            afficherReponse(welcome);

            // 2. Envoyer numéro abonné
            System.out.print("\nNuméro abonné : ");
            String numAbonne = scanner.nextLine();
            out.println(numAbonne);

            // 3. Recevoir confirmation abonné
            String confirmationAbonne = in.readLine();
            afficherReponse(confirmationAbonne);
            
            // Si erreur avec l'abonné, arrêter
            if (confirmationAbonne.startsWith("FAILURE")) {
                return;
            }

            // 4. Envoyer numéro document
            System.out.print("\nNuméro document : ");
            String numDocument = scanner.nextLine();
            out.println(numDocument);

            // 5. Recevoir résultat emprunt
            String resultat = in.readLine();
            System.out.println("\n=== Résultat de l'emprunt ===");
            afficherReponse(resultat);

            // 6. Envoyer confirmation de réception
            out.println("RECEIVED");

        } catch (IOException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
    }
} 