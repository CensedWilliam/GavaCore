package library.commands;

import library.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class ResolveAnimationBugsCommand implements CommandExecutor {

    private final Main plugin;
    // Effetti che potrebbero impedire il movimento
    private final List<PotionEffectType> movementEffects = Arrays.asList(
        PotionEffectType.SLOW,
        PotionEffectType.BLINDNESS,
        PotionEffectType.JUMP,
        PotionEffectType.LEVITATION,
        PotionEffectType.CONFUSION
    );

    public ResolveAnimationBugsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
            return true;
        }

        Player player = (Player) sender;
        boolean fixedSomething = false;
        
        // Verifica se si tratta di un admin che vuole aiutare un altro giocatore
        Player targetPlayer = player;
        if (args.length > 0 && player.hasPermission("gavacore.resolvebugs.others")) {
            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                player.sendMessage("§c§l[GavaCore] §cGiocatore non trovato!");
                return true;
            }
        }
        
        // 1. Risolvi le animazioni di teletrasporto bloccate
        if (plugin.getTeleportAnimation() != null) {
            plugin.getTeleportAnimation().cancelAnimation(targetPlayer);
            fixedSomething = true;
        }
        
        // 2. Risolvi le animazioni di primo accesso bloccate
        if (plugin.getFirstJoinAnimation() != null) {
            plugin.getFirstJoinAnimation().resetPlayerAnimation(targetPlayer.getUniqueId());
            fixedSomething = true;
        }
        
        // 3. Verifica se il giocatore è in modalità spettatore senza motivo
        if (targetPlayer.getGameMode() == GameMode.SPECTATOR) {
            // Controlla se è collegato a qualche entità come spettatore
            Entity spectatorTarget = targetPlayer.getSpectatorTarget();
            if (spectatorTarget != null) {
                targetPlayer.setSpectatorTarget(null);
                fixedSomething = true;
                
                // Se è un ArmorStand, rimuovilo (potrebbe essere un residuo di un'animazione)
                if (spectatorTarget instanceof ArmorStand) {
                    spectatorTarget.remove();
                }
            }
            
            // Ripristina la modalità di gioco a sopravvivenza
            targetPlayer.setGameMode(GameMode.SURVIVAL);
            fixedSomething = true;
        }
        
        // 4. Rimuovi effetti di pozione che potrebbero bloccare il movimento
        for (PotionEffectType effectType : movementEffects) {
            if (targetPlayer.hasPotionEffect(effectType)) {
                targetPlayer.removePotionEffect(effectType);
                fixedSomething = true;
            }
        }
        
        // 5. Risolvi i bug con il volo
        if (targetPlayer.getAllowFlight() && 
            targetPlayer.getGameMode() != GameMode.CREATIVE && 
            targetPlayer.getGameMode() != GameMode.SPECTATOR) {
            targetPlayer.setAllowFlight(false);
            targetPlayer.setFlying(false);
            fixedSomething = true;
        }
        
        // 6. Verifica se il giocatore è su un ArmorStand (seduto) e lo rimuove
        if (targetPlayer.getVehicle() instanceof ArmorStand) {
            targetPlayer.getVehicle().remove();
            targetPlayer.leaveVehicle();
            fixedSomething = true;
        }
        
        // 7. Controlla altre potenziali cause di blocco del movimento
        if (targetPlayer.isSleeping()) {
            targetPlayer.wakeup(true);
            fixedSomething = true;
        }
        
        // Effettua un "tick di forzatura" sul giocatore
        targetPlayer.setNoDamageTicks(0);
        
        // Messaggi di completamento
        if (fixedSomething) {
            if (player == targetPlayer) {
                player.sendMessage("§6§l[GavaCore] §aProblemi di animazione risolti! Ora dovresti poterti muovere correttamente.");
            } else {
                player.sendMessage("§6§l[GavaCore] §aProblemi di animazione risolti per §e" + targetPlayer.getName() + "§a!");
                targetPlayer.sendMessage("§6§l[GavaCore] §aUn amministratore ha risolto i tuoi problemi di animazione. Ora dovresti poterti muovere correttamente.");
            }
        } else {
            if (player == targetPlayer) {
                player.sendMessage("§6§l[GavaCore] §eNessun problema rilevato. Se continui ad avere difficoltà, contatta un amministratore.");
            } else {
                player.sendMessage("§6§l[GavaCore] §eNessun problema rilevato per §6" + targetPlayer.getName() + "§e.");
            }
        }
        
        return true;
    }
} 