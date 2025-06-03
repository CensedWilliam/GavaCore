package library;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MoonRotationEffect implements Listener {
    private final Main plugin;
    private final Set<UUID> sleepingPlayers = new HashSet<>();
    private final Map<UUID, Location> bedLocations = new HashMap<>();
    private final Map<UUID, Long> lastMessageTime = new HashMap<>();
    private boolean isAnimationRunning = false;
    
    // Parametri di configurazione
    private final int ANIMATION_DURATION = 200; // Durata totale in tick (10 secondi)
    private final int ANIMATION_STEPS = 100;    // Numero di passi dell'animazione
    private final float MOON_ROTATION_DEGREE = 180.0f; // Gradi di rotazione totali
    private final long MESSAGE_COOLDOWN = 5000; // 5 secondi di cooldown tra messaggi
    
    public MoonRotationEffect(Main plugin) {
        this.plugin = plugin;
    }
    
    // Intercetta l'evento con priorità LOWEST per poterlo gestire prima di Main
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBedEnterEarly(PlayerBedEnterEvent event) {
        // Se l'animazione è in corso e stiamo tentando di rimettere un giocatore a letto
        // cancelliamo l'evento per evitare che Main.onPlayerBedEnter venga chiamato
        if (isAnimationRunning && (sleepingPlayers.contains(event.getPlayer().getUniqueId()) || 
            isReentrySleep(event.getPlayer().getUniqueId()))) {
            // Annulla l'evento per evitare che Main.onPlayerBedEnter lo gestisca e causi NullPointerException
            event.setCancelled(true);
        }
    }
    
    // Flag per tenere traccia dei giocatori che stiamo per rimettere a letto
    private final Set<UUID> playersBeingReentered = new HashSet<>();
    
    private boolean isReentrySleep(UUID playerUUID) {
        return playersBeingReentered.contains(playerUUID);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        // Se l'evento è cancellato o non è un evento di sonno notturno, ignoriamo
        if (event.isCancelled() || !event.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK)) {
            return;
        }
        
        Player player = event.getPlayer();
        World world = player.getWorld();
        
        // Aggiungiamo il giocatore alla lista dei giocatori che dormono
        sleepingPlayers.add(player.getUniqueId());
        
        // Salviamo la posizione del letto
        bedLocations.put(player.getUniqueId(), event.getBed().getLocation());
        
        // Controlliamo se è notte e se ci sono abbastanza giocatori che dormono
        if (world.getTime() >= 12500 && world.getTime() <= 23500) {
            checkSleepingPlayers(world);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        // Se l'animazione è in corso e il giocatore sta dormendo, proviamo a rimetterlo a letto
        if (isAnimationRunning && sleepingPlayers.contains(playerUUID)) {
            // Non cancelliamo l'evento perché Minecraft potrebbe forzare l'uscita dal letto
            
            // Riporta il giocatore a dormire se necessario (in un tick successivo per evitare problemi)
            final Location bedLoc = bedLocations.get(playerUUID);
            if (bedLoc != null) {
                // Segna questo giocatore come "in fase di rientro a letto"
                playersBeingReentered.add(playerUUID);
                
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline() && isAnimationRunning) {
                        try {
                            player.teleport(bedLoc);
                            player.sleep(bedLoc, true);
                        } catch (Exception e) {
                            // Gestione eccezione in caso di problemi con il letto
                            plugin.getLogger().warning("Impossibile rimettere a letto il giocatore: " + e.getMessage());
                        } finally {
                            // Rimuovi il flag di rientro
                            playersBeingReentered.remove(playerUUID);
                        }
                    } else {
                        // Rimuovi il flag di rientro se non è più necessario
                        playersBeingReentered.remove(playerUUID);
                    }
                }, 5L); // Attendi 5 tick prima di provare a rimettere il giocatore a letto
            }
        } else {
            // Rimuoviamo il giocatore dalla lista quando lascia il letto
            sleepingPlayers.remove(playerUUID);
            bedLocations.remove(playerUUID);
            lastMessageTime.remove(playerUUID);
        }
    }
    
    private void checkSleepingPlayers(World world) {
        // Calcoliamo quanti giocatori sono necessari per attivare l'effetto
        int playersInWorld = world.getPlayers().size();
        int requiredPlayers = Math.max(1, (int) Math.ceil(playersInWorld * 0.5)); // 50% dei giocatori
        
        // Se ci sono abbastanza giocatori che dormono e l'animazione non è già in corso
        if (sleepingPlayers.size() >= requiredPlayers && !isAnimationRunning) {
            startMoonRotationAnimation(world);
        }
    }
    
    private void startMoonRotationAnimation(World world) {
        isAnimationRunning = true;
        
        // Riduciamo la durata dell'animazione per evitare che Minecraft forzi l'uscita dal letto
        final int SHORTER_DURATION = 100; // Dimezziamo la durata a 5 secondi
        
        // Salviamo il tempo attuale del mondo
        final long initialTime = world.getTime();
        
        // Avviamo l'animazione
        new BukkitRunnable() {
            int step = 0;
            final long targetTime = 24000; // Obiettivo: inizio giorno
            
            @Override
            public void run() {
                step++;
                
                if (step <= ANIMATION_STEPS) {
                    // Calcoliamo il tempo corrente in base allo step dell'animazione
                    double progress = (double) step / ANIMATION_STEPS;
                    long newTime = initialTime + (long) ((targetTime - initialTime) * progress);
                    
                    // Assicuriamoci che il tempo sia entro i limiti (0-24000)
                    while (newTime >= 24000) newTime -= 24000;
                    
                    // Impostiamo il nuovo tempo del mondo
                    world.setTime(newTime);
                    
                    // Effetto sonoro per i giocatori (meno frequente)
                    if (step % 20 == 0) {
                        for (Player player : world.getPlayers()) {
                            player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 0.3f, 0.7f + (float)progress * 0.5f);
                        }
                    }
                    
                    // Assicuriamoci che i giocatori che dovrebbero dormire siano ancora a letto
                    // ma lo facciamo meno frequentemente per evitare lo spam
                    if (step % 5 == 0) {
                        for (UUID playerId : sleepingPlayers) {
                            Player player = Bukkit.getPlayer(playerId);
                            Location bedLoc = bedLocations.get(playerId);
                            if (player != null && player.isOnline() && !player.isSleeping() && bedLoc != null) {
                                try {
                                    // Segna questo giocatore come "in fase di rientro a letto"
                                    playersBeingReentered.add(playerId);
                                    
                                    player.teleport(bedLoc);
                                    player.sleep(bedLoc, true);
                                    
                                    // Rimuovi il flag dopo un breve ritardo
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                        playersBeingReentered.remove(playerId);
                                    }, 10L);
                                } catch (Exception e) {
                                    // Gestiamo silenziosamente eventuali errori
                                    playersBeingReentered.remove(playerId);
                                }
                            }
                        }
                    }
                } else {
                    // Animazione completata
                    world.setTime(0); // Impostiamo l'inizio del giorno
                    world.setStorm(false); // Rimuoviamo eventuale pioggia
                    world.setThundering(false); // Rimuoviamo eventuale tempesta
                    
                    // Ora permettiamo ai giocatori di alzarsi
                    isAnimationRunning = false;
                    
                    // Reset delle variabili
                    sleepingPlayers.clear();
                    bedLocations.clear();
                    lastMessageTime.clear();
                    playersBeingReentered.clear();
                    
                    // Fermiamo questo task
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, SHORTER_DURATION / ANIMATION_STEPS);
    }
    
    // Metodo per interrompere forzatamente l'animazione (utile per comandi amministrativi)
    public void stopAnimation(World world) {
        if (isAnimationRunning) {
            world.setTime(0); // Impostiamo l'inizio del giorno
            isAnimationRunning = false;
            
            // Puliamo la lista dei giocatori che dormono
            sleepingPlayers.clear();
            bedLocations.clear();
            lastMessageTime.clear();
            playersBeingReentered.clear();
        }
    }
} 