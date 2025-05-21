package fr.archilog.mediatheque.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import fr.archilog.mediatheque.exception.EmpruntException;
import fr.archilog.mediatheque.exception.ReservationException;
import fr.archilog.mediatheque.model.Abonne;
import fr.archilog.mediatheque.model.Document;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final String service;
    private final Map<Integer, Document> documents;
    private final Map<Integer, Abonne> abonnes;

    public ClientHandler(Socket socket, String service, Map<Integer, Document> documents, Map<Integer, Abonne> abonnes) {
        this.clientSocket = socket;
        this.service = service;
        this.documents = documents;
        this.abonnes = abonnes;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            // Lecture des numéros d'abonné et de document
            int numeroAbonne = Integer.parseInt(in.readLine());
            int numeroDocument = Integer.parseInt(in.readLine());

            // Récupération de l'abonné et du document
            Abonne abonne = abonnes.get(numeroAbonne);
            Document document = documents.get(numeroDocument);

            if (abonne == null || document == null) {
                out.println("Abonné ou document non trouvé");
                return;
            }

            // Traitement selon le service demandé
                switch (service) {
                    case "Réservation" -> {
                        try {
                            document.reserver(abonne);
                            out.println("Réservation effectuée avec succès");
                        } catch (ReservationException e) {
                            out.println("Erreur de réservation : " + e.getMessage());
                        }
                    }
                    case "Emprunt" -> {
                        try {
                            document.emprunter(abonne);
                            out.println("Emprunt effectué avec succès");
                        } catch (EmpruntException e) {
                            out.println("Erreur d'emprunt : " + e.getMessage());
                        }
                    }
                    case "Retour" -> {
                        document.retourner();
                        out.println("Retour effectué avec succès");
                    }
                }
          

        } catch (IOException | NumberFormatException e) {
            System.err.println("Erreur lors du traitement de la requête: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
}