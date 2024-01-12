package github.lefoutrolleur.paperbot.Login;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * The type Login manager.
 */
public class LoginService implements IDataService{

    private static String fileName = "login.yaml";
    private static Guild guild;
    private static String path;
    private static Map<String, String> data = new HashMap<>();
    private static JDA jda;

    private static final List<UUID> deletableUsers = new ArrayList<>();
    private static final Map<String, UUID> connectionCodes = new HashMap<>();
    /**
     * The Instance.
     */
    static LoginService instance = new LoginService();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static LoginService getInstance() {
        return instance;
    }

    /**
     * Initialize the class.
     *
     * @param plugin the Main Plugin Class
     * @param jda the JDA used
     */
    @Override
    public void initialize(Plugin plugin, JDA jda,Guild g) throws IOException {
        initialize(plugin, new File(plugin.getDataFolder(), "login.yaml"), jda, guild);
    }


    /**
     * Initialize the class.
     *
     * @param plugin the Main Plugin Class
     * @param jd the JDA used
     * @param g the guild used
     */
    @Override
    public void initialize(Plugin plugin, File file,JDA jd, Guild g) throws IOException {
        jda = jd;
        guild = g;
        fileName = file.getPath();
        if(!file.exists()){
            file.createNewFile();
            plugin.saveResource(fileName, false);
        }
        path = plugin.getDataFolder().getPath();
        loadToCache();
    }

