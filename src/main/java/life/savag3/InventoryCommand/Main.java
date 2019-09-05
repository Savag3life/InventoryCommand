package life.savag3.InventoryCommand;

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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    private List<Player> history = new ArrayList<>();
    public static Main main;
    public boolean deluxeChat = false;

    @Override
    public void onEnable() {
        main = this;
        if (Bukkit.getPluginManager().isPluginEnabled("DeluxeChat")) {
            deluxeChat = true;
        } else {
            Bukkit.getLogger().info("Couldn't Find DeluxeChat");
            Bukkit.getLogger().info("Defaulting to config.yml settings!");
            deluxeChat = false;
        }

        SettingsManager.getInstance().setup(this);

        if (!(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))) {
            Bukkit.getLogger().info("InventoryCommand could not find PlaceholderAPI, Disabling!");
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

    @Override
    public FileConfiguration getConfig() { return SettingsManager.getInstance().getConfig(); }

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
                        TextComponent Message = new TextComponent(getDeluxeFormat(e.getPlayer(), p));
                        Message.addExtra(buildMessage(e.getMessage(), e.getPlayer()));
                        p.spigot().sendMessage(Message);
                    }
                    addPlayer(e.getPlayer());
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        TextComponent Message = new TextComponent(getDefaultFormat(e.getPlayer(), p));
                        Message.addExtra(buildMessage(e.getMessage(), e.getPlayer()));
                        p.spigot().sendMessage(Message);
                    }
                    addPlayer(e.getPlayer());
                }
            }
        }
    }

    private TextComponent getDefaultFormat(Player player, Player player2) {
        FileConfiguration d = getConfig();
        Bukkit.getLogger().info("Default Format");
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

    private TextComponent getDeluxeFormat(Player player, Player player2) {
        FileConfiguration d = SettingsManager.getInstance().getDconfig();
        Bukkit.getLogger().info("DChat Format");
        String p = "formats.default.";

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
        String inv = color(getConfig().getString("Settings.ReplaceMessage").replace("{players}", p.getName()));
        TextComponent invPart = new TextComponent(inv);
        String hoverText = color(getConfig().getString("Settings.HoverText").replace("{players}", p.getName()));
        invPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
        invPart.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/seeinv " + p.getName()));

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

        String line = "";
        for (int i = 0; i <= tool.size() - 1; i++) {
            tool.set(i, color(tool.get(i)));
            tool.set(i, PlaceholderAPI.setPlaceholders(p, tool.get(i)));
            tool.set(i,  PlaceholderAPI.setRelationalPlaceholders(p, p2, tool.get(i)));
            line = line + "\n" + tool.get(i);
        }

        channel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(line).create()));

        return channel;
    }

    private String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("seeinv")) {
            if (!history.contains(Bukkit.getPlayer(args[0]))) {
                sender.sendMessage(color(getConfig().getString("Messages.NoAccess")));
                return true;
            }

            if (sender.getName().equalsIgnoreCase(args[0])) {
                sender.sendMessage(color("&c[!] You cannot view your own inventory!"));
                return true;
            }

            Inventory inv = getBuiltInventory(Bukkit.getPlayer(args[0]), (Player) sender);
            Player p = (Player) sender;
            p.openInventory(inv);
            return true;
            }
        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getClickedInventory().getTitle() == null || e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCursor() == null) return;
        if (e.getClickedInventory().getTitle().equals(color(getConfig().getString("Settings.MenuTitle"))))
            e.setCancelled(true);
    }


    @EventHandler
    public void onShiftClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getClickedInventory().getTitle() == null || e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null || e.getCursor() == null) return;
        if (e.getClickedInventory().getTitle().equals(color(getConfig().getString("Settings.MenuTitle"))))
            e.setCancelled(true);
    }



    private Inventory getBuiltInventory(Player p, Player p2) {
        Inventory inv = Bukkit.createInventory(null, 45, color(getConfig().getString("Settings.MenuTitle")));
        PlayerInventory pi = p.getInventory();
            for (int x = 0; x <= 35; x++) {
                if (pi.getItem(x) == null || pi.getItem(x).getType() == Material.AIR) continue;
                inv.setItem(x, pi.getItem(x));
            }
                ItemStack tag = new ItemStack(Material.getMaterial(getConfig().getString("InfoItem.Type")), getConfig().getInt("InfoItem.Size"), (short) getConfig().getInt("InfoItem.Damage"));
                ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
                List<String> tagLore = getConfig().getStringList("InfoItem.Lore");
                ItemMeta meta = tag.getItemMeta();
                    int count = 0;
                    for (String line : tagLore) {
                        String msg = PlaceholderAPI.setPlaceholders(p, line);
                        String msg2 = PlaceholderAPI.setRelationalPlaceholders(p, p2, msg);
                        tagLore.set(count, color(msg2));
                        count++;
                    }
                meta.setLore(tagLore);
                meta.setDisplayName(color(getConfig().getString("InfoItem.Name")));
                tag.setItemMeta(meta);

                inv.setItem(36, glass);

                inv.setItem(37, pi.getHelmet());
                inv.setItem(38, pi.getChestplate());
                inv.setItem(39, pi.getLeggings());
                inv.setItem(40, pi.getBoots());

                inv.setItem(41, glass);
                inv.setItem(42, glass);
                inv.setItem(44, glass);

                inv.setItem(43, tag);

        return inv;

    }
}
