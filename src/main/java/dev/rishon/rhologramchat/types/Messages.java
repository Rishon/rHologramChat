package dev.rishon.rhologramchat.types;

public enum Messages {
    DATA_ERROR("data-error", new String[]{
            "&cAn error occurred while trying to get your data."
    }),
    NO_PERMISSION("no-permission", new String[]{
            "&cYou do not have permission to use this command."
    }),
    NO_CONSOLE("no-console", new String[]{
            "&cYou must be a player to use this command."
    }),
    SELF_ENABLED("self-hologram.enabled", new String[]{
            "&aYou have enabled self holograms."
    }),
    SELF_DISABLED("self-hologram.disabled", new String[]{
            "&cYou have disabled self holograms."
    }),
    HELP_COMMAND("help-command", new String[]{
            "&#15d176&m--------------------------------",
            "&#3d9dbf&lrHologramChat &8&l• &cCommands",
            "&e/hologramchat &7- &fHelp command.",
            "&e/hologramchat toggle &7- &fToggle hologram chat.",
            "&e/selfhologram &7- &fToggle self hologram.",
            "&#15d176&m--------------------------------",
    }),
    ADMIN_COMMAND("admin-command", new String[]{
            "&#15d176&m--------------------------------",
            "&#3d9dbf&lrHologramChat &8&l• &cAdmin Commands",
            "&e/hologramchat reload &7- &fReload the plugin.",
            "&#15d176&m--------------------------------",
    }),
    RELOAD_COMMAND("reload-command", new String[]{
            "&#2eb337Plugin config reloaded!",
            "&#FF0000Any database type changes will not take effect until the server is restarted."
    });
    private final String path;
    private String[] value;

    Messages(String path, String[] value) {
        this.path = path;
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

}
