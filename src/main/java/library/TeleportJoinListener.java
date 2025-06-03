package library;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener per gestire gli eventi di connessione e disconnessione 
 * relativi all'animazione di teletrasporto
 */
public class TeleportJoinListener implements Listener {
    
    private final TeleportAnimation teleportAnimation;
    private final Main plugin;
    
    public TeleportJoinListener(TeleportAnimation teleportAnimation, Main plugin) {
        this.teleportAnimation = teleportAnimation;
        this.plugin = plugin;
    }
    
    /**
     * Gestisce l'evento di connessione del giocatore
     * Verifica se il giocatore aveva un teletrasporto pendente e lo completa
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Esegui un ritardo per assicurarsi che il giocatore sia completamente caricato
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                teleportAnimation.checkAndCompletePendingTeleport(player);
            }
        }, 10L); // Attendi 10 tick (mezzo secondo)
    }
    
    /**
     * Gestisce l'evento di disconnessione del giocatore
     * Salva il teletrasporto pendente se il giocatore si disconnette durante un'animazione
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        teleportAnimation.savePendingTeleport(player);
    }
} 
