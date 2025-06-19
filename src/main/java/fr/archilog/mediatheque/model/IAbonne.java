package fr.archilog.mediatheque.model;

import java.time.LocalDate;

public interface IAbonne {
    int getNumero();
    String getNom();
    LocalDate getDateNaissance();
    boolean estAdulte();
} 