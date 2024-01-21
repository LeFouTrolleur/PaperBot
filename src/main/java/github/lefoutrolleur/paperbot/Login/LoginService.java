package github.lefoutrolleur.paperbot.Login;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

public class LoginService<K extends ISnowflake, V extends OfflinePlayer> {

    private final Guild guild;
    private String path;
    private Map<String, String> data = new HashMap<>();
    private final JDA jda;
    private final Plugin plugin;
    private final File file;

    private final List<K> deletableUsers = new ArrayList<>();
    private final ConcurrentHashMap<String, String> connectionCodes = new ConcurrentHashMap<>();

    public LoginService(Plugin plugin, Guild guild, @Nullable String roleName) {
        this(plugin, guild, new File(plugin.getDataFolder(), "login.yaml"), roleName);
    }

    public LoginService(Plugin plugin, Guild guild, File file, @Nullable String roleName) {
        this.jda = guild.getJDA();
        this.guild = guild;
        this.plugin = plugin;
        this.file = file;
        try {
            initialize();
        } catch (IOException e) {
            // Log the error
            e.printStackTrace();
            // Handle the error gracefully
            // ...
        }
        if (roleName != null) {
            this.roleName = roleName;
            initializeRole();
        }
    }

    public void initialize() throws IOException {
        if (!file.exists()) {
            file.createNewFile();
            plugin.saveResource(file.getName(), false);
        }
        path = plugin.getDataFolder().getPath();
        loadToCache();
    }

    private void loadToCache() throws IOException {
        Yaml yaml = new Yaml();
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(path + "\\" + file.getName());
            data = yaml.load(inputStream);
            inputStream.close();
        } catch (FileNotFoundException ignored) {
        }
        if (data == null) data = new HashMap<>();
    }

    public void setKey(K key, V value) {
        data.computeIfAbsent(key.getId(), k -> value.getUniqueId().toString());
    }

    public String getValue(K key) {
        return data.get(key.getId());
    }

    public String getStringKeyWithValue(V value) {
        for (String i : data.keySet()) {
            if (Objects.equals(data.get(i), value.getUniqueId().toString())) return i;
        }
        return null;
    }

    public String getValueByCode(String code) {
        return connectionCodes.get(code);
    }

    public void putCodeForUUID(String code, K value) {
        connectionCodes.put(code, value.getId());
    }

    public void removeCodeForUUID(String code) {
        connectionCodes.remove(code);
    }

    public String createRandomCode() {
        StringBuilder sb = new StringBuilder();
        new Random().ints(0, 10)
                .limit(12)
                .forEach(randomNumber -> {
                    if (sb.length() > 0 && sb.length() % 4 == 0) {
                        sb.append("-");
                    }
                    sb.append(randomNumber);
                });
        String code = sb.toString();
        if (connectionCodes.get(sb.toString()) != null) return createRandomCode();
        return code;
    }

    public boolean isSynchronised(K key) {
        return data.get(key.getId()) != null;
    }

    public boolean isSynchronised(V value) {
        return getStringKeyWithValue(value) != null;
    }

    public void removeUserUUID(K user) {
        data.remove(user.getId());
    }

    public void disconnectPlayer(K user) {
        removeUserUUID(user);
        removeUserVerifiedRole(jda.getUserById(user.getId()));
    }

    public int getTotalConnections() {
        return data.size();
    }

    public void save() {
        try {
            Path filePath = Paths.get(path, file.getName());
            Yaml yaml = new Yaml();
            Files.write(filePath, yaml.dump(data).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeDeleteable(K user) {
        deletableUsers.remove(user);
    }

    public boolean isDeleteable(K user) {
        return deletableUsers.contains(user);
    }

    public void setDeleteable(K user) {
        deletableUsers.add(user);
    }

    public Collection<String> getPlayers() {
        return data.values();
    }

    public Set<String> getUsers() {
        return data.keySet();
    }

    String roleName;
    Role role;

    private void initializeRole() {
        List<Role> roles = guild.getRoles().stream().filter(i -> i.getName().equals(roleName)).toList();
        if (roles.size() == 0) {
            RoleAction roleAction = guild.createRole();
            role = roleAction.setName(roleName).setMentionable(false).complete();
        } else role = guild.getRolesByName(roleName, true).get(0);
        loadPlayersRoles();
    }

    public Role getRole() {
        return role;
    }

    public void addPlayerVerifiedRole(V player) {
        User user = jda.getUserById(getStringKeyWithValue(player));
        if (user == null) return;
        addUserVerifiedRole(user);
    }

    public void removePlayerVerifiedRole(V player) {
        User user = jda.getUserById(getStringKeyWithValue(player));
        if (user == null) return;
        removeUserVerifiedRole(user);
    }

    public void addUserVerifiedRole(User user) {
        guild.addRoleToMember(user, role).queue();
    }

    public void removeUserVerifiedRole(User user) {
        guild.removeRoleFromMember(user, role).queue();
    }

    private void loadPlayersRoles() {
        getUsers().forEach(s -> {
            Member member = guild.getMemberById(s);
            if (member == null) return;
            if (!member.getRoles().contains(role)) {
                guild.addRoleToMember(member, role).queue();
            }
        });
    }
}
