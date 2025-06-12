package fr.archilog.mediatheque.model;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import fr.archilog.mediatheque.exception.EmpruntException;
import fr.archilog.mediatheque.exception.ReservationException;


public class DocumentConcurrencyTest {

    @Test
    public void testConcurrentReservations() throws InterruptedException {
        // Création d'un document et de deux abonnés
        DVD dvd = new DVD(1, "Test DVD", false);
        Abonne abonne1 = new Abonne(1, "Test User 1", LocalDate.of(1990, 1, 1));
        Abonne abonne2 = new Abonne(2, "Test User 2", LocalDate.of(1990, 1, 1));
        
        // Nombre de threads à exécuter
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // Compteur de réservations réussies
        final int[] successCount = {0};
        
        // Lancement de plusieurs threads qui tentent de réserver le même DVD
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    // Alternance entre les deux abonnés
                    Abonne abonne = (index % 2 == 0) ? abonne1 : abonne2;
                    dvd.reserver(abonne);
                    synchronized (successCount) {
                        successCount[0]++;
                    }
                } catch (ReservationException e) {
                    // Réservation échouée, c'est normal si un autre thread a déjà réservé
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Attente de la fin de tous les threads
        latch.await(5, TimeUnit.SECONDS);
        executorService.shutdown();
        
        // Vérification qu'une seule réservation a réussi
        assert successCount[0] == 1;
    }
    
    @Test
    public void testConcurrentEmpruntEtRetour() throws InterruptedException, ReservationException {
        // Création d'un document et d'un abonné
        DVD dvd = new DVD(1, "Test DVD", false);
        Abonne abonne = new Abonne(1, "Test User", LocalDate.of(1990, 1, 1));
        
        // Réservation préalable
        dvd.reserver(abonne);
        
        // Nombre de threads à exécuter
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // Lancement de plusieurs threads qui alternent entre emprunt et retour
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    if (index % 2 == 0) {
                        // Tentative d'emprunt
                        try {
                            dvd.emprunter(abonne);
                        } catch (EmpruntException e) {
                            // Emprunt échoué, c'est normal si déjà emprunté
                        }
                    } else {
                        // Retour
                        dvd.retourner();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Attente de la fin de tous les threads
        latch.await(5, TimeUnit.SECONDS);
        executorService.shutdown();
    }
}