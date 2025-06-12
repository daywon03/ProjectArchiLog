package fr.archilog.mediatheque.model;

import fr.archilog.mediatheque.exception.EmpruntException;
import fr.archilog.mediatheque.exception.ReservationException;

public class DVD implements Document {
    private final int numero;
    private final String titre;
    private final boolean adulte;
    private Abonne reservePar;
    private Abonne empruntePar;

    public DVD(int numero, String titre, boolean adulte) {
        this.numero = numero;
        this.titre = titre;
        this.adulte = adulte;
    }

    @Override
    public int numero() {
        return numero;
    }

    @Override
    public synchronized void reserver(Abonne ab) throws ReservationException {
        if (reservePar != null || empruntePar != null) {
            throw new ReservationException("Le DVD est déjà réservé ou emprunté");
        }
        if (adulte && !ab.estAdulte()) {
            throw new ReservationException("Vous n'avez pas l'âge requis pour ce DVD");
        }
        reservePar = ab;
    }

    @Override
    public synchronized void emprunter(Abonne ab) throws EmpruntException {
        if (empruntePar != null) {
            throw new EmpruntException("Le DVD est déjà emprunté");
        }
        if (reservePar != null && reservePar.getNumero() != ab.getNumero()) {
            throw new EmpruntException("Le DVD est réservé par un autre abonné");
        }
        if (adulte && !ab.estAdulte()) {
            throw new EmpruntException("Vous n'avez pas l'âge requis pour ce DVD");
        }
        empruntePar = ab;
        reservePar = null;
    }

    @Override
    public synchronized void retourner() {
        empruntePar = null;
        reservePar = null;
    }
}