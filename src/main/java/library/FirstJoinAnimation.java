package library;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.block.Block;
import org.bukkit.Sound;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirstJoinAnimation implements Listener {
    private final Main plugin;
    private final File animationDataFile;
    private FileConfiguration animationData;
    private final Map<UUID, ArmorStand> activeAnimations = new HashMap<>();
    
    // Parametri dell'animazione
    private final int ANIMATION_HEIGHT = 100; // Altezza iniziale in blocchi sopra il giocatore
    private final int MAX_ANIMATION_DURATION = 400; // Durata massima in ticks (20 secondi)
    private final int PARTICLE_INTERVAL = 5; // Ogni quanti tick mostrare le particelle
    private final double DESCENT_SPEED = 0.5; // Velocità di discesa (blocchi per tick)
    private final double ROTATION_SPEED = 0.5; // Velocità di rotazione (gradi per tick)
    
    // Testo dell'animazione
    private final String MAIN_TITLE = "§6GavaCore";
    private final String SUBTITLE = "§eby GavaTech";
    private final int TEXT_ANIMATION_DELAY = 2; // Ticks tra ogni lettera
    
    public FirstJoinAnimation(Main plugin) {
        this.plugin = plugin;
        this.animationDataFile = new File(plugin.getDataFolder(), "animation_data.yml");
        loadAnimationData();
    }
    
    private void loadAnimationData() {
        if (!animationDataFile.exists()) {
            try {
                animationDataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossibile creare il file animation_data.yml: " + e.getMessage());
            }
        }
        
        animationData = YamlConfiguration.loadConfiguration(animationDataFile);
        
        // Assicuriamoci che la sezione players esista
        if (!animationData.contains("players")) {
            animationData.createSection("players");
            saveAnimationData();
        }
        
        // Assicuriamoci che la sezione interrupted_animations esista
        if (!animationData.contains("interrupted_animations")) {
            animationData.createSection("interrupted_animations");
            saveAnimationData();
        }
    }
    
    private void saveAnimationData() {
        try {
            animationData.save(animationDataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossibile salvare animation_data.yml: " + e.getMessage());
        }
    }
    
    private boolean hasPlayerSeenAnimation(UUID playerUUID) {
        return animationData.getBoolean("players." + playerUUID.toString(), false);
    }
    
    private void markPlayerAsAnimated(UUID playerUUID) {
        animationData.set("players." + playerUUID.toString(), true);
        saveAnimationData();
    }
    
    /**
     * Salva l'informazione che un giocatore aveva un'animazione interrotta
     * @param playerUUID UUID del giocatore
     * @param location Posizione originale del giocatore
     * @param gameMode Modalità di gioco originale
     */
    private void saveInterruptedAnimation(UUID playerUUID, Location location, GameMode gameMode) {
        String path = "interrupted_animations." + playerUUID.toString();
        animationData.set(path + ".world", location.getWorld().getName());
        animationData.set(path + ".x", location.getX());
        animationData.set(path + ".y", location.getY());
        animationData.set(path + ".z", location.getZ());
        animationData.set(path + ".yaw", location.getYaw());
        animationData.set(path + ".pitch", location.getPitch());
        animationData.set(path + ".gamemode", gameMode.name());
        saveAnimationData();
    }
    
    /**
     * Controlla se un giocatore aveva un'animazione interrotta
     * @param playerUUID UUID del giocatore
     * @return true se il giocatore aveva un'animazione interrotta
     */
    private boolean hasInterruptedAnimation(UUID playerUUID) {
        return animationData.contains("interrupted_animations." + playerUUID.toString());
    }
    
    /**
     * Ripristina la posizione originale di un giocatore con un'animazione interrotta
     * @param player Il giocatore da ripristinare
     */
    private void restoreInterruptedAnimation(Player player) {
        UUID playerUUID = player.getUniqueId();
        String path = "interrupted_animations." + playerUUID.toString();
        
        if (!animationData.contains(path)) {
            return;
        }
        
        // Recupera le informazioni sulla posizione originale
        String worldName = animationData.getString(path + ".world");
        World world = Bukkit.getWorld(worldName);
        
        if (world == null) {
            plugin.getLogger().warning("Mondo non trovato per animazione interrotta: " + worldName);
            animationData.set(path, null);
            saveAnimationData();
            return;
        }
        
        double x = animationData.getDouble(path + ".x");
        double y = animationData.getDouble(path + ".y");
        double z = animationData.getDouble(path + ".z");
        float yaw = (float) animationData.getDouble(path + ".yaw");
        float pitch = (float) animationData.getDouble(path + ".pitch");
        
        Location originalLocation = new Location(world, x, y, z, yaw, pitch);
        
        // Controlla che la GameMode sia valida
        GameMode gameMode = GameMode.SURVIVAL; // Default a sopravvivenza
        try {
            String gameModeStr = animationData.getString(path + ".gamemode", "SURVIVAL");
            gameMode = GameMode.valueOf(gameModeStr);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("GameMode non valida per animazione interrotta: " + e.getMessage());
        }
        
        // Ripristina lo stato del giocatore
        player.setGameMode(gameMode);
        player.teleport(originalLocation);
        
        // Rimuovi gli effetti pozione che potrebbero essere rimasti
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.JUMP);
        
        // Rimuovi l'animazione interrotta
        animationData.set(path, null);
        saveAnimationData();
        
        // Informa il giocatore
        player.sendMessage("§6§l[GavaCore] §aLa tua posizione è stata ripristinata dopo un'animazione interrotta.");
        
        // Segna il giocatore come avendo visto l'animazione
        markPlayerAsAnimated(playerUUID);
    }
    
    /**
     * Controlla se un giocatore ha un'animazione interrotta e la ripristina
     * @param player Il giocatore da controllare
     * @return true se il giocatore aveva un'animazione interrotta
     */
    public boolean checkAndRestoreInterruptedAnimation(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        if (hasInterruptedAnimation(playerUUID)) {
            restoreInterruptedAnimation(player);
            return true;
        }
        
        return false;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        
        // Controlla se il giocatore aveva un'animazione interrotta
        if (hasInterruptedAnimation(playerUUID)) {
            // Aspetta un po' prima di ripristinare la posizione
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    restoreInterruptedAnimation(player);
                }
            }, 20L); // Ritardo di 1 secondo
            
            return;
        }
        
        // Controlla se il giocatore era in un'animazione attiva quando il server si è spento
        if (activeAnimations.containsKey(playerUUID)) {
            // Pulisci l'animazione precedente
            ArmorStand camera = activeAnimations.get(playerUUID);
            if (camera != null && !camera.isDead()) {
                camera.remove();
            }
            activeAnimations.remove(playerUUID);
            
            // Ripristina lo stato del giocatore
            player.setGameMode(GameMode.SURVIVAL); // Assumi survival come predefinito
            player.setFlying(false);
            
            // Rimuovi effetti che potrebbero essere rimasti
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.JUMP);
            
            // Informa il giocatore
            player.sendMessage("§6§l[GavaCore] §eL'animazione precedente è stata interrotta per un problema del server.");
            
            // Se non aveva ancora visto l'animazione completa, puoi decidere di riavviarla o lasciarla così
            if (!hasPlayerSeenAnimation(playerUUID)) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        startAnimation(player);
                    }
                }, 60L); // Ritardo di 3 secondi
            }
            
            return;
        }
        
        // Verifica se il giocatore ha già visto l'animazione
        if (hasPlayerSeenAnimation(playerUUID)) {
            return;
        }
        
        // Ritardo di 1 secondo per assicurarsi che il giocatore sia completamente caricato
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                startAnimation(player);
            }
        }, 20L);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        // Verifica se il giocatore ha un'animazione attiva
        if (activeAnimations.containsKey(playerUUID)) {
            // Salva la posizione originale per ripristinarla alla riconnessione
            Location originalPosition = player.getLocation();
            GameMode originalGameMode = player.getGameMode();
            saveInterruptedAnimation(playerUUID, originalPosition, originalGameMode);
            
            // Rimuovi l'ArmorStand
            ArmorStand camera = activeAnimations.get(playerUUID);
            if (camera != null && !camera.isDead()) {
                camera.remove();
            }
            activeAnimations.remove(playerUUID);
            
            // Se non aveva ancora visto l'animazione completa, mantienilo come tale
            // in modo che la veda la prossima volta che si collega
            // Non chiamare markPlayerAsAnimated(playerUUID)
            
            plugin.getLogger().info("Animazione interrotta per la disconnessione di " + player.getName());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (activeAnimations.containsKey(player.getUniqueId())) {
            // Annulla l'evento di sneak se il giocatore è in un'animazione
            event.setCancelled(true);
            player.sendMessage("§6§l[GavaCore] §cNon puoi accovacciarti durante l'animazione!");
            
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
                
                player.sendMessage("§6§l[GavaCore] §cNon puoi accovacciarti durante l'animazione!");
            }
        }
    }
    
    /**
     * Mostra un testo con animazione lettera per lettera
     * @param player Il giocatore a cui mostrare l'animazione
     * @param fullTitle Il titolo completo da animare
     * @param fullSubtitle Il sottotitolo completo da animare
     */
    private void showAnimatedText(Player player, String fullTitle, String fullSubtitle) {
        new BukkitRunnable() {
            int step = 0;
            final int maxStep = Math.max(fullTitle.length(), fullSubtitle.length());
            
            @Override
            public void run() {
                if (!player.isOnline() || step > maxStep) {
                    this.cancel();
                    return;
                }
                
                // Ottieni le porzioni correnti del testo in base allo step
                String currentTitle = fullTitle.length() > step ? 
                    fullTitle.substring(0, step) : fullTitle;
                
                String currentSubtitle = fullSubtitle.length() > step ? 
                    fullSubtitle.substring(0, step) : fullSubtitle;
                
                // Mostra il titolo attuale
                player.sendTitle(currentTitle, currentSubtitle, 0, 30, 10);
                
                // Aggiungi un effetto sonoro per ogni lettera
                if (step > 0 && (step <= fullTitle.length() || step <= fullSubtitle.length())) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.5f);
                }
                
                step++;
            }
        }.runTaskTimer(plugin, 0L, TEXT_ANIMATION_DELAY);
    }
    
    private void startAnimation(Player player) {
        // Salva la posizione iniziale del giocatore
        final Location playerLoc = player.getLocation().clone();
        final World world = playerLoc.getWorld();
        
        // Salva la modalità di gioco attuale
        GameMode previousGameMode = player.getGameMode();
        
        // Rimuovi effetti che potrebbero interferire con l'animazione
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        
        // Impedisci al giocatore di accovacciarsi durante l'animazione
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, MAX_ANIMATION_DURATION, 128, false, false, false));
        player.setSneaking(false);
        
        // Crea un ArmorStand invisibile per la visuale della camera
        final Location cameraLoc = playerLoc.clone().add(0, ANIMATION_HEIGHT, 0);
        ArmorStand camera = (ArmorStand) world.spawnEntity(cameraLoc, EntityType.ARMOR_STAND);
        
        // Configura l'ArmorStand
        camera.setVisible(false);
        camera.setGravity(false);
        camera.setInvulnerable(true);
        camera.setSmall(true);
        camera.setMarker(true);
        camera.setCustomName("Camera_" + player.getName());
        camera.setCustomNameVisible(false);
        
        // Salva l'ArmorStand per poterlo rimuovere in seguito
        activeAnimations.put(player.getUniqueId(), camera);
        
        // Imposta il giocatore in modalità spettatore e blocca i movimenti
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(cameraLoc.clone().setDirection(new Vector(0, -1, 0))); // Guarda verso il basso
        
        // Breve pausa prima di collegare il giocatore all'ArmorStand
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline() && camera.isValid()) {
                // Collega la visuale all'ArmorStand
                player.setSpectatorTarget(camera);
                
                // Avvia l'animazione del titolo dopo un breve ritardo
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        // Rimuovi qualsiasi titolo precedente
                        player.resetTitle();
                        
                        // Avvia l'animazione del testo
                        showAnimatedText(player, "§6Benvenuto nel server", "§7Preparati all'avventura...");
                    }
                }, 20L);
                
                // Effetto di immobilità e riduzione visibilità
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1, false, false, false));
            }
        }, 5L);
        
        // Inizia l'animazione di discesa
        new BukkitRunnable() {
            int tick = 0;
            double angle = 0; // Angolo di rotazione corrente
            
            @Override
            public void run() {
                // Se il player non è più online o l'ArmorStand è stato rimosso, interrompi l'animazione
                if (!player.isOnline() || camera.isDead() || !activeAnimations.containsKey(player.getUniqueId())) {
                    this.cancel();
                    return;
                }
                
                // Controllo aggiuntivo anti-sneak in ogni tick
                if (player.isSneaking()) {
                    player.setSneaking(false);
                }
                
                tick++;
                
                // Calcola la nuova posizione dell'ArmorStand
                double progress = (double) tick / MAX_ANIMATION_DURATION;
                double heightProgress = Math.min(1.0, progress * 1.5); // Accelerazione iniziale
                double currentHeight = ANIMATION_HEIGHT * (1 - heightProgress);
                
                Location newLoc = playerLoc.clone().add(0, currentHeight, 0);
                
                // Trova l'altezza del terreno
                Block groundBlock = world.getHighestBlockAt(playerLoc.getBlockX(), playerLoc.getBlockZ());
                int groundHeight = groundBlock.getY();
                
                // Ruota la visuale intorno al giocatore
                angle += ROTATION_SPEED;
                if (angle >= 360) angle -= 360;
                
                // Calcola il raggio di rotazione in base all'altezza (più alto = raggio maggiore)
                double viewRadius = Math.max(5, currentHeight / 4);
                double offsetX = Math.cos(Math.toRadians(angle)) * viewRadius;
                double offsetZ = Math.sin(Math.toRadians(angle)) * viewRadius;
                
                // Aggiorna la posizione con l'offset di rotazione
                newLoc.add(offsetX, 0, offsetZ);
                
                // Fai guardare la camera verso la posizione originale del giocatore
                Vector direction = playerLoc.clone().subtract(newLoc).toVector().normalize();
                newLoc.setDirection(direction);
                
                camera.teleport(newLoc);
                
                // Effetto particellare di "generazione del mondo"
                if (tick % PARTICLE_INTERVAL == 0) {
                    // Crea un effetto circolare di particelle che si espande dal giocatore
                    double radius = progress * 15; // Il raggio aumenta con il progredire dell'animazione
                    for (int i = 0; i < 16; i++) {
                        double particleAngle = 2 * Math.PI * i / 16;
                        double x = Math.cos(particleAngle) * radius;
                        double z = Math.sin(particleAngle) * radius;
                        
                        // Effetto di creazione di blocchi
                        world.spawnParticle(
                            Particle.CLOUD,
                            playerLoc.getX() + x,
                            playerLoc.getY() + 0.5,
                            playerLoc.getZ() + z,
                            1, 0.2, 0.1, 0.2, 0,
                            null,
                            true // Visibile solo al giocatore
                        );
                        
                        // Effetto energetico
                        world.spawnParticle(
                            Particle.END_ROD,
                            playerLoc.getX() + x * 0.8,
                            playerLoc.getY() + progress * 5,
                            playerLoc.getZ() + z * 0.8,
                            1, 0, 0, 0, 0.05,
                            null,
                            true
                        );
                    }
                }
                
                // Quando l'animazione è a metà, mostra un messaggio speciale
                if (tick == MAX_ANIMATION_DURATION / 2) {
                    if (player.isOnline()) {
                        player.resetTitle();
                        player.sendTitle("§6Il mondo", "§esi sta generando...", 10, 40, 10);
                    }
                }
                
                // Termina l'animazione se:
                // 1. Si è raggiunto un blocco sopra il terreno
                // 2. O è stato superato il tempo massimo di animazione
                if (newLoc.getY() <= (groundHeight + 2) || tick >= MAX_ANIMATION_DURATION) {
                    finishAnimation(player, camera, previousGameMode);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
    
    private void finishAnimation(Player player, ArmorStand camera, GameMode previousGameMode) {
        // Salva la posizione originale del giocatore
        Location originalPlayerLocation = player.getLocation().clone();
        
        // Rimuovi lo spettatore dall'ArmorStand
        player.setSpectatorTarget(null);
        
        // Rimuovi l'ArmorStand
        camera.remove();
        activeAnimations.remove(player.getUniqueId());
        
        // Ripristina immediatamente la modalità di gioco
        player.setGameMode(previousGameMode);
        
        // Teleporta il giocatore alla posizione corretta con piccoli aggiustamenti per evitare problemi
        Location safeLoc = originalPlayerLocation.clone();
        safeLoc.setY(Math.floor(safeLoc.getY()) + 0.5);
        safeLoc.setPitch(0); // Resetta la visuale verso l'orizzonte
        safeLoc.setYaw(originalPlayerLocation.getYaw());
        
        // Esegui il teleport con un piccolo ritardo per assicurarsi che tutto sia pronto
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                // Teleporta e correggi la posizione
                player.teleport(safeLoc);
                
                // Rimuovi effetti che potrebbero interferire con il movimento
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.removePotionEffect(PotionEffectType.SLOW);
                player.removePotionEffect(PotionEffectType.JUMP);
                
                // Assicurati che possa volare se era in creative o spettatore
                if (previousGameMode == GameMode.CREATIVE || previousGameMode == GameMode.SPECTATOR) {
                    player.setAllowFlight(true);
                    player.setFlying(previousGameMode == GameMode.SPECTATOR);
                }
                
                // Mostra titolo finale con animazione lettera per lettera
                player.resetTitle();
                showAnimatedText(player, MAIN_TITLE, SUBTITLE);
                
                // Effetto finale
                player.getWorld().spawnParticle(
                    Particle.EXPLOSION_HUGE,
                    player.getLocation().add(0, 1, 0),
                    1, 0, 0, 0, 0,
                    null,
                    true
                );
                
                // Effetto sonoro finale
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                
                // Segna il giocatore come avendo visto l'animazione
                markPlayerAsAnimated(player.getUniqueId());
            }
        }, 5L);
    }
    
    // Metodo pubblico per forzare l'animazione su un giocatore (utile per i comandi di test)
    public void forceAnimation(Player player) {
        startAnimation(player);
    }
    
    // Metodo pubblico per interrompere tutte le animazioni in corso
    public void stopAllAnimations() {
        for (ArmorStand camera : activeAnimations.values()) {
            camera.remove();
        }
        activeAnimations.clear();
    }
    
    // Metodo per resettare l'animazione per un giocatore specifico (per test)
    public void resetPlayerAnimation(UUID playerUUID) {
        animationData.set("players." + playerUUID.toString(), false);
        // Rimuovi anche eventuali animazioni interrotte
        animationData.set("interrupted_animations." + playerUUID.toString(), null);
        saveAnimationData();
    }
    
    // Metodo chiamato quando il server si spegne (da chiamare in onDisable del plugin)
    public void cleanupOnServerShutdown() {
        // Salva lo stato delle animazioni attive
        for (UUID playerUUID : activeAnimations.keySet()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                // Salva la posizione originale per ripristinarla alla riconnessione
                Location originalPosition = player.getLocation();
                GameMode originalGameMode = player.getGameMode();
                saveInterruptedAnimation(playerUUID, originalPosition, originalGameMode);
            }
        }
        
        // Rimuovi tutte le entità create per le animazioni
        for (ArmorStand camera : activeAnimations.values()) {
            if (camera != null && !camera.isDead()) {
                camera.remove();
            }
        }
        
        // Registra informazioni di debug
        if (!activeAnimations.isEmpty()) {
            plugin.getLogger().info("Pulizia di " + activeAnimations.size() + " animazioni attive durante lo spegnimento del server");
        }
        
        // Svuota la mappa
        activeAnimations.clear();
    }
    
    public void cleanup() {
        cleanupOnServerShutdown();
    }
} 