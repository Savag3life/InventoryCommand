package life.savag3.InventoryCommand;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    private List<Player> history = new ArrayList<>();
    private FileConfiguration config;
    public boolean deluxeChat = false;

    static Main main;

    @Override
    public void onEnable() {
        main = this;
        if (Bukkit.getPluginManager().isPluginEnabled("DeluxeChat")) {
            log(" Found DeluxeChat, using DChat formatting.");
            deluxeChat = true;
        } else {
            log("Couldn't Find DeluxeChat.. defaulting to config.yml settings.");
            deluxeChat = false;
        }

        SettingsManager.getInstance().setup(this);
        config = SettingsManager.getInstance().getConfig();

        if (!(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))) {
            log("PlaceHolderAPI was not found. This is a required dependency.. Disabling!");
            suicide();
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    public void suicide() {
        Bukkit.getPluginManager().disablePlugin(this);
    }

    private void removePlayer(Player p) {
        history.remove(p);
    }

    private void addPlayer(Player p) {
        history.add(p);
        Bukkit.getScheduler().runTaskLater(this, () -> removePlayer(p), getConfig().getInt("Settings.Cooldown") * 20);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (ChatColor.stripColor(e.getMessage()).contains("[inventory]") || ChatColor.stripColor(e.getMessage()).contains("[brag]")) {
            if (e.getPlayer().hasPermission("InventoryCommand.Use")) {
                e.setCancelled(true);
                if (history.contains(e.getPlayer())) {
                    e.setMessage(color(getConfig().getString("Messages.Cooldown")));
                    return;
                }
                if (deluxeChat) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        TextComponent Message = new TextComponent(getFormat(e.getPlayer(), p, SettingsManager.getInstance().getDConfig()));
                        Message.addExtra(buildMessage(e.getMessage(), e.getPlayer()));
                        p.spigot().sendMessage(Message);
                    }
                    addPlayer(e.getPlayer());
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        TextComponent Message = new TextComponent(getFormat(e.getPlayer(), p, SettingsManager.getInstance().getConfig()));
                        Message.addExtra(buildMessage(e.getMessage(), e.getPlayer()));
                        p.spigot().sendMessage(Message);
                    }
                    addPlayer(e.getPlayer());
                }
            }
        }
    }

    private TextComponent getFormat(Player player, Player player2, FileConfiguration d) {
        String p = "Format.";

        String channel = d.getString(p + "channel");
        String prefix = d.getString(p + "prefix");
        String nameColor = d.getString(p + "name_color");
        String name = d.getString(p + "name");
        String suffix = d.getString(p + "suffix");
        String chatColor = d.getString(p + "chat_color");

        List<String> channelTooltip = d.getStringList(p + "prefix_tooltip");
        List<String> prefixTooltip = d.getStringList(p + "prefix_tooltip");
        List<String> nameTooltip = d.getStringList(p + "prefix_tooltip");
        List<String> suffixTooltip = d.getStringList(p + "prefix_tooltip");

        String channelCommand = d.getString(p + "channel_click_command");
        String prefixCommand = d.getString(p + "prefix_click_command");
        String nameCommand = d.getString(p + "name_click_command");
        String suffixCommand = d.getString(p + "suffix_click_command");

        TextComponent channelComp = buildComp(channel, channelTooltip, channelCommand, player, player2);
        TextComponent prefixComp = buildComp(prefix, prefixTooltip, prefixCommand, player, player2);
        TextComponent nameComp = buildComp(nameColor + name, nameTooltip, nameCommand, player, player2);
        TextComponent suffixComp = buildComp(suffix + chatColor, suffixTooltip, suffixCommand, player, player2);

        TextComponent message = new TextComponent(channelComp);
        message.addExtra(prefixComp);
        message.addExtra(nameComp);
        message.addExtra(suffixComp);

        TextComponent reset = new TextComponent("");
        reset.setHoverEvent(null);
        reset.setClickEvent(null);
        message.addExtra(reset);

        return message;
    }

    private TextComponent buildMessage(String msg1, Player p) {
        String msg = msg1.replace("[inventory]", " ; ");
        String[] args = msg.split(";");
        String inv = color(config.getString("Settings.ReplaceMessage").replace("{players}", p.getName()));
        TextComponent invPart = new TextComponent(inv);
        String hoverText = color(config.getString("Settings.HoverText").replace("{players}", p.getName()));
        invPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
        invPart.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/seeinv " + p.getName()));

        if (args[0] == null) args[0] = " ";
        TextComponent message = new TextComponent(color(args[0]));
        message.addExtra(invPart);
        if (args[1] == null) args[1] = " ";
        message.addExtra(color(args[1]));

        return message;
    }

    private TextComponent buildComp(String text, List<String> tool, String command, Player p, Player p2) {
        String replaced1 = PlaceholderAPI.setRelationalPlaceholders(p, p2, text);
        String replaced2 = PlaceholderAPI.setPlaceholders(p, replaced1);
        String cleaned = color(replaced2);

        TextComponent channel = new TextComponent(cleaned);
        channel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

        StringBuilder line = new StringBuilder();
        for (int i = 0; i <= tool.size() - 1; i++) {
            tool.set(i, color(tool.get(i)));
            tool.set(i, PlaceholderAPI.setPlaceholders(p, tool.get(i)));
            tool.set(i, PlaceholderAPI.setRelationalPlaceholders(p, p2, tool.get(i)));
            line.append("\n").append(tool.get(i));
        }

        channel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(line.toString()).create()));

        return channel;
    }

    private String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equals("invreload")) {
            if (sender.hasPermission("inventorycommand.reload")) {
                SettingsManager.getInstance().reloadConfig();
                this.config = SettingsManager.getInstance().getConfig();
                sender.sendMessage(color(this.config.getString("Messages.Reloaded")));
                return true;
            } else {
                sender.sendMessage(color(this.config.getString("Messages.NoPermission")));
                return true;
            }
        }

        if (label.equalsIgnoreCase("seeinv")) {
            if (!history.contains(Bukkit.getPlayer(args[0]))) {
                sender.sendMessage(color(this.config.getString("Messages.NoAccess")));
                return true;
            }

            if (sender.getName().equalsIgnoreCase(args[0])) {
                sender.sendMessage(color("&c[!] You cannot view your own inventory!"));
                return true;
            }

            displayInventory(Bukkit.getPlayer(args[0]), (Player) sender);
            return true;
        }
        return false;
    }

    private void displayInventory(Player inQuestion, Player sender) {
        Gui inv = new Gui(this, 45, color(config.getString("Settings.MenuTitle")));

        PaginatedPane pane = new PaginatedPane(0, 0, 9, inv.getRows());
        List<GuiItem> GUIItems = new ArrayList<>();
        ItemStack dumby = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1, (short) 7);


        PlayerInventory pi = inQuestion.getInventory();

        for (int x = 0; x <= 35; x++) {
            if (pi.getItem(x) == null || pi.getItem(x).getType() == Material.AIR) continue;
            GUIItems.set(x, new GuiItem(pi.getItem(x), e -> e.setCancelled(true)));
        }

        ItemStack tag = new ItemStack(
                Material.getMaterial(config.getString("InfoItem.Type")),
                config.getInt("InfoItem.Size"),
                (short) config.getInt("InfoItem.Damage"));

        List<String> tagLore = config.getStringList("InfoItem.Lore");
        ItemMeta meta = tag.getItemMeta();

        int count = 0;
        for (String line : tagLore) {
            String msg = PlaceholderAPI.setPlaceholders(sender, line);
            String msg2 = PlaceholderAPI.setRelationalPlaceholders(sender, inQuestion, msg);
            tagLore.set(count, color(msg2));
            count++;
        }

        meta.setLore(tagLore);
        meta.setDisplayName(color(config.getString("InfoItem.Name")));
        tag.setItemMeta(meta);

        GUIItems.set(36, new GuiItem(dumby, e -> e.setCancelled(true)));

        GUIItems.set(37, new GuiItem(pi.getHelmet() == null ? pi.getHelmet() : new ItemStack(Material.AIR), e -> e.setCancelled(true)));
        GUIItems.set(38, new GuiItem(pi.getChestplate() == null ? pi.getChestplate() : new ItemStack(Material.AIR), e -> e.setCancelled(true)));
        GUIItems.set(39, new GuiItem(pi.getLeggings() == null ? pi.getLeggings() : new ItemStack(Material.AIR), e -> e.setCancelled(true)));
        GUIItems.set(40, new GuiItem(pi.getBoots() == null ? pi.getBoots() : new ItemStack(Material.AIR), e -> e.setCancelled(true)));

        GUIItems.set(41, new GuiItem(dumby, e -> e.setCancelled(true)));
        GUIItems.set(42, new GuiItem(dumby, e -> e.setCancelled(true)));
        GUIItems.set(44, new GuiItem(dumby, e -> e.setCancelled(true)));

        GUIItems.set(43, new GuiItem(tag, e -> e.setCancelled(true)));

        pane.populateWithGuiItems(GUIItems);
        inv.addPane(pane);
        inv.update();
        inv.show(sender);
    }

    private void log(String message) {
        Bukkit.getLogger().info("[InventoryCommand] " + message);
    }
}
