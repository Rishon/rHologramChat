package dev.rishon.rhologramchat.handler;

import dev.rishon.rhologramchat.Main;
import dev.rishon.rhologramchat.commands.MainCommand;
import dev.rishon.rhologramchat.commands.SelfHologramCommand;
import dev.rishon.rhologramchat.data.CacheData;
import dev.rishon.rhologramchat.data.SQLData;
import dev.rishon.rhologramchat.listeners.PlayerChat;
import dev.rishon.rhologramchat.listeners.ServerTick;
import dev.rishon.rhologramchat.types.DataTypes;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class MainHandler implements Handler {

    // Instance
    private final Main plugin;
    // Handlers
    private NMSHandler nmsHandler;
    private FileHandler fileHandler;
    // Config
    private FileConfiguration config;

    // Data
    private DataTypes dataType;
    private SQLData sqlData;
    private CacheData cacheData;

    public MainHandler(Main plugin) {
        this.plugin = plugin;
        this.plugin.getLogger().info("Initializing MainHandler...");
        this.register();
    }

    @Override
    public void register() {
        this.config = plugin.getConfig();
        this.cacheData = new CacheData(this);
        this.fileHandler = new FileHandler(this);

        this.nmsHandler = new NMSHandler(this);
        this.sqlData = new SQLData(this);

        registerCommands();
        registerListeners();

        loadPlayers();
        this.plugin.getLogger().info("MainHandler initialized!");
    }

    @Override
    public void unregister() {
        this.nmsHandler.unregister();
        this.sqlData.unregister();
        this.fileHandler.unregister();
    }

    private void registerListeners() {
        PluginManager manager = this.plugin.getServer().getPluginManager();
        manager.registerEvents(new PlayerChat(this), this.plugin);
        manager.registerEvents(new ServerTick(this.getNmsHandler()), this.plugin);
    }

    private void registerCommands() {
        this.plugin.getCommand("rhologramchat").setExecutor(new MainCommand(this));
        this.plugin.getCommand("selfhologram").setExecutor(new SelfHologramCommand(this.getCacheData()));
    }

    void loadPlayers() {
        if (!this.getSqlData().isEnabled()) return;
        this.getPlugin().getLogger().info("Loading players...");
        this.getPlugin().getServer().getOnlinePlayers().forEach(player -> {
            UUID uuid = player.getUniqueId();
            CompletableFuture.runAsync(() -> this.getSqlData().loadUser(uuid));
        });
    }

    public void setDataType(DataTypes dataType) {
        this.dataType = dataType;
    }

}
