package library;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.List;

public class ConfigManager {
    private final Main plugin;
    private FileConfiguration config;
    
    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public String getPrefix() {
        return config.getString("prefix.main", "§6§l[GavaCore]");
    }
    
    public String getErrorPrefix() {
        return config.getString("prefix.error", "§c§l[GavaCore]");
    }
    
    public String getPrimaryColor() {
        return config.getString("colors.primary", "§6");
    }
    
    public String getSecondaryColor() {
        return config.getString("colors.secondary", "§7");
    }
    
    public String getAccentColor() {
        return config.getString("colors.accent", "§e");
    }
    
    public String getSuccessColor() {
        return config.getString("colors.success", "§a");
    }
    
    public String getErrorColor() {
        return config.getString("colors.error", "§c");
    }
    
    public String getInfoColor() {
        return config.getString("colors.info", "§b");
    }
    
    public String getWarningColor() {
        return config.getString("colors.warning", "§e");
    }
    
    public String getHighlightColor() {
        return config.getString("colors.highlight", "§f");
    }
    
    public String formatMessage(String message) {
        return message.replace("%prefix%", getPrefix())
                     .replace("%prefix-error%", getErrorPrefix())
                     .replace("%primary%", getPrimaryColor())
                     .replace("%secondary%", getSecondaryColor())
                     .replace("%accent%", getAccentColor())
                     .replace("%success%", getSuccessColor())
                     .replace("%error%", getErrorColor())
                     .replace("%info%", getInfoColor())
                     .replace("%warning%", getWarningColor())
                     .replace("%highlight%", getHighlightColor())
                     .replace("%chat-arrow%", config.getString("colors.chat-arrow", "§8»"));
    }
    
    public String getJoinMessage(String playerName) {
        return formatMessage(config.getString("messages.join", "%prefix% %primary%%player% %secondary%si è unito al server!")
                .replace("%player%", playerName));
    }
    
    public String getQuitMessage(String playerName) {
        return formatMessage(config.getString("messages.quit", "%prefix% %primary%%player% %secondary%ha lasciato il server!")
                .replace("%player%", playerName));
    }
    
    public String getSleepMessage(String playerName) {
        return formatMessage(config.getString("messages.sleep", "%prefix% %primary%%player% %secondary%sta dormendo.")
                .replace("%player%", playerName));
    }
    
    public String getWakeMessage(String playerName) {
        return formatMessage(config.getString("messages.wake", "%prefix% %success%%player% si sta svegliando!")
                .replace("%player%", playerName));
    }
    
    public String getChatFormat() {
        return formatMessage(config.getString("chat.format.default", "%primary%%player% %chat-arrow% %secondary%%message%"));
    }
    
    public boolean isChatEnabled() {
        return config.getBoolean("chat.enabled", true);
    }
    
    public String getNicknameSuccessMessage(String nickname) {
        return formatMessage(config.getString("commands.soprannome.success", 
                "%prefix% %success%Il tuo soprannome è stato cambiato in: %primary%%nickname%")
                .replace("%nickname%", nickname));
    }
    
    public String getNicknameErrorMessage() {
        return formatMessage(config.getString("commands.soprannome.error", 
                "%prefix-error% %error%Uso corretto: /soprannome <nuovo_nome>"));
    }
    
    public boolean isFeatureEnabled(String feature) {
        return config.getBoolean("features." + feature, true);
    }
    
    public int getBackpackSlots(String type) {
        return config.getInt("backpacks.types." + type + ".slots", 9);
    }
    
    public int getBackpackCustomModelData(String type) {
        return config.getInt("backpacks.types." + type + ".custom-model-data", 800001);
    }
    
    public String getGuiTitle() {
        return formatMessage(config.getString("gui.main-menu.title", "%primary%GavaCore Menu"));
    }
    
    public String getFileName(String type) {
        return config.getString("storage.files." + type, type + ".yml");
    }
    
    public boolean isDebugEnabled() {
        return config.getBoolean("debug.enabled", false);
    }
    
    public void saveConfig() {
        plugin.saveConfig();
    }
    
    // ===== METODI TIME & WEATHER =====
    
    public boolean isTimeWeatherEnabled() {
        return config.getBoolean("time-weather.enabled", true);
    }
    
    public long getCustomTime(String timeType) {
        return config.getLong("time-weather.times." + timeType, getDefaultTime(timeType));
    }
    
    private long getDefaultTime(String timeType) {
        switch (timeType.toLowerCase()) {
            case "dawn": return 23000;
            case "day": return 1000;
            case "noon": return 6000;
            case "sunset": return 12000;
            case "night": return 13000;
            case "midnight": return 18000;
            default: return 1000;
        }
    }
    
    public boolean areSoundsEnabled() {
        return config.getBoolean("time-weather.sounds.enabled", true);
    }
    
    public String getTimeSound(String timeType) {
        return config.getString("time-weather.sounds." + timeType, getDefaultTimeSound(timeType));
    }
    
    private String getDefaultTimeSound(String timeType) {
        switch (timeType.toLowerCase()) {
            case "day": return "BLOCK_NOTE_BLOCK_CHIME";
            case "night": return "ENTITY_WOLF_HOWL";
            case "dawn": return "ENTITY_CHICKEN_EGG";
            case "sunset": return "BLOCK_NOTE_BLOCK_BELL";
            case "clear": return "ENTITY_EXPERIENCE_ORB_PICKUP";
            case "rain": return "WEATHER_RAIN";
            case "thunder": return "ENTITY_LIGHTNING_BOLT_THUNDER";
            default: return "BLOCK_NOTE_BLOCK_CHIME";
        }
    }
    
