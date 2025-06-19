package fr.archilog.mediatheque.model;

public class Response {
    private final RequestStatus status;
    private final String message;

    public Response(RequestStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return status + ": " + message;
    }
} 