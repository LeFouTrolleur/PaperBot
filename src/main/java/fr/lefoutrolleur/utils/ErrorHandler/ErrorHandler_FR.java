package fr.lefoutrolleur.utils.ErrorHandler;

import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

public class ErrorHandler_FR {



    public static ErrorHandler handle(Player player, DefaultErrorResponse... errorResponses){
        if(player == null) return null;
        ErrorHandler rep = new ErrorHandler();
        Arrays.stream(errorResponses).forEach(i -> {
            rep.handle(i.getErrorResponse(), m -> player.sendMessage(i.getMessage()));
        });
        return rep;
    }
    public static ErrorHandler handleAll(Player player){
        if(player == null) return ignoreAll();
        ErrorHandler rep = new ErrorHandler();
        for (ErrorHandler_FR.DefaultErrorResponse er : ErrorHandler_FR.DefaultErrorResponse.values()) {
            rep.handle(er.getErrorResponse(), m -> player.sendMessage(er.getMessage()));
        }
        return rep;
    }
    public static ErrorHandler ignore(DefaultErrorResponse... responses){
        ErrorHandler rep = new ErrorHandler();
        Arrays.stream(responses).forEach(i -> {
            rep.ignore(i.getErrorResponse());
        });
        return rep;
    }
    public static ErrorHandler ignoreAll(){
        ErrorHandler rep = new ErrorHandler();
        Arrays.stream(DefaultErrorResponse.values()).forEach(i -> rep.ignore(i.getErrorResponse()));
        return rep;
    }
    public static ErrorHandler handleAllButIgnore(Player player, Collection<ErrorHandler_FR.DefaultErrorResponse> handled, Collection<ErrorHandler_FR.DefaultErrorResponse> ignored){
        if(player == null) return null;
        ErrorHandler rep = new ErrorHandler();
            for (ErrorHandler_FR.DefaultErrorResponse er : ErrorHandler_FR.DefaultErrorResponse.values()){
                if(ignored.contains(er)){
                    rep.ignore(er.getErrorResponse());
                } else rep.handle(er.getErrorResponse(), m -> player.sendMessage(er.getMessage()));
        }
        return rep;
    }
    public static ErrorHandler ignoreAllButHandle(Player player, Collection<ErrorHandler_FR.DefaultErrorResponse> ignored, Collection<ErrorHandler_FR.DefaultErrorResponse> handled){
        if(player == null) return null;
        ErrorHandler rep = new ErrorHandler();
        for (ErrorHandler_FR.DefaultErrorResponse er : ErrorHandler_FR.DefaultErrorResponse.values()){
            if(ignored.contains(er)){
                rep.ignore(er.getErrorResponse());
            } else rep.handle(er.getErrorResponse(), m -> player.sendMessage(er.getMessage()));
        }
        return rep;
    }

    public enum DefaultErrorResponse {
        USER_NOT_CONNECTED(ErrorResponse.USER_NOT_CONNECTED,"L'utilisateur n'est pas connecté!"),
        SERVER_ERROR(ErrorResponse.SERVER_ERROR,"Une erreur interne au serveur de Discord s'est produite!"),
        EMPTY_MESSAGE(ErrorResponse.EMPTY_MESSAGE,"Le message est vide!"),
        CANNOT_SEND_MESSAGE_TO_USER(ErrorResponse.CANNOT_SEND_TO_USER, "Le bot n'arrive pas envoyer de message à l'utilisateur!"),
        EMBED_DISABLED(ErrorResponse.EMBED_DISABLED,"Ce serveur a désactivé les Embeds!"),
        UNKNOWN_WEBHOOK(ErrorResponse.UNKNOWN_WEBHOOK,"Ce Webhook n'existe pas!"),
        UNKNOWN_ACCOUNT(ErrorResponse.UNKNOWN_ACCOUNT,"Ce compte n'existe pas!");

        private final ErrorResponse response;
        private final String message;
        DefaultErrorResponse(ErrorResponse e, String m) {
            response = e;
            message = m;
        }
        public ErrorResponse getErrorResponse(){
            return response;
        }
        public String getMessage(){
            return message;
    }
    }
}
