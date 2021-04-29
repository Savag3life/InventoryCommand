package life.savag3.InventoryCommand.listener;

import life.savag3.InventoryCommand.CooldownManager;
import life.savag3.InventoryCommand.Main;
import life.savag3.InventoryCommand.Settings;
import life.savag3.InventoryCommand.menus.MenuController;
import life.savag3.InventoryCommand.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandListener implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equals("invreload")) {
            if (sender.hasPermission("inventorycommand.reload")) {
                Main.main.setSettings(new Settings());
                sender.sendMessage(Strings.color(Main.main.getSettings().getConfig().getString("Messages.Reloaded")));
                return true;
            } else {
                sender.sendMessage(Strings.color(Main.main.getSettings().getConfig().getString("Messages.NoPermission")));
                return true;
            }
        }

        if (label.equalsIgnoreCase("seeinv")) {
            if (!(sender instanceof Player)) return true;
            Player p = Bukkit.getPlayer(UUID.fromString(args[0]));

            if (p == null) {
                sender.sendMessage(Strings.color(Main.main.getSettings().getConfig().getString("Messages.NoAccess")));
                return true;
            }

            if (!p.isOnline()) {
                sender.sendMessage(Strings.color(Main.main.getSettings().getConfig().getString("Messages.NoAccess")));
                return true;
            }

            if (!CooldownManager.getInstance().isOnCooldown(Bukkit.getPlayer(UUID.fromString(args[0])))) {
                sender.sendMessage(Strings.color(Main.main.getSettings().getConfig().getString("Messages.NoAccess")));
                return true;
            }

            Player s = (Player) sender;
            if (s.getUniqueId().toString().equals(args[0])) {
                sender.sendMessage(Strings.color("&c[!] You cannot view your own inventory!"));
                return true;
            }

            MenuController.getInstance().displayInventory(Bukkit.getPlayer(UUID.fromString(args[0])), (Player) sender);
            return true;
        }
        return false;
    }


}
