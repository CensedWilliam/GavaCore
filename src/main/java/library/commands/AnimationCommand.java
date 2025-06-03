package library.commands;

import library.Main;
import library.FirstJoinAnimation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AnimationCommand implements CommandExecutor, TabCompleter {
    private final Main plugin;
    
    public AnimationCommand(Main plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }
        
        FirstJoinAnimation animation = plugin.getFirstJoinAnimation();
        
        switch (args[0].toLowerCase()) {
            case "play":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Specifica un giocatore: /animation play <player>");
                    return true;
                }
                
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Giocatore non trovato!");
                    return true;
                }
                
                // Avvia l'animazione per il giocatore specificato
                animation.forceAnimation(target);
                sender.sendMessage(ChatColor.GREEN + "Animazione avviata per " + target.getName());
                return true;
                
            case "reset":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Specifica un giocatore: /animation reset <player>");
                    return true;
                }
                
                Player resetTarget = Bukkit.getPlayer(args[1]);
                if (resetTarget == null) {
                    sender.sendMessage(ChatColor.RED + "Giocatore non trovato!");
                    return true;
                }
                
                // Resetta lo stato dell'animazione per il giocatore
                animation.resetPlayerAnimation(resetTarget.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "Animazione resettata per " + resetTarget.getName() + ". Sarà mostrata al prossimo accesso.");
                return true;
                
            case "stop":
                // Interrompe tutte le animazioni in corso
                animation.stopAllAnimations();
                sender.sendMessage(ChatColor.GREEN + "Tutte le animazioni in corso sono state interrotte.");
                return true;
                
            case "list":
                // Qui si potrebbe aggiungere una funzionalità per elencare tutti i giocatori che hanno visto l'animazione
                sender.sendMessage(ChatColor.YELLOW + "Questa funzionalità non è ancora implementata.");
                return true;
                
            default:
                sendUsage(sender);
                return true;
        }
    }
    
    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Comandi Animazione ===");
        sender.sendMessage(ChatColor.YELLOW + "/animation play <player> " + ChatColor.WHITE + "- Avvia l'animazione per un giocatore");
        sender.sendMessage(ChatColor.YELLOW + "/animation reset <player> " + ChatColor.WHITE + "- Resetta l'animazione per un giocatore");
        sender.sendMessage(ChatColor.YELLOW + "/animation stop " + ChatColor.WHITE + "- Interrompe tutte le animazioni in corso");
        sender.sendMessage(ChatColor.YELLOW + "/animation list " + ChatColor.WHITE + "- Mostra i giocatori che hanno visto l'animazione");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("play", "reset", "stop", "list");
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("play") || args[0].equalsIgnoreCase("reset"))) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }
        
        return completions;
    }
} 
