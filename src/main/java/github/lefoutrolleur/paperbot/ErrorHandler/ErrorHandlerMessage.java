package github.lefoutrolleur.paperbot.ErrorHandler;

import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

public class ErrorHandlerMessage {



    public static ErrorHandler handle(CommandSender sender, ErrorResponse... errorResponses){
        if(sender == null) return null;
        ErrorHandler rep = new ErrorHandler();
        Arrays.stream(errorResponses).forEach(i -> rep.handle(i, m -> sender.sendMessage(getMessageByErrorResponse(i))));
        return rep;
    }
    public static ErrorHandler handleAll(CommandSender sender){
        if(sender == null) return ignoreAll();
        ErrorHandler rep = new ErrorHandler();
        for (ErrorResponse er : ErrorResponse.values()) {
            rep.handle(er, m -> sender.sendMessage(getMessageByErrorResponse(er)));
        }
        return rep;
    }
    public static ErrorHandler ignore(ErrorResponse... responses){
        ErrorHandler rep = new ErrorHandler();
        Arrays.stream(responses).forEach(rep::ignore);
        return rep;
    }
    public static ErrorHandler ignoreAll(){
        ErrorHandler rep = new ErrorHandler();
        Arrays.stream(ErrorResponse.values()).forEach(rep::ignore);
        return rep;
    }
    public static ErrorHandler handleAndIgnore(CommandSender sender, Collection<ErrorResponse> handled, Collection<ErrorResponse> ignored){
        if(sender == null) return null;
        ErrorHandler rep = new ErrorHandler();
        handled.forEach(handle -> rep.handle(handle, m -> sender.sendMessage(getMessageByErrorResponse(handle))));
        ignored.forEach(rep::ignore);
        return rep;
    }
    private static String getMessageByErrorResponse(ErrorResponse errorResponse){
        String fieldName = errorResponse.name();
        try {
            Field field = ErrorMessage.class.getDeclaredField(fieldName);
            ErrorMessage value = (ErrorMessage) field.get(null);
            return value.message;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public enum ErrorMessage{
        USER_NOT_CONNECTED("L'utilisateur n'est pas connecté!"),
        SERVER_ERROR("Une erreur interne au serveur de Discord s'est produite!"),
        EMPTY_MESSAGE("Le message est vide!"),
        CANNOT_SEND_TO_USER("Le bot n'arrive pas envoyer de message à l'utilisateur!"),
        EMBED_DISABLED("Ce serveur a désactivé les Embeds!"),
        UNKNOWN_WEBHOOK("Ce Webhook n'existe pas!"),
        UNKNOWN_ACCOUNT("Ce compte n'existe pas!");

        private final String message;
        ErrorMessage(String m) {
            message = m;
        }
        public String getMessage(){
            return message;
    }
    }
}
