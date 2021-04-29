package life.savag3.InventoryCommand;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Settings {

    @Getter private FileConfiguration config, deluxeChatConfig;
    private File configFile, deluxeChatFile;

    public Settings() {
        Main.main.getDataFolder().mkdir();

        configFile = new File(Main.main.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                File en = new File(Main.main.getDataFolder(), "config.yml");
                InputStream E = getClass().getResourceAsStream("/config.yml");
                copyFile(E, en);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().info("Could Not Create Config.yml!");
                Main.main.suicide();
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        if (Main.main.isDeluxeChat()) return;

        deluxeChatFile = new File("plugins/DeluxeChat/config.yml");
        if (!deluxeChatFile.exists()) return;
        deluxeChatConfig = YamlConfiguration.loadConfiguration(deluxeChatFile);
    }

    private static void copyFile(InputStream in, File out) throws Exception {
        try (InputStream fis = in; FileOutputStream fos = new FileOutputStream(out)) {
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }
    }
}
