package fr.archilog.mediatheque.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fr.archilog.mediatheque.exception.EmpruntException;
import fr.archilog.mediatheque.model.Abonne;
import fr.archilog.mediatheque.model.DVD;
import fr.archilog.mediatheque.model.Document;
import fr.archilog.mediatheque.model.IAbonne;
import fr.archilog.mediatheque.model.Livre;
import fr.archilog.mediatheque.model.RequestStatus;
import fr.archilog.mediatheque.model.Response;

public class EmpruntServer {
    public static final int PORT = 4000;
    private final Map<Integer, Document> documents;
    private final Map<Integer, IAbonne> abonnes;

    public EmpruntServer() {
        this.documents = new ConcurrentHashMap<>();
        this.abonnes = new ConcurrentHashMap<>();
        initialiserDonnees();
    }

    private void initialiserDonnees() {
        documents.put(1, new Livre(1, "Le Petit Prince", 100));
        documents.put(2, new DVD(2, "Matrix", true));
        documents.put(3, new Livre(3, "Les Misérables", 500));

        abonnes.put(1, new Abonne(1, "Jean Dupont", LocalDate.of(1990, 1, 1)));
        abonnes.put(2, new Abonne(2, "Marie Martin", LocalDate.of(2010, 1, 1)));
    }

    public void demarrer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[EmpruntServer] Démarré sur le port " + PORT);
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    // 1. Envoyer message d'accueil
                    Response welcome = new Response(RequestStatus.SUCCESS, "Bienvenue sur le service d'emprunt");
                    out.println(welcome);
                    System.out.println("[EmpruntServer] Message d'accueil envoyé: " + welcome);

                    // 2. Recevoir numéro abonné
                    String numAbonneStr = in.readLine();
                    if (numAbonneStr == null || numAbonneStr.isEmpty()) {
                        Response error = new Response(RequestStatus.FAILURE, "Numéro d'abonné manquant");
                        out.println(error);
                        System.out.println("[EmpruntServer] Erreur: " + error);
                        return;
                    }

                    // 3. Vérifier existence abonné
                    int numAbonne;
                    try {
                        numAbonne = Integer.parseInt(numAbonneStr);
                    } catch (NumberFormatException e) {
                        Response error = new Response(RequestStatus.FAILURE, "Numéro d'abonné invalide");
                        out.println(error);
                        System.out.println("[EmpruntServer] Erreur: " + error);
                        return;
                    }

                    IAbonne abonne = abonnes.get(numAbonne);
                    if (abonne == null) {
                        Response error = new Response(RequestStatus.FAILURE, "Abonné non trouvé");
                        out.println(error);
                        System.out.println("[EmpruntServer] Erreur: " + error);
                        return;
                    }

                    // 4. Confirmer identification abonné
                    Response confirm = new Response(RequestStatus.SUCCESS, "Abonné identifié");
                    out.println(confirm);
                    System.out.println("[EmpruntServer] Confirmation: " + confirm);

                    // 5. Recevoir numéro document
                    String numDocumentStr = in.readLine();
                    if (numDocumentStr == null || numDocumentStr.isEmpty()) {
                        Response error = new Response(RequestStatus.FAILURE, "Numéro de document manquant");
                        out.println(error);
                        System.out.println("[EmpruntServer] Erreur: " + error);
                        return;
                    }

                    // 6. Vérifier existence document
                    int numDocument;
                    try {
                        numDocument = Integer.parseInt(numDocumentStr);
                    } catch (NumberFormatException e) {
                        Response error = new Response(RequestStatus.FAILURE, "Numéro de document invalide");
                        out.println(error);
                        System.out.println("[EmpruntServer] Erreur: " + error);
                        return;
                    }

                    Document document = documents.get(numDocument);
                    if (document == null) {
                        Response error = new Response(RequestStatus.FAILURE, "Document non trouvé");
                        out.println(error);
                        System.out.println("[EmpruntServer] Erreur: " + error);
                        return;
                    }

                    // 7. Tenter l'emprunt
                    try {
                        document.emprunter(abonne);
                        Response success = new Response(RequestStatus.SUCCESS, "Emprunt effectué avec succès");
                        out.println(success);
                        System.out.println("[EmpruntServer] Succès: " + success);
                    } catch (EmpruntException e) {
                        Response error = new Response(RequestStatus.FAILURE, "Erreur d'emprunt: " + e.getMessage());
                        out.println(error);
                        System.out.println("[EmpruntServer] Erreur: " + error);
                    }

                    // 8. Attendre confirmation de réception du client
                    String confirmation = in.readLine();
                    System.out.println("[EmpruntServer] Confirmation client reçue: " + confirmation);
                }
            }
        } catch (IOException e) {
            System.err.println("[EmpruntServer] Erreur: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new EmpruntServer().demarrer();
    }
} 