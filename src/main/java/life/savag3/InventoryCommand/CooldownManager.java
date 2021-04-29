package life.savag3.InventoryCommand;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CooldownManager {

    @Getter private static CooldownManager instance;

    @Getter private long cooldownDuration;
    private List<Player> cooldowns = new ArrayList<>();

    public CooldownManager() {
        instance = this;
        this.cooldownDuration = Main.main.getSettings().getConfig().getInt("Settings.Cooldown") * 20L;
    }

    public boolean isOnCooldown(Player p) { return this.cooldowns.contains(p); }

    public void addCooldown(Player p) {
        this.cooldowns.add(p);
        Bukkit.getScheduler().runTaskLater(Main.main, () -> this.cooldowns.remove(p), this.cooldownDuration);
    }
}
