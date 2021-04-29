package life.savag3.InventoryCommand.menus;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import life.savag3.InventoryCommand.Main;
import life.savag3.InventoryCommand.XMaterial;
import life.savag3.InventoryCommand.utils.Strings;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MenuController {

    private static MenuController instance;

    private List<String> displayLore;
    private ItemStack displayItem;

    public MenuController() {
        instance = this;

        displayItem = new ItemStack(
                Material.getMaterial(Main.main.getSettings().getConfig().getString("InfoItem.Type")),
                Main.main.getSettings().getConfig().getInt("InfoItem.Size"),
                (short) Main.main.getSettings().getConfig().getInt("InfoItem.Damage")
        );

        displayLore = Main.main.getSettings().getConfig().getStringList("InfoItem.Lore");
    }

    public static MenuController getInstance() { return instance; }

    public void displayInventory(Player inQuestion, Player sender) {
        Gui inv = new Gui(Main.main, 5, Strings.color(Main.main.getSettings().getConfig().getString("Settings.MenuTitle")));

        PaginatedPane pane = new PaginatedPane(0, 0, 9, inv.getRows());
        List<GuiItem> GUIItems = new ArrayList<>();
        ItemStack dumby = new ItemStack(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());

        for (int x = 0; x <= 44; x++) GUIItems.add(x, new GuiItem(XMaterial.AIR.parseItem(), e -> e.setCancelled(true)));

        PlayerInventory pi = inQuestion.getInventory();

        for (int x = 0; x <= 35; x++) {
            if (pi.getItem(x) == null || pi.getItem(x).getType() == Material.AIR) continue;
            GUIItems.set(x, new GuiItem(pi.getItem(x), e -> e.setCancelled(true)));
        }

        ItemStack tag = this.displayItem.clone();
        ItemMeta meta = tag.getItemMeta();
        List<String> lore = new ArrayList<>(this.displayLore); // shallow copy the array to preserve the placeholders.

        int count = 0;
        for (String line : lore) {
            String msg = PlaceholderAPI.setPlaceholders(sender, line);
            String msg2 = PlaceholderAPI.setRelationalPlaceholders(inQuestion, sender, msg);
            lore.set(count, Strings.color(msg2));
            count++;
        }

        meta.setLore(lore);
        meta.setDisplayName(Strings.color(Main.main.getSettings().getConfig().getString("InfoItem.Name")));
        tag.setItemMeta(meta);

        GUIItems.set(36, new GuiItem(dumby, e -> e.setCancelled(true)));

        GUIItems.set(37, new GuiItem(pi.getHelmet() != null ? pi.getHelmet() : new ItemStack(Material.AIR), e -> e.setCancelled(true)));
        GUIItems.set(38, new GuiItem(pi.getChestplate() != null ? pi.getChestplate() : new ItemStack(Material.AIR), e -> e.setCancelled(true)));
        GUIItems.set(39, new GuiItem(pi.getLeggings() != null ? pi.getLeggings() : new ItemStack(Material.AIR), e -> e.setCancelled(true)));
        GUIItems.set(40, new GuiItem(pi.getBoots() != null ? pi.getBoots() : new ItemStack(Material.AIR), e -> e.setCancelled(true)));

        GUIItems.set(41, new GuiItem(dumby, e -> e.setCancelled(true)));
        GUIItems.set(42, new GuiItem(dumby, e -> e.setCancelled(true)));
        GUIItems.set(44, new GuiItem(dumby, e -> e.setCancelled(true)));

        GUIItems.set(43, new GuiItem(tag, e -> e.setCancelled(true)));

        pane.populateWithGuiItems(GUIItems);
        inv.addPane(pane);
        inv.update();
        inv.show(sender);
    }
}
