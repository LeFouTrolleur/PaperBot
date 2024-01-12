package fr.lefoutrolleur.utils.Login;

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
    static LoginService instance = new LoginService();
    public static LoginService getInstance() {
        return instance;
    }

    /**
     * Initialize.
     *
     * @param plugin the plugin
     */
    @Override
    public void initialize(Plugin plugin, JDA jda,Guild g) throws IOException {
        initialize(plugin, new File(plugin.getDataFolder(), "login.yaml"), jda, guild);
    }


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
     * Set user uuid.
     *
     * @param user the user
     * @param uuid the uuid
     */
    public static void setUserUUID(User user, UUID uuid){
         data.computeIfAbsent(user.getId(), k -> String.valueOf(uuid));
     }

    /**
     * Get string uuid of user string.
     *
     * @param user the user
     * @return the string
     */
    public static String getStringUUIDOfUser(User user){
        return data.get(user.getId());
    }

    /**
     * Get uuid of user uuid.
     *
     * @param user the user
     * @return the uuid
     */
    public static UUID getUUIDOfUser(User user){
        String identifier = user.getId();
        return UUID.fromString(data.get(identifier));
    }

    /**
     * Get user with uuid string.
     *
     * @param uuid the uuid
     * @return the string
     */
    public static String getUserWithUUID(String uuid){
        for (String i : data.keySet()) {
            if(Objects.equals(data.get(i), uuid)) return i;
        }
        return null;
    }
    public static String getUserWithUUID(UUID uuid){
        for (String i : data.keySet()) {
            if(data.get(i).equals(String.valueOf(uuid))) return i;
        }
        return null;
    }
    public static long getLongUserWithUUID(UUID uuid){
        for (String i : data.keySet()) {
            if(data.get(i).equals(String.valueOf(uuid))) return Long.parseLong(i);
        }
        return 0;
    }

    /**
     * Get uuid by code string.
     *
     * @param code the code
     * @return the string
     */
    public static UUID getUUIDByCode(String code){
        return connectionCodes.get(code);
    }

    /**
     * Put code for uuid.
     *
     * @param code the code
     * @param uuid the uuid
     */
    public static void putCodeForUUID(String code, UUID uuid){
        connectionCodes.put(code, uuid);
    }

    /**
     * Remove code for uuid.
     *
     * @param code the code
     */
    public static void removeCodeForUUID(String code){
        connectionCodes.remove(code);
    }

    /**
     * Create random code string.
     *
     * @return the string
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
     * Is connected boolean.
     *
     * @param user the user
     * @return the boolean
     */
    public static boolean isConnected(User user) {
        return getStringUUIDOfUser(user) != null;
    }

    /**
     * Is connected boolean.
     *
     * @param player the player
     * @return the boolean
     */
    public static boolean isConnected(OfflinePlayer player) {
        return getUserWithUUID(String.valueOf(player.getUniqueId())) != null;
    }

    /**
     * Remove user uuid.
     *
     * @param user the user
     */
    public static void removeUserUUID(String user){
        data.remove(user);
    }
    public static void disconnectPlayer(String user){
        removeUserUUID(user);
        removeUserVerifiedRole(jda.getUserById(user));
    }

    /**
     * Get total connections int.
     *
     * @return the int
     */
    public static int getTotalConnections(){
        return data.size();
    }

    /**
     * Save.
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
     * Remove deleteable.
     *
     * @param player the player
     */
    public static void removeDeleteable(OfflinePlayer player){
        deletableUsers.remove(player.getUniqueId());
    }

    /**
     * Is deleteable boolean.
     *
     * @param player the player
     * @return the boolean
     */
    public static boolean isDeleteable(OfflinePlayer player) {
        return deletableUsers.contains(player.getUniqueId());
    }

    /**
     * Set deleteable.
     *
     * @param player the player
     */
    public static void setDeleteable(OfflinePlayer player){
        deletableUsers.add(player.getUniqueId());
    }
    public static Collection<String> getPlayers() {
        return data.values();
    }
    public static Set<String> getUsers() {
        return data.keySet();
    }

    static String roleName = "Verified";
    static Role role;
    public static void initializeRole(boolean debug){
        List<Role> roles = guild.getRoles().stream().filter(i -> i.getName().equals(roleName)).toList();
        if (roles.size() == 0){
            RoleAction roleAction = guild.createRole();
            role = roleAction.setName(roleName).setMentionable(false).complete();
        } else role = guild.getRolesByName(roleName,true).get(0);
        loadPlayersRoles(debug);

    }
    public static Role getRole(){
        return role;
    }
    public static void addPlayerVerifiedRole(Player player){
        User user = jda.getUserById(getUserWithUUID(player.getUniqueId()));
        if(user == null) return;
        addUserVerifiedRole(user);
    }
    public static void removePlayerVerifiedRole(Player player){
        User user = jda.getUserById(getUserWithUUID(player.getUniqueId()));
        if(user == null) return;
        removeUserVerifiedRole(user);
    }
    public static void addUserVerifiedRole(User user){
        guild.addRoleToMember(user,role).queue();
    }
    public static void removeUserVerifiedRole(User user){
        guild.removeRoleFromMember(user,role).queue();
    }
    private static void loadPlayersRoles(boolean debug){
        getUsers().forEach(s -> {
            Member member = guild.getMemberById(s);
            if (member == null) return;
            if(!member.getRoles().contains(role)){
                guild.addRoleToMember(member, role).queue();
            }
        });
    }
}
