package library;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import java.io.File;
import java.io.IOException;
import org.bukkit.GameMode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.World;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.event.block.BlockBreakEvent;
import java.util.Map;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.BlockFace;
import org.bukkit.Location;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.NamespacedKey;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.player.PlayerMoveEvent;
import java.util.stream.Collectors;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.entity.Minecart;
import org.bukkit.util.Vector;
import org.bukkit.block.Sign;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import java.util.Iterator;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Wolf;
import org.bukkit.attribute.Attribute;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.DyeColor;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitTask;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;
import java.util.Arrays;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.EventPriority;
import java.lang.reflect.Method;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
import org.bstats.bukkit.Metrics;

public class Main extends JavaPlugin implements Listener {
    private File nicknamesFile;
    private FileConfiguration nicknames;
    private boolean isCommandWeatherChange = false;
    private boolean isWeatherLocked = false;
    private HashMap<String, UUID> lockedChests = new HashMap<>();
    private File lockedChestsFile;
    private FileConfiguration lockedChestsConfig;
    
    private GUIManager guiManager;
    
    private ConfigManager configManager;
    private CacheManager cacheManager;
    private BackupManager backupManager;
    private ChatManager chatManager;
    
    // Aggiungi questa variabile per il TreeCutter
    private TreeCutter treeCutter;
    
    // Aggiungi anche questa configurazione per il TreeCutter
    private boolean treeChopEnabled = true;
    
    // Aggiungi queste variabili di classe
    private boolean hasTrainAddon = false;
    private boolean hasPosteAddon = false;
    private Plugin trainAddon = null;
    private Plugin posteAddon = null;
    private boolean hasBackpackPlus = false;
    private Plugin backpackPlugin = null;
    
    // Aggiungi queste variabili di classe
    private int ironBackpackSlots = 9;    // Default values
    private int goldBackpackSlots = 18;   // in case we can't
    private int diamondBackpackSlots = 27; // read from config
    
    // Aggiungi queste variabili di classe
    private boolean hasCoins = false;
    private boolean hasIConomy = false;
    private Plugin coinsPlugin = null;
    private Plugin iconomyPlugin = null;
    
    // Aggiungi queste variabili di classe
    private boolean hasSkinRestorer = false;
    private Plugin skinRestorerPlugin = null;
    private File skinsFile;
    private FileConfiguration skinsConfig;
    
    // Aggiungi queste variabili di classe
    private boolean hasSmoothTimber = false;
    private Plugin smoothTimberPlugin = null;
    
    // Aggiungi questa variabile di classe
    private File developerModeFile;
    
    // Aggiungi queste variabili di classe
    private HashMap<UUID, List<ItemReplacement>> replacedItems = new HashMap<>();
    
    // Aggiungi questa classe interna per tenere traccia degli item sostituiti
    private class ItemReplacement {
        private final int slot;
        private final ItemStack originalItem;
        
        public ItemReplacement(int slot, ItemStack originalItem) {
            this.slot = slot;
            this.originalItem = originalItem;
        }
    }
    
    // Variabili per il debug
    private boolean showBlockInfo = false;
    private boolean showEntityInfo = false;
    private boolean noClip = false;
    
    // Aggiungi queste variabili
    private boolean developerMode = false;
    private String originalMotd = "";
    private HashMap<UUID, UUID> lastMessageFrom = new HashMap<>();
    private boolean chatEnabled = true;
    
    // Modifica queste variabili per una gestione più precisa delle richieste di teletrasporto
    private HashMap<UUID, HashMap<UUID, Boolean>> tpRequests = new HashMap<>(); // target -> (requester -> isTpHere)
    private HashMap<UUID, HashMap<UUID, BukkitTask>> tpRequestTasks = new HashMap<>(); // target -> (requester -> task)
    
    private HashMap<UUID, ArmorStand> sittingPlayers = new HashMap<>();
    private HashMap<UUID, UUID> leashedPlayers = new HashMap<>();
    private HashMap<String, List<UUID>> sharedChests = new HashMap<>();
    private File sharedDataFile;
    private FileConfiguration sharedDataConfig;
    
    private HashMap<UUID, Location> lastPosition = new HashMap<>();
    private boolean performanceMonitoring = false;
    private HashMap<String, Long> pluginTimings = new HashMap<>();
    private BukkitTask performanceTask = null;
    
    private MoonRotationEffect moonRotationEffect;
    private FirstJoinAnimation firstJoinAnimation;
    private TeleportAnimation teleportAnimation;
    
    @Override
    public void onEnable() {
        try {
            // Inizializza bStats
            try {
                int pluginId = 25947; // ID del plugin su bStats
                Metrics metrics = new Metrics(this, pluginId);
                getLogger().info("§a[GavaCore] bStats inizializzato con successo!");
            } catch (Exception e) {
                getLogger().warning("§e[GavaCore] Impossibile inizializzare bStats: " + e.getMessage());
            }
            
            // Carica la configurazione
            saveDefaultConfig();
            
            // Inizializza i file di configurazione
            initializeConfigFiles();
            
            // Inizializza i manager
            configManager = new ConfigManager(this);
            cacheManager = new CacheManager(this, configManager);
            backupManager = new BackupManager(this, configManager);
            chatManager = new ChatManager(this, configManager);
            guiManager = new GUIManager(this);
            moonRotationEffect = new MoonRotationEffect(this);
            firstJoinAnimation = new FirstJoinAnimation(this);
            teleportAnimation = new TeleportAnimation(this);
            
            // Registra il listener per gli eventi
            getServer().getPluginManager().registerEvents(this, this);
            getServer().getPluginManager().registerEvents(guiManager, this);
            getServer().getPluginManager().registerEvents(moonRotationEffect, this);
            getServer().getPluginManager().registerEvents(firstJoinAnimation, this);
            
            // Carica le configurazioni esistenti
            loadConfigurations();
            
            // Registra i comandi
            registerCommands();
            
            // Registra le ricette
            registerRecipes();
            
            getLogger().info("§a[GavaCore] Plugin attivato con successo!");
            getLogger().info("Versione: " + getDescription().getVersion());
            
        } catch (Exception e) {
            getLogger().severe("[GavaCore] Errore durante l'inizializzazione del plugin:");
            e.printStackTrace();
        }
    }

    private void initializeConfigFiles() {
        // Inizializza il file dei soprannomi
        nicknamesFile = new File(getDataFolder(), "nicknames.yml");
        if (!nicknamesFile.exists()) {
            try {
                nicknamesFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("[GavaCore] Errore nella creazione del file nicknames.yml");
            }
        }
        nicknames = YamlConfiguration.loadConfiguration(nicknamesFile);
        
        // Inizializza il file delle chest bloccate
        lockedChestsFile = new File(getDataFolder(), "locked_chests.yml");
        if (!lockedChestsFile.exists()) {
            try {
                lockedChestsFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("[GavaCore] Errore nella creazione del file locked_chests.yml");
            }
        }
        lockedChestsConfig = YamlConfiguration.loadConfiguration(lockedChestsFile);
        
        // Inizializza il file delle aree condivise
        sharedDataFile = new File(getDataFolder(), "shared_data.yml");
        if (!sharedDataFile.exists()) {
            try {
                sharedDataFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("[GavaCore] Errore nella creazione del file shared_data.yml");
            }
        }
        sharedDataConfig = YamlConfiguration.loadConfiguration(sharedDataFile);
        
        // Inizializza il file della modalità sviluppatore
        developerModeFile = new File(getDataFolder(), "developer_mode.yml");
        if (!developerModeFile.exists()) {
            try {
                developerModeFile.createNewFile();
                FileConfiguration devConfig = YamlConfiguration.loadConfiguration(developerModeFile);
                devConfig.set("enabled", false);
                devConfig.save(developerModeFile);
            } catch (IOException e) {
                getLogger().severe("[GavaCore] Errore nella creazione del file developer_mode.yml");
            }
        } else {
            FileConfiguration devConfig = YamlConfiguration.loadConfiguration(developerModeFile);
            developerMode = devConfig.getBoolean("enabled", false);
        }
        
        // Inizializza il file delle skin se SkinsRestorer è presente
        skinsFile = new File(getDataFolder(), "skins.yml");
        if (!skinsFile.exists()) {
            try {
                skinsFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("[GavaCore] Errore nella creazione del file skins.yml");
            }
        }
        skinsConfig = YamlConfiguration.loadConfiguration(skinsFile);
    }

    private void loadConfigurations() {
        // Carica le chest bloccate
        if (lockedChestsConfig.contains("chests")) {
            for (String location : lockedChestsConfig.getConfigurationSection("chests").getKeys(false)) {
                String uuidString = lockedChestsConfig.getString("chests." + location);
                lockedChests.put(location, UUID.fromString(uuidString));
            }
        }
        
        // Carica la configurazione per il TreeCutter
        treeChopEnabled = getConfig().getBoolean("features.tree-chopper", true);
        
        // Inizializza TreeCutter
        treeCutter = new TreeCutter(this);
    }

