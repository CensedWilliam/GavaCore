package library;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {
    
    private final Main plugin;
    private final ConfigManager configManager;
    
    // Anti-spam data
    private final Map<UUID, Long> lastMessageTime = new ConcurrentHashMap<>();
    private final Map<UUID, String> lastMessage = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> repeatedCount = new ConcurrentHashMap<>();
    private final Map<UUID, Long> mutedUntil = new ConcurrentHashMap<>();
    
    // Tasks per la pulizia automatica
    private BukkitTask cleanupTask;
    
    public ChatManager(Main plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        startCleanupTask();
    }
    
    public boolean canSendMessage(Player player, String message) {
        if (!configManager.isChatAntiSpamEnabled()) {
            return true;
        }
        
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        
        // Controllo se il giocatore è mutato
        if (mutedUntil.containsKey(playerId)) {
            long muteEnd = mutedUntil.get(playerId);
            if (currentTime < muteEnd) {
                long remainingSeconds = (muteEnd - currentTime) / 1000;
                player.sendMessage(configManager.formatMessage(
                    "%warning%Sei stato mutato per spam! Tempo rimanente: %highlight%" + remainingSeconds + " %warning%secondi"));
                return false;
            } else {
                mutedUntil.remove(playerId);
                repeatedCount.remove(playerId);
            }
        }
        
        // Controllo delay tra messaggi
        if (lastMessageTime.containsKey(playerId)) {
            long timeDiff = (currentTime - lastMessageTime.get(playerId)) / 1000;
            if (timeDiff < configManager.getChatMessageDelay()) {
                player.sendMessage(configManager.formatMessage(
                    "%warning%Stai scrivendo troppo velocemente! Aspetta %highlight%" + 
                    (configManager.getChatMessageDelay() - timeDiff) + " %warning%secondi"));
                return false;
            }
        }
        
        // Controllo messaggi ripetuti
        if (lastMessage.containsKey(playerId) && lastMessage.get(playerId).equals(message)) {
            int count = repeatedCount.getOrDefault(playerId, 0) + 1;
            repeatedCount.put(playerId, count);
            
            if (count >= configManager.getChatMaxRepeated()) {
                // Muta il giocatore
                long muteEnd = currentTime + (configManager.getChatTimeout() * 1000L);
                mutedUntil.put(playerId, muteEnd);
                
                player.sendMessage(configManager.formatMessage(
                    "%error%Sei stato mutato per %highlight%" + configManager.getChatTimeout() + 
                    " %error%secondi per aver ripetuto lo stesso messaggio!"));
                
                // Notifica gli admin se online
                notifyAdmins(player.getName() + " è stato mutato automaticamente per spam");
                
                return false;
            } else {
                player.sendMessage(configManager.formatMessage(
                    "%warning%Non ripetere lo stesso messaggio! (%highlight%" + count + 
                    "%warning%/" + configManager.getChatMaxRepeated() + ")"));
            }
        } else {
            repeatedCount.remove(playerId);
        }
        
        // Aggiorna dati per tracking
        lastMessageTime.put(playerId, currentTime);
        lastMessage.put(playerId, message);
        
        return true;
    }
    
    public String processMessage(Player player, String message) {
        if (!configManager.isChatModerationEnabled()) {
            return message;
        }
        
        String processedMessage = message;
        
        // Filtro parole vietate
        List<String> blockedWords = configManager.getBlockedWords();
        for (String word : blockedWords) {
            if (processedMessage.toLowerCase().contains(word.toLowerCase())) {
                // Crea asterischi per sostituire la parola
                String replacement = "";
                for (int i = 0; i < word.length(); i++) {
                    replacement += "*";
                }
                processedMessage = processedMessage.replaceAll("(?i)" + word, replacement);
                
                player.sendMessage(configManager.formatMessage(
                    "%warning%Il tuo messaggio conteneva parole non appropriate e è stato modificato"));
            }
        }
        
        // Filtro maiuscole eccessive
        if (configManager.shouldFilterCaps()) {
            if (getCapitalPercentage(processedMessage) > configManager.getMaxCapsPercentage()) {
                processedMessage = processedMessage.toLowerCase();
                
                // Capitalizza la prima lettera
                if (!processedMessage.isEmpty()) {
                    processedMessage = Character.toUpperCase(processedMessage.charAt(0)) + 
                                     processedMessage.substring(1);
                }
                
                player.sendMessage(configManager.formatMessage(
                    "%warning%Il tuo messaggio aveva troppe maiuscole ed è stato modificato"));
            }
        }
        
        return processedMessage;
    }
    
    private double getCapitalPercentage(String message) {
        if (message.isEmpty()) return 0;
        
        long letters = message.chars().filter(Character::isLetter).count();
        if (letters == 0) return 0;
        
        long capitals = message.chars().filter(Character::isUpperCase).count();
        return (double) capitals / letters * 100;
    }
    
    public void unmutePlayer(UUID playerId) {
        mutedUntil.remove(playerId);
        repeatedCount.remove(playerId);
    }
    
    public boolean isPlayerMuted(UUID playerId) {
        if (!mutedUntil.containsKey(playerId)) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        long muteEnd = mutedUntil.get(playerId);
        
        if (currentTime >= muteEnd) {
            mutedUntil.remove(playerId);
            return false;
        }
        
        return true;
    }
    
    public long getMuteTimeRemaining(UUID playerId) {
        if (!mutedUntil.containsKey(playerId)) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long muteEnd = mutedUntil.get(playerId);
        
        return Math.max(0, (muteEnd - currentTime) / 1000);
    }
    
    private void notifyAdmins(String message) {
        plugin.getServer().getOnlinePlayers().stream()
            .filter(player -> player.hasPermission("gavacore.admin"))
            .forEach(admin -> admin.sendMessage(configManager.formatMessage(
                "%info%[Chat Auto-Moderazione] " + message)));
    }
    
    private void startCleanupTask() {
        // Pulisci i dati ogni 5 minuti
        cleanupTask = new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                
                // Rimuovi mute scaduti
                mutedUntil.entrySet().removeIf(entry -> currentTime >= entry.getValue());
                
                // Rimuovi dati vecchi (più di 10 minuti)
                long cutoffTime = currentTime - (10 * 60 * 1000);
                lastMessageTime.entrySet().removeIf(entry -> entry.getValue() < cutoffTime);
                
                // Rimuovi messaggi vecchi
                lastMessage.entrySet().removeIf(entry -> 
                    !lastMessageTime.containsKey(entry.getKey()));
                
                // Rimuovi contatori per giocatori non più tracciati
                repeatedCount.entrySet().removeIf(entry -> 
                    !lastMessage.containsKey(entry.getKey()));
                
                if (configManager.isDebugEnabled()) {
                    plugin.getLogger().info("[ChatManager] Pulizia automatica completata");
                }
            }
        }.runTaskTimerAsynchronously(plugin, 6000L, 6000L); // 5 minuti
    }
    
    public void shutdown() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
        }
        
        lastMessageTime.clear();
        lastMessage.clear();
        repeatedCount.clear();
        mutedUntil.clear();
    }
    
    // Statistiche chat
    public ChatStats getStats() {
        return new ChatStats(
            mutedUntil.size(),
            lastMessageTime.size(),
            configManager.isChatAntiSpamEnabled(),
            configManager.isChatModerationEnabled()
        );
    }
    
    public static class ChatStats {
        private final int mutedPlayers;
        private final int trackedPlayers;
        private final boolean antiSpamEnabled;
        private final boolean moderationEnabled;
        
        public ChatStats(int mutedPlayers, int trackedPlayers, boolean antiSpamEnabled, boolean moderationEnabled) {
            this.mutedPlayers = mutedPlayers;
            this.trackedPlayers = trackedPlayers;
            this.antiSpamEnabled = antiSpamEnabled;
            this.moderationEnabled = moderationEnabled;
        }
        
        public int getMutedPlayers() { return mutedPlayers; }
        public int getTrackedPlayers() { return trackedPlayers; }
        public boolean isAntiSpamEnabled() { return antiSpamEnabled; }
        public boolean isModerationEnabled() { return moderationEnabled; }
    }
} 