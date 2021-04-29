package life.savag3.InventoryCommand;

import life.savag3.InventoryCommand.listener.ChatEventListener;
import life.savag3.InventoryCommand.listener.CommandListener;
import life.savag3.InventoryCommand.menus.MenuController;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    public static Main main;

    @Getter @Setter private Settings settings;
    @Getter private boolean deluxeChat = false;

    @Override
    public void onEnable() {
        main = this;
        this.settings = new Settings();

        if (Bukkit.getPluginManager().isPluginEnabled("DeluxeChat")) {
            this.deluxeChat = true;
            log(" Found DeluxeChat, using Attempting to get DChat formatting.");
            if (this.settings.getDeluxeChatConfig() == null) {
                log("DChat Config was null");
                this.deluxeChat = false;
            } else {
                log(" Found DeluxeChat config, using it.");
            }
        } else {
            log("Couldn't Find DeluxeChat.. defaulting to config.yml settings.");
            this.deluxeChat = false;
        }

        if (!(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))) {
            log("PlaceHolderAPI was not found. This is a required dependency.. Disabling!");
            suicide();
            return;
        }

        new MenuController();
        new CooldownManager();

        Bukkit.getPluginManager().registerEvents(new ChatEventListener(), this);

        getCommand("seeinv").setExecutor(new CommandListener());
        getCommand("invreload").setExecutor(new CommandListener());
    }

    @Override
    public void onDisable() { }

    public void suicide() { Bukkit.getPluginManager().disablePlugin(this); }

    private void log(String message) {
        Bukkit.getLogger().info("[InventoryCommand] " + message);
    }
}
