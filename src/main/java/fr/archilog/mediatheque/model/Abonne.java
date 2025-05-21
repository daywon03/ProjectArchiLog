package fr.archilog.mediatheque.model;

import java.time.LocalDate;

public class Abonne {
    private final int numero;
    private final String nom;
    private final LocalDate dateNaissance;

    public Abonne(int numero, String nom, LocalDate dateNaissance) {
        this.numero = numero;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
    }

    public int getNumero() {
        return numero;
    }

    public String getNom() {
        return nom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public boolean estAdulte() {
        return LocalDate.now().minusYears(16).isAfter(dateNaissance);
    }
}