    private void registerCommands() {
        // Registra il comando principale del plugin
        getCommand("gavacore").setExecutor(this);
        
        // Registra il comando per l'animazione
        getCommand("animation").setExecutor(new library.commands.AnimationCommand(this));
        
        // Registra il comando furry
        getCommand("furry").setExecutor(new library.commands.FurryCommand(this));
        
        // Registra il comando per risolvere i bug delle animazioni
        getCommand("resolveanimationbugs").setExecutor(new library.commands.ResolveAnimationBugsCommand(this));
        
        // Registra i comandi del tempo e meteo
        library.commands.TimeCommands timeCommands = new library.commands.TimeCommands(this);
        getCommand("giorno").setExecutor(timeCommands);
        getCommand("notte").setExecutor(timeCommands);
        getCommand("alba").setExecutor(timeCommands);
        getCommand("tramonto").setExecutor(timeCommands);
        getCommand("tempo").setExecutor(timeCommands);
        getCommand("sereno").setExecutor(timeCommands);
        getCommand("pioggia").setExecutor(timeCommands);
        getCommand("temporale").setExecutor(timeCommands);
        getCommand("meteo").setExecutor(timeCommands);
        
        // Registra anche i tab completers per i comandi
        getCommand("giorno").setTabCompleter(timeCommands);
        getCommand("notte").setTabCompleter(timeCommands);
        getCommand("alba").setTabCompleter(timeCommands);
        getCommand("tramonto").setTabCompleter(timeCommands);
        getCommand("tempo").setTabCompleter(timeCommands);
        getCommand("sereno").setTabCompleter(timeCommands);
        getCommand("pioggia").setTabCompleter(timeCommands);
        getCommand("temporale").setTabCompleter(timeCommands);
        getCommand("meteo").setTabCompleter(timeCommands);
        
        // Registra tutti i comandi qui
        getCommand("gmc").setExecutor(this);
        getCommand("gms").setExecutor(this);
        getCommand("gmsp").setExecutor(this);
        getCommand("gma").setExecutor(this);
        
        // Registra il comando craft solo se BackpackPlus è presente
        if (hasBackpackPlus) {
            this.getCommand("craft").setExecutor(new CommandExecutor() {
                @Override
                public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                        return true;
                    }
                    
                    Player player = (Player) sender;
                    
                    // Controlla se il giocatore ha uno zaino
                    boolean hasBackpack = false;
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item != null && item.getType() == Material.BROWN_DYE && item.hasItemMeta() && 
                            item.getItemMeta().hasCustomModelData()) {
                            int modelId = item.getItemMeta().getCustomModelData();
                            // Verifica gli ID specifici dei backpack
                            if (modelId == 800001 || modelId == 800002 || modelId == 800003) {
                                hasBackpack = true;
                                break;
                            }
                        }
                    }
                    
                    if (!hasBackpack) {
                        player.sendMessage("§c§l[GavaCore] §cDevi avere uno zaino nell'inventario per usare questo comando!");
                        return true;
                    }
                    
                    // Apri la crafting table
                    player.openWorkbench(null, true);
                    return true;
                }
            });
        }
        
        // ... altri comandi ...
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String nickname = nicknames.getString(player.getUniqueId().toString());
        
        if (nickname != null) {
            player.setPlayerListName(configManager.getPrimaryColor() + nickname);
            event.setJoinMessage(configManager.getJoinMessage(nickname));
        } else {
            event.setJoinMessage(configManager.getJoinMessage(player.getName()));
        }
        
        // Ripristina la skin se presente
        if (hasSkinRestorer) {
            String skinName = skinsConfig.getString(player.getUniqueId().toString());
            if (skinName != null) {
                // Usa l'API di SkinsRestorer per applicare la skin
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " " + skinName);
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String nickname = nicknames.getString(player.getUniqueId().toString());
        
        if (nickname != null) {
            // Usa solo il soprannome per il messaggio di quit
            event.setQuitMessage(configManager.getQuitMessage(nickname));
        } else {
            event.setQuitMessage(configManager.getQuitMessage(player.getName()));
        }
        
        // Rimuovi la sedia se il giocatore esce
        standUp(player);
    }
    
    @Override
    public void onDisable() {
        try {
            // Salva le configurazioni prima di disabilitare
            saveNicknames();
            saveLockedChests();
            saveSharedData();
            
            // Shutdown dei manager
            if (cacheManager != null) {
                cacheManager.shutdown();
            }
            
            if (backupManager != null) {
                backupManager.shutdown();
            }
            
            if (chatManager != null) {
                chatManager.shutdown();
            }
            
            // Cancella tutte le animazioni attive
            if (firstJoinAnimation != null) {
                firstJoinAnimation.cleanup();
            }
            
            if (teleportAnimation != null) {
                teleportAnimation.cleanup();
            }
            
            // Ferma tutti i task di performance monitoring
            if (performanceTask != null) {
                performanceTask.cancel();
            }
            
            // Alza tutti i giocatori seduti
            for (UUID playerUUID : new HashMap<>(sittingPlayers).keySet()) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    standUp(player);
                }
            }
            
            // Ripristina il MOTD originale se in modalità sviluppatore
            if (developerMode) {
                Bukkit.getServer().setMotd(originalMotd);
            }
            
            getLogger().info("§c[GavaCore] Plugin disabilitato con successo!");
            
        } catch (Exception e) {
            getLogger().severe("[GavaCore] Errore durante la disabilitazione del plugin:");
            e.printStackTrace();
        }
    }
    
    private void saveNickname(Player player, String nickname) {
        nicknames.set(player.getUniqueId().toString(), nickname);
        try {
            nicknames.save(nicknamesFile);
            
            // Imposta il soprannome nella tab list
            player.setPlayerListName("§e" + nickname);
            
            // Esegui il comando /tag per il nametag
            player.performCommand("tag " + player.getName() + " " + nickname);
            
        } catch (IOException e) {
            getLogger().severe("§c[GavaCore] Errore nel salvare il soprannome per " + player.getName());
        }
    }
    
    private void removeNickname(Player player) {
        nicknames.set(player.getUniqueId().toString(), null);
        try {
            nicknames.save(nicknamesFile);
            
            // Fai eseguire il comando /tag remove al giocatore
            player.performCommand("tag " + player.getName() + " remove");
            
        } catch (IOException e) {
            getLogger().severe("§c[GavaCore] Errore nel rimuovere il soprannome per " + player.getName());
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("developer-mode")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere eseguito solo da un player!");
                return true;
            }
            
            Player player = (Player) sender;
            if (!player.hasPermission("gavacore.developer")) {
                player.sendMessage("§c§l[GavaCore] §cNon hai il permesso di usare questo comando!");
                return true;
            }

            // Inverte lo stato della modalità sviluppatore
            developerMode = !developerMode;
            
            // Salva lo stato nel file
            try {
                FileConfiguration devConfig = YamlConfiguration.loadConfiguration(developerModeFile);
                devConfig.set("enabled", developerMode);
                devConfig.save(developerModeFile);
                
                if (developerMode) {
                    // Salva il MOTD originale
                    originalMotd = Bukkit.getMotd();
                    // Imposta il nuovo MOTD
                    Bukkit.setMotd("§c§l✖ SERVER IN MANUTENZIONE ✖\n" +
                                 "§7§o⚡ Modalità sviluppatore attiva §8- §7§oAccesso riservato");
                    
                    // Messaggio di attivazione
                    Bukkit.broadcastMessage("§8§l§m--------------------------------");
                    Bukkit.broadcastMessage("§c§l⚠ MODALITÀ SVILUPPATORE ATTIVA ⚠");
                    Bukkit.broadcastMessage("§7§oIl server è in modalità manutenzione");
                    Bukkit.broadcastMessage("§7§oSolo gli operatori possono accedere");
                    Bukkit.broadcastMessage("§8§l§m--------------------------------");
                } else {
                    // Ripristina il MOTD originale
                    Bukkit.setMotd(originalMotd);
                    
                    // Messaggio di disattivazione
                    Bukkit.broadcastMessage("§8§l§m--------------------------------");
                    Bukkit.broadcastMessage("§a§l✔ MODALITÀ SVILUPPATORE DISATTIVATA");
                    Bukkit.broadcastMessage("§7§oIl server è tornato alla normalità");
                    Bukkit.broadcastMessage("§8§l§m--------------------------------");
                }
                
                return true;
                
            } catch (IOException e) {
                player.sendMessage("§c§l[GavaCore] §cErrore nel salvare lo stato della modalità sviluppatore!");
                e.printStackTrace();
                return false;
            }
        }
        
        if (cmd.getName().equalsIgnoreCase("gavacore")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }
            
            Player player = (Player) sender;
            openMainMenu(player);
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere eseguito solo da un giocatore!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("ciao")) {
            sender.sendMessage("§6§l[GavaCore] §e§lBenvenuto nel server!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("sito")) {
            sender.sendMessage("6§l[GavaCore] §bVisita il nostro sito: §enwww.gavatech.org");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("soprannome")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }
            
            if (args.length != 1) {
                sender.sendMessage("§c§l[GavaCore] §cUso corretto: /soprannome <nuovo_nome>");
                return true;
            }
            
            Player player = (Player) sender;
            String newNickname = args[0];
            
            // Salva il soprannome e imposta il display name
            saveNickname(player, newNickname);
            
            // Usa il nome originale per il messaggio di conferma
            player.sendMessage("§6§l[GavaCore] §aIl tuo soprannome è stato cambiato in: §f" + newNickname);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("ripristina")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }
            
            Player player = (Player) sender;
            String originalName = player.getName();
            
            player.setDisplayName(originalName);
            player.setPlayerListName(originalName);
            player.setCustomName(null);
            player.setCustomNameVisible(false);
            
            // Rimuovi il soprannome salvato
            removeNickname(player);
            
            player.sendMessage("§6§l[GavaCore] aIl tuo nome è stato ripristinato a: §f" + originalName);
            return true;
        }

        // Gestione comandi gamemode
        if (cmd.getName().equalsIgnoreCase("gmc") || 
            cmd.getName().equalsIgnoreCase("gms") || 
            cmd.getName().equalsIgnoreCase("gmsp") || 
            cmd.getName().equalsIgnoreCase("gma")) {
            
            if (!(sender instanceof Player) && args.length == 0) {
                sender.sendMessage("§c§l[GavaCore] §cDalla console devi specificare un giocatore!");
                return true;
            }
            
            Player targetPlayer;
            if (args.length > 0) {
                // Se viene specificato un giocatore
                targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer == null) {
                    sender.sendMessage("§c§l[GavaCore] §cGiocatore non trovato!");
                    return true;
                }
            } else {
                // Se non viene specificato, usa il giocatore che ha eseguito il comando
                targetPlayer = (Player) sender;
            }
            
            // Verifica i permessi
            if (sender instanceof Player && !sender.hasPermission("gavacore.gamemode")) {
                sender.sendMessage("§c§l[GavaCore] §cNon hai il permesso di usare questo comando!");
                return true;
            }
            
            GameMode newGameMode;
            String modeName;
            
            switch (cmd.getName().toLowerCase()) {
                case "gmc":
                    newGameMode = GameMode.CREATIVE;
                    modeName = "creativa";
                    break;
                case "gms":
                    newGameMode = GameMode.SURVIVAL;
                    modeName = "sopravvivenza";
                    break;
                case "gmsp":
                    newGameMode = GameMode.SPECTATOR;
                    modeName = "spettatore";
                    break;
                case "gma":
                    newGameMode = GameMode.ADVENTURE;
                    modeName = "avventura";
                    break;
                default:
                    return false;
            }
            
            targetPlayer.setGameMode(newGameMode);
            
            // Messaggio al giocatore target
            targetPlayer.sendMessage("§6§l[GavaCore] §aModalità di gioco impostata a: §e" + modeName);
            
            // Se il comando è stato eseguito su un altro giocatore, invia un messaggio anche all'esecutore
            if (args.length > 0 && sender != targetPlayer) {
                sender.sendMessage("§6§l[GavaCore] §aModalità di gioco di §e" + targetPlayer.getName() + " §aimpostata a: §e" + modeName);
            }
            
            return true;
        }

        // Gestione comandi meteo
        // VECCHI COMANDI METEO RIMOSSI - sostituiti dai nuovi comandi in italiano (gestiti da TimeCommands)
        // - cielo-sereno -> sereno
        // - tempesta -> temporale  
        // - pioggia -> pioggia
        // - tuoni -> integrato in temporale

        if (cmd.getName().equalsIgnoreCase("blocca")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }
            
            Player player = (Player) sender;
            Block targetBlock = player.getTargetBlock(null, 5);
            
            if (targetBlock == null || (targetBlock.getType() != Material.CHEST && targetBlock.getType() != Material.TRAPPED_CHEST)) {
                player.sendMessage("§c§l[GavaCore] §cDevi guardare una chest per bloccarla!");
                return true;
            }
            
            // Controlla se è una chest doppia e ottieni l'altra metà
            Block otherHalf = null;
            if (targetBlock.getBlockData() instanceof Chest) {
                Chest chest = (Chest) targetBlock.getBlockData();
                if (chest.getType() != Chest.Type.SINGLE) {
                    BlockFace face = chest.getFacing();
                    // Controlla i blocchi adiacenti per trovare l'altra metà
                    Block[] adjacentBlocks = {
                        targetBlock.getRelative(face.getOppositeFace()),
                        targetBlock.getRelative(BlockFace.EAST),
                        targetBlock.getRelative(BlockFace.WEST),
                        targetBlock.getRelative(BlockFace.NORTH),
                        targetBlock.getRelative(BlockFace.SOUTH)
                    };
                    
                    for (Block adjacent : adjacentBlocks) {
                        if (adjacent.getType() == targetBlock.getType() && 
                            adjacent.getBlockData() instanceof Chest) {
                            Chest adjacentChest = (Chest) adjacent.getBlockData();
                            if (adjacentChest.getType() != Chest.Type.SINGLE) {
                                otherHalf = adjacent;
                                break;
                            }
                        }
                    }
                }
            }
            
            // Blocca la chest principale
            String chestLoc = targetBlock.getWorld().getName() + "," + targetBlock.getX() + "," + targetBlock.getY() + "," + targetBlock.getZ();
            
            if (lockedChests.containsKey(chestLoc)) {
                player.sendMessage("§c§l[GavaCore] §cQuesta chest è già bloccata!");
                return true;
            }
            
            // Se è una chest doppia, blocca anche l'altra metà
            if (otherHalf != null) {
                String otherChestLoc = otherHalf.getWorld().getName() + "," + otherHalf.getX() + "," + otherHalf.getY() + "," + otherHalf.getZ();
                if (lockedChests.containsKey(otherChestLoc)) {
                    player.sendMessage("§c§l[GavaCore] §cL'altra metà della chest è già bloccata!");
                    return true;
                }
                lockedChests.put(otherChestLoc, player.getUniqueId());
            }
            
            lockedChests.put(chestLoc, player.getUniqueId());
            saveLockedChests();
            player.sendMessage("§6§l[GavaCore] §aChest bloccata con successo!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("sblocca")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }
            
            Player player = (Player) sender;
            Block targetBlock = player.getTargetBlock(null, 5);
            
            if (targetBlock == null || (targetBlock.getType() != Material.CHEST && targetBlock.getType() != Material.TRAPPED_CHEST)) {
                player.sendMessage("§c§l[GavaCore] §cDevi guardare una chest per sbloccarla!");
                return true;
            }
            
            // Controlla se è una chest doppia e ottieni l'altra metà
            Block otherHalf = null;
            if (targetBlock.getBlockData() instanceof Chest) {
                Chest chest = (Chest) targetBlock.getBlockData();
                if (chest.getType() != Chest.Type.SINGLE) {
                    BlockFace face = chest.getFacing();
                    // Controlla i blocchi adiacenti per trovare l'altra metà
                    Block[] adjacentBlocks = {
                        targetBlock.getRelative(face.getOppositeFace()),
                        targetBlock.getRelative(BlockFace.EAST),
                        targetBlock.getRelative(BlockFace.WEST),
                        targetBlock.getRelative(BlockFace.NORTH),
                        targetBlock.getRelative(BlockFace.SOUTH)
                    };
                    
                    for (Block adjacent : adjacentBlocks) {
                        if (adjacent.getType() == targetBlock.getType() && 
                            adjacent.getBlockData() instanceof Chest) {
                            Chest adjacentChest = (Chest) adjacent.getBlockData();
                            if (adjacentChest.getType() != Chest.Type.SINGLE) {
                                otherHalf = adjacent;
                                break;
                            }
                        }
                    }
                }
            }
            
            String chestLoc = targetBlock.getWorld().getName() + "," + targetBlock.getX() + "," + targetBlock.getY() + "," + targetBlock.getZ();
            
            if (!lockedChests.containsKey(chestLoc)) {
                player.sendMessage("§c§l[GavaCore] cQuesta chest non è bloccata!");
                return true;
            }
            
            if (!lockedChests.get(chestLoc).equals(player.getUniqueId())) {
                player.sendMessage("§cl[GavaCore] §cNon puoi sbloccare una chest che non hai bloccato tu!");
                return true;
            }
            
            // Sblocca entrambe le metà se è una chest doppia
            if (otherHalf != null) {
                String otherChestLoc = otherHalf.getWorld().getName() + "," + otherHalf.getX() + "," + otherHalf.getY() + "," + otherHalf.getZ();
                lockedChests.remove(otherChestLoc);
            }
            
            lockedChests.remove(chestLoc);
            saveLockedChests();
            player.sendMessage("§6§l[GavaCore] §aChest sbloccata con successo!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("monete-fisiche")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }
            
            if (args.length != 1) {
                sender.sendMessage("§c§l[GavaCore] §cUso corretto: /monete-fisiche <importo>");
                return true;
            }
            
            Player player = (Player) sender;
            int amount;
            
            try {
                amount = Integer.parseInt(args[0]);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                player.sendMessage("§c§l[GavaCore] §cL'importo deve essere un numero positivo!");
                return true;
            }
            
            // Aggiungi temporaneamente il permesso
            PermissionAttachment attachment = player.addAttachment(this, "coins.withdraw", true);
            
            // Esegui il comando /withdraw
            Bukkit.dispatchCommand(player, "withdraw " + amount);
            
            // Rimuovi il permesso dopo un tick
            Bukkit.getScheduler().runTaskLater(this, () -> {
                player.removeAttachment(attachment);
            }, 1L);
            
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("soldi")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }
            
            Player player = (Player) sender;
            
            // Esegui il comando /money
            Bukkit.dispatchCommand(player, "money");
            return true;
        }

        // Comando per ottenere la wand di selezione
        if (cmd.getName().equalsIgnoreCase("wand")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }

            Player player = (Player) sender;
            ItemStack wand = new ItemStack(Material.WOODEN_AXE);
            ItemMeta meta = wand.getItemMeta();
            meta.setDisplayName("§6§lWand di Protezione");
            List<String> lore = new ArrayList<>();
            lore.add("§7Usata per proteggere le aree");
            lore.add("§7Click sinistro: Seleziona il primo punto");
            lore.add("§7Click destro: Seleziona il secondo punto");
            meta.setLore(lore);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            wand.setItemMeta(meta);
            player.getInventory().addItem(wand);
            player.sendMessage("§6§l[GavaCore] §aHai ricevuto la wand di protezione!");
            return true;
        }

        // Aggiungi questo comando per rimuovere la condivisione
        if (cmd.getName().equalsIgnoreCase("rimuovi-condivisione")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage("§c§l[GavaCore] §cUso corretto: /rimuovi-condivisione <giocatore>");
                return true;
            }

            Player player = (Player) sender;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            if (target == null || !target.hasPlayedBefore()) {
                player.sendMessage("§c§l[GavaCore] §cGiocatore non trovato!");
                return true;
            }

            Block targetBlock = player.getTargetBlock(null, 5);
            if (targetBlock != null && (targetBlock.getType() == Material.CHEST || targetBlock.getType() == Material.TRAPPED_CHEST)) {
                String chestLoc = targetBlock.getWorld().getName() + "," + targetBlock.getX() + "," + targetBlock.getY() + "," + targetBlock.getZ();
                
                if (!lockedChests.containsKey(chestLoc) || !lockedChests.get(chestLoc).equals(player.getUniqueId())) {
                    player.sendMessage("§c§l[GavaCore] §cNon sei il proprietario di questa chest!");
                    return true;
                }

                List<UUID> sharedWith = sharedChests.get(chestLoc);
                if (sharedWith == null || !sharedWith.contains(target.getUniqueId())) {
                    player.sendMessage("§c§l[GavaCore] §cQuesta chest non è condivisa con questo giocatore!");
                    return true;
                }

                sharedWith.remove(target.getUniqueId());
                if (sharedWith.isEmpty()) {
                    sharedChests.remove(chestLoc);
                }
                saveSharedData();

                player.sendMessage("§6§l[GavaCore] §aHai rimosso la condivisione della chest con §e" + target.getName());
                if (target.isOnline()) {
                    ((Player)target).sendMessage("§6§l[GavaCore] §c" + player.getDisplayName() + " §cha rimosso la condivisione di una chest con te!");
                }
                return true;
            }
            return true;
        }

        // Aggiungi il comando condividi
        if (cmd.getName().equalsIgnoreCase("condividi")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage("§c§l[GavaCore] §cUso corretto: /condividi <giocatore>");
                return true;
            }

            Player player = (Player) sender;
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage("§c§l[GavaCore] §cGiocatore non trovato!");
                return true;
            }

            if (target == player) {
                player.sendMessage("§c§l[GavaCore] §cNon puoi condividere con te stesso!");
                return true;
            }

            // Controlla se il player sta guardando una chest
            Block targetBlock = player.getTargetBlock(null, 5);
            if (targetBlock != null && (targetBlock.getType() == Material.CHEST || targetBlock.getType() == Material.TRAPPED_CHEST)) {
                String chestLoc = targetBlock.getWorld().getName() + "," + targetBlock.getX() + "," + targetBlock.getY() + "," + targetBlock.getZ();
                
                // Controlla se è una chest doppia e ottieni l'altra metà
                Block otherHalf = null;
                if (targetBlock.getBlockData() instanceof Chest) {
                    Chest chest = (Chest) targetBlock.getBlockData();
                    if (chest.getType() != Chest.Type.SINGLE) {
                        BlockFace face = chest.getFacing();
                        Block[] adjacentBlocks = {
                            targetBlock.getRelative(face.getOppositeFace()),
                            targetBlock.getRelative(BlockFace.EAST),
                            targetBlock.getRelative(BlockFace.WEST),
                            targetBlock.getRelative(BlockFace.NORTH),
                            targetBlock.getRelative(BlockFace.SOUTH)
                        };
                        
                        for (Block adjacent : adjacentBlocks) {
                            if (adjacent.getType() == targetBlock.getType() && 
                                adjacent.getBlockData() instanceof Chest) {
                                Chest adjacentChest = (Chest) adjacent.getBlockData();
                                if (adjacentChest.getType() != Chest.Type.SINGLE) {
                                    otherHalf = adjacent;
                                    break;
                                }
                            }
                        }
                    }
                }
                
                // Verifica che sia il proprietario della chest
                if (!lockedChests.containsKey(chestLoc) || !lockedChests.get(chestLoc).equals(player.getUniqueId())) {
                    player.sendMessage("§c[GavaCore] §cNon sei il proprietario di questa chest!");
                    return true;
                }

                // Se è una chest doppia, verifica anche l'altra metà
                if (otherHalf != null) {
                    String otherChestLoc = otherHalf.getWorld().getName() + "," + otherHalf.getX() + "," + otherHalf.getY() + "," + otherHalf.getZ();
                    if (!lockedChests.containsKey(otherChestLoc) || !lockedChests.get(otherChestLoc).equals(player.getUniqueId())) {
                        player.sendMessage("§c[GavaCore] §cNon sei il proprietario di questa chest doppia!");
                        return true;
                    }
                }

                // Aggiungi la condivisione per entrambe le metà
                List<UUID> sharedWith = sharedChests.getOrDefault(chestLoc, new ArrayList<>());
                if (sharedWith.contains(target.getUniqueId())) {
                    player.sendMessage("§c[GavaCore] §cHai già condiviso questa chest con questo giocatore!");
                    return true;
                }

                sharedWith.add(target.getUniqueId());
                sharedChests.put(chestLoc, sharedWith);

                // Se è una chest doppia, condividi anche l'altra metà
                if (otherHalf != null) {
                    String otherChestLoc = otherHalf.getWorld().getName() + "," + otherHalf.getX() + "," + otherHalf.getY() + "," + otherHalf.getZ();
                    List<UUID> otherSharedWith = sharedChests.getOrDefault(otherChestLoc, new ArrayList<>());
                    otherSharedWith.add(target.getUniqueId());
                    sharedChests.put(otherChestLoc, otherSharedWith);
                }

                saveSharedData();

                player.sendMessage("§6§l[GavaCore] §aHai condiviso la chest con §e" + target.getDisplayName());
                target.sendMessage("§6§l[GavaCore] §a" + player.getDisplayName() + " §aha condiviso una chest con te!");
                return true;
            } else {
                player.sendMessage("§c§l[GavaCore] §cDevi guardare una chest per condividerla!");
                return true;
            }
        }

        if (cmd.getName().equalsIgnoreCase("lista-condivisioni")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }

            Player player = (Player) sender;
            Block targetBlock = player.getTargetBlock(null, 5);

            if (targetBlock != null && (targetBlock.getType() == Material.CHEST || targetBlock.getType() == Material.TRAPPED_CHEST)) {
                String chestLoc = targetBlock.getWorld().getName() + "," + targetBlock.getX() + "," + targetBlock.getY() + "," + targetBlock.getZ();
                
                if (!lockedChests.containsKey(chestLoc) || !lockedChests.get(chestLoc).equals(player.getUniqueId())) {
                    player.sendMessage("§c§l[GavaCore] §cNon sei il proprietario di questa chest!");
                    return true;
                }

                List<UUID> sharedWith = sharedChests.getOrDefault(chestLoc, new ArrayList<>());
                if (sharedWith.isEmpty()) {
                    player.sendMessage("§6§l[GavaCore] §eQuesta chest non è condivisa con nessuno");
                    return true;
                }

                player.sendMessage("§6§l[GavaCore] §eQuesta chest è condivisa con:");
                for (UUID uuid : sharedWith) {
                    OfflinePlayer sharedPlayer = Bukkit.getOfflinePlayer(uuid);
                    String name = sharedPlayer.isOnline() ? ((Player)sharedPlayer).getDisplayName() : sharedPlayer.getName();
                    player.sendMessage("§e- " + name);
                }
                return true;
            }

            // Se non sta guardando una chest, mostra messaggio di errore
            player.sendMessage("§c§l[GavaCore] §cDevi guardare una tua chest bloccata per vedere le condivisioni!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("sedia") || cmd.getName().equalsIgnoreCase("sit")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }

            Player player = (Player) sender;

            // Se il giocatore è già seduto, fallo alzare
            if (sittingPlayers.containsKey(player.getUniqueId())) {
                standUp(player);
                return true;
            }

            Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
            BlockData blockData = block.getBlockData();

            // Crea l'ArmorStand invisibile con altezza variabile
            Location seatLoc = player.getLocation();
            
            // Imposta l'altezza in base al tipo di blocco
            if (blockData instanceof Slab || blockData instanceof Stairs) {
                seatLoc.setY(Math.floor(seatLoc.getY()) + 1.2); // Altezza maggiore per slab e scale
            } else {
                seatLoc.setY(Math.floor(seatLoc.getY()) + 0.3); // Altezza minore per blocchi normali
            }
            
            ArmorStand chair = (ArmorStand) player.getWorld().spawnEntity(
                seatLoc,
                EntityType.ARMOR_STAND
            );
            
            // Configura l'ArmorStand
            chair.setVisible(false);
            chair.setGravity(false);
            chair.setInvulnerable(true);
            chair.setSmall(true);
            chair.setMarker(true);
            
            // Fai sedere il giocatore
            chair.addPassenger(player);
            sittingPlayers.put(player.getUniqueId(), chair);
            
            player.sendMessage("§6§l[GavaCore] §aOra sei seduto! Usa SHIFT per alzarti");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("libera")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere eseguito solo da un giocatore!");
                return true;
            }

            Player player = (Player) sender;
            player.sendMessage("§c§l[GavaCore] §cQuesta funzionalità è stata disabilitata.");
            return true;
        }

        // Comando /msg
        if (cmd.getName().equalsIgnoreCase("msg")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage("§c§l[GavaCore] §cUso corretto: /msg <giocatore> <messaggio>");
                return true;
            }

            Player player = (Player) sender;
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage("§c§l[GavaCore] §cGiocatore non trovato!");
                return true;
            }

            if (target == player) {
                player.sendMessage("§c§l[GavaCore] §cNon puoi mandare messaggi a te stesso!");
                return true;
            }

            // Costruisci il messaggio
            StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                message.append(args[i]).append(" ");
            }

            // Salva l'ultimo mittente per il comando /r
            lastMessageFrom.put(target.getUniqueId(), player.getUniqueId());

            // Invia i messaggi
            player.sendMessage("§d§lA §f" + target.getDisplayName() + "§d§l: §f" + message);
            target.sendMessage("§d§lDA §f" + player.getDisplayName() + "§d§l: §f" + message);
            
            return true;
        }

        // Comando /r
        if (cmd.getName().equalsIgnoreCase("r")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }

            if (args.length < 1) {
                sender.sendMessage("§c§l[GavaCore] §cUso corretto: /r <messaggio>");
                return true;
            }

            Player player = (Player) sender;
            UUID lastSenderUUID = lastMessageFrom.get(player.getUniqueId());

            if (lastSenderUUID == null) {
                player.sendMessage("§c§l[GavaCore] §cNon hai nessuno a cui rispondere!");
                return true;
            }

            Player target = Bukkit.getPlayer(lastSenderUUID);
            if (target == null) {
                player.sendMessage("§c§l[GavaCore] §cIl giocatore non è più online!");
                return true;
            }

            // Costruisci il messaggio
            StringBuilder message = new StringBuilder();
            for (String arg : args) {
                message.append(arg).append(" ");
            }

            // Aggiorna l'ultimo mittente
            lastMessageFrom.put(target.getUniqueId(), player.getUniqueId());

            // Invia i messaggi
            player.sendMessage("§d§lA §f" + target.getDisplayName() + "§d§l: §f" + message);
            target.sendMessage("§d§lDA §f" + player.getDisplayName() + "§d§l: §f" + message);
            
            return true;
        }

        // Comando /chat
        if (cmd.getName().equalsIgnoreCase("chat")) {
            if (!sender.hasPermission("gavacore.chat")) {
                sender.sendMessage("§c§l[GavaCore] §cNon hai il permesso di usare questo comando!");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage("§c§l[GavaCore] §cUso corretto: /chat <on/off>");
                return true;
            }

            if (args[0].equalsIgnoreCase("on")) {
                chatEnabled = true;
                Bukkit.broadcastMessage("§6§l[GavaCore] §aLa chat è stata §2attivata §ada §e" + sender.getName());
            } else if (args[0].equalsIgnoreCase("off")) {
                chatEnabled = false;
                Bukkit.broadcastMessage("§6§l[GavaCore] §cLa chat è stata §4disattivata §cda §e" + sender.getName());
            } else {
                sender.sendMessage("§c§l[GavaCore] §cUso corretto: /chat <on/off>");
            }
            return true;
        }

        // Comando /puliscichat
        if (cmd.getName().equalsIgnoreCase("puliscichat")) {
            if (!sender.hasPermission("gavacore.chat")) {
                sender.sendMessage("§c§l[GavaCore] §cNon hai il permesso di usare questo comando!");
                return true;
            }

            // Invia 100 righe vuote
            for (Player p : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < 100; i++) {
                    p.sendMessage("");
                }
            }
            
            Bukkit.broadcastMessage("§6§l[GavaCore] §aLa chat è stata pulita da §e" + sender.getName());
            return true;
        }

        // Comando /tp
        if (cmd.getName().equalsIgnoreCase("tp")) {
            // Caso base: TP normale per non-operatori
            if (args.length == 1 && sender instanceof Player && !sender.isOp()) {
                Player player = (Player) sender;
                String targetName = args[0];

                // Cerca prima per nome esatto
                Player target = Bukkit.getPlayer(targetName);
                
                // Se non trovato, cerca per soprannome
                if (target == null) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        String nickname = getNickname(onlinePlayer.getUniqueId());
                        if (nickname != null && nickname.equalsIgnoreCase(targetName)) {
                            target = onlinePlayer;
                            break;
                        }
                    }
                }

                if (target == null) {
                    player.sendMessage("§c§l[GavaCore] §cGiocatore non trovato!");
                    return true;
                }

                handleTeleportCommand(player, target.getName());
                return true;
            }
            
            // Da qui in poi, solo gli operatori possono usare questi comandi
            if (!sender.isOp()) {
                sender.sendMessage("§c§l[GavaCore] §cNon hai il permesso di usare questo comando!");
                return true;
            }
            
            // Caso 1: /tp <player> - Operatore teleporta se stesso a un giocatore
            if (args.length == 1 && sender instanceof Player) {
                Player player = (Player) sender;
                Player target = Bukkit.getPlayer(args[0]);
                
                if (target == null) {
                    player.sendMessage("§c§l[GavaCore] §cGiocatore non trovato!");
                    return true;
                }
                
                player.teleport(target.getLocation());
                player.sendMessage("§6§l[GavaCore] §aTeletrasportato a §e" + target.getName());
                return true;
            }
            
            // Caso 2: /tp <x> <y> <z> - Operatore teleporta se stesso a coordinate
            if (args.length == 3 && sender instanceof Player) {
                Player player = (Player) sender;
                try {
                    double x = Double.parseDouble(args[0]);
                    double y = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);
                    
                    Location loc = new Location(player.getWorld(), x, y, z);
                    player.teleport(loc);
                    player.sendMessage(String.format("§6§l[GavaCore] §aTeletrasportato alle coordinate §e%.1f %.1f %.1f", x, y, z));
                    return true;
                } catch (NumberFormatException e) {
                    player.sendMessage("§c§l[GavaCore] §cCoordinate non valide!");
                    return true;
                }
            }
            
            // Caso 3: /tp <player> <target> - Teleporta un giocatore a un altro
            if (args.length == 2) {
                Player playerToTeleport = Bukkit.getPlayer(args[0]);
                if (playerToTeleport == null) {
                    sender.sendMessage("§c§l[GavaCore] §cGiocatore da teletrasportare non trovato!");
                    return true;
                }
                
                Player targetPlayer = Bukkit.getPlayer(args[1]);
                if (targetPlayer == null) {
                    sender.sendMessage("§c§l[GavaCore] §cGiocatore destinazione non trovato!");
                    return true;
                }
                
                playerToTeleport.teleport(targetPlayer.getLocation());
                playerToTeleport.sendMessage("§6§l[GavaCore] §aSei stato teletrasportato a §e" + targetPlayer.getName());
                sender.sendMessage("§6§l[GavaCore] §aHai teletrasportato §e" + playerToTeleport.getName() + " §da §e" + targetPlayer.getName());
                return true;
            }
            
            // Caso 4: /tp <player> <x> <y> <z> - Teleporta un giocatore a coordinate
            if (args.length == 4) {
                Player playerToTeleport = Bukkit.getPlayer(args[0]);
                if (playerToTeleport == null) {
                    sender.sendMessage("§c§l[GavaCore] §cGiocatore da teletrasportare non trovato!");
                    return true;
                }
                
                try {
                    double x = Double.parseDouble(args[1]);
                    double y = Double.parseDouble(args[2]);
                    double z = Double.parseDouble(args[3]);
                    
                    Location loc = new Location(playerToTeleport.getWorld(), x, y, z);
                    playerToTeleport.teleport(loc);
                    
                    playerToTeleport.sendMessage(String.format("§6§l[GavaCore] §aSei stato teletrasportato alle coordinate §e%.1f %.1f %.1f", x, y, z));
                    sender.sendMessage(String.format("§6§l[GavaCore] §aHai teletrasportato §e%s §aalle coordinate §e%.1f %.1f %.1f", 
                                      playerToTeleport.getName(), x, y, z));
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c§l[GavaCore] §cCoordinate non valide!");
                    return true;
                }
            }
            
            // Se arriva qui, formato non riconosciuto
            sender.sendMessage("§c§l[GavaCore] §cUso: /tp <giocatore>");
            sender.sendMessage("§c§l[GavaCore] §cOppure (solo per operatori):");
            sender.sendMessage("§c§l[GavaCore] §c/tp <x> <y> <z>");
            sender.sendMessage("§c§l[GavaCore] §c/tp <giocatore> <giocatore_destinazione>");
            sender.sendMessage("§c§l[GavaCore] §c/tp <giocatore> <x> <y> <z>");
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("tpqui")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage("§c§l[GavaCore] §cUso: /tpqui <giocatore>");
                return true;
            }

            Player player = (Player) sender;
            String targetName = args[0];

            // Cerca prima per nome esatto
            Player target = Bukkit.getPlayer(targetName);
            
            // Se non trovato, cerca per soprannome
            if (target == null) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    String nickname = getNickname(onlinePlayer.getUniqueId());
                    if (nickname != null && nickname.equalsIgnoreCase(targetName)) {
                        target = onlinePlayer;
                        break;
                    }
                }
            }

            if (target == null) {
                player.sendMessage("§c§l[GavaCore] §cGiocatore non trovato!");
                return true;
            }

            handleTeleportHereCommand(player, target.getName());
            return true;
        }

        // Comando /tpaccept
        if (cmd.getName().equalsIgnoreCase("tpaccept")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage("§c§l[GavaCore] §cUso corretto: /tpaccept <giocatore>");
                return true;
            }

            Player player = (Player) sender;
            Player requester = Bukkit.getPlayer(args[0]);

            if (requester == null) {
                player.sendMessage("§c§l[GavaCore] §cGiocatore non trovato!");
                return true;
            }

            HashMap<UUID, Boolean> playerRequests = tpRequests.get(player.getUniqueId());
            if (playerRequests == null || !playerRequests.containsKey(requester.getUniqueId())) {
                player.sendMessage("§c§l[GavaCore] §cNon hai richieste di teletrasporto da questo giocatore!");
                return true;
            }

            // Verifica se la richiesta è di tipo "/tpqui" o "/tp"
            boolean isTpHere = playerRequests.get(requester.getUniqueId());

            // Cancella il task di scadenza
            HashMap<UUID, BukkitTask> requestTasks = tpRequestTasks.get(player.getUniqueId());
            if (requestTasks != null) {
                BukkitTask task = requestTasks.remove(requester.getUniqueId());
                if (task != null) task.cancel();
                
                // Rimuovi la mappa interna se è vuota
                if (requestTasks.isEmpty()) {
                    tpRequestTasks.remove(player.getUniqueId());
                }
            }

            // Rimuovi la richiesta
            playerRequests.remove(requester.getUniqueId());
            if (playerRequests.isEmpty()) {
                tpRequests.remove(player.getUniqueId());
            }

            if (isTpHere) {
                // Per /tpqui, teleporta il target al richiedente con animazione
                player.sendMessage("§6§l[GavaCore] §aIn teletrasporto verso §e" + requester.getDisplayName());
                requester.sendMessage("§6§l[GavaCore] §aTeletrasporto di §e" + player.getDisplayName() + " §ain corso...");
                
                // Usa l'animazione di teletrasporto
                teleportAnimation.startTeleportAnimation(player, requester.getLocation());
            } else {
                // Per /tp, teleporta il richiedente al target con animazione
                requester.sendMessage("§6§l[GavaCore] §aIn teletrasporto verso §e" + player.getDisplayName());
                player.sendMessage("§6§l[GavaCore] §aTeletrasporto di §e" + requester.getDisplayName() + " §ain corso...");
                
                // Usa l'animazione di teletrasporto
                teleportAnimation.startTeleportAnimation(requester, player.getLocation());
            }
            return true;
        }

        // Comando /tpdeny
        if (cmd.getName().equalsIgnoreCase("tpdeny")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage("§c§l[GavaCore] §cUso corretto: /tpdeny <giocatore>");
                return true;
            }

            Player player = (Player) sender;
            Player requester = Bukkit.getPlayer(args[0]);

            if (requester == null) {
                player.sendMessage("§c§l[GavaCore] §cGiocatore non trovato!");
                return true;
            }

            HashMap<UUID, Boolean> playerRequests = tpRequests.get(player.getUniqueId());
            if (playerRequests == null || !playerRequests.containsKey(requester.getUniqueId())) {
                player.sendMessage("§c§l[GavaCore] §cNon hai richieste di teletrasporto da questo giocatore!");
                return true;
            }

            // Cancella il task di scadenza
            HashMap<UUID, BukkitTask> requestTasks = tpRequestTasks.get(player.getUniqueId());
            if (requestTasks != null) {
                BukkitTask task = requestTasks.remove(requester.getUniqueId());
                if (task != null) task.cancel();
                
                // Rimuovi la mappa interna se è vuota
                if (requestTasks.isEmpty()) {
                    tpRequestTasks.remove(player.getUniqueId());
                }
            }

            // Rimuovi la richiesta
            playerRequests.remove(requester.getUniqueId());
            if (playerRequests.isEmpty()) {
                tpRequests.remove(player.getUniqueId());
            }

            // Invia i messaggi
            requester.sendMessage("§c§l[GavaCore] §c" + player.getDisplayName() + " §cha rifiutato la tua richiesta di teletrasporto!");
            player.sendMessage("§c§l[GavaCore] §cHai rifiutato la richiesta di teletrasporto di §e" + requester.getDisplayName());
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("gavacore")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }
            
            Player player = (Player) sender;
            openMainMenu(player);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("backpacks") || 
            cmd.getName().equalsIgnoreCase("zaini") || 
            cmd.getName().equalsIgnoreCase("bp")) {
            
            if (!hasBackpackPlus) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando richiede il plugin BackpackPlus!");
                return true;
            }
            
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }

            Player player = (Player) sender;
            openBackpackMenu(player);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("craft")) {
            if (!hasBackpackPlus) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando richiede il plugin BackpackPlus!");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }
            
            Player player = (Player) sender;
            
            // Controlla se il giocatore ha uno zaino
            boolean hasBackpack = false;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == Material.BROWN_DYE && item.hasItemMeta() && 
                    item.getItemMeta().hasCustomModelData()) {
                    int modelId = item.getItemMeta().getCustomModelData();
                    // Verifica gli ID specifici dei backpack
                    if (modelId == 800001 || modelId == 800002 || modelId == 800003) {
                        hasBackpack = true;
                        break;
                    }
                }
            }
            
            if (!hasBackpack) {
                player.sendMessage("§c§l[GavaCore] §cDevi avere uno zaino nell'inventario per usare questo comando!");
                return true;
            }
            
            // Apri la crafting table
            player.openWorkbench(null, true);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("dev")) {
            if (!developerMode || !sender.isOp()) {
                sender.sendMessage("§c§l[GavaCore] §cComando disponibile solo in modalità sviluppatore!");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere usato solo da un giocatore!");
                return true;
            }

            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage("§8§l§m--------------------------------");
                player.sendMessage("§6§l[Dev] §eComandi disponibili:");
                player.sendMessage("§7• §f/dev blockinfo §7- Mostra info sui blocchi");
                player.sendMessage("§7• §f/dev entityinfo §7- Mostra info sulle entità");
                player.sendMessage("§7• §f/dev noclip §7- Attiva/disattiva noclip");
                player.sendMessage("§7• §f/dev speed <1-10> §7- Imposta velocità di volo");
                player.sendMessage("§7• §f/dev mark §7- Salva posizione corrente");
                player.sendMessage("§7• §f/dev back §7- Torna alla posizione salvata");
                player.sendMessage("§7• §f/dev performance §7- Monitora performance");
                player.sendMessage("§7• §f/dev debug <plugin> §7- Info plugin");
                player.sendMessage("§8§l§m--------------------------------");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "blockinfo":
                    showBlockInfo = !showBlockInfo;
                    player.sendMessage("§6§l[Dev] §eInfo blocchi: " + (showBlockInfo ? "§aAttivato" : "§cDisattivato"));
                    break;

                case "entityinfo":
                    showEntityInfo = !showEntityInfo;
                    player.sendMessage("§6§l[Dev] §eInfo entità: " + (showEntityInfo ? "§aAttivato" : "§cDisattivato"));
                    break;

                case "noclip":
                    noClip = !noClip;
                    player.setGameMode(noClip ? GameMode.SPECTATOR : GameMode.CREATIVE);
                    player.sendMessage("§6§l[Dev] §eNoClip: " + (noClip ? "§aAttivato" : "§cDisattivato"));
                    break;

                case "speed":
                    if (args.length != 2) {
                        player.sendMessage("§c§l[Dev] §cUso: /dev speed <1-10>");
                        return true;
                    }
                    try {
                        float speed = Math.min(10, Math.max(1, Float.parseFloat(args[1])));
                        player.setFlySpeed(speed * 0.1f);
                        player.sendMessage("§6§l[Dev] §eVelocità impostata a: §f" + speed);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§c§l[Dev] §cValore non valido!");
                    }
                    break;

                case "mark":
                    lastPosition.put(player.getUniqueId(), player.getLocation());
                    player.sendMessage("§6§l[Dev] §ePosizione salvata!");
                    break;

                case "back":
                    Location lastPos = lastPosition.get(player.getUniqueId());
                    if (lastPos != null) {
                        player.teleport(lastPos);
                        player.sendMessage("§6§l[Dev] §eTeletrasportato alla posizione salvata!");
                    } else {
                        player.sendMessage("§c§l[Dev] §cNessuna posizione salvata!");
                    }
                    break;

                case "performance":
                    performanceMonitoring = !performanceMonitoring;
                    if (performanceMonitoring) {
                        startPerformanceMonitoring();
                        player.sendMessage("§6§l[Dev] §eMonitoraggio performance attivato");
                    } else {
                        if (performanceTask != null) {
                            performanceTask.cancel();
                            performanceTask = null;
                        }
                        player.sendMessage("§6§l[Dev] §cMonitoraggio performance disattivato");
                    }
                    break;

                case "debug":
                    if (args.length < 2) {
                        player.sendMessage("§c§l[Dev] §cUso: /dev debug <plugin>");
                        return true;
                    }
                    Plugin targetPlugin = Bukkit.getPluginManager().getPlugin(args[1]);
                    if (targetPlugin == null) {
                        player.sendMessage("§c§l[Dev] §cPlugin non trovato!");
                        return true;
                    }
                    player.sendMessage("§8§l§m--------------------------------");
                    player.sendMessage("§6§l[Dev] §eInfo Plugin " + targetPlugin.getName() + ":");
                    player.sendMessage("§7• Versione: §f" + targetPlugin.getDescription().getVersion());
                    player.sendMessage("§7• Autori: §f" + String.join(", ", targetPlugin.getDescription().getAuthors()));
                    player.sendMessage("§7• Dipendenze: §f" + String.join(", ", targetPlugin.getDescription().getDepend()));
                    player.sendMessage("§7• Comandi: §f" + String.join(", ", targetPlugin.getDescription().getCommands().keySet()));
                    player.sendMessage("§8§l§m--------------------------------");
                    break;

                default:
                    player.sendMessage("§c§l[Dev] §cComando non riconosciuto! Usa /dev per la lista dei comandi.");
                    break;
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("cane")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c§l[GavaCore] §cQuesto comando può essere eseguito solo da un giocatore!");
                return true;
            }

            Player player = (Player) sender;
            player.sendMessage("§c§l[GavaCore] §cQuesta funzionalità è stata disabilitata.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("resolveanimationbugs")) {
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
            if (teleportAnimation != null) {
                teleportAnimation.cancelAnimation(targetPlayer);
                fixedSomething = true;
            }
            
            // 2. Risolvi le animazioni di primo accesso bloccate
            if (firstJoinAnimation != null) {
                if (firstJoinAnimation.checkAndRestoreInterruptedAnimation(targetPlayer)) {
                    fixedSomething = true;
                }
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
            List<PotionEffectType> movementEffects = Arrays.asList(
                PotionEffectType.SLOW,
                PotionEffectType.BLINDNESS,
                PotionEffectType.JUMP,
                PotionEffectType.LEVITATION,
                PotionEffectType.CONFUSION
            );
            
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

        return false;
    }

    private void registerRecipes() {
        // Crea l'item del panino
        ItemStack hamburger = new ItemStack(Material.BEEF);
        ItemMeta hamburgerMeta = hamburger.getItemMeta();
        hamburgerMeta.setDisplayName("§6§lPanino");
        hamburgerMeta.setCustomModelData(800009);
        List<String> hamburgerLore = new ArrayList<>();
        hamburgerLore.add("§7Un delizioso panino");
        hamburgerLore.add("§7con carne di manzo");
        hamburgerMeta.setLore(hamburgerLore);
        hamburger.setItemMeta(hamburgerMeta);

        ShapedRecipe hamburgerRecipe = new ShapedRecipe(new NamespacedKey(this, "hamburger"), hamburger);
        hamburgerRecipe.shape("B", "C", "B");
        hamburgerRecipe.setIngredient('B', Material.BREAD);
        hamburgerRecipe.setIngredient('C', Material.COOKED_BEEF);
        Bukkit.addRecipe(hamburgerRecipe);

        // Crea l'item della Coca Cola
        ItemStack cocaCola = new ItemStack(Material.POTION);
        ItemMeta cocaColaMeta = cocaCola.getItemMeta();
        cocaColaMeta.setDisplayName("§6§lCoca Cola");
        cocaColaMeta.setCustomModelData(800010);
        List<String> cocaColaLore = new ArrayList<>();
        cocaColaLore.add("§7Una bevanda fresca e gassata");
        cocaColaLore.add("§7Effetti:");
        cocaColaLore.add("§b- Respirazione subacquea (60s)");
        cocaColaLore.add("§b- Visione notturna (90s)");
        cocaColaLore.add("§b- +3 Fame");
        cocaColaMeta.setLore(cocaColaLore);
        cocaCola.setItemMeta(cocaColaMeta);

        ShapedRecipe cocaColaRecipe = new ShapedRecipe(new NamespacedKey(this, "coca_cola"), cocaCola);
        cocaColaRecipe.shape("S", "W", "B");
        cocaColaRecipe.setIngredient('S', Material.SUGAR);
        cocaColaRecipe.setIngredient('W', Material.POTION);
        cocaColaRecipe.setIngredient('B', Material.BLACK_DYE);
        Bukkit.addRecipe(cocaColaRecipe);

        // Crea l'item delle patatine fritte
        ItemStack patatineFritte = new ItemStack(Material.POTATO);
        ItemMeta patatineMeta = patatineFritte.getItemMeta();
        patatineMeta.setDisplayName("§6§lPatatine Fritte");
        patatineMeta.setCustomModelData(800014);
        List<String> patatineLore = new ArrayList<>();
        patatineLore.add("§7Croccanti patatine fritte");
        patatineLore.add("§7Effetti:");
        patatineLore.add("§b- Velocità di scavo (60s)");
        patatineLore.add("§b- +4 Fame");
        patatineMeta.setLore(patatineLore);
        patatineFritte.setItemMeta(patatineMeta);

        ShapedRecipe patatineRecipe = new ShapedRecipe(new NamespacedKey(this, "patatine_fritte"), patatineFritte);
        patatineRecipe.shape("P", "S", "C");
        patatineRecipe.setIngredient('P', Material.BAKED_POTATO);
        patatineRecipe.setIngredient('S', new RecipeChoice.MaterialChoice(
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD
        ));
        patatineRecipe.setIngredient('C', Material.PAPER);
        Bukkit.addRecipe(patatineRecipe);

        // Crea l'item della sedia
        ItemStack chair = new ItemStack(Material.OAK_STAIRS);
        ItemMeta chairMeta = chair.getItemMeta();
        chairMeta.setDisplayName("§6§lSedia");
        chairMeta.setCustomModelData(800015);
        List<String> chairLore = new ArrayList<>();
        chairLore.add("§7Una comoda sedia");
        chairLore.add("§7Click destro per sederti");
        chairLore.add("§7Shift per alzarti");
        chairMeta.setLore(chairLore);
        chair.setItemMeta(chairMeta);

        ShapelessRecipe chairRecipe = new ShapelessRecipe(new NamespacedKey(this, "custom_chair"), chair);
        chairRecipe.addIngredient(Material.OAK_STAIRS);
        Bukkit.addRecipe(chairRecipe);

        // Crea il guinzaglio magico
        ItemStack magicLeash = new ItemStack(Material.LEAD);
        ItemMeta leashMeta = magicLeash.getItemMeta();
        leashMeta.setDisplayName("§d§lGuinzaglio Magico");
        List<String> leashLore = new ArrayList<>();
        leashLore.add("§7Usa questo guinzaglio per");
        leashLore.add("§7trasformare i giocatori in cani");
        leashLore.add("§c§lNon può essere distrutto");
        leashMeta.setLore(leashLore);
        leashMeta.addEnchant(Enchantment.DURABILITY, 10, true);
        leashMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        leashMeta.setUnbreakable(true);
        magicLeash.setItemMeta(leashMeta);

        // Ricetta del guinzaglio magico
        ShapedRecipe leashRecipe = new ShapedRecipe(new NamespacedKey(this, "magic_leash"), magicLeash);
        leashRecipe.shape("GDG", "DLD", "GDG");
        leashRecipe.setIngredient('G', Material.GOLD_INGOT);
        leashRecipe.setIngredient('D', Material.DIAMOND);
        leashRecipe.setIngredient('L', Material.LEAD);
        Bukkit.addRecipe(leashRecipe);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (!configManager.isFeatureEnabled("bed-sleep")) return;
        if (event.getBedEnterResult() != BedEnterResult.OK) return;
        
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        
        String nickname = nicknames.getString(player.getUniqueId().toString());
        String playerName = nickname != null ? nickname : player.getName();
        
        // Controlla che sia effettivamente notte (da 13000 a 23000 ticks)
        long time = world.getTime();
        if (time < 13000 || time > 23000) {
            player.sendMessage("§c§l[GavaCore] §cPuoi dormire solo di notte!");
            event.setCancelled(true);
            return;
        }
        
        // Invia il messaggio a tutti i giocatori nel mondo
        for (Player p : world.getPlayers()) {
            p.sendMessage(configManager.getSleepMessage(playerName));
        }
        
        // Controlla quanti giocatori stanno dormendo rispetto al totale
        int playersInWorld = world.getPlayers().size();
        int sleepingPlayers = 0;
        
        for (Player p : world.getPlayers()) {
            if (p.isSleeping()) {
                sleepingPlayers++;
            }
        }
        
        // Se almeno il 30% dei giocatori sta dormendo, passa alla mattina
        double sleepRatio = (double) sleepingPlayers / playersInWorld;
        if (sleepRatio >= 0.3) {
            // Usa un delay per permettere l'animazione del sonno
            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (player.isSleeping()) {
                    world.setTime(1000); // Imposta l'ora all'alba
                    world.setStorm(false); // Rimuovi la pioggia
                    world.setThundering(false); // Rimuovi i tuoni
                    
                    // Annuncia a tutti i giocatori
                    for (Player p : world.getPlayers()) {
                        p.sendMessage(configManager.getWakeMessage(playerName));
                    }
                }
            }, getConfig().getInt("timings.sleep-delay", 2) * 20L); // Converti secondi in ticks
        }
    }
    
    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        
        // Avvisa i giocatori che qualcuno ha smesso di dormire
        for (Player p : world.getPlayers()) {
            p.sendMessage("§6§l[GavaCore] §e" + player.getDisplayName() + " §7ha smesso di dormire.");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event) {
        // Se il tempo sta cambiando in pioggia
        if (event.toWeatherState()) {
            // Controlla se il tempo è bloccato
            if (isWeatherLocked) {
                event.setCancelled(true);
                return;
            }
            
            // Se non è un cambiamento da comando, invia il messaggio
            if (!isCommandWeatherChange) {
                for (Player p : event.getWorld().getPlayers()) {
                    p.sendMessage("§6§l[GavaCore] §aHa iniziato a piovere!");
                }
            }
        } else {
            // Se il tempo sta tornando sereno
            if (!isCommandWeatherChange) {
                for (Player p : event.getWorld().getPlayers()) {
                    p.sendMessage("§6§l[GavaCore] §aIl cielo si è schiarito!");
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onThunderChange(ThunderChangeEvent event) {
        // Se i tuoni stanno iniziando
        if (event.toThunderState()) {
            // Controlla se il tempo è bloccato
            if (isWeatherLocked) {
                event.setCancelled(true);
                return;
            }
            
            // Invia il messaggio a tutti i giocatori
            for (Player p : event.getWorld().getPlayers()) {
                p.sendMessage("§6§l[GavaCore] §aÈ iniziato un temporale!");
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        // Controlla se è un guinzaglio magico
        if (item != null && item.getType() == Material.LEAD && 
            item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
            item.getItemMeta().getDisplayName().equals("§d§lGuinzaglio Magico")) {
            
            // Se il click è su un player
            if (event.getRightClicked() instanceof Player) {
                player.sendMessage("§c§l[GavaCore] §cQuesta funzionalità è stata disabilitata.");
                
                // Cancella l'evento per evitare comportamenti indesiderati
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        // Codice esistente per la protezione dei blocchi
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        // Verifica se è una chest bloccata
        if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
            String location = block.getLocation().getWorld().getName() + "," + 
                              block.getLocation().getBlockX() + "," + 
                              block.getLocation().getBlockY() + "," + 
                              block.getLocation().getBlockZ();
            
            if (lockedChests.containsKey(location) && !lockedChests.get(location).equals(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(configManager.getErrorPrefix() + " §cQuesta chest è bloccata da un altro giocatore!");
                return;
            }
        }
        
        // Nuova funzionalità di taglio degli alberi
        if (treeChopEnabled && player.isSneaking()) {
            if (treeCutter.tryChopTree(player, block)) {
                // Se l'albero viene gestito dal TreeCutter, annulla l'evento di rottura del blocco originale
                event.setCancelled(true);
            }
        }
    }
    
    // Aggiungi anche questo evento per prevenire il danno ai blocchi
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        // Gestione TreeCutter: impedisce la rottura normale per gestire il taglio dell'albero intero
        if ((block.getType().name().contains("_LOG") || block.getType().name().contains("_WOOD")) && 
            treeChopEnabled && treeCutter != null) {
            // Il TreeCutter gestirà questo evento
            return;
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Evento rimosso - non più necessario per la protezione aree
    }
    
    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        // Evento rimosso - non più necessario per la protezione aree
    }
    
    private boolean isProtected(Location loc, Player player) {
        // Funzione rimossa - non più utilizzata
        return false;
    }
    
    private boolean isInArea(Location loc, String areaStr) {
        // Funzione rimossa - non più utilizzata
        return false;
    }
    
    private void saveLockedChests() {
        lockedChestsConfig.set("chests", null);
        for (Map.Entry<String, UUID> entry : lockedChests.entrySet()) {
            lockedChestsConfig.set("chests." + entry.getKey(), entry.getValue().toString());
        }
        
        try {
            lockedChestsConfig.save(lockedChestsFile);
        } catch (IOException e) {
            getLogger().severe("§c[GavaCore] Errore nel salvare le chest bloccate!");
        }
    }
    
    // Aggiungi questo evento per gestire l'interazione con le chest
    @EventHandler
    public void onChestInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        Block block = event.getClickedBlock();
        if (block == null || (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST)) return;
        
        String chestLoc = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();
        
        if (lockedChests.containsKey(chestLoc)) {
            Player player = event.getPlayer();
            UUID owner = lockedChests.get(chestLoc);
            
            if (!owner.equals(player.getUniqueId())) {
                List<UUID> sharedWith = sharedChests.get(chestLoc);
                if (sharedWith == null || !sharedWith.contains(player.getUniqueId())) {
                    event.setCancelled(true);
                    Player ownerPlayer = Bukkit.getPlayer(owner);
                    String ownerName = ownerPlayer != null ? ownerPlayer.getDisplayName() : 
                                     Bukkit.getOfflinePlayer(owner).getName();
                    player.sendMessage("§c§l[GavaCore] §cQuesta chest è bloccata da: §e" + ownerName);
                }
            }
        }
    }
    
    // Aggiungi questo evento per impedire la distruzione delle chest bloccate
    @EventHandler
    public void onChestBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) return;
        
        String chestLoc = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();
        
        if (lockedChests.containsKey(chestLoc)) {
            Player player = event.getPlayer();
            
            // Se il giocatore è il proprietario, rimuovi il blocco
            if (lockedChests.get(chestLoc).equals(player.getUniqueId())) {
                lockedChests.remove(chestLoc);
                saveLockedChests();
                player.sendMessage("§6§l[GavaCore] §aChest sbloccata automaticamente!");
            } else {
                // Altrimenti, blocca la distruzione
                event.setCancelled(true);
                Player owner = Bukkit.getPlayer(lockedChests.get(chestLoc));
                String ownerName = owner != null ? owner.getName() : "un giocatore offline";
                player.sendMessage("§c§l[GavaCore] §cNon puoi distruggere questa chest! È protetta da: §e" + ownerName);
            }
        }
    }
    
    // Metodo per salvare i dati delle condivisioni
    private void saveSharedData() {
        sharedDataConfig.set("chests", null);
        
        for (Map.Entry<String, List<UUID>> entry : sharedChests.entrySet()) {
            List<String> uuidStrings = entry.getValue().stream().map(UUID::toString).collect(Collectors.toList());
            sharedDataConfig.set("chests." + entry.getKey(), uuidStrings);
        }
        
        try {
            sharedDataConfig.save(sharedDataFile);
        } catch (IOException e) {
            getLogger().severe("§c[GavaCore] Errore nel salvare i dati delle condivisioni!");
        }
    }
    
    private void standUp(Player player) {
        ArmorStand chair = sittingPlayers.remove(player.getUniqueId());
        if (chair != null) {
            chair.eject();
            chair.remove();
            Location loc = player.getLocation();
            
            // Controlla il tipo di blocco sotto il giocatore
            Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
            BlockData blockData = block.getBlockData();
            
            loc.setY(Math.floor(loc.getY()) + 1);
            if (blockData instanceof Slab || blockData instanceof Stairs) {
                loc.add(0, 0.5, 0.5); // Offset maggiore per slab e scale
            } else {
                loc.add(0, 0.2, 0.5); // Offset minore per blocchi normali
            }
            
            player.teleport(loc);
            player.sendMessage("§6§l[GavaCore] §aTi sei alzato!");
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Rimuovi la sedia se il giocatore muore
        standUp(event.getEntity());
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // Rimuovi la sedia se il giocatore si teletrasporta
        if (sittingPlayers.containsKey(event.getPlayer().getUniqueId())) {
            standUp(event.getPlayer());
        }
    }
    
    // Aggiungi questo evento per gestire lo shift
    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        // Se il giocatore sta sneaking e sta sedendo
        if (event.isSneaking() && sittingPlayers.containsKey(playerUUID)) {
            standUp(player);
            event.setCancelled(false); // Assicura che l'evento prosegua normalmente
        }
    }
    
    // Aggiungi questo evento per gestire il consumo del panino
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        
        // Controlla se l'item ha metadata e custom model data
        if (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
            int modelData = item.getItemMeta().getCustomModelData();
            
            // Gestione del panino (ID: 800009)
            if (item.getType() == Material.BEEF && modelData == 800009) {
                // Aggiungi 10 punti vita (5 cuori)
                double newHealth = Math.min(player.getHealth() + 10, player.getMaxHealth());
                player.setHealth(newHealth);
                
                // Imposta la fame al massimo (20)
                player.setFoodLevel(20);
                // Imposta anche la saturazione al massimo
                player.setSaturation(20);
                
                // Messaggio di conferma
                player.sendMessage("§6§l[GavaCore] §aHai mangiato un delizioso panino! §e(+5 cuori, Fame ripristinata)");
            }
            
            // Gestione della Coca Cola (ID: 800010)
            else if (item.getType() == Material.POTION && modelData == 800010) {
                // Aggiungi gli effetti
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1200, 0)); // 60 secondi
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1800, 0));   // 90 secondi
                
                // Aumenta la fame di 3 punti
                int newFoodLevel = Math.min(player.getFoodLevel() + 3, 20);
                player.setFoodLevel(newFoodLevel);
                
                // Restituisci l'ampolla vuota
                Bukkit.getScheduler().runTask(this, () -> {
                    player.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE));
                });
                
                // Messaggio di conferma
                player.sendMessage("§6§l[GavaCore] §aHai bevuto una fresca Coca Cola!");
            }
            
            // Gestione delle patatine fritte (ID: 800014)
            else if (item.getType() == Material.POTATO && modelData == 800014) {
                // Aggiungi gli effetti
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1200, 9)); // Fast Digging 10 per 60 secondi
                
                // Aumenta la fame di 4 punti
                int newFoodLevel = Math.min(player.getFoodLevel() + 4, 20);
                player.setFoodLevel(newFoodLevel);
                
                // Messaggio di conferma
                player.sendMessage("§6§l[GavaCore] §aHai mangiato delle croccanti patatine fritte!");
            }
        }
    }
    
    // Rimuovi l'evento dal metodo onEnable e spostalo qui come metodo separato
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        final Player crafter = (Player) event.getWhoClicked();
        
        // Controlla se è il crafting delle patatine
        if (event.getRecipe().getResult().hasItemMeta() && 
            event.getRecipe().getResult().getItemMeta().hasCustomModelData() && 
            event.getRecipe().getResult().getItemMeta().getCustomModelData() == 800014) {
            
            // Trova la spada nell'inventario di crafting
            ItemStack foundSword = null;
            int swordSlot = -1;
            
            for (int i = 0; i < event.getInventory().getMatrix().length; i++) {
                ItemStack item = event.getInventory().getMatrix()[i];
                if (item != null && item.getType().name().endsWith("_SWORD")) {
                    foundSword = item.clone(); // Clona la spada per preservarne i metadata
                    swordSlot = i;
                    break;
                }
            }
            
            if (foundSword != null) {
                final ItemStack sword = foundSword; // Crea una variabile final per la lambda
                // Rimuovi la spada dal crafting grid
                event.getInventory().setItem(swordSlot, null);
                
                // Danneggia la spada
                ItemMeta meta = sword.getItemMeta();
                if (meta instanceof Damageable) {
                    Damageable damageable = (Damageable) meta;
                    int currentDurability = damageable.getDamage();
                    int newDurability = currentDurability + 5;
                    
                    // Controlla se la spada si romperebbe
                    if (newDurability >= sword.getType().getMaxDurability()) {
                        event.setCancelled(true);
                        crafter.sendMessage("§c§l[GavaCore] §cLa spada è troppo danneggiata per essere usata!");
                        return;
                    }
                    
                    // Applica il danno
                    damageable.setDamage(newDurability);
                    sword.setItemMeta(meta);
                    
                    // Restituisci la spada danneggiata al giocatore
                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        HashMap<Integer, ItemStack> leftover = crafter.getInventory().addItem(sword);
                        if (!leftover.isEmpty()) {
                            // Se l'inventario è pieno, droppa la spada
                            crafter.getWorld().dropItemNaturally(crafter.getLocation(), sword);
                            crafter.sendMessage("§6§l[GavaCore] §eLa spada è stata droppata perché il tuo inventario è pieno!");
                        }
                    }, 1L);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String nickname = nicknames.getString(player.getUniqueId().toString());
        
        if (nickname != null) {
            event.setFormat("§e" + nickname + " §8» §f%2$s");
        } else {
            event.setFormat("§7" + player.getName() + " §8» §f%2$s");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        
        // Se la modalità sviluppatore è attiva e il player non è op
        if (developerMode && !player.isOp()) {
            // Trova il primo operatore online
            String developerName = Bukkit.getOnlinePlayers().stream()
                .filter(Player::isOp)
                .findFirst()
                .map(Player::getName)
                .orElse("Staff");
                
            // Messaggio di kick personalizzato
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, 
                "§c§l✦ §4§lMANUTENZIONE §c§l✦\n\n" +
                "§7Il server è in §c§lmodalità sviluppatore§7.\n" +
                "§7Sviluppatore: §b§l" + developerName + "\n\n" +
                "§e§lTorneremo presto!\n" +
                "§7Sito: §bwww.gavatech.org/server"
            );
            
            // Log dell'evento
            getLogger().info("[GavaCore] " + player.getName() + " ha tentato di connettersi durante la modalità sviluppatore");
        }
    }

    // Aggiungi questo metodo per creare il menu principale
    private void openMainMenu(Player player) {
        if (this.guiManager != null) {
            this.guiManager.openMainMenu(player);
        } else {
            player.sendMessage("§c§l[GavaCore] §cErrore: GUIManager non inizializzato!");
            getLogger().severe("GUIManager non inizializzato!");
        }
    }

    // Implementa i menu mancanti
    private void openWeatherMenu(Player player) {
        guiManager.openWeatherMenu(player);
    }

    private void openGamemodeMenu(Player player) {
        guiManager.openGamemodeMenu(player);
    }

    private void openChatMenu(Player player) {
        guiManager.openChatMenu(player);
    }

    private void openTeleportMenu(Player player) {
        guiManager.openTeleportMenu(player);
    }

    private void openTransformMenu(Player player) {
        guiManager.openTransformMenu(player);
    }

    private void openCustomItemsMenu(Player player) {
        guiManager.openCustomItemsMenu(player);
    }

    private void openBackpackMenu(Player player) {
        guiManager.openBackpackMenu(player);
    }

    // Metodi per i sottomenu
    private void handleWeatherMenuClick(Player player, ItemStack clicked) {
        guiManager.handleWeatherMenuClick(player, clicked);
    }

    private void handleGamemodeMenuClick(Player player, ItemStack clicked) {
        guiManager.handleGamemodeMenuClick(player, clicked);
    }

    private void handleChatMenuClick(Player player, ItemStack clicked) {
        guiManager.handleChatMenuClick(player, clicked);
    }

    private void handleTeleportMenuClick(Player player, ItemStack clicked) {
        guiManager.handleTeleportMenuClick(player, clicked);
    }

    private void handleTransformMenuClick(Player player, ItemStack clicked) {
        guiManager.handleTransformMenuClick(player, clicked);
    }

    private void handleItemsMenuClick(Player player, ItemStack clicked) {
        guiManager.handleItemsMenuClick(player, clicked);
    }

    private void handleBackpackMenuClick(Player player, ItemStack clicked) {
        guiManager.handleBackpackMenuClick(player, clicked);
    }

    private void handleAddonManagerClick(Player player, ItemStack clicked) {
        guiManager.handlePluginsMenuClick(player, clicked);
    }

    private void showItemRecipe(Player player, String item) {
        guiManager.showItemRecipe(player, item);
    }

    private void showBackpackRecipe(Player player, int modelData) {
        guiManager.showBackpackRecipe(player, modelData);
    }

    private void openDevCommandsGUI(Player player) {
        guiManager.openDevCommandsGUI(player);
    }

    // Aggiungi questi metodi nella classe Main

    private void checkAddons() {
        // Controlla la presenza degli addon
        trainAddon = getServer().getPluginManager().getPlugin("GavaCore-TrainAddons");
        hasTrainAddon = trainAddon != null;
        
        posteAddon = getServer().getPluginManager().getPlugin("GavaCore-PosteAddons");
        hasPosteAddon = posteAddon != null;
        
        backpackPlugin = getServer().getPluginManager().getPlugin("BackpackPlus");
        hasBackpackPlus = backpackPlugin != null;
        
        coinsPlugin = getServer().getPluginManager().getPlugin("Coins");
        hasCoins = coinsPlugin != null;
        
        iconomyPlugin = getServer().getPluginManager().getPlugin("iConomyUnlocked");
        hasIConomy = iconomyPlugin != null;
        
        skinRestorerPlugin = getServer().getPluginManager().getPlugin("SkinsRestorer");
        hasSkinRestorer = skinRestorerPlugin != null;
        
        smoothTimberPlugin = getServer().getPluginManager().getPlugin("SmoothTimber");
        hasSmoothTimber = smoothTimberPlugin != null;
    }

    private void restoreAxes(Player player) {
        // Implementazione del ripristino delle asce
    }

    private void startPerformanceMonitoring() {
        // Implementazione del monitoraggio delle performance
    }

    public boolean isDeveloperMode() {
        return developerMode;
    }

    private void handleMainMenuClick(Player player, ItemStack clicked) {
        switch (clicked.getType()) {
            case WATER_BUCKET: // Meteo
                openWeatherMenu(player);
                break;
            case DIAMOND_SWORD: // Gamemode
                openGamemodeMenu(player);
                break;
            case WRITABLE_BOOK: // Chat
                openChatMenu(player);
                break;
            case ENDER_PEARL: // Teleport
                openTeleportMenu(player);
                break;
            case BONE: // Trasformazione
                openTransformMenu(player);
                break;
            case CHEST: // Items Custom
                openCustomItemsMenu(player);
                break;
            case NETHER_STAR: // Addon Manager
                if (guiManager != null) {
                    guiManager.openPluginsMenu(player);
                } else {
                    player.sendMessage("§c§l[GavaCore] §cErrore: Menu non disponibile!");
                }
                break;
            case COMMAND_BLOCK: // Dev Tools
                if (player.isOp()) {
                    if (developerMode) {
                        openDevCommandsGUI(player);
                    } else {
                        player.sendMessage("§c§l[GavaCore] §cAttiva prima la modalità sviluppatore con /developer-mode");
                        player.closeInventory();
                    }
                }
                break;
        }
    }

    // Aggiungi questi getter per le variabili private
    public boolean hasTrainAddon() {
        return hasTrainAddon;
    }

    public boolean hasPosteAddon() {
        return hasPosteAddon;
    }

    public boolean hasBackpackPlus() {
        return hasBackpackPlus;
    }

    public int getIronBackpackSlots() {
        return ironBackpackSlots;
    }

    public int getGoldBackpackSlots() {
        return goldBackpackSlots;
    }

    public int getDiamondBackpackSlots() {
        return diamondBackpackSlots;
    }

    public boolean hasCoins() {
        return hasCoins;
    }

    public boolean hasIConomy() {
        return hasIConomy;
    }

    public Map<String, ItemStack> getRegisteredRecipes() {
        Map<String, ItemStack> recipes = new HashMap<>();
        recipes.put("panino", createHamburgerItem());
        recipes.put("coca_cola", createCocaColaItem());
        recipes.put("patatine", createPatatineItem());
        recipes.put("guinzaglio", createMagicLeashItem());
        return recipes;
    }

    // Metodi helper per creare gli item
    private ItemStack createHamburgerItem() {
        ItemStack hamburger = new ItemStack(Material.BEEF);
        ItemMeta meta = hamburger.getItemMeta();
        meta.setDisplayName("§6§lPanino");
        meta.setCustomModelData(800009);
        meta.setLore(Arrays.asList(
            "§7Un delizioso panino",
            "§7con carne di manzo",
            "",
            "§bEffetti:",
            "§7- +5 cuori",
            "§7- Fame al massimo"
        ));
        hamburger.setItemMeta(meta);
        return hamburger;
    }

    private ItemStack createCocaColaItem() {
        ItemStack cola = new ItemStack(Material.POTION);
        ItemMeta colaMeta = cola.getItemMeta();
        colaMeta.setDisplayName("§6§lCoca Cola");
        colaMeta.setCustomModelData(800010);
        colaMeta.setLore(Arrays.asList(
            "§7Una bevanda fresca e gassata",
            "",
            "§bEffetti:",
            "§7- Respirazione (60s)",
            "§7- Visione notturna (90s)",
            "§7- +3 Fame"
        ));
        cola.setItemMeta(colaMeta);
        return cola;
    }

    private ItemStack createPatatineItem() {
        ItemStack patatine = new ItemStack(Material.POTATO);
        ItemMeta patatineMeta = patatine.getItemMeta();
        patatineMeta.setDisplayName("§6§lPatatine Fritte");
        patatineMeta.setCustomModelData(800014);
        patatineMeta.setLore(Arrays.asList(
            "§7Croccanti patatine fritte",
            "",
            "§bEffetti:",
            "§7- Velocità di scavo (60s)",
            "§7- +4 Fame"
        ));
        patatine.setItemMeta(patatineMeta);
        return patatine;
    }

    private ItemStack createMagicLeashItem() {
        ItemStack guinzaglio = new ItemStack(Material.LEAD);
        ItemMeta guinzaglioMeta = guinzaglio.getItemMeta();
        guinzaglioMeta.setDisplayName("§d§lGuinzaglio Magico");
        guinzaglioMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        guinzaglioMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        guinzaglioMeta.setLore(Arrays.asList(
            "§7Trasforma i giocatori in cani",
            "§7e controllali"
        ));
        guinzaglio.setItemMeta(guinzaglioMeta);
        return guinzaglio;
    }

    // ... altri metodi helper per gli altri item

    public boolean hasSkinRestorer() {
        return hasSkinRestorer;
    }

    public boolean hasSmoothTimber() {
        return hasSmoothTimber;
    }

    public String getTrainAddonVersion() {
        return "1.0.0";
    }

    public String getPosteAddonVersion() {
        return posteAddon != null ? posteAddon.getDescription().getVersion() : "";
    }

    public String getBackpackVersion() {
        return backpackPlugin != null ? backpackPlugin.getDescription().getVersion() : "";
    }

    public String getCoinsVersion() {
        return coinsPlugin != null ? coinsPlugin.getDescription().getVersion() : "";
    }

    public String getIconomyVersion() {
        return iconomyPlugin != null ? iconomyPlugin.getDescription().getVersion() : "";
    }

    public String getSkinRestorerVersion() {
        return skinRestorerPlugin != null ? skinRestorerPlugin.getDescription().getVersion() : "";
    }

    public String getSmoothTimberVersion() {
        return smoothTimberPlugin != null ? smoothTimberPlugin.getDescription().getVersion() : "";
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        Player player = event.getPlayer();
        
        // Salva il display name originale
        String originalDisplayName = player.getDisplayName();
        
        // Imposta il display name con il soprannome se presente
        String nickname = nicknames.getString(player.getUniqueId().toString());
        if (nickname != null) {
            player.setDisplayName("§e" + nickname);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerCommand(ServerCommandEvent event) {
        String command = event.getCommand().toLowerCase();
        
        // Modifica i messaggi di output per tutti i giocatori online
        for (Player player : Bukkit.getOnlinePlayers()) {
            String nickname = nicknames.getString(player.getUniqueId().toString());
            if (nickname != null) {
                player.setDisplayName("§e" + nickname);
            }
        }
    }

    // Aggiungi questo metodo per gestire i messaggi di comando
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandProcess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        
        // Se inizia con "/" è un comando, ignoralo
        if (message.startsWith("/")) {
            return;
        }
        
        // Per tutti gli altri messaggi in chat
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (message.toLowerCase().contains(target.getName().toLowerCase())) {
                String nickname = nicknames.getString(target.getUniqueId().toString());
                if (nickname != null) {
                    // Sostituisci il nome con il soprannome nel messaggio
                    event.setMessage(message.replace(
                        target.getName(), 
                        "§e" + nickname + "§r"
                    ));
                }
            }
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    // Aggiungi un getter per il GUIManager
    public GUIManager getGUIManager() {
        return this.guiManager;
    }

    // Aggiungi questo metodo pubblico
    public static Main getInstance() {
        return (Main) Bukkit.getPluginManager().getPlugin("GavaCore");
    }

    // Aggiungi questo metodo pubblico
    public String getNickname(UUID playerUUID) {
        return nicknames.getString(playerUUID.toString());
    }

    // Modifica il metodo che gestisce il comando /tp
    private void handleTeleportCommand(Player sender, String targetName) {
        // Cerca il giocatore prima per nome esatto
        final Player target = Bukkit.getPlayer(targetName);
        final Player finalTarget;
        
        // Se non trovato, cerca per soprannome
        if (target == null) {
            Player found = null;
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String nickname = getNickname(onlinePlayer.getUniqueId());
                if (nickname != null && nickname.equalsIgnoreCase(targetName)) {
                    found = onlinePlayer;
                    break;
                }
            }
            finalTarget = found;
        } else {
            finalTarget = target;
        }
        
        if (finalTarget == null) {
            sender.sendMessage("§c§l[GavaCore] §cGiocatore non trovato!");
            return;
        }
        
        if (finalTarget == sender) {
            sender.sendMessage("§c§l[GavaCore] §cNon puoi teletrasportarti da te stesso!");
            return;
        }
        
        // Verifica se il mittente è un operatore (ha OP)
        if (sender.isOp()) {
            // Teletrasporto istantaneo senza richiesta di conferma
            sender.sendMessage("§6§l[GavaCore] §aIn teletrasporto verso §e" + finalTarget.getDisplayName() + "§a...");
            
            // Usa l'animazione di teletrasporto
            teleportAnimation.startTeleportAnimation(sender, finalTarget.getLocation());
            
            // Informa il giocatore destinazione che un operatore si è teletrasportato
            finalTarget.sendMessage("§6§l[GavaCore] §eL'operatore §6" + sender.getDisplayName() + " §esi è teletrasportato da te.");
            return;
        }
        
        // Se non è un operatore, procedi con il normale sistema di richieste
        // Inizializza la mappa per il target se non esiste
        tpRequests.computeIfAbsent(finalTarget.getUniqueId(), k -> new HashMap<>());
        tpRequestTasks.computeIfAbsent(finalTarget.getUniqueId(), k -> new HashMap<>());
        
        // Salva la richiesta (false = è una richiesta /tp, non /tpqui)
        tpRequests.get(finalTarget.getUniqueId()).put(sender.getUniqueId(), false);
        
        // Ottieni il tempo di timeout dalle configurazioni, con fallback di 60 secondi
        int timeoutSeconds = 60;
        if (configManager != null && getConfig().contains("teleport.teleport-request-timeout")) {
            timeoutSeconds = getConfig().getInt("teleport.teleport-request-timeout", 60);
        }
        
        // Cancella la richiesta dopo il timeout configurato
        BukkitTask task = Bukkit.getScheduler().runTaskLater(this, () -> {
            HashMap<UUID, Boolean> playerRequests = tpRequests.get(finalTarget.getUniqueId());
            if (playerRequests != null && playerRequests.remove(sender.getUniqueId()) != null) {
                if (playerRequests.isEmpty()) {
                    tpRequests.remove(finalTarget.getUniqueId());
                }
                
                HashMap<UUID, BukkitTask> requestTasks = tpRequestTasks.get(finalTarget.getUniqueId());
                if (requestTasks != null) {
                    requestTasks.remove(sender.getUniqueId());
                    if (requestTasks.isEmpty()) {
                        tpRequestTasks.remove(finalTarget.getUniqueId());
                    }
                }
                
                sender.sendMessage("§c§l[GavaCore] §cLa richiesta di teletrasporto è scaduta!");
                finalTarget.sendMessage("§c§l[GavaCore] §cLa richiesta di teletrasporto è scaduta!");
            }
        }, timeoutSeconds * 20L); // Converti secondi in ticks
        
        // Salva il task di scadenza
        tpRequestTasks.get(finalTarget.getUniqueId()).put(sender.getUniqueId(), task);
        
        // Invia i messaggi con i pulsanti di conferma/rifiuto
        String senderName = sender.getDisplayName() != null ? sender.getDisplayName() : sender.getName();
        String targetDisplayName = finalTarget.getDisplayName() != null ? finalTarget.getDisplayName() : finalTarget.getName();
        
        sender.sendMessage("§6§l[GavaCore] §eRichiesta di teletrasporto inviata a §f" + targetDisplayName);
        
        TextComponent message = new TextComponent("§6§l[GavaCore] §f" + senderName + " §evuole teletrasportarsi da te\n");
        
        TextComponent accept = new TextComponent("§a[Accetta]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sender.getName()));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder("§aClicca per accettare").create()));
        
        TextComponent deny = new TextComponent(" §c[✘ Rifiuta]");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sender.getName()));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder("§cClicca per rifiutare").create()));
        
        message.addExtra(accept);
        message.addExtra(deny);
        
        finalTarget.spigot().sendMessage(message);
    }

    // Modifica il metodo che gestisce il comando /tpqui
    private void handleTeleportHereCommand(Player sender, String targetName) {
        // Cerca il giocatore prima per nome esatto
        final Player target = Bukkit.getPlayer(targetName);
        final Player finalTarget;
        
        // Se non trovato, cerca per soprannome
        if (target == null) {
            Player found = null;
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String nickname = getNickname(onlinePlayer.getUniqueId());
                if (nickname != null && nickname.equalsIgnoreCase(targetName)) {
                    found = onlinePlayer;
                    break;
                }
            }
            finalTarget = found;
        } else {
            finalTarget = target;
        }
        
        if (finalTarget == null) {
            sender.sendMessage("§c§l[GavaCore] §cGiocatore non trovato!");
            return;
        }
        
        if (finalTarget == sender) {
            sender.sendMessage("§c§l[GavaCore] §cNon puoi teletrasportarti da te stesso!");
            return;
        }
        
        // Verifica se il mittente è un operatore (ha OP)
        if (sender.isOp()) {
            // Teletrasporto istantaneo senza richiesta di conferma
            sender.sendMessage("§6§l[GavaCore] §aTeletrasporto di §e" + finalTarget.getDisplayName() + " §ain corso...");
            
            // Usa l'animazione di teletrasporto
            teleportAnimation.startTeleportAnimation(finalTarget, sender.getLocation());
            
            // Informa il giocatore che è stato teletrasportato da un operatore
            finalTarget.sendMessage("§6§l[GavaCore] §eSei stato teletrasportato dall'operatore §6" + sender.getDisplayName());
            return;
        }
        
        // Se non è un operatore, procedi con il normale sistema di richieste
        // Inizializza la mappa per il target se non esiste
        tpRequests.computeIfAbsent(finalTarget.getUniqueId(), k -> new HashMap<>());
        tpRequestTasks.computeIfAbsent(finalTarget.getUniqueId(), k -> new HashMap<>());
        
        // Salva la richiesta (true = è una richiesta /tpqui, non /tp)
        tpRequests.get(finalTarget.getUniqueId()).put(sender.getUniqueId(), true);
        
        // Ottieni il tempo di timeout dalle configurazioni, con fallback di 60 secondi
        int timeoutSeconds = 60;
        if (configManager != null && getConfig().contains("teleport.teleport-request-timeout")) {
            timeoutSeconds = getConfig().getInt("teleport.teleport-request-timeout", 60);
        }
        
        // Cancella la richiesta dopo il timeout configurato
        BukkitTask task = Bukkit.getScheduler().runTaskLater(this, () -> {
            HashMap<UUID, Boolean> playerRequests = tpRequests.get(finalTarget.getUniqueId());
            if (playerRequests != null && playerRequests.remove(sender.getUniqueId()) != null) {
                if (playerRequests.isEmpty()) {
                    tpRequests.remove(finalTarget.getUniqueId());
                }
                
                HashMap<UUID, BukkitTask> requestTasks = tpRequestTasks.get(finalTarget.getUniqueId());
                if (requestTasks != null) {
                    requestTasks.remove(sender.getUniqueId());
                    if (requestTasks.isEmpty()) {
                        tpRequestTasks.remove(finalTarget.getUniqueId());
                    }
                }
                
                sender.sendMessage("§c§l[GavaCore] §cLa richiesta di teletrasporto è scaduta!");
                finalTarget.sendMessage("§c§l[GavaCore] §cLa richiesta di teletrasporto è scaduta!");
            }
        }, timeoutSeconds * 20L); // Converti secondi in ticks
        
        // Salva il task di scadenza
        tpRequestTasks.get(finalTarget.getUniqueId()).put(sender.getUniqueId(), task);
        
        // Invia i messaggi con i pulsanti di conferma/rifiuto
        String senderName = sender.getDisplayName() != null ? sender.getDisplayName() : sender.getName();
        String targetDisplayName = finalTarget.getDisplayName() != null ? finalTarget.getDisplayName() : finalTarget.getName();
        
        sender.sendMessage("§6§l[GavaCore] §eRichiesta di teletrasporto inviata a §f" + targetDisplayName);
        
        TextComponent message = new TextComponent("§6§l[GavaCore] §f" + senderName + " §evuole teletrasportarti da lui\n");
        
        TextComponent accept = new TextComponent("§a[Accetta]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sender.getName()));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder("§aClicca per accettare").create()));
        
        TextComponent deny = new TextComponent(" §c[Rifiuta]");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sender.getName()));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder("§cClicca per rifiutare").create()));
        
        message.addExtra(accept);
        message.addExtra(deny);
        
        finalTarget.spigot().sendMessage(message);
    }

    // Aggiungi anche un metodo per gestire il blocco del tempo
    public void setWeatherLocked(boolean locked) {
        this.isWeatherLocked = locked;
    }

    public boolean isWeatherLocked() {
        return this.isWeatherLocked;
    }

    // Metodo per ottenere l'istanza di FirstJoinAnimation
    public FirstJoinAnimation getFirstJoinAnimation() {
        return firstJoinAnimation;
    }

    // Metodo per ottenere l'istanza di TeleportAnimation
    public TeleportAnimation getTeleportAnimation() {
        return teleportAnimation;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }
    
    public BackupManager getBackupManager() {
        return backupManager;
    }
    
    public ChatManager getChatManager() {
        return chatManager;
    }

    private void saveNicknames() {
        try {
            nicknames.save(nicknamesFile);
        } catch (Exception e) {
            getLogger().severe("§c[GavaCore] Errore nel salvare i soprannomi: " + e.getMessage());
        }
    }
}