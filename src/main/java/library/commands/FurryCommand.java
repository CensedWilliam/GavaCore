package library.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Piston;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import library.Main;

import java.util.HashMap;
import java.util.UUID;

public class FurryCommand implements CommandExecutor, Listener {
    
    private final Main plugin;
    private HashMap<UUID, AnimationData> activeAnimations = new HashMap<>();
    
    public FurryCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    // Classe per memorizzare i dati dell'animazione
    private class AnimationData {
        BukkitTask task;
        Block pistonBlock;
        Block rodBlock;
        Block extendedRodBlock;
        ArmorStand armorStand;
        Material originalPistonBlockMaterial;
        BlockData originalPistonBlockData;
        Material originalRodBlockMaterial;
        BlockData originalRodBlockData;
        Material originalExtendedRodBlockMaterial;
        BlockData originalExtendedRodBlockData;
        float originalPlayerWalkSpeed;
        float originalPlayerFlySpeed;
        
        public AnimationData(BukkitTask task, Block pistonBlock, Block rodBlock, Block extendedRodBlock, 
                            ArmorStand armorStand, Material originalPistonBlockMaterial, 
                            BlockData originalPistonBlockData, Material originalRodBlockMaterial, 
                            BlockData originalRodBlockData, Material originalExtendedRodBlockMaterial,
                            BlockData originalExtendedRodBlockData, float originalPlayerWalkSpeed,
                            float originalPlayerFlySpeed) {
            this.task = task;
            this.pistonBlock = pistonBlock;
            this.rodBlock = rodBlock;
            this.extendedRodBlock = extendedRodBlock;
            this.armorStand = armorStand;
            this.originalPistonBlockMaterial = originalPistonBlockMaterial;
            this.originalPistonBlockData = originalPistonBlockData;
            this.originalRodBlockMaterial = originalRodBlockMaterial;
            this.originalRodBlockData = originalRodBlockData;
            this.originalExtendedRodBlockMaterial = originalExtendedRodBlockMaterial;
            this.originalExtendedRodBlockData = originalExtendedRodBlockData;
            this.originalPlayerWalkSpeed = originalPlayerWalkSpeed;
            this.originalPlayerFlySpeed = originalPlayerFlySpeed;
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere eseguito solo da un giocatore!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Controlla se il player ha già un'animazione attiva
        if (activeAnimations.containsKey(player.getUniqueId())) {
            player.sendMessage("§c§l[GavaCore] §cHai già un'animazione attiva!");
            return true;
        }
        
        player.sendMessage("§6§l[GavaCore] §aPreparati per una sorpresa speciale...");
        player.sendMessage("§6§l[GavaCore] §ePer una migliore esperienza, premi §fF5 §eper passare alla visuale in terza persona!");
        
        // Forza il player in shift
        player.setSneaking(true);
        
        // Memorizza la posizione e la direzione originale del player
        Location playerLoc = player.getLocation();
        float originalYaw = playerLoc.getYaw();
        float originalPitch = playerLoc.getPitch();
        
        // Determina in quale direzione cardinale il giocatore è rivolto (nord, sud, est, ovest)
        BlockFace playerFacing = getCardinalDirection(playerLoc.getYaw());
        
        // Calcola la posizione del pistone a 3 blocchi dietro al player
        Location pistonLoc = playerLoc.clone();
        switch (playerFacing) {
            case NORTH:
                pistonLoc.add(0, 0, 3); // Se il player guarda nord, il pistone va a sud
                break;
            case SOUTH:
                pistonLoc.add(0, 0, -3); // Se il player guarda sud, il pistone va a nord
                break;
            case EAST:
                pistonLoc.add(-3, 0, 0); // Se il player guarda est, il pistone va a ovest
                break;
            case WEST:
                pistonLoc.add(3, 0, 0); // Se il player guarda ovest, il pistone va a est
                break;
            default:
                pistonLoc.add(0, 0, 3); // Fallback
        }
        
        // Blocco che era nella posizione del pistone (da ripristinare dopo)
        Block pistonBlock = pistonLoc.getBlock();
        Material originalPistonBlockMaterial = pistonBlock.getType();
        BlockData originalPistonBlockData = pistonBlock.getBlockData().clone();
        
        // Calcola la posizione dell'end rod davanti al pistone, verso il player
        Location rodLoc = pistonLoc.clone();
        switch (playerFacing) {
            case NORTH:
                rodLoc.add(0, 0, -1); // Se il player guarda nord, l'end rod va a nord
                break;
            case SOUTH:
                rodLoc.add(0, 0, 1); // Se il player guarda sud, l'end rod va a sud
                break;
            case EAST:
                rodLoc.add(1, 0, 0); // Se il player guarda est, l'end rod va a est
                break;
            case WEST:
                rodLoc.add(-1, 0, 0); // Se il player guarda ovest, l'end rod va a ovest
                break;
            default:
                rodLoc.add(0, 0, -1); // Fallback
        }
        
        // Blocco che era nella posizione dell'end rod (da ripristinare dopo)
        Block rodBlock = rodLoc.getBlock();
        Material originalRodBlockMaterial = rodBlock.getType();
        BlockData originalRodBlockData = rodBlock.getBlockData().clone();
        
        // Posizione originale dell'end rod (non esteso)
        Location rodOriginalLoc = rodLoc.clone();
        
        // Posizione estesa dell'end rod (quando il pistone è esteso)
        Location rodExtendedLoc = rodOriginalLoc.clone();
        switch (playerFacing) {
            case NORTH:
                rodExtendedLoc.add(0, 0, -1); // Muovi verso nord
                break;
            case SOUTH:
                rodExtendedLoc.add(0, 0, 1); // Muovi verso sud
                break;
            case EAST:
                rodExtendedLoc.add(1, 0, 0); // Muovi verso est
                break;
            case WEST:
                rodExtendedLoc.add(-1, 0, 0); // Muovi verso ovest
                break;
        }
        
        // Blocca il giocatore in posizione fissa
        Location fixedPlayerLoc = rodExtendedLoc.clone();
        switch (playerFacing) {
            case NORTH:
                fixedPlayerLoc.add(0, 0, -1); // Un blocco a nord dall'end rod esteso
                break;
            case SOUTH:
                fixedPlayerLoc.add(0, 0, 1); // Un blocco a sud dall'end rod esteso
                break;
            case EAST:
                fixedPlayerLoc.add(1, 0, 0); // Un blocco a est dall'end rod esteso
                break;
            case WEST:
                fixedPlayerLoc.add(-1, 0, 0); // Un blocco a ovest dall'end rod esteso
                break;
        }
        fixedPlayerLoc.setYaw(originalYaw);
        fixedPlayerLoc.setPitch(originalPitch);
        
        // Teletrasporta il giocatore nella posizione fissa
        player.teleport(fixedPlayerLoc);
        
        // Posizione dell'ArmorStand tra il giocatore e il pistone (a 5 blocchi di distanza dal pistone)
        Location armorStandLoc = pistonLoc.clone();
        switch (playerFacing) {
            case NORTH:
                armorStandLoc.add(0, 0, -2); // 2 blocchi verso nord dal pistone
                break;
            case SOUTH:
                armorStandLoc.add(0, 0, 2); // 2 blocchi verso sud dal pistone
                break;
            case EAST:
                armorStandLoc.add(2, 0, 0); // 2 blocchi verso est dal pistone
                break;
            case WEST:
                armorStandLoc.add(-2, 0, 0); // 2 blocchi verso ovest dal pistone
                break;
        }
        armorStandLoc.add(0, 1, 0); // Alza l'ArmorStand di 1 blocco
        
        // Crea un ArmorStand invisibile per la rotazione
        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(armorStandLoc, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setBasePlate(false);
        armorStand.setCustomName("CiccioAnimation");
        armorStand.setCustomNameVisible(false);
        
        // Memorizza la velocità originale del player
        float originalPlayerWalkSpeed = player.getWalkSpeed();
        float originalPlayerFlySpeed = player.getFlySpeed();
        
        // Rende il giocatore immobile
        player.setWalkSpeed(0);
        player.setFlySpeed(0);
        
        // Salva lo stato del blocco che sarà occupato dall'end rod quando esteso
        Block extendedRodBlock = rodExtendedLoc.getBlock();
        Material originalExtendedRodBlockMaterial = extendedRodBlock.getType();
        BlockData originalExtendedRodBlockData = extendedRodBlock.getBlockData().clone();
        
        // Blocca la rotazione del player e gestisce l'animazione
        BukkitTask task = new BukkitRunnable() {
            int time = 0;
            boolean extended = false;
            Block currentRodBlock = rodBlock; // Blocco attuale dell'end rod
            Location currentRodLoc = rodOriginalLoc.clone(); // Posizione attuale dell'end rod
            double rotationAngle = 0; // Angolo di rotazione dell'ArmorStand
            
            @Override
            public void run() {
                // Incrementa il timer
                time++;
                
                // Ruota l'ArmorStand intorno al player
                rotationAngle += Math.PI / 40; // 1/80 di un giro completo per tick (9 gradi)
                double radius = 5.0; // Raggio di rotazione di 5 blocchi
                double x = fixedPlayerLoc.getX() + Math.cos(rotationAngle) * radius;
                double z = fixedPlayerLoc.getZ() + Math.sin(rotationAngle) * radius;
                Location newArmorStandLoc = new Location(player.getWorld(), x, armorStandLoc.getY(), z);
                armorStand.teleport(newArmorStandLoc);
                
                // Forza il player a rimanere nella posizione fissa ma cambia la direzione in cui guarda
                // Calcola la direzione verso l'ArmorStand
                Vector direction = newArmorStandLoc.toVector().subtract(fixedPlayerLoc.toVector()).normalize();
                
                // Crea una nuova location con la posizione fissa ma con la direzione aggiornata
                Location lookLocation = fixedPlayerLoc.clone();
                lookLocation.setDirection(direction);
                
                // Imposta solo la direzione (yaw e pitch) senza teleportare il player
                player.teleport(lookLocation, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN);
                
                // Mantieni lo shift
                player.setSneaking(true);
                
                // Posiziona il pistone e l'end rod se è il primo tick
                if (time == 1) {
                    // Imposta il pistone appiccicoso
                    pistonBlock.setType(Material.STICKY_PISTON);
                    Directional pistonData = (Directional) pistonBlock.getBlockData();
                    // Inverti la direzione per far puntare il pistone verso il player
                    pistonData.setFacing(playerFacing);
                    pistonBlock.setBlockData(pistonData);
                    
                    // Imposta l'end rod
                    rodBlock.setType(Material.END_ROD);
                    Directional rodData = (Directional) rodBlock.getBlockData();
                    rodData.setFacing(playerFacing);
                    rodBlock.setBlockData(rodData);
                }
                
                // Attiva/disattiva il pistone ogni secondo
                if (time % 20 == 0) {
                    Piston pistonData = (Piston) pistonBlock.getBlockData();
                    pistonData.setExtended(!extended);
                    pistonBlock.setBlockData(pistonData);
                    
                    // Muovi l'end rod
                    if (!extended) {
                        // Estendi l'end rod
                        currentRodBlock.setType(Material.AIR); // Rimuovi l'end rod attuale
                        Block newRodBlock = rodExtendedLoc.getBlock();
                        newRodBlock.setType(Material.END_ROD);
                        Directional rodData = (Directional) newRodBlock.getBlockData();
                        rodData.setFacing(playerFacing);
                        newRodBlock.setBlockData(rodData);
                        currentRodBlock = newRodBlock;
                        currentRodLoc = rodExtendedLoc.clone();
                        
                        // Genera particelle quando l'end rod "tocca" il player
                        player.getWorld().spawnParticle(org.bukkit.Particle.CLOUD, fixedPlayerLoc, 30, 0.3, 0.3, 0.3, 0.05);
                        player.getWorld().spawnParticle(org.bukkit.Particle.FLAME, fixedPlayerLoc, 15, 0.2, 0.2, 0.2, 0.01);
                    } else {
                        // Ritira l'end rod
                        currentRodBlock.setType(Material.AIR); // Rimuovi l'end rod esteso
                        rodBlock.setType(Material.END_ROD);
                        Directional rodData = (Directional) rodBlock.getBlockData();
                        rodData.setFacing(playerFacing);
                        rodBlock.setBlockData(rodData);
                        currentRodBlock = rodBlock;
                        currentRodLoc = rodOriginalLoc.clone();
                    }
                    
                    extended = !extended;
                    
                    // Effetto sonoro
                    player.getWorld().playSound(pistonLoc, extended ? 
                                              org.bukkit.Sound.BLOCK_PISTON_EXTEND : 
                                              org.bukkit.Sound.BLOCK_PISTON_CONTRACT, 
                                              1.0f, 1.0f);
                }
                
                // Termina dopo 20 secondi (400 ticks)
                if (time >= 400 || !player.isOnline()) {
                    cleanupAnimation(player.getUniqueId());
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        // Salva i dati dell'animazione
        AnimationData animationData = new AnimationData(
            task, pistonBlock, rodBlock, extendedRodBlock, armorStand,
            originalPistonBlockMaterial, originalPistonBlockData,
            originalRodBlockMaterial, originalRodBlockData,
            originalExtendedRodBlockMaterial, originalExtendedRodBlockData,
            originalPlayerWalkSpeed, originalPlayerFlySpeed
        );
        
        activeAnimations.put(player.getUniqueId(), animationData);
        
        return true;
    }
    
    /**
     * Metodo per pulire l'animazione e ripristinare lo stato originale
     */
    private void cleanupAnimation(UUID playerId) {
        AnimationData data = activeAnimations.remove(playerId);
        if (data == null) return;
        
        // Cancella il task se ancora attivo
        if (data.task != null && !data.task.isCancelled()) {
            data.task.cancel();
        }
        
        // Rimuovi l'ArmorStand
        if (data.armorStand != null && !data.armorStand.isDead()) {
            data.armorStand.remove();
        }
        
        // Ripristina i blocchi
        if (data.pistonBlock != null) {
            data.pistonBlock.setType(data.originalPistonBlockMaterial);
            data.pistonBlock.setBlockData(data.originalPistonBlockData);
        }
        
        if (data.rodBlock != null) {
            data.rodBlock.setType(data.originalRodBlockMaterial);
            data.rodBlock.setBlockData(data.originalRodBlockData);
        }
        
        if (data.extendedRodBlock != null) {
            data.extendedRodBlock.setType(data.originalExtendedRodBlockMaterial);
            data.extendedRodBlock.setBlockData(data.originalExtendedRodBlockData);
        }
        
        // Ripristina il player se ancora online
        Player player = plugin.getServer().getPlayer(playerId);
        if (player != null && player.isOnline()) {
            player.setSneaking(false);
            player.setWalkSpeed(data.originalPlayerWalkSpeed);
            player.setFlySpeed(data.originalPlayerFlySpeed);
            player.sendMessage("§6§l[GavaCore] §aLa sorpresa è terminata!");
        }
    }
    
    /**
     * Gestisce l'uscita del player dal server
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (activeAnimations.containsKey(player.getUniqueId())) {
            cleanupAnimation(player.getUniqueId());
        }
    }
    
    /**
     * Metodo per pulire tutte le animazioni attive
     * Chiamato quando il plugin viene disabilitato
     */
    public void cleanupAll() {
        // Copia l'elenco degli UUID per evitare ConcurrentModificationException
        UUID[] playerIds = activeAnimations.keySet().toArray(new UUID[0]);
        for (UUID playerId : playerIds) {
            cleanupAnimation(playerId);
        }
        
        // Rimuovi il listener
        HandlerList.unregisterAll(this);
    }
    
    /**
     * Determina la direzione cardinale in base alla direzione yaw del player
     */
    private BlockFace getCardinalDirection(float yaw) {
        // Normalizza lo yaw per assicurarsi che sia nel range 0-360
        yaw = (yaw % 360 + 360) % 360;
        
        if (yaw >= 315 || yaw < 45) {
            return BlockFace.SOUTH;
        } else if (yaw >= 45 && yaw < 135) {
            return BlockFace.WEST;
        } else if (yaw >= 135 && yaw < 225) {
            return BlockFace.NORTH;
        } else { // yaw >= 225 && yaw < 315
            return BlockFace.EAST;
        }
    }
    
    /**
     * Ottiene la direzione opposta di una BlockFace
     */
    private BlockFace getOppositeDirection(BlockFace face) {
        switch(face) {
            case NORTH: return BlockFace.SOUTH;
            case SOUTH: return BlockFace.NORTH;
            case EAST: return BlockFace.WEST;
            case WEST: return BlockFace.EAST;
            case UP: return BlockFace.DOWN;
            case DOWN: return BlockFace.UP;
            default: return face;
        }
    }
} 