    public float getSoundVolume() {
        return (float) config.getDouble("time-weather.sounds.volume", 0.7);
    }
    
    public float getSoundPitch() {
        return (float) config.getDouble("time-weather.sounds.pitch", 1.0);
    }
    
    public boolean areParticlesEnabled() {
        return config.getBoolean("time-weather.particles.enabled", true);
    }
    
    public String getWeatherParticle(String weatherType) {
        return config.getString("time-weather.particles." + weatherType + ".type", getDefaultParticle(weatherType));
    }
    
    public int getParticleCount(String weatherType) {
        return config.getInt("time-weather.particles." + weatherType + ".count", getDefaultParticleCount(weatherType));
    }
    
    private String getDefaultParticle(String weatherType) {
        switch (weatherType.toLowerCase()) {
            case "clear": return "FIREWORK_SPARK";
            case "rain": return "WATER_DROP";
            case "thunder": return "ELECTRIC_SPARK";
            default: return "FIREWORK_SPARK";
        }
    }
    
    private int getDefaultParticleCount(String weatherType) {
        switch (weatherType.toLowerCase()) {
            case "clear": return 10;
            case "rain": return 20;
            case "thunder": return 30;
            default: return 10;
        }
    }
    
    public int getWeatherDuration(String weatherType) {
        return config.getInt("time-weather.weather-duration." + weatherType, 6000);
    }
    
    public String getTimeWeatherMessage(String messageType) {
        String path = "time-weather.messages." + messageType;
        String defaultMsg = getDefaultTimeWeatherMessage(messageType);
        return formatMessage(config.getString(path, defaultMsg));
    }
    
    private String getDefaultTimeWeatherMessage(String messageType) {
        switch (messageType) {
            case "day-set": return "%prefix% %accent%%player% %secondary%ha impostato il %accent%giorno %secondary%in %primary%%world%";
            case "night-set": return "%prefix% %accent%%player% %secondary%ha impostato la %accent%notte %secondary%in %primary%%world%";
            case "dawn-set": return "%prefix% %accent%%player% %secondary%ha impostato l'%accent%alba %secondary%in %primary%%world%";
            case "sunset-set": return "%prefix% %accent%%player% %secondary%ha impostato il %accent%tramonto %secondary%in %primary%%world%";
            case "time-set": return "%prefix% %accent%%player% %secondary%ha impostato il tempo a %info%%time% %secondary%in %primary%%world%";
            case "clear-set": return "%prefix% %accent%%player% %secondary%ha impostato il tempo %success%sereno %secondary%in %primary%%world%";
            case "rain-set": return "%prefix% %accent%%player% %secondary%ha fatto iniziare la %info%pioggia %secondary%in %primary%%world%";
            case "thunder-set": return "%prefix% %accent%%player% %secondary%ha scatenato un %error%temporale %secondary%in %primary%%world%";
            case "world-not-found": return "%error%Mondo non trovato!";
            case "no-permission": return "%error%Non hai il permesso per usare questo comando!";
            default: return "%prefix% Azione completata";
        }
    }
    
    // ===== METODI CHAT SYSTEM =====
    
    public boolean isChatAntiSpamEnabled() {
        return config.getBoolean("chat.anti-spam.enabled", true);
    }
    
    public int getChatMessageDelay() {
        return config.getInt("chat.anti-spam.message-delay", 2);
    }
    
    public int getChatMaxRepeated() {
        return config.getInt("chat.anti-spam.max-repeated", 3);
    }
    
    public int getChatTimeout() {
        return config.getInt("chat.anti-spam.timeout", 30);
    }
    
    public boolean isChatModerationEnabled() {
        return config.getBoolean("chat.moderation.enabled", true);
    }
    
    public boolean shouldFilterCaps() {
        return config.getBoolean("chat.moderation.filter-caps", true);
    }
    
    public int getMaxCapsPercentage() {
        return config.getInt("chat.moderation.max-caps-percentage", 50);
    }
    
    public List<String> getBlockedWords() {
        return config.getStringList("chat.moderation.blocked-words");
    }
    
    // ===== METODI PERFORMANCE =====
    
    public boolean isCacheEnabled() {
        return config.getBoolean("performance.cache.enabled", true);
    }
    
    public int getCacheSize() {
        return config.getInt("performance.cache.size", 1000);
    }
    
    public int getCacheExpireTime() {
        return config.getInt("performance.cache.expire-time", 300);
    }
    
    public boolean isAsyncOperationsEnabled() {
        return config.getBoolean("performance.async-operations", true);
    }
    
    public int getMaxConcurrentAnimations() {
        return config.getInt("performance.limits.max-concurrent-animations", 10);
    }
    
    // ===== METODI BACKUP =====
    
    public boolean isBackupEnabled() {
        return config.getBoolean("storage.yaml.backup.enabled", true);
    }
    
    public int getBackupInterval() {
        return config.getInt("storage.yaml.backup.interval", 3600);
    }
    
    public int getMaxBackups() {
        return config.getInt("storage.yaml.backup.max-backups", 10);
    }
    
    public boolean isAutoSaveEnabled() {
        return config.getBoolean("storage.yaml.auto-save", true);
    }
    
    public int getSaveInterval() {
        return config.getInt("storage.yaml.save-interval", 300);
    }
} 