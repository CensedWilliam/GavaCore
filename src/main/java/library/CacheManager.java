package library;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CacheManager {
    
    private final Main plugin;
    private final ConfigManager configManager;
    private final Map<String, CacheEntry> cache;
    private BukkitTask cleanupTask;
    
    public CacheManager(Main plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.cache = new ConcurrentHashMap<>();
        startCleanupTask();
    }
    
    public void put(String key, Object value) {
        if (!configManager.isCacheEnabled()) {
            return;
        }
        
        // Controllo dimensione cache
        if (cache.size() >= configManager.getCacheSize()) {
            cleanOldestEntries();
        }
        
        long expireTime = System.currentTimeMillis() + (configManager.getCacheExpireTime() * 1000L);
        cache.put(key, new CacheEntry(value, expireTime));
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        if (!configManager.isCacheEnabled()) {
            return null;
        }
        
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        
        try {
            return type.cast(entry.getValue());
        } catch (ClassCastException e) {
            cache.remove(key);
            return null;
        }
    }
    
    public boolean contains(String key) {
        if (!configManager.isCacheEnabled()) {
            return false;
        }
        
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return false;
        }
        return true;
    }
    
    public void remove(String key) {
        cache.remove(key);
    }
    
    public void clear() {
        cache.clear();
    }
    
    public int size() {
        return cache.size();
    }
    
    public void invalidateExpired() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    private void cleanOldestEntries() {
        // Rimuovi il 25% delle entry più vecchie
        int toRemove = Math.max(1, cache.size() / 4);
        
        cache.entrySet().stream()
            .sorted((e1, e2) -> Long.compare(e1.getValue().getCreationTime(), e2.getValue().getCreationTime()))
            .limit(toRemove)
            .map(Map.Entry::getKey)
            .forEach(cache::remove);
    }
    
    private void startCleanupTask() {
        // Task di pulizia automatica ogni 60 secondi
        cleanupTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (configManager.isCacheEnabled()) {
                    invalidateExpired();
                    
                    // Log debug se abilitato
                    if (configManager.isDebugEnabled()) {
                        plugin.getLogger().info("[Cache] Pulizia automatica completata. Elementi in cache: " + cache.size());
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 1200L, 1200L); // 60 secondi
    }
    
    public void shutdown() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
        }
        clear();
    }
    
    // Metodi per statistiche cache
    public CacheStats getStats() {
        return new CacheStats(
            cache.size(),
            configManager.getCacheSize(),
            configManager.getCacheExpireTime()
        );
    }
    
    // Classe interna per le entry della cache
    private static class CacheEntry {
        private final Object value;
        private final long expireTime;
        private final long creationTime;
        
        public CacheEntry(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
            this.creationTime = System.currentTimeMillis();
        }
        
        public Object getValue() {
            return value;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
        
        public long getCreationTime() {
            return creationTime;
        }
    }
    
    // Classe per le statistiche della cache
    public static class CacheStats {
        private final int currentSize;
        private final int maxSize;
        private final int expireTimeSeconds;
        
        public CacheStats(int currentSize, int maxSize, int expireTimeSeconds) {
            this.currentSize = currentSize;
            this.maxSize = maxSize;
            this.expireTimeSeconds = expireTimeSeconds;
        }
        
        public int getCurrentSize() { return currentSize; }
        public int getMaxSize() { return maxSize; }
        public int getExpireTimeSeconds() { return expireTimeSeconds; }
        public double getUsagePercentage() { return (double) currentSize / maxSize * 100; }
    }
} 
