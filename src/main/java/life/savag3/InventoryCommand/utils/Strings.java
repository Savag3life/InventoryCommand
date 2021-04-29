package life.savag3.InventoryCommand.utils;

import life.savag3.InventoryCommand.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class Strings {

    public static String color(String x) {
        return ChatColor.translateAlternateColorCodes('&', x);
    }

    public static TextComponent buildComp(String text, List<String> tool, String command, Player p, Player p2) {
        String replaced1 = PlaceholderAPI.setRelationalPlaceholders(p, p2, text);
        String replaced2 = PlaceholderAPI.setPlaceholders(p, replaced1);
        String cleaned = Strings.color(replaced2);

        TextComponent channel = new TextComponent(cleaned);
        channel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command.replace("%player%", p.getName())));

        StringBuilder line = new StringBuilder();
        for (int i = 0; i <= tool.size() - 1; i++) {
            tool.set(i, Strings.color(tool.get(i)));
            tool.set(i, PlaceholderAPI.setPlaceholders(p, tool.get(i)));
            tool.set(i, PlaceholderAPI.setRelationalPlaceholders(p, p2, tool.get(i)));
            line.append("\n").append(tool.get(i));
        }

        channel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(line.toString()).create()));

        return channel;
    }

    public static TextComponent buildMessage(String msg1, Player p) {
        String msg = msg1.replace("[inventory]", " ; ");
        String[] args = msg.split(";");
        String inv = Strings.color(Main.main.getSettings().getConfig().getString("Settings.ReplaceMessage").replace("{players}", p.getName()));
        TextComponent invPart = new TextComponent(inv);
        String hoverText = Strings.color(Main.main.getSettings().getConfig().getString("Settings.HoverText").replace("{players}", p.getName()));
        invPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
        invPart.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/seeinv " + p.getUniqueId().toString()));

        if (args[0] == null) args[0] = " ";
        TextComponent message = new TextComponent(Strings.color(args[0]));
        message.addExtra(invPart);
        if (args[1] == null) args[1] = " ";
        message.addExtra(Strings.color(args[1]));

        return message;
    }

    public static TextComponent getFormat(Player player, Player player2, boolean dc) {
        String p = (dc ? "formats.default." : "Format.");
        FileConfiguration d = dc ? Main.main.getSettings().getDeluxeChatConfig() : Main.main.getSettings().getConfig();

        String channel = d.getString(p + "channel");
        String prefix = d.getString(p + "prefix");
        String nameColor = d.getString(p + "name_color");
        String name = d.getString(p + "name");
        String suffix = d.getString(p + "suffix");
        String chatColor = d.getString(p + "chat_color");

        List<String> channelTooltip = d.getStringList(p + "channel_tooltip");
        List<String> prefixTooltip = d.getStringList(p + "prefix_tooltip");
        List<String> nameTooltip = d.getStringList(p + "name_tooltip");
        List<String> suffixTooltip = d.getStringList(p + "suffix_tooltip");

        String channelCommand = d.getString(p + "channel_click_command");
        String prefixCommand = d.getString(p + "prefix_click_command");
        String nameCommand = d.getString(p + "name_click_command");
        String suffixCommand = d.getString(p + "suffix_click_command");

        TextComponent channelComp = Strings.buildComp(channel, channelTooltip, channelCommand, player, player2);
        TextComponent prefixComp = Strings.buildComp(prefix, prefixTooltip, prefixCommand, player, player2);
        TextComponent nameComp = Strings.buildComp(nameColor + name, nameTooltip, nameCommand, player, player2);
        TextComponent suffixComp = Strings.buildComp(suffix + chatColor, suffixTooltip, suffixCommand, player, player2);

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

    private Strings() { throw new UnsupportedOperationException("Class cannot be initialized"); }

}
