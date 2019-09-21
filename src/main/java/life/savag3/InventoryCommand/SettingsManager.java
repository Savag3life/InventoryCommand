package life.savag3.InventoryCommand;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SettingsManager {

        private SettingsManager() { }

        private static SettingsManager instance = new SettingsManager();

        public static SettingsManager getInstance() {
            return instance;
        }

        private FileConfiguration config;
        private FileConfiguration dconfig;

        private File ConfigFile;
        private File DChatFile;

        public void setup(Plugin p) {
            if (!p.getDataFolder().exists()) p.getDataFolder().mkdir();

            ConfigFile = new File(p.getDataFolder(), "config.yml");

            if (!ConfigFile.exists()) {
                try {
                    File en = new File(p.getDataFolder(), "config.yml");
                    InputStream E = getClass().getResourceAsStream("/config.yml");
                    copyFile(E, en);
                }catch (Exception e) {
                    e.printStackTrace();
                    Bukkit.getLogger().info("Could Not Create Config.yml!");
                    Main.main.suicide();
                }
            }

            config = YamlConfiguration.loadConfiguration(ConfigFile);

            if (Main.main.deluxeChat) return;
            DChatFile = new File("plugins/DeluxeChat/config.yml");
            if (!DChatFile.exists()) return;
            dconfig = YamlConfiguration.loadConfiguration(DChatFile);
        }

        FileConfiguration getConfig() {
            return config;
        }

        FileConfiguration getDConfig() {
            return dconfig;
        }

        public void saveConfig() {
            try { config.save(ConfigFile);
            } catch (Exception e) { e.printStackTrace(); }
        }

        public void reloadConfig() {
            config = YamlConfiguration.loadConfiguration(ConfigFile);
        }

        public void reloadDChatConfig() {
            dconfig = YamlConfiguration.loadConfiguration(DChatFile);
        }

        private static void copyFile(InputStream in, File out) throws Exception { // https://bukkit.org/threads/extracting-file-from-jar.16962/
            try (InputStream fis = in; FileOutputStream fos = new FileOutputStream(out)) {
                byte[] buf = new byte[1024];
                int i = 0;
                while ((i = fis.read(buf)) != -1) {
                    fos.write(buf, 0, i);
                }
            }
        }

    }