    /**
     * Stock to the Cache all temporary data
     * @throws IOException
     */
    private static void loadToCache() throws IOException {
        Yaml yaml = new Yaml();
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(path +"\\" + fileName);
            data = yaml.load(inputStream);
            inputStream.close();
        } catch (FileNotFoundException ignored) {
        }
        if(data == null) data = new HashMap<>();
     }

    /**
     * Set a User ID to a Player with his UUID.
     *
     * @param user the user ID
     * @param uuid the uuid of the Player
     */
    public static void setUserUUID(User user, UUID uuid){
         data.computeIfAbsent(user.getId(), k -> String.valueOf(uuid));
     }

    /**
     * Get the String of the UUID of the Player synchronised with the targeted User.
     *
     * @param user the target user
     * @return the String of UUID
     */
    public static String getStringUUIDOfUser(User user){
        return data.get(user.getId());
    }

    /**
     * Get the UUID of the Player synchronised with the targeted User.
     *
     * @param user the target user
     * @return the UUID of the Player
     */
    public static UUID getUUIDOfUser(User user){
        return UUID.fromString(getStringUUIDOfUser(user));
    }

    /**
     * Get the User ID with the String UUID of the targeted Player.
     *
     * @param uuid the String UUID of the targeted Player
     * @return the discord User ID
     */
    public static String getUserWithUUID(String uuid){
        for (String i : data.keySet()) {
            if(Objects.equals(data.get(i), uuid)) return i;
        }
        return null;
    }

    /**
     * Get the User ID with the UUID of the targeted Player.
     *
     * @param uuid the UUID of the targeted Player
     * @return the discord User ID
     */
    public static String getUserWithUUID(UUID uuid){
        return getUserWithUUID(String.valueOf(uuid));
    }

    /**
     * Get the Long User ID with the UUID of the targeted Player.
     *
     * @param uuid the UUID of the targeted Player
     * @return the discord User ID
     */
    public static long getLongUserWithUUID(UUID uuid){
        for (String i : data.keySet()) {
            if(data.get(i).equals(String.valueOf(uuid))) return Long.parseLong(i);
        }
        return 0;
    }

    /**
     * Get the String Player UUID of the login code
     *
     * @param code the code
     * @return the String UUID
     */
    public static UUID getUUIDByCode(String code){
        return connectionCodes.get(code);
    }

    /**
     * put the Code of the UUID from the cache
     *
     * @param code the code
     * @param uuid the Player UUID
     */
    public static void putCodeForUUID(String code, UUID uuid){
        connectionCodes.put(code, uuid);
    }

    /**
     * Remove the Code from the cache
     *
     * @param code the code
     */
    public static void removeCodeForUUID(String code){
        connectionCodes.remove(code);
    }

    /**
     * Create a String new random code
     *
     * @return the String code
     */
    public static String createRandomCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            if (i > 0 && i % 4 == 0) {
                sb.append("-");
            }
            int randomNumber = random.nextInt(10);
            sb.append(randomNumber);
        }
        String code = sb.toString();
        if(connectionCodes.get(sb.toString()) != null) return createRandomCode();
        return code;
    }

    /**
     * Get if the User is synchronised with a Player
     *
     * @param user the user
     * @return if is Synchronised
     */
    public static boolean isSynchronised(User user) {
        return getStringUUIDOfUser(user) != null;
    }

    /**
     * Get if the Player is synchronised with a Discord Account
     *
     * @param player the player
     * @return if is Synchronised
     */
    public static boolean isSynchronised(OfflinePlayer player) {
        return getUserWithUUID(String.valueOf(player.getUniqueId())) != null;
    }

    /**
     * Remove the User and the Player UUID of the User from the data/cache
     *
     * @param user the targeted USer
     */
    public static void removeUserUUID(String user){
        data.remove(user);
    }

    /**
     * UnSynchronise the User
     *
     * @param user the user
     */
    public static void disconnectPlayer(String user){
        removeUserUUID(user);
        removeUserVerifiedRole(jda.getUserById(user));
    }

    /**
     * Get Total synchronised account
     *
     * @return the int
     */
    public static int getTotalConnections(){
        return data.size();
    }

    /**
     * Save the cache to a file
     */
    @Override
    public void save(){
        try {
            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(path + "\\" + fileName);
            yaml.dump(data, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove the state of Deletable of a Player
     *
     * @param player the player
     */
    public static void removeDeleteable(OfflinePlayer player){
        deletableUsers.remove(player.getUniqueId());
    }

    /**
     * Get the Deletable state of a Player
     *
     * @param player the player
     * @return the boolean
     */
    public static boolean isDeleteable(OfflinePlayer player) {
        return deletableUsers.contains(player.getUniqueId());
    }

    /**
     * Set the state of Player has Deletable
     *
     * @param player the player
     */
    public static void setDeleteable(OfflinePlayer player){
        deletableUsers.add(player.getUniqueId());
    }

    /**
     * Gets all synchronised Players
     *
     * @return Synchronised Players Collection
     */
    public static Collection<String> getPlayers() {
        return data.values();
    }

    /**
     * Gets all synchronised Users
     *
     * @return Synchronised Users Set
     */
    public static Set<String> getUsers() {
        return data.keySet();
    }

    /**
     * The Verified Role name.
     */
    static String roleName = "Verified";
    /**
     * The Verified Role.
     */
    static Role role;

    /**
     * Initialize role cache and Handlers.
     *
     */
    public static void initializeRole(){
        List<Role> roles = guild.getRoles().stream().filter(i -> i.getName().equals(roleName)).toList();
        if (roles.size() == 0){
            RoleAction roleAction = guild.createRole();
            role = roleAction.setName(roleName).setMentionable(false).complete();
        } else role = guild.getRolesByName(roleName,true).get(0);
        loadPlayersRoles();

    }

    /**
     * Get the Verified role
     *
     * @return the role
     */
    public static Role getRole(){
        return role;
    }

    /**
     * Add of a Player, the Verified Role of his Discord Account
     *
     * @param player the player
     */
    public static void addPlayerVerifiedRole(Player player){
        User user = jda.getUserById(getUserWithUUID(player.getUniqueId()));
        if(user == null) return;
        addUserVerifiedRole(user);
    }

    /**
     * Remove of a Player, the Verified Role of his Discord Account
     *
     * @param player the player
     */
    public static void removePlayerVerifiedRole(Player player){
        User user = jda.getUserById(getUserWithUUID(player.getUniqueId()));
        if(user == null) return;
        removeUserVerifiedRole(user);
    }

    /**
     * Add of a User the Verified Role
     *
     * @param user the user
     */
    public static void addUserVerifiedRole(User user){
        guild.addRoleToMember(user,role).queue();
    }

    /**
     * Remove of a User the Verified Role
     *
     * @param user the user
     */
    public static void removeUserVerifiedRole(User user){
        guild.removeRoleFromMember(user,role).queue();
    }
    /**
     * Check all Synchronised Account if they have the Verified Role
     */
    private static void loadPlayersRoles(){
        getUsers().forEach(s -> {
            Member member = guild.getMemberById(s);
            if (member == null) return;
            if(!member.getRoles().contains(role)){
                guild.addRoleToMember(member, role).queue();
            }
        });
    }
}
