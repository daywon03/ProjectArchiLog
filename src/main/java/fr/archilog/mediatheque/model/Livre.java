package fr.archilog.mediatheque.model;

import fr.archilog.mediatheque.exception.EmpruntException;
import fr.archilog.mediatheque.exception.ReservationException;

public class Livre implements Document {
    private final int numero;
    private final String titre;
    private final int nombrePages;
    private Abonne reservePar;
    private Abonne empruntePar;

    public Livre(int numero, String titre, int nombrePages) {
        this.numero = numero;
        this.titre = titre;
        this.nombrePages = nombrePages;
    }

    @Override
    public int numero() {
        return numero;
    }

    @Override
    public void reserver(Abonne ab) throws ReservationException {
        if (reservePar != null || empruntePar != null) {
            throw new ReservationException("Le livre est déjà réservé ou emprunté");
        }
        reservePar = ab;
    }

    @Override
    public void emprunter(Abonne ab) throws EmpruntException {
        if (empruntePar != null) {
            throw new EmpruntException("Le livre est déjà emprunté");
        }
        if (reservePar != null && reservePar.getNumero() != ab.getNumero()) {
            throw new EmpruntException("Le livre est réservé par un autre abonné");
        }
        empruntePar = ab;
        reservePar = null;
    }

    @Override
    public void retourner() {
        empruntePar = null;
        reservePar = null;
    }

    public int getNombrePages() {
        return nombrePages;
    }
}