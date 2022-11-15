package dev.rishon.rhologramchat.handler;

import dev.rishon.rhologramchat.Main;
import dev.rishon.rhologramchat.listeners.PlayerChat;
import dev.rishon.rhologramchat.listeners.ServerTick;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;

@Getter
public class MainHandler implements Handler {

    private final Main plugin;
    private NMSHandler nmsHandler;

    public MainHandler(Main plugin) {
        this.plugin = plugin;
        this.plugin.getLogger().info("Initializing MainHandler...");
        this.register();
    }

    @Override
    public void register() {
        this.nmsHandler = new NMSHandler(this);
        registerListeners();
        this.plugin.getLogger().info("MainHandler initialized!");
    }

    @Override
    public void unregister() {
        this.nmsHandler.unregister();
    }

    private void registerListeners() {
        PluginManager manager = this.plugin.getServer().getPluginManager();
        manager.registerEvents(new PlayerChat(this), this.plugin);
        manager.registerEvents(new ServerTick(this.getNmsHandler()), this.plugin);
    }


}
