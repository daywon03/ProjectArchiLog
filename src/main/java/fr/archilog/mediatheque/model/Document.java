package fr.archilog.mediatheque.model;

import fr.archilog.mediatheque.exception.EmpruntException;
import fr.archilog.mediatheque.exception.ReservationException;

public interface Document {
    int numero();
    
    // exception si déjà réservé ou emprunté
    void reserver(IAbonne ab) throws ReservationException;
    
    // exception si réservé pour une autre abonné ou déjà emprunté
    void emprunter(IAbonne ab) throws EmpruntException;
    
    // sert au retour d'un document ou à l'annulation d'une réservation
    void retourner();
}