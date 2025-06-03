package library;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportAnimation implements Listener {
    private final Main plugin;
    private final Map<UUID, AnimationData> activeAnimations = new HashMap<>();
    private final File pendingTeleportsFile;
    private FileConfiguration pendingTeleports;
    
    // Parametri dell'animazione
    private final int ANIMATION_HEIGHT = 80; // Altezza a cui sale la camera
    private final int ANIMATION_DURATION = 60; // Durata totale in ticks (3 secondi)
    private final int RISE_DURATION = 20; // Ticks per la fase di salita
    private final int MOVE_DURATION = 20; // Ticks per la fase di movimento orizzontale
    private final int DESCEND_DURATION = 20; // Ticks per la fase di discesa
    
    // Classe per memorizzare i dati dell'animazione
    private class AnimationData {
        final Location startLocation;
        final Location targetLocation;
        final ArmorStand camera;
        final GameMode originalGameMode;
        final boolean wasFlying;
        
        public AnimationData(Location startLocation, Location targetLocation, ArmorStand camera, 
                            GameMode originalGameMode, boolean wasFlying) {
            this.startLocation = startLocation;
            this.targetLocation = targetLocation;
            this.camera = camera;
            this.originalGameMode = originalGameMode;
            this.wasFlying = wasFlying;
        }
    }
    
    public TeleportAnimation(Main plugin) {
        this.plugin = plugin;
        
        // Inizializza il file per i teletrasporti pendenti
        this.pendingTeleportsFile = new File(plugin.getDataFolder(), "pending_teleports.yml");
        loadPendingTeleports();
        
        // Registra l'evento di join per gestire i teletrasporti pendenti
        Bukkit.getPluginManager().registerEvents(new TeleportJoinListener(this, plugin), plugin);
        
        // Registra questo listener per gestire l'evento di sneak
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Carica i teletrasporti pendenti dal file
     */
    private void loadPendingTeleports() {
        if (!pendingTeleportsFile.exists()) {
            try {
                pendingTeleportsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossibile creare il file pending_teleports.yml: " + e.getMessage());
            }
        }
        
        pendingTeleports = YamlConfiguration.loadConfiguration(pendingTeleportsFile);
    }
    
    /**
     * Salva i teletrasporti pendenti nel file
     */
    private void savePendingTeleports() {
        try {
            pendingTeleports.save(pendingTeleportsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossibile salvare pending_teleports.yml: " + e.getMessage());
        }
    }
    
    /**
     * Salva un teletrasporto pendente quando un giocatore si disconnette durante un'animazione
     * @param player Il giocatore che si è disconnesso
     */
    public void savePendingTeleport(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        if (!activeAnimations.containsKey(playerUUID)) {
            return; // Il giocatore non è in un'animazione
        }
        
        AnimationData data = activeAnimations.get(playerUUID);
        Location target = data.targetLocation;
        
        // Salva le informazioni sulla destinazione
        pendingTeleports.set(playerUUID.toString() + ".world", target.getWorld().getName());
        pendingTeleports.set(playerUUID.toString() + ".x", target.getX());
        pendingTeleports.set(playerUUID.toString() + ".y", target.getY());
        pendingTeleports.set(playerUUID.toString() + ".z", target.getZ());
        pendingTeleports.set(playerUUID.toString() + ".yaw", target.getYaw());
        pendingTeleports.set(playerUUID.toString() + ".pitch", target.getPitch());
        pendingTeleports.set(playerUUID.toString() + ".gamemode", data.originalGameMode.name());
        
        savePendingTeleports();
        
        // Pulisci l'animazione
        if (data.camera != null && !data.camera.isDead()) {
            data.camera.remove();
        }
        
        activeAnimations.remove(playerUUID);
    }
    
    /**
     * Controlla se un giocatore che si connette ha un teletrasporto pendente
     * @param player Il giocatore che si è connesso
     * @return true se il giocatore aveva un teletrasporto pendente
     */
    public boolean checkAndCompletePendingTeleport(Player player) {
        UUID playerUUID = player.getUniqueId();
        String path = playerUUID.toString();
        
        if (!pendingTeleports.contains(path)) {
            return false; // Nessun teletrasporto pendente
        }
        
        // Recupera le informazioni sulla destinazione
        String worldName = pendingTeleports.getString(path + ".world");
        World world = Bukkit.getWorld(worldName);
        
        if (world == null) {
            plugin.getLogger().warning("Mondo non trovato per teletrasporto pendente: " + worldName);
            pendingTeleports.set(path, null);
            savePendingTeleports();
            return false;
        }
        
        double x = pendingTeleports.getDouble(path + ".x");
        double y = pendingTeleports.getDouble(path + ".y");
        double z = pendingTeleports.getDouble(path + ".z");
        float yaw = (float) pendingTeleports.getDouble(path + ".yaw");
        float pitch = (float) pendingTeleports.getDouble(path + ".pitch");
        
        Location destination = new Location(world, x, y, z, yaw, pitch);
        
        // Controlla che la GameMode sia valida
        GameMode gameMode = GameMode.SURVIVAL; // Default a sopravvivenza
        try {
            String gameModeStr = pendingTeleports.getString(path + ".gamemode", "SURVIVAL");
            gameMode = GameMode.valueOf(gameModeStr);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("GameMode non valida per teletrasporto pendente: " + e.getMessage());
        }
        
        // Esegui il teletrasporto
        player.teleport(destination);
        player.setGameMode(gameMode);
        
        // Rimuovi il teletrasporto pendente
        pendingTeleports.set(path, null);
        savePendingTeleports();
        
        // Mostra un effetto minimo
        player.getWorld().spawnParticle(
            Particle.PORTAL,
            destination.clone().add(0, 1, 0),
            30, 0.5, 0.5, 0.5, 0.1,
            null, true
        );
        player.playSound(destination, Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 1.0f);
        
        // Informa il giocatore
        player.sendMessage("§6§l[GavaCore] §aIl tuo teletrasporto è stato completato dopo la riconnessione.");
        
        return true;
    }
    
    /**
     * Avvia un'animazione di teletrasporto stile GTA per un giocatore
     * @param player Il giocatore da teletrasportare
     * @param targetLocation La posizione di destinazione
     */
    public void startTeleportAnimation(Player player, Location targetLocation) {
        // Se il giocatore è già in un'animazione, interrompila
        if (activeAnimations.containsKey(player.getUniqueId())) {
            cancelAnimation(player);
        }
        
        // Salva lo stato attuale del giocatore
        GameMode originalGameMode = player.getGameMode();
        boolean wasFlying = player.isFlying();
        Location startLocation = player.getLocation().clone();
        
        // Rimuovi effetti che potrebbero interferire con l'animazione
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        
        // Impedisci al giocatore di accovacciarsi durante l'animazione
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, ANIMATION_DURATION, 128, false, false, false));
        player.setSneaking(false);
        
        // Crea un ArmorStand invisibile per la visuale della camera
        World world = player.getWorld();
        Location cameraStartLoc = startLocation.clone().add(0, 2, 0);
        ArmorStand camera = (ArmorStand) world.spawnEntity(cameraStartLoc, EntityType.ARMOR_STAND);
        
        // Configura l'ArmorStand
        camera.setVisible(false);
        camera.setGravity(false);
        camera.setInvulnerable(true);
        camera.setSmall(true);
        camera.setMarker(true);
        camera.setCustomName("TeleportCamera_" + player.getName());
        camera.setCustomNameVisible(false);
        
        // Salva i dati dell'animazione
        AnimationData animData = new AnimationData(
            startLocation, targetLocation.clone(), camera, originalGameMode, wasFlying
        );
        activeAnimations.put(player.getUniqueId(), animData);
        
        // Imposta il giocatore in modalità spettatore
        player.setGameMode(GameMode.SPECTATOR);
        
        // Effetto sonoro iniziale
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        
        // Breve pausa prima di collegare il giocatore all'ArmorStand
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline() || !activeAnimations.containsKey(player.getUniqueId())) return;
            
            // Collega la visuale all'ArmorStand
            player.setSpectatorTarget(camera);
            
            // Inizia l'animazione
            new BukkitRunnable() {
                int tick = 0;
                
                @Override
                public void run() {
                    // Se il player non è più online o l'animazione è stata annullata
                    if (!player.isOnline() || !activeAnimations.containsKey(player.getUniqueId())) {
                        this.cancel();
                        return;
                    }
                    
                    // Controllo aggiuntivo per impedire lo sneak in ogni tick
                    if (player.isSneaking()) {
                        player.setSneaking(false);
                    }
                    
                    tick++;
                    
                    // Fase 1: Salita
                    if (tick <= RISE_DURATION) {
                        double progress = (double) tick / RISE_DURATION;
                        double height = ANIMATION_HEIGHT * progress;
                        
                        Location newLoc = startLocation.clone().add(0, height, 0);
                        newLoc.setDirection(new Vector(0, -1, 0)); // Guarda verso il basso
                        
                        camera.teleport(newLoc);
                        
                        // Effetto particellare di salita
                        if (tick % 2 == 0) {
                            player.getWorld().spawnParticle(
                                Particle.CLOUD,
                                startLocation.getX(), startLocation.getY() + height / 2, startLocation.getZ(),
                                10, 0.5, height / 2, 0.5, 0.05,
                                null, true
                            );
                        }
                    }
                    // Fase 2: Movimento orizzontale verso la destinazione
                    else if (tick <= RISE_DURATION + MOVE_DURATION) {
                        double progress = (double) (tick - RISE_DURATION) / MOVE_DURATION;
                        double x = startLocation.getX() + (targetLocation.getX() - startLocation.getX()) * progress;
                        double z = startLocation.getZ() + (targetLocation.getZ() - startLocation.getZ()) * progress;
                        
                        Location newLoc = new Location(
                            startLocation.getWorld(), x, startLocation.getY() + ANIMATION_HEIGHT, z
                        );
                        
                        // Guarda verso la direzione del movimento
                        Vector moveDir = targetLocation.toVector().subtract(startLocation.toVector()).normalize();
                        moveDir.setY(-0.5); // Guarda leggermente verso il basso
                        newLoc.setDirection(moveDir);
                        
                        camera.teleport(newLoc);
                        
                        // Effetto particellare di movimento
                        if (tick % 2 == 0) {
                            player.getWorld().spawnParticle(
                                Particle.END_ROD,
                                x, startLocation.getY() + ANIMATION_HEIGHT - 5, z,
                                3, 0.2, 0.2, 0.2, 0.05,
                                null, true
                            );
                        }
                    }
                    // Fase 3: Discesa verso la destinazione finale
                    else if (tick <= ANIMATION_DURATION) {
                        double progress = (double) (tick - RISE_DURATION - MOVE_DURATION) / DESCEND_DURATION;
                        double height = ANIMATION_HEIGHT * (1 - progress);
                        
                        Location newLoc = targetLocation.clone().add(0, height, 0);
                        newLoc.setDirection(new Vector(0, -1, 0)); // Guarda verso il basso
                        
                        camera.teleport(newLoc);
                        
                        // Effetto particellare di discesa
                        if (tick % 2 == 0) {
                            player.getWorld().spawnParticle(
                                Particle.CLOUD,
                                targetLocation.getX(), targetLocation.getY() + height / 2, targetLocation.getZ(),
                                10, 0.5, height / 2, 0.5, 0.05,
                                null, true
                            );
                        }
                    }
                    // Fine dell'animazione
                    else {
                        finishAnimation(player);
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 1L, 1L);
        }, 5L);
    }
    
    /**
     * Termina l'animazione e teletrasporta il giocatore alla destinazione
     */
    private void finishAnimation(Player player) {
        if (!activeAnimations.containsKey(player.getUniqueId())) return;
        
        AnimationData data = activeAnimations.get(player.getUniqueId());
        Location targetLocation = data.targetLocation;
        ArmorStand camera = data.camera;
        GameMode originalGameMode = data.originalGameMode;
        boolean wasFlying = data.wasFlying;
        
        // Rimuovi lo spettatore dall'ArmorStand
        player.setSpectatorTarget(null);
        
        // Rimuovi l'ArmorStand
        camera.remove();
        
        // Ripristina lo stato del giocatore
        player.teleport(targetLocation);
        player.setGameMode(originalGameMode);
        
        // Ripristina lo stato di volo se necessario
        if (originalGameMode == GameMode.CREATIVE || originalGameMode == GameMode.SPECTATOR) {
            player.setAllowFlight(true);
            player.setFlying(wasFlying);
        }
        
        // Effetto finale
        player.getWorld().spawnParticle(
            Particle.PORTAL,
            targetLocation.clone().add(0, 1, 0),
            50, 0.5, 0.5, 0.5, 0.1,
            null, true
        );
        
        // Effetto sonoro finale
        player.playSound(targetLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        
        // Rimuovi dall'elenco delle animazioni attive
        activeAnimations.remove(player.getUniqueId());
    }
    
    /**
     * Cancella un'animazione in corso
     */
    public void cancelAnimation(Player player) {
        if (!activeAnimations.containsKey(player.getUniqueId())) return;
        
        AnimationData data = activeAnimations.get(player.getUniqueId());
        
        // Ripristina lo stato del giocatore
        player.setSpectatorTarget(null);
        player.setGameMode(data.originalGameMode);
        player.teleport(data.targetLocation); // Teletrasporta direttamente alla destinazione
        
        // Rimuovi l'ArmorStand
        if (data.camera != null && !data.camera.isDead()) {
            data.camera.remove();
        }
        
        activeAnimations.remove(player.getUniqueId());
    }
    
    /**
     * Cancella tutte le animazioni in corso
     */
    public void cancelAllAnimations() {
        for (UUID playerId : activeAnimations.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                cancelAnimation(player);
            }
        }
        
        // Rimuovi tutte le entità create per le animazioni
        for (AnimationData data : activeAnimations.values()) {
            if (data.camera != null && !data.camera.isDead()) {
                data.camera.remove();
            }
        }
        
        activeAnimations.clear();
    }
    
    /**
     * Chiamato quando il server si spegne
     */
    public void cleanupOnServerShutdown() {
        // Salva teletrasporti pendenti per i giocatori in animazione
        for (UUID playerId : activeAnimations.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                // Salva la destinazione per completare il teletrasporto alla riconnessione
                AnimationData data = activeAnimations.get(playerId);
                Location target = data.targetLocation;
                
                pendingTeleports.set(playerId.toString() + ".world", target.getWorld().getName());
                pendingTeleports.set(playerId.toString() + ".x", target.getX());
                pendingTeleports.set(playerId.toString() + ".y", target.getY());
                pendingTeleports.set(playerId.toString() + ".z", target.getZ());
                pendingTeleports.set(playerId.toString() + ".yaw", target.getYaw());
                pendingTeleports.set(playerId.toString() + ".pitch", target.getPitch());
                pendingTeleports.set(playerId.toString() + ".gamemode", data.originalGameMode.name());
            }
        }
        
        // Salva i teletrasporti pendenti
        savePendingTeleports();
        
        // Rimuovi tutte le entità create per le animazioni
        for (AnimationData data : activeAnimations.values()) {
            if (data.camera != null && !data.camera.isDead()) {
                data.camera.remove();
            }
        }
        
        // Svuota la mappa
        activeAnimations.clear();
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (activeAnimations.containsKey(player.getUniqueId())) {
            // Annulla l'evento di sneak se il giocatore è in un'animazione
            event.setCancelled(true);
            player.sendMessage("§6§l[GavaCore] §cNon puoi accovacciarti durante il teletrasporto!");
            
            // Forza il giocatore a non accovacciarsi
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (player.isOnline() && player.isSneaking()) {
                    player.setSneaking(false);
                }
            });
        }
    }
    
    // Aggiungi un controllo anche sul movimento
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (activeAnimations.containsKey(player.getUniqueId())) {
            // Se il giocatore è in un'animazione e sta tentando di accovacciarsi
            if (player.isSneaking()) {
                // Forza il giocatore a non essere accovacciato
                player.setSneaking(false);
                
                // Cancella qualsiasi tentativo di accovacciamento
                Location to = event.getTo();
                if (to != null) {
                    to.setPitch(event.getFrom().getPitch());
                    to.setYaw(event.getFrom().getYaw());
                    event.setTo(to);
                }
                
                player.sendMessage("§6§l[GavaCore] §cNon puoi accovacciarti durante il teletrasporto!");
            }
        }
    }
    
    public void cleanup() {
        cleanupOnServerShutdown();
    }
} 