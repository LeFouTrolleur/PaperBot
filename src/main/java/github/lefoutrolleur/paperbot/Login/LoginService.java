package github.lefoutrolleur.paperbot.Login;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * The type Login manager.
 */
public class LoginService<K extends ISnowflake,V extends Audience>{

    private final Guild guild;
    private String path;
    private Map<String, V> data = new HashMap<>();
    private final JDA jda;
    private final Plugin plugin;
    private final File file;

    private final List<K> deletableUsers = new ArrayList<>();
    private final Map<String, String> connectionCodes = new HashMap<>();
    public LoginService(Plugin plugin,Guild guild, @Nullable String roleName){
        this(plugin,guild, new File(plugin.getDataFolder(), "login.yaml"),roleName);
    }
    public LoginService(Plugin plugin,Guild guild, File file,@Nullable String roleName){
        this.jda = guild.getJDA();
        this.guild = guild;
        this.plugin = plugin;
        this.file = file;
        try {
            initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(roleName != null){
            this.roleName = roleName;
            initializeRole();
        }
    }

    public void initialize() throws IOException {
        if(!file.exists()){
            file.createNewFile();
            plugin.saveResource(file.getName(), false);
        }
        path = plugin.getDataFolder().getPath();
        loadToCache();
    }

    /**
     * Stock to the Cache all temporary data
     */
    private void loadToCache() throws IOException {
        Yaml yaml = new Yaml();
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(path +"\\" + file.getName());
            data = yaml.load(inputStream);
            inputStream.close();
        } catch (FileNotFoundException ignored) {
        }
        if(data == null) data = new HashMap<>();
     }


    public void setKey(K key, V value){
         data.computeIfAbsent(key.getId(), k -> value);
     }


    public V getValue(K key){
        return data.get(key);
    }

    public String getStringKeyWithValue(V value){
        for (String i : data.keySet()) {
            if(Objects.equals(data.get(i), value)) return i;
        }
        return null;
    }


    /**
     * Get the String Player UUID of the login code
     *
     * @param code the code
     * @return the String UUID
     */
    public String getValueByCode(String code){
        return connectionCodes.get(code);
    }

    public void putCodeForUUID(String code, K value){
        connectionCodes.put(code, value.getId());
    }

    /**
     * Remove the Code from the cache
     *
     * @param code the code
     */
    public void removeCodeForUUID(String code){
        connectionCodes.remove(code);
    }

    /**
     * Create a String new random code
     *
     * @return the String code
     */
    public String createRandomCode() {
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

    public boolean isSynchronised(K key) {
        return data.get(key.getId()) != null;
    }

    public boolean isSynchronised(V value) {
        return getStringKeyWithValue(value) != null;
    }

    /**
     * Remove the User and the Player UUID of the User from the data/cache
     *
     * @param user the targeted USer
     */
    public void removeUserUUID(K user){
        data.remove(user.getId());
    }

    /**
     * UnSynchronise the User
     *
     * @param user the user
     */
    public void disconnectPlayer(K user){
        removeUserUUID(user);
        removeUserVerifiedRole(jda.getUserById(user.getId()));
    }

    /**
     * Get Total synchronised account
     *
     * @return the int
     */
    public int getTotalConnections(){
        return data.size();
    }

    /**
     * Save the cache to a file
     */
    public void save(){
        try {
            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(path + "\\" + file.getName());
            yaml.dump(data, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove the state of Deletable of a Player
     *
     * @param user the user
     */
    public void removeDeleteable(K user){
        deletableUsers.remove(user);
    }

    public boolean isDeleteable(K user) {
        return deletableUsers.contains(user);
    }


    public void setDeleteable(K user){
        deletableUsers.add(user);
    }

    /**
     * Gets all synchronised Players
     *
     * @return Synchronised Players Collection
     */
    public Collection<V> getPlayers() {
        return data.values();
    }

    /**
     * Gets all synchronised Users
     *
     * @return Synchronised Users Set
     */
    public Set<String> getUsers() {
        return data.keySet();
    }



    /**
     * The Verified Role name.
     */
    String roleName;
    /**
     * The Verified Role.
     */
    Role role;

    /**
     * Initialize role cache and Handlers.
     *
     */
    private void initializeRole(){
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
    public Role getRole(){
        return role;
    }

    /**
     * Add of a Player, the Verified Role of his Discord Account
     *
     * @param player the player
     */
    public void addPlayerVerifiedRole(V player){
        User user = jda.getUserById(getStringKeyWithValue(player));
        if(user == null) return;
        addUserVerifiedRole(user);
    }

    /**
     * Remove of a Player, the Verified Role of his Discord Account
     *
     * @param player the player
     */
    public void removePlayerVerifiedRole(V player){
        User user = jda.getUserById(getStringKeyWithValue(player));
        if(user == null) return;
        removeUserVerifiedRole(user);
    }

    /**
     * Add of a User the Verified Role
     *
     * @param user the user
     */
    public void addUserVerifiedRole(User user){
        guild.addRoleToMember(user,role).queue();
    }

    /**
     * Remove of a User the Verified Role
     *
     * @param user the user
     */
    public void removeUserVerifiedRole(User user){
        guild.removeRoleFromMember(user,role).queue();
    }
    /**
     * Check all Synchronised Account if they have the Verified Role
     */
    private void loadPlayersRoles(){
        getUsers().forEach(s -> {
            Member member = guild.getMemberById(s);
            if (member == null) return;
            if(!member.getRoles().contains(role)){
                guild.addRoleToMember(member, role).queue();
            }
        });
    }
}
