package fr.lefoutrolleur.test;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {

    private static final String token = "";
    @Getter
    private static JDA jda;
    public static void main(String[] args){
        JDABuilder builder = JDABuilder.createDefault(token);
        jda = builder.build();
    }
}
