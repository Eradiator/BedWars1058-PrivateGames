package me.notlewx.privategames.support.bedwars2023;

import me.notlewx.privategames.PrivateGames;
import me.notlewx.privategames.api.database.DatabaseType;
import me.notlewx.privategames.commands.proxy2023.MainCommand;
import me.notlewx.privategames.config.MainConfig;
import me.notlewx.privategames.config.proxy2023.MessagesData;
import me.notlewx.privategames.database.providers.MySQL;
import me.notlewx.privategames.listeners.bwproxy2023.ArenaJoin;
import me.notlewx.privategames.listeners.bwproxy2023.CommandListener;
import me.notlewx.privategames.listeners.bwproxy2023.ProxyReceiveListener;
import me.notlewx.privategames.messaging.redis.ProxyListener;
import me.notlewx.privategames.messaging.socket.ProxySocket;
import me.notlewx.privategames.support.Support;
import me.notlewx.privategames.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

import static me.notlewx.privategames.PrivateGames.*;

public class BedWarsProxy2023 {
    private final Plugin pl;

    public BedWarsProxy2023(Plugin plugin) {
        this.pl = plugin;
        start();
    }

    private void start() {
        if (Bukkit.getPluginManager().getPlugin("BWProxy2023") != null) {
            Utility.info("&aLoading Private Games addon...");
            support = Support.BEDWARSPROXY2023;
            bwProxyConfig = Bukkit.getPluginManager().getPlugin("BWProxy2023").getConfig();
            Utility.info("&eCreating main config...");
            mainConfig = new MainConfig(pl, "config", Bukkit.getWorldContainer().getPath() + "/plugins/BWProxy2023/Addons/PrivateGames/");
            Utility.info("&aMain config created successfully!");
            Utility.info("&eLoading messages...");
            new MessagesData();
            registerCommands();
            loadDatabase();
            registerListeners();
            PrivateGames.getInstance().loadMainListeners();
        }
    }

    private void registerCommands() {
        Utility.info("&eRegistering commands...");
        PrivateGames.getInstance().getCommand("pg").setExecutor(new MainCommand());
        Utility.info("&aCommands registered successfully!");
    }

    private void loadDatabase() {
        Utility.info("&eConnecting to database...");
        if (bwProxyConfig.getBoolean("database.enable")) {
            Utility.info("&eUsing &cMySQL &eas database provider...");
            database = new MySQL();
            databaseType = DatabaseType.MySQL;
            Utility.info("&aYour database is ready!");
        } else {
            Utility.info("&cPlease, to use the plugin in PROXY MODE use a MySQL database...");
            Bukkit.getPluginManager().disablePlugin(pl);
        }
    }

    private void registerListeners() {
        Utility.info("&eLoading listeners...");
        pl.getServer().getPluginManager().registerEvents(new ProxyListener(), pl);
        pl.getServer().getPluginManager().registerEvents(new ArenaJoin(), pl);
        pl.getServer().getPluginManager().registerEvents(new ProxyReceiveListener(), pl);
        pl.getServer().getPluginManager().registerEvents(new CommandListener(), pl);
        Utility.info("&eListeners loaded successfully");
    }
}
