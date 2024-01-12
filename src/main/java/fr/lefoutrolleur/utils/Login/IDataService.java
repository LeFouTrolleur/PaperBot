package fr.lefoutrolleur.utils.Login;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public interface IDataService {

    void initialize(Plugin plugin, JDA jd, Guild guild) throws IOException;
    void initialize(Plugin plugin, File file, JDA jda, Guild guild) throws IOException;

    void save();
}
