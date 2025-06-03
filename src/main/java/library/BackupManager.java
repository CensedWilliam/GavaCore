package library;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BackupManager {
    
    private final Main plugin;
    private final ConfigManager configManager;
    private BukkitTask backupTask;
    private BukkitTask autoSaveTask;
    
    private static final String[] CONFIG_FILES = {
        "config.yml",
        "nicknames.yml", 
        "lockedchests.yml",
        "skins.yml",
        "shared_data.yml"
    };
    
    public BackupManager(Main plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        
        if (configManager.isBackupEnabled()) {
            startBackupTask();
        }
        
        if (configManager.isAutoSaveEnabled()) {
            startAutoSaveTask();
        }
    }
    
    public void createBackup() {
        if (!configManager.isBackupEnabled()) {
            return;
        }
        
        // Esegui backup in modo asincrono per non bloccare il server
        new BukkitRunnable() {
            @Override
            public void run() {
                performBackup();
            }
        }.runTaskAsynchronously(plugin);
    }
    
    private void performBackup() {
        try {
            File dataFolder = plugin.getDataFolder();
            File backupFolder = new File(dataFolder, "backups");
            
            if (!backupFolder.exists()) {
                backupFolder.mkdirs();
            }
            
            // Crea cartella backup con timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            File currentBackup = new File(backupFolder, "backup_" + timestamp);
            currentBackup.mkdirs();
            
            // Copia tutti i file di configurazione
            int copiedFiles = 0;
            for (String fileName : CONFIG_FILES) {
                File sourceFile = new File(dataFolder, fileName);
                if (sourceFile.exists()) {
                    File destFile = new File(currentBackup, fileName);
                    try {
                        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        copiedFiles++;
                    } catch (IOException e) {
                        plugin.getLogger().warning("Errore nel backup del file " + fileName + ": " + e.getMessage());
                    }
                }
            }
            
            if (copiedFiles > 0) {
                plugin.getLogger().info("§a[Backup] Backup completato! " + copiedFiles + " file salvati in: " + currentBackup.getName());
                
                // Rimuovi backup vecchi se necessario
                cleanOldBackups(backupFolder);
            } else {
                // Se non ci sono file da fare il backup, rimuovi la cartella vuota
                currentBackup.delete();
            }
            
        } catch (Exception e) {
            plugin.getLogger().severe("§c[Backup] Errore durante il backup: " + e.getMessage());
        }
    }
    
    private void cleanOldBackups(File backupFolder) {
        File[] backups = backupFolder.listFiles(File::isDirectory);
        if (backups == null) return;
        
        int maxBackups = configManager.getMaxBackups();
        if (backups.length > maxBackups) {
            // Ordina per data di modificazione (più vecchi prima)
            Arrays.sort(backups, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
            
            // Rimuovi i backup più vecchi
            int toRemove = backups.length - maxBackups;
            for (int i = 0; i < toRemove; i++) {
                File oldBackup = backups[i];
                if (deleteDirectory(oldBackup)) {
                    plugin.getLogger().info("§7[Backup] Rimosso backup vecchio: " + oldBackup.getName());
                }
            }
        }
    }
    
    private boolean deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return directory.delete();
    }
    
    public void autoSave() {
        if (!configManager.isAutoSaveEnabled()) {
            return;
        }
        
        new BukkitRunnable() {
            @Override
            public void run() {
                // Salva tutte le configurazioni
                plugin.saveConfig();
                
                // Salva altri file se necessario
                if (configManager.isDebugEnabled()) {
                    plugin.getLogger().info("§7[AutoSave] Configurazioni salvate automaticamente");
                }
            }
        }.runTaskAsynchronously(plugin);
    }
    
    private void startBackupTask() {
        int intervalTicks = configManager.getBackupInterval() * 20; // Converti secondi in ticks
        
        backupTask = new BukkitRunnable() {
            @Override
            public void run() {
                createBackup();
            }
        }.runTaskTimerAsynchronously(plugin, intervalTicks, intervalTicks);
        
        plugin.getLogger().info("§a[Backup] Sistema di backup automatico avviato ogni " + 
                               (configManager.getBackupInterval() / 60) + " minuti");
    }
    
    private void startAutoSaveTask() {
        int intervalTicks = configManager.getSaveInterval() * 20; // Converti secondi in ticks
        
        autoSaveTask = new BukkitRunnable() {
            @Override
            public void run() {
                autoSave();
            }
        }.runTaskTimerAsynchronously(plugin, intervalTicks, intervalTicks);
        
        plugin.getLogger().info("§a[AutoSave] Sistema di salvataggio automatico avviato ogni " + 
                               (configManager.getSaveInterval() / 60) + " minuti");
    }
    
    public void shutdown() {
        if (backupTask != null) {
            backupTask.cancel();
        }
        
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }
        
        // Esegui un ultimo backup prima della chiusura
        if (configManager.isBackupEnabled()) {
            plugin.getLogger().info("§e[Backup] Eseguendo backup finale...");
            performBackup();
        }
        
        // Esegui un ultimo salvataggio
        if (configManager.isAutoSaveEnabled()) {
            plugin.saveConfig();
        }
    }
    
    public BackupInfo getBackupInfo() {
        File backupFolder = new File(plugin.getDataFolder(), "backups");
        File[] backups = backupFolder.exists() ? backupFolder.listFiles(File::isDirectory) : new File[0];
        
        return new BackupInfo(
            backups != null ? backups.length : 0,
            configManager.getMaxBackups(),
            configManager.getBackupInterval(),
            configManager.isBackupEnabled()
        );
    }
    
    // Classe per informazioni sui backup
    public static class BackupInfo {
        private final int currentBackups;
        private final int maxBackups;
        private final int intervalSeconds;
        private final boolean enabled;
        
        public BackupInfo(int currentBackups, int maxBackups, int intervalSeconds, boolean enabled) {
            this.currentBackups = currentBackups;
            this.maxBackups = maxBackups;
            this.intervalSeconds = intervalSeconds;
            this.enabled = enabled;
        }
        
        public int getCurrentBackups() { return currentBackups; }
        public int getMaxBackups() { return maxBackups; }
        public int getIntervalSeconds() { return intervalSeconds; }
        public boolean isEnabled() { return enabled; }
        public int getIntervalMinutes() { return intervalSeconds / 60; }
    }
} 