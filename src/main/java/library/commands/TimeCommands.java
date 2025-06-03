package library.commands;

import library.Main;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeCommands implements CommandExecutor, TabCompleter {
    
    private final Main plugin;
    
    public TimeCommands(Main plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Controllo se il sistema tempo/meteo è abilitato
        if (!plugin.getConfigManager().isTimeWeatherEnabled()) {
            sender.sendMessage(plugin.getConfigManager().formatMessage("%error%Sistema tempo/meteo disabilitato!"));
            return true;
        }
        
        String cmd = command.getName().toLowerCase();
        
        switch (cmd) {
            case "giorno":
                return handleGiornoCommand(sender, args);
            case "notte":
                return handleNotteCommand(sender, args);
            case "alba":
                return handleAlbaCommand(sender, args);
            case "tramonto":
                return handleTramontoCommand(sender, args);
            case "tempo":
                return handleTempoCommand(sender, args);
            case "sereno":
                return handleSerenoCommand(sender, args);
            case "pioggia":
                return handlePioggiaCommand(sender, args);
            case "temporale":
                return handleTemporaleCommand(sender, args);
            case "meteo":
                return handleMeteoCommand(sender, args);
            default:
                return false;
        }
    }
    
    private boolean handleGiornoCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gavacore.time.day")) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("no-permission"));
            return true;
        }
        
        World world = getTargetWorld(sender, args);
        if (world == null) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("world-not-found"));
            return true;
        }
        
        // Usa il tempo configurato invece di valore hardcoded
        world.setTime(plugin.getConfigManager().getCustomTime("day"));
        
        // Effetti sonori configurabili
        if (plugin.getConfigManager().areSoundsEnabled()) {
            playConfigurableSound(world, "day");
        }
        
        // Messaggio configurabile
        String message = plugin.getConfigManager().getTimeWeatherMessage("day-set")
                .replace("%player%", sender.getName())
                .replace("%world%", world.getName());
        
        broadcastToWorld(world, sender, message);
        return true;
    }
    
    private boolean handleNotteCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gavacore.time.night")) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("no-permission"));
            return true;
        }
        
        World world = getTargetWorld(sender, args);
        if (world == null) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("world-not-found"));
            return true;
        }
        
        world.setTime(plugin.getConfigManager().getCustomTime("night"));
        
        if (plugin.getConfigManager().areSoundsEnabled()) {
            playConfigurableSound(world, "night");
        }
        
        String message = plugin.getConfigManager().getTimeWeatherMessage("night-set")
                .replace("%player%", sender.getName())
                .replace("%world%", world.getName());
        
        broadcastToWorld(world, sender, message);
        return true;
    }
    
    private boolean handleAlbaCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gavacore.time.dawn")) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("no-permission"));
            return true;
        }
        
        World world = getTargetWorld(sender, args);
        if (world == null) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("world-not-found"));
            return true;
        }
        
        world.setTime(plugin.getConfigManager().getCustomTime("dawn"));
        
        if (plugin.getConfigManager().areSoundsEnabled()) {
            playConfigurableSound(world, "dawn");
        }
        
        String message = plugin.getConfigManager().getTimeWeatherMessage("dawn-set")
                .replace("%player%", sender.getName())
                .replace("%world%", world.getName());
        
        broadcastToWorld(world, sender, message);
        return true;
    }
    
    private boolean handleTramontoCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gavacore.time.sunset")) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("no-permission"));
            return true;
        }
        
        World world = getTargetWorld(sender, args);
        if (world == null) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("world-not-found"));
            return true;
        }
        
        world.setTime(plugin.getConfigManager().getCustomTime("sunset"));
        
        if (plugin.getConfigManager().areSoundsEnabled()) {
            playConfigurableSound(world, "sunset");
        }
        
        String message = plugin.getConfigManager().getTimeWeatherMessage("sunset-set")
                .replace("%player%", sender.getName())
                .replace("%world%", world.getName());
        
        broadcastToWorld(world, sender, message);
        return true;
    }
    
    private boolean handleTempoCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gavacore.time.set")) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(plugin.getConfigManager().formatMessage("%error%Uso: /tempo <valore> [mondo]"));
            sender.sendMessage("§7Esempi:");
            sender.sendMessage("§7- /tempo 6000 §8(mezzogiorno)");
            sender.sendMessage("§7- /tempo 18000 §8(mezzanotte)");
            return true;
        }
        
        try {
            long time = Long.parseLong(args[0]);
            
            World world;
            if (args.length >= 2) {
                world = Bukkit.getWorld(args[1]);
                if (world == null) {
                    sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("world-not-found"));
                    return true;
                }
            } else {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getConfigManager().formatMessage("%error%Devi specificare un mondo quando usi questo comando dalla console!"));
                    return true;
                }
                world = ((Player) sender).getWorld();
            }
            
            world.setTime(time);
            
            if (plugin.getConfigManager().areSoundsEnabled()) {
                playConfigurableSound(world, "day"); // Suono generico per tempo personalizzato
            }
            
            String message = plugin.getConfigManager().getTimeWeatherMessage("time-set")
                    .replace("%player%", sender.getName())
                    .replace("%world%", world.getName())
                    .replace("%time%", String.valueOf(time));
            
            broadcastToWorld(world, sender, message);
            return true;
            
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().formatMessage("%error%Il valore del tempo deve essere un numero!"));
            return true;
        }
    }
    
    private boolean handleSerenoCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gavacore.weather.clear")) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("no-permission"));
            return true;
        }
        
        World world = getTargetWorld(sender, args);
        if (world == null) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("world-not-found"));
            return true;
        }
        
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(plugin.getConfigManager().getWeatherDuration("clear"));
        
        if (plugin.getConfigManager().areSoundsEnabled()) {
            playConfigurableSound(world, "clear");
        }
        
        if (plugin.getConfigManager().areParticlesEnabled()) {
            spawnWeatherParticles(world, "clear");
        }
        
        String message = plugin.getConfigManager().getTimeWeatherMessage("clear-set")
                .replace("%player%", sender.getName())
                .replace("%world%", world.getName());
        
        broadcastToWorld(world, sender, message);
        return true;
    }
    
    private boolean handlePioggiaCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gavacore.weather.rain")) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("no-permission"));
            return true;
        }
        
        World world = getTargetWorld(sender, args);
        if (world == null) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("world-not-found"));
            return true;
        }
        
        world.setStorm(true);
        world.setThundering(false);
        world.setWeatherDuration(plugin.getConfigManager().getWeatherDuration("rain"));
        
        if (plugin.getConfigManager().areSoundsEnabled()) {
            playConfigurableSound(world, "rain");
        }
        
        if (plugin.getConfigManager().areParticlesEnabled()) {
            spawnWeatherParticles(world, "rain");
        }
        
        String message = plugin.getConfigManager().getTimeWeatherMessage("rain-set")
                .replace("%player%", sender.getName())
                .replace("%world%", world.getName());
        
        broadcastToWorld(world, sender, message);
        return true;
    }
    
    private boolean handleTemporaleCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gavacore.weather.thunder")) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("no-permission"));
            return true;
        }
        
        World world = getTargetWorld(sender, args);
        if (world == null) {
            sender.sendMessage(plugin.getConfigManager().getTimeWeatherMessage("world-not-found"));
            return true;
        }
        
        world.setStorm(true);
        world.setThundering(true);
        world.setWeatherDuration(plugin.getConfigManager().getWeatherDuration("thunder"));
        
        if (plugin.getConfigManager().areSoundsEnabled()) {
            playConfigurableSound(world, "thunder");
        }
        
        if (plugin.getConfigManager().areParticlesEnabled()) {
            spawnWeatherParticles(world, "thunder");
        }
        
        String message = plugin.getConfigManager().getTimeWeatherMessage("thunder-set")
                .replace("%player%", sender.getName())
                .replace("%world%", world.getName());
        
        broadcastToWorld(world, sender, message);
        return true;
    }
    
    private boolean handleMeteoCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gavacore.weather.info")) {
            sender.sendMessage("§c§l[GavaCore] §cNon hai il permesso per usare questo comando!");
            return true;
        }
        
        World world = getTargetWorld(sender, args);
        if (world == null) {
            sender.sendMessage("§c§l[GavaCore] §cMondo non trovato!");
            return true;
        }
        
        // Informazioni sul tempo
        long time = world.getTime();
        String timeString = getReadableTime(time);
        String dayPhase = getDayPhase(time);
        
        // Informazioni sul meteo
        String weather;
        if (world.hasStorm()) {
            if (world.isThundering()) {
                weather = "§4§lTemporale ⛈";
            } else {
                weather = "§b§lPioggia 🌧";
            }
        } else {
            weather = "§a§lSereno ☀";
        }
        
        sender.sendMessage("§6§l[GavaCore] §7Informazioni meteo per §6" + world.getName() + "§7:");
        sender.sendMessage("§7Tempo: §f" + timeString + " §8(" + dayPhase + "§8)");
        sender.sendMessage("§7Meteo: " + weather);
        sender.sendMessage("§7Ciclo giorno/notte: " + (Boolean.parseBoolean(world.getGameRuleValue("doDaylightCycle")) ? "§aAttivo" : "§cDisabilitato"));
        sender.sendMessage("§7Ciclo meteo: " + (Boolean.parseBoolean(world.getGameRuleValue("doWeatherCycle")) ? "§aAttivo" : "§cDisabilitato"));
        
        return true;
    }
    
    private World getTargetWorld(CommandSender sender, String[] args) {
        // Se è stato specificato un mondo nell'ultimo argomento
        if (args.length > 0) {
            String lastArg = args[args.length - 1];
            World world = Bukkit.getWorld(lastArg);
            if (world != null) {
                return world;
            }
        }
        
        // Se il mittente è un giocatore, usa il suo mondo
        if (sender instanceof Player) {
            return ((Player) sender).getWorld();
        }
        
        // Altrimenti usa il mondo principale
        return Bukkit.getWorlds().get(0);
    }
    
    private String getReadableTime(long time) {
        // Minecraft time: 0 = 6:00, 6000 = 12:00, 12000 = 18:00, 18000 = 0:00
        time = time % 24000; // Assicurati che sia nel range 0-23999
        
        int hours = (int) ((time / 1000 + 6) % 24);
        int minutes = (int) ((time % 1000) / 1000.0 * 60);
        
        return String.format("%02d:%02d", hours, minutes);
    }
    
    private String getDayPhase(long time) {
        time = time % 24000;
        
        if (time >= 23000 || time < 1000) {
            return "§6Alba";
        } else if (time >= 1000 && time < 11000) {
            return "§eGiorno";
        } else if (time >= 11000 && time < 13000) {
            return "§cTramonto";
        } else {
            return "§9Notte";
        }
    }
    
    private void playConfigurableSound(World world, String soundType) {
        try {
            String soundName = plugin.getConfigManager().getTimeSound(soundType);
            Sound sound = Sound.valueOf(soundName);
            float volume = plugin.getConfigManager().getSoundVolume();
            float pitch = plugin.getConfigManager().getSoundPitch();
            
            world.getPlayers().forEach(player -> 
                player.playSound(player.getLocation(), sound, volume, pitch)
            );
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Suono non valido configurato: " + soundType);
        }
    }
    
    private void spawnWeatherParticles(World world, String weatherType) {
        try {
            String particleName = plugin.getConfigManager().getWeatherParticle(weatherType);
            Particle particle = Particle.valueOf(particleName);
            int count = plugin.getConfigManager().getParticleCount(weatherType);
            
            world.getPlayers().forEach(player -> {
                // Spawn particelle attorno al giocatore
                player.spawnParticle(particle, 
                    player.getLocation().add(0, 2, 0), 
                    count, 
                    2.0, 1.0, 2.0, 0.1);
            });
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Particella non valida configurata: " + weatherType);
        }
    }
    
    private void broadcastToWorld(World world, CommandSender sender, String message) {
        if (world.getPlayers().size() > 0) {
            world.getPlayers().forEach(player -> player.sendMessage(message));
        } else {
            // Se non ci sono giocatori nel mondo, invia il messaggio solo al sender
            sender.sendMessage(message);
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        String cmd = command.getName().toLowerCase();
        
        if (args.length == 1) {
            switch (cmd) {
                case "tempo":
                    // Suggerisci alcuni valori comuni
                    completions.addAll(Arrays.asList("0", "1000", "6000", "12000", "13000", "18000", "23000"));
                    break;
                case "giorno":
                case "notte":
                case "alba":
                case "tramonto":
                case "sereno":
                case "pioggia":
                case "temporale":
                case "meteo":
                    // Suggerisci i nomi dei mondi
                    for (World world : Bukkit.getWorlds()) {
                        completions.add(world.getName());
                    }
                    break;
            }
        } else if (args.length == 2 && cmd.equals("tempo")) {
            // Secondo argomento per /tempo: nome del mondo
            for (World world : Bukkit.getWorlds()) {
                completions.add(world.getName());
            }
        }
        
        // Filtra i risultati basandosi su quello che l'utente ha già digitato
        String partial = args[args.length - 1].toLowerCase();
        completions.removeIf(completion -> !completion.toLowerCase().startsWith(partial));
        
        return completions;
    }
} 