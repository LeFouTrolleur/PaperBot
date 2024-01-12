package fr.lefoutrolleur.utils.ErrorHandler;

import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;

public class ErrorHandler_FR {



    public static ErrorHandler handle(CommandSender sender, ErrorReponses... errorResponses){
        if(sender == null) return ignoreAll();
        ErrorHandler rep = new ErrorHandler();
        Arrays.stream(errorResponses).forEach(i -> {
            rep.handle(i.getErrorResponse(), m -> sender.sendMessage(i.getMessage()));
        });
        return rep;
    }
    public static ErrorHandler handleAll(CommandSender sender){
        if(sender == null) return ignoreAll();
        ErrorHandler rep = new ErrorHandler();
        for (ErrorReponses er : ErrorReponses.values()) {
            rep.handle(er.getErrorResponse(), m -> sender.sendMessage(er.getMessage()));
        }
        return rep;
    }
    public static ErrorHandler ignore(ErrorReponses... responses){
        ErrorHandler rep = new ErrorHandler();
        Arrays.stream(responses).forEach(i -> {
            rep.ignore(i.getErrorResponse());
        });
        return rep;
    }
    public static ErrorHandler ignoreAll(){
        ErrorHandler rep = new ErrorHandler();
        Arrays.stream(ErrorReponses.values()).forEach(i -> rep.ignore(i.getErrorResponse()));
        return rep;
    }
    public static ErrorHandler handleAllButIgnore(CommandSender sender, Collection<ErrorReponses> handled, Collection<ErrorReponses> ignored){
        if(sender == null) return ignoreAll();
        ErrorHandler rep = new ErrorHandler();
            for (ErrorReponses er : ErrorReponses.values()){
                if(ignored.contains(er)){
                    rep.ignore(er.getErrorResponse());
                } else rep.handle(er.getErrorResponse(), m -> sender.sendMessage(er.getMessage()));
        }
        return rep;
    }
    public static ErrorHandler ignoreAllButHandle(CommandSender sender, Collection<ErrorReponses> ignored, Collection<ErrorReponses> handled){
        if(sender == null) return ignoreAll();
        ErrorHandler rep = new ErrorHandler();
        for (ErrorReponses er : ErrorReponses.values()){
            if(ignored.contains(er)){
                rep.ignore(er.getErrorResponse());
            } else rep.handle(er.getErrorResponse(), m -> sender.sendMessage(er.getMessage()));
        }
        return rep;
    }

    public enum ErrorReponses {
        USER_NOT_CONNECTED(ErrorResponse.USER_NOT_CONNECTED,"L'utilisateur n'est pas connecté!"),
        SERVER_ERROR(ErrorResponse.SERVER_ERROR,"Une erreur interne au serveur de Discord s'est produite!"),
        EMPTY_MESSAGE(ErrorResponse.EMPTY_MESSAGE,"Le message est vide!"),
        CANNOT_SEND_MESSAGE_TO_USER(ErrorResponse.CANNOT_SEND_TO_USER, "Le bot n'arrive pas envoyer de message à l'utilisateur!"),
        EMBED_DISABLED(ErrorResponse.EMBED_DISABLED,"Ce serveur a désactivé les Embeds!"),
        UNKNOWN_WEBHOOK(ErrorResponse.UNKNOWN_WEBHOOK,"Ce Webhook n'existe pas!"),
        UNKNOWN_ACCOUNT(ErrorResponse.UNKNOWN_ACCOUNT,"Ce compte n'existe pas!");

        private final ErrorResponse response;
        private final String message;
        ErrorReponses(ErrorResponse e, String m) {
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
