package dev.rishon.rhologramchat;

import dev.rishon.rhologramchat.handler.MainHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private MainHandler handler;

    @Override
    public void onEnable() {
        this.handler = new MainHandler(this);
        this.getLogger().info("The plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        this.handler.unregister();
        this.getLogger().info("The plugin has been disabled!");
    }
}
