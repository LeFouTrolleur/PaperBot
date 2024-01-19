package github.lefoutrolleur.paperbot.ErrorHandler;

import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

public class ErrorHandlerMessage {
    private final Lang lang;

    final Audience audience;
    public ErrorHandlerMessage(@Nullable Audience audience, Lang lang){
        this.audience = audience;
        this.lang = lang;
    }
    public ErrorHandlerMessage(@Nullable Audience audience){
        this.audience = audience;
        lang = Lang.ENGLISH;
    }
    public Audience getAudience(){
        return audience;
    }

    public ErrorHandler handle(CommandSender sender, ErrorResponse... errorResponses){
        if(sender == null) return null;
        ErrorHandler rep = new ErrorHandler();
        Arrays.stream(errorResponses).forEach(i -> rep.handle(i, m -> sender.sendMessage(getMessageByErrorResponse(i))));
        return rep;
    }
    public ErrorHandler handleAll(CommandSender sender){
        if(sender == null) return ignoreAll();
        ErrorHandler rep = new ErrorHandler();
        for (ErrorResponse er : ErrorResponse.values()) {
            rep.handle(er, m -> sender.sendMessage(getMessageByErrorResponse(er)));
        }
        return rep;
    }
    public ErrorHandler ignore(ErrorResponse... responses){
        ErrorHandler rep = new ErrorHandler();
        Arrays.stream(responses).forEach(rep::ignore);
        return rep;
    }
    public ErrorHandler ignoreAll(){
        ErrorHandler rep = new ErrorHandler();
        Arrays.stream(ErrorResponse.values()).forEach(rep::ignore);
        return rep;
    }
    public ErrorHandler handleAndIgnore(CommandSender sender, Collection<ErrorResponse> handled, Collection<ErrorResponse> ignored){
        if(sender == null) return null;
        ErrorHandler rep = new ErrorHandler();
        handled.forEach(handle -> rep.handle(handle, m -> sender.sendMessage(getMessageByErrorResponse(handle))));
        ignored.forEach(rep::ignore);
        return rep;
    }
    private String getMessageByErrorResponse(ErrorResponse errorResponse){
        if(lang == Lang.ENGLISH) return errorResponse.getMeaning();
        String fieldName = lang.prefix + "_" + errorResponse.name();
        try {
            Field field = ErrorMessage.class.getDeclaredField(fieldName);
            ErrorMessage value = (ErrorMessage) field.get(null);
            return value.message;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Lang getLang() {
        return lang;
    }

    public enum ErrorMessage{
        FR_UNKNOWN_ACCOUNT("Ce Compte n'existe pas"),
        FR_UNKNOWN_APPLICATION("Cette application n'existe pas"),
        FR_UNKNOWN_CHANNEL("Ce salon n'existe pas"),
        FR_UNKNOWN_GUILD("Ce serveur n'existe pas!"),
        FR_UNKNOWN_INTEGRATION("Cette intégration n'existe pas"),
        FR_UNKNOWN_INVITE("Cette invitation ne fonctionne pas!"),
        FR_UNKNOWN_MEMBER("Ce membre n'a pas été trouvé!"),
        FR_UNKNOWN_MESSAGE("Ce message n'existe pas"),
        FR_UNKNOWN_OVERRIDE("Dérogation inconnu"),
        FR_UNKNOWN_PROVIDER("Ce fournisseur n'existe pas!"),
        FR_UNKNOWN_ROLE("Ce rôle n'existe pas!"),
        FR_UNKNOWN_TOKEN("Cette clé est inconnu"),
        FR_UNKNOWN_USER("Cet utilisateur est introuvable"),
        FR_UNKNOWN_EMOJI("Cette emoji n'existe pas"),
        FR_UNKNOWN_WEBHOOK("Ce Webhook n'existe pas"),
        FR_UNKNOWN_WEBHOOK_SERVICE("Ce service de Webhook n'existe pas"),
        FR_UNKNOWN_SESSION("Cette session est inconnu"),
        FR_UNKNOWN_BAN("Ce bannissement n'est pas trouvable"),
        FR_UNKNOWN_SKU("SKU introuvable"),
        FR_UNKNOWN_STORE_LISTING("Liste de boutique inconnu!"),
        FR_UNKNOWN_ENTITLEMENT("Titre inconnu"),
        FR_UNKNOWN_BUILD("Bâtiment inconnu"),
        FR_UNKNOWN_LOBBY("Lobby inconnu"),
        FR_UNKNOWN_BRANCH("Cette branche est inconnue"),
        FR_UNKNOWN_STORE_DIRECTORY_LAYOUT("Disposition inconnue du répertoire du magasin"),
        FR_UNKNOWN_REDISTRIBUTABLE("Redistribuable inconnu"),
        FR_UNKNOWN_GIFT_CODE("Ce code cadeau est introuvable"),
        FR_UNKNOWN_STREAM("Cette diffusion n'est pas disponible"),
        FR_UNKNOWN_PREMIUM_SERVER_SUBSCRIBE_COOLDOWN("Temps de rechargement de l'abonnement Prenium sur un serveur inconnu"),
        FR_UNKNOWN_GUILD_TEMPLATE("Modèle de serveur inconnu"),
        FR_UNKNOWN_DISCOVERABLE_SERVER_CATEGORY("Catégorie de serveur découvrable inconnue"),
        FR_UNKNOWN_STICKER("Autocollant inconnu"),
        FR_UNKNOWN_INTERACTION("Cette interaction n'existe pas"),
        FR_UNKNOWN_COMMAND("Commande inconnue !"),
        FR_UNKNOWN_COMMAND_PERMISSIONS("Cette permission de commande n'existe pas !"),
        FR_UNKNOWN_STAGE_INSTANCE("Instance de scène inconnue"),
        FR_UNKNOWN_GUILD_MEMBER_VERIFICATION_FORM("Formulaire de vérification de membre de guilde inconnu"),
        FR_UNKNOWN_GUILD_WELCOME_SCREEN("Cet écran de bienvenue n'existe pas"),
        FR_SCHEDULED_EVENT("Événement programmé inconnu"),
        FR_SCHEDULED_EVENT_USER("Utilisateur inconnu de l'événement programmé"),
        FR_BOTS_NOT_ALLOWED("Les robots ne peuvent pas utiliser ce point d'accès"),

        FR_ONLY_BOTS_ALLOWED("Seuls les robots peuvent utiliser ce point de terminaison"),
        FR_EXPLICIT_CONTENT_CANNOT_SEND_TO_RECIPIENT("Le contenu explicite ne peut pas être envoyé au(x) destinataire(s) souhaité(s)"),
        FR_NOT_AUTHORIZED_PERFORM_ACTION("Vous n'êtes pas autorisé à effectuer cette action sur cette application"),
        FR_SLOWMODE_RATE_LIMIT("Cette action ne peut être exécutée en raison d'une limite de débit en mode lent"),
        FR_OWNER_ONLY("Seul le propriétaire de ce compte peut effectuer cette action"),
        FR_ANNOUNCEMENT_RATE_LIMIT("Ce message ne peut pas être édité en raison des limites de débit des annonces"),
        FR_CHANNEL_WRITE_RATE_LIMIT("Le canal que vous êtes en train d'écrire a atteint la limite du débit d'écriture"),
        FR_GUILD_EXPLICIT_CONTENT_FILTER("Le sujet de votre stage, le nom de votre serveur, la description de votre serveur ou les noms de vos canaux contiennent des mots qui ne sont pas autorisés"),
        FR_GUILD_PREMIUM_LEVEL_TOO_LOW("Le niveau d'abonnement premium de la guilde est trop bas"),
        FR_MAX_GUILDS("Nombre maximum de guildes atteint (100)"),
        FR_MAX_FRIENDS("Nombre maximum d'amis atteint (1000)"),
        FR_MAX_MESSAGE_PINS("Nombre maximum de messages épinglés atteint (50)"),
        FR_MAX_USERS_PER_DM("Nombre maximal de destinataires atteint. (10)"),
        FR_MAX_ROLES_PER_GUILD("Nombre maximum de rôles de guilde atteint (250)"),
        FR_MAX_WEBHOOKS("Nombre maximum de webhooks atteint (10)"),
        FR_MAX_EMOJIS("Nombre maximal d'emojis atteint"),
        FR_TOO_MANY_REACTIONS("Nombre maximal de réactions atteint (20)"),
        FR_MAX_CHANNELS("Nombre maximum de canaux de guilde atteint (500)"),
        FR_MAX_ATTACHMENTS("Nombre maximum de pièces jointes dans un message atteint (10)"),
        FR_MAX_INVITES("Nombre maximal d'invitations atteint (1000)"),
        FR_MAX_ANIMATED_EMOJIS("Nombre maximal d'emojis animés atteint"),
        FR_MAX_MEMBERS("Nombre maximum de membres du serveur atteint"),
        FR_MAX_CATEGORIES("Le nombre maximum de catégories du serveur a été atteint (5)"),
        FR_ALREADY_HAS_TEMPLATE("La guilde a déjà un modèle"),
        FR_MAX_THREAD_PARTICIPANTS("Le nombre maximum de participants à la discussion a été atteint (1000)"),
        FR_FR_MAX_NON_GUILD_MEMBER_BANS("Le nombre maximum de bannissements pour les non-membres de la guilde a été dépassé"),
        FR_MAX_BAN_FETCHES("Le nombre maximum d'interdictions a été atteint"),
        FR_MAX_STICKERS("Le nombre maximum d'autocollants a été atteint"),
        FR_MAX_PRUNE_REQUESTS("Le nombre maximum de demandes d'élagage a été atteint. Réessayez plus tard"),
        FR_MAX_GUILD_WIDGET_UPDATES("Le nombre maximum de mises à jour des paramètres du widget de guilde a été atteint. Réessayez plus tard"),
        FR_MAX_PREMIUM_EMOJIS("Le nombre maximum d'emojis premium a été atteint (25)"),
        FR_UNAUTHORIZED("Non autorisé"),
        FR_NOT_VERIFIED("Vous devez vérifier votre compte pour effectuer cette action"),
        FR_OPEN_DM_TOO_FAST("Vous ouvrez des messages directs trop rapidement"),
        FR_REQUEST_ENTITY_TOO_LARGE("L'entité demandée est trop grande"),
        FR_FEATURE_TEMPORARILY_DISABLED("Cette fonctionnalité a été temporairement désactivée côté serveur"),
        FR_USER_BANNED_FROM_GUILD("L'utilisateur est banni de cette guilde"),
        FR_USER_NOT_CONNECTED("L'utilisateur cible n'est pas connecté à la voix"),
        FR_ALREADY_CROSSPOSTED("Ce message a déjà été transféré"),
        FR_APPLICATION_COMMAND_NAME_ALREADY_EXISTS("Une commande d'application portant ce nom existe déjà"),
        FR_MISSING_ACCESS("Accès manquant"),
        FR_INVALID_ACCOUNT_TYPE("Type de compte non valide"),
        FR_INVALID_DM_ACTION("Impossible d'exécuter une action sur un canal DM"),
        FR_EMBED_DISABLED("Widget désactivé"),
        FR_INVALID_AUTHOR_EDIT("Impossible de modifier un message rédigé par un autre utilisateur"),
        FR_EMPTY_MESSAGE("Impossible d'envoyer un message vide"),
        FR_CANNOT_SEND_TO_USER("Impossible d'envoyer des messages à cet utilisateur"),
        FR_CANNOT_MESSAGE_VC("Impossible d'envoyer des messages dans un canal vocal"),
        FR_VERIFICATION_ERROR("Le niveau de vérification du canal est trop élevé"),
        FR_OAUTH_NOT_BOT("L'application OAuth2 n'a pas de bot"),
        FR_MAX_OAUTH_APPS("Limite de l'application OAuth2 atteinte"),
        FR_INVALID_OAUTH_STATE("État OAuth non valide"),
        FR_MISSING_PERMISSIONS("Permissions manquantes"),
        FR_INVALID_TOKEN("Jeton d'authentification invalide"),
        FR_NOTE_TOO_LONG("Note trop longue"),
        FR_INVALID_BULK_DELETE("Fourni trop peu ou trop de messages à supprimer. Doit fournir au moins 2 et moins de 100 messages à supprimer"),
        FR_INVALID_MFA_LEVEL("Le niveau MFA fourni n'est pas valide"),
        FR_INVALID_PASSWORD("Le mot de passe fourni n'est pas valide"),
        FR_INVALID_PIN("Un message ne peut être épinglé que sur le canal dans lequel il a été envoyé"),
        FR_INVITE_CODE_INVALID("Le code d'invitation est soit invalide, soit pris"),
        FR_INVALID_MESSAGE_TARGET("Impossible d'exécuter une action sur un message système"),
        FR_INVALID_CHANNEL_TYPE("Impossible d'exécuter une action sur ce type de canal"),
        FR_INVALID_OAUTH_ACCESS_TOKEN("Jeton d'accès OAuth2 invalide"),
        FR_MISSING_OAUTH_SCOPE("Portée OAuth2 manquante"),
        FR_INVALID_WEBHOOK_TOKEN("Invalid Webhook Token"),
        FR_INVALID_ROLE("Rôle non valide"),
        FR_INVALID_RECIPIENT("Destinataire(s) non valide(s)"),
        FR_INVALID_BULK_DELETE_MESSAGE_AGE("Un message fourni à bulk_delete date de plus de 2 semaines"),
        FR_INVALID_FORM_BODY("Corps du formulaire non valide"),
        FR_INVITE_FOR_UNKNOWN_GUILD("Une invitation a été acceptée pour une guilde dans laquelle le bot de l'application ne se trouve pas"),
        FR_INVALID_API_VERSION("Version d'API non valide"),
        FR_FILE_UPLOAD_MAX_SIZE_EXCEEDED("Le fichier téléchargé dépasse la taille maximale"),
        FR_INVALID_FILE_UPLOADED("Fichier téléchargé non valide"),
        FR_CANNOT_SELF_REDEEM_GIFT("Impossible de racheter ce cadeau"),
        FR_PAYMENT_SOURCE_REQUIRED("Source de paiement requise pour échanger le cadeau"),
        FR_CANNOT_DELETE_CHANNEL_COMMUNITY("Impossible de supprimer un canal requis pour les guildes communautaires"),
        FR_CANNOT_EDIT_STICKER_MESSAGE("Impossible de modifier un message avec des autocollants"),
        FR_INVALID_STICKER_SENT("Autocollant non valide envoyé"),
        FR_ILLEGAL_OPERATION_ARCHIVED_THREAD("Tentative d'opération sur un fil de discussion archivé, comme la modification d'un message ou l'ajout d'un utilisateur au fil de discussion"),
        FR_INVALID_THREAD_NOTIFICATION_SETTINGS("Paramètres de notification de discussion non valides"),
        FR_BEFORE_VALUE_EARLIER_THAN_THREAD_CREATION("La valeur est antérieure à la date de création de la discussion"),
        FR_SERVER_NOT_AVAILABLE_IN_YOUR_LOCATION("Ce serveur n'est pas disponible dans votre région"),
        FR_SERVER_MONETIZATION_DISABLED("Ce serveur a besoin que la monétisation soit activée pour effectuer cette action"),
        FR_SERVER_NOT_ENOUGH_BOOSTS("Ce serveur a besoin de plus de boosts pour effectuer cette action"),
        FR_MIXED_PREMIUM_ROLES_FOR_EMOJI("Impossible de mélanger les rôles avec et sans abonnement pour un emoji"),
        FR_ILLEGAL_EMOJI_CONVERSION("Impossible de convertir un emoji premium en emoji normal"),
        FR_MFA_NOT_ENABLED("Authentification MFA requise mais non activée"),
        FR_NO_USER_WITH_TAG_EXISTS("Aucun utilisateur avec DiscordTag n'existe"),
        FR_REACTION_BLOCKED("Réaction bloquée"),
        FR_RESOURCES_OVERLOADED("Ressource surchargée"),
        FR_STAGE_ALREADY_OPEN("La scène est déjà ouverte"),
        FR_REPLY_FAILED_MISSING_MESSAGE_HISTORY_PERM("Impossible de répondre sans la permission de lire l'historique des messages"),
        FR_THREAD_WITH_THIS_MESSAGE_ALREADY_EXISTS("Une discussion a déjà été créée pour ce message"),
        FR_THREAD_LOCKED("Le thread est verrouillé"),
        FR_MAX_ACTIVE_THREADS("Nombre maximal de threads actifs atteint"),
        FR_MAX_ANNOUNCEMENT_THREADS("Nombre maximal de fils d'annonces actifs atteint"),
        FR_INVALID_LOTTIE_JSON("JSON invalide pour le fichier Lottie téléchargé"),
        FR_LOTTIE_CANNOT_CONTAIN_RASTERIZED_IMAGE("Les Lotties téléchargées ne peuvent pas contenir d'images tramées telles que PNG ou JPEG"),
        FR_MAX_STICKER_FPS("Le taux de rafraîchissement maximum de l'autocollant a été dépassé"),
        FR_MAX_STICKER_FRAMES("Le nombre d'images de l'autocollant dépasse le maximum de 1000 images"),
        FR_MAX_LOTTIE_ANIMATION_DIMENSION("Dimensions maximales de l'animation Lottie dépassées"),
        FR_STICKER_FPS_TOO_SMALL_OR_TOO_LARGE("La fréquence d'images de l'autocollant est soit trop petite, soit trop grande"),
        FR_MAX_STICKER_ANIMATION_DURATION("La durée de l'animation de l'autocollant dépasse le maximum de 5 secondes"),
        FR_MESSAGE_BLOCKED_BY_AUTOMOD("Le message a été bloqué par la modération automatique"),
        FR_TITLE_BLOCKED_BY_AUTOMOD("Le titre a été bloqué par la modération automatique"),
        FR_MESSAGE_BLOCKED_BY_HARMFUL_LINK_FILTER("Message bloqué par le filtre des liens nuisibles"),
        FR_SERVER_ERROR("Une erreur interne s'est produit chez Discord! Pas de chance!");

        private final String message;
        ErrorMessage(String m) {
            message = m;
        }
    }
    public enum Lang {
        ENGLISH("EN"),
        FRENCH("FR"),
        DEUTSCH("DE"),
        SPANISH("ES");

        private final String prefix;
        Lang(String prefix){
            this.prefix = prefix;
        }

    }
}
