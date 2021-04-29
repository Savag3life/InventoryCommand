package life.savag3.InventoryCommand.listener;

import life.savag3.InventoryCommand.CooldownManager;
import life.savag3.InventoryCommand.Main;
import life.savag3.InventoryCommand.utils.Strings;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEventListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (ChatColor.stripColor(e.getMessage()).contains("[inventory]") || ChatColor.stripColor(e.getMessage()).contains("[Inventory]") || ChatColor.stripColor(e.getMessage()).contains("[brag]")) {
            if (e.getPlayer().hasPermission("InventoryCommand.Use")) {
                e.setCancelled(true);

                if (CooldownManager.getInstance().isOnCooldown(e.getPlayer())) {
                    e.setMessage(Strings.color(Main.main.getSettings().getConfig().getString("Messages.Cooldown")));
                    return;
                }

                for (Player p : Bukkit.getOnlinePlayers()) {
                    TextComponent Message = new TextComponent(Strings.getFormat(e.getPlayer(), p, Main.main.isDeluxeChat()));
                    Message.addExtra(Strings.buildMessage(e.getMessage(), e.getPlayer()));
                    p.spigot().sendMessage(Message);
                }

                CooldownManager.getInstance().addCooldown(e.getPlayer());
            }
        }
    }

}
