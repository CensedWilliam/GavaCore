import java.io.*;
import java.util.Properties;

public class Config {
    private static final String CONFIG_FILE = "installer.properties";
    private static Properties props = new Properties();

    static {
        load();
    }

    public static void load() {
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            // File non esiste ancora
        }
    }

    public static void save() {
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "GavaCore Installer Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setMinecraftPath(String path) {
        props.setProperty("minecraft.path", path);
        save();
    }

    public static String getMinecraftPath() {
        return props.getProperty("minecraft.path");
    }
} 