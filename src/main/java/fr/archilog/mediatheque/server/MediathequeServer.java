package fr.archilog.mediatheque.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fr.archilog.mediatheque.model.Abonne;
import fr.archilog.mediatheque.model.DVD;
import fr.archilog.mediatheque.model.Document;
import fr.archilog.mediatheque.model.Livre;

public class MediathequeServer {
    private static final int PORT_RESERVATION = 3000;
    private static final int PORT_EMPRUNT = 4000;
    private static final int PORT_RETOUR = 5000;

    private final Map<Integer, Document> documents;
    private final Map<Integer, Abonne> abonnes;

    public MediathequeServer() {
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

    // Démarrage des trois serveurs d'écoute sur des threads séparés
    public void demarrer() {
        new Thread(() -> ecouterConnexions(PORT_RESERVATION, "Réservation")).start();
        new Thread(() -> ecouterConnexions(PORT_EMPRUNT, "Emprunt")).start();
        new Thread(() -> ecouterConnexions(PORT_RETOUR, "Retour")).start();
    }

    private void ecouterConnexions(int port, String service) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur " + service + " démarré sur le port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket, service, documents, abonnes)).start();
            }
        } catch (IOException e) {
            System.err.println("Erreur sur le serveur " + service + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new MediathequeServer().demarrer();
    }
}