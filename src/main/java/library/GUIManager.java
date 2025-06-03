package library;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import java.util.Map;
import org.bukkit.event.Listener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class GUIManager implements Listener {
    private final String GUI_TITLE = "§6§lGavaCore Menu";
    private final HashMap<UUID, String> openMenus = new HashMap<>();
    private final Main plugin;
    private final ConfigManager configManager;
    private final Map<UUID, StringBuilder> amountInputs = new HashMap<>();

    public GUIManager(Main plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        if (this.configManager == null) {
            plugin.getLogger().severe("ConfigManager non inizializzato!");
        }
    }

    public String getGuiTitle() {
        return GUI_TITLE;
    }

    public HashMap<UUID, String> getOpenMenus() {
        return openMenus;
    }

    // Metodo per riempire l'inventario con vetro nero
    public void fillWithGlass(Inventory gui) {
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        for(int i = 0; i < gui.getSize(); i++) {
            if(gui.getItem(i) == null) {
                gui.setItem(i, glass);
            }
        }
    }

    // Metodo helper per creare il bottone "Torna al Menu"
    private ItemStack createBackButton() {
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName("§c§lTorna al Menu");
        meta.setLore(Arrays.asList("§7Clicca per tornare", "§7al menu principale"));
        back.setItemMeta(meta);
        return back;
    }

    // Aggiungi questo metodo per gestire il click sul bottone "Torna al Menu"
    public void handleBackButton(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked != null && clicked.getType() == Material.BARRIER && 
            clicked.hasItemMeta() && clicked.getItemMeta().getDisplayName().equals("§c§lTorna al Menu")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            // Chiudi l'inventario corrente e apri il menu principale nel prossimo tick
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.closeInventory();
                openMainMenu(player);
            });
        }
    }

    // Metodo per aprire il menu principale
    public void openMainMenu(Player player) {
        if (player.isOp()) {
            player.sendMessage("§6Debug: Apertura menu principale...");
        }
        
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE);
        
        // Bordo decorativo superiore
        ItemStack border = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName("§e✦");
        border.setItemMeta(borderMeta);
        
        for(int i = 0; i < 9; i++) {
            gui.setItem(i, border);
        }
        
        // Prima riga di funzionalità (slot 10-16)
        // TreeCutter (Taglialegna)
        ItemStack treeCutter = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta treeCutterMeta = treeCutter.getItemMeta();
        treeCutterMeta.setDisplayName("§a§lTaglialegna");
        treeCutterMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        treeCutterMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        List<String> treeCutterLore = Arrays.asList(
            "§7Taglia interi alberi con un",
            "§7solo colpo mentre sei in shift",
            "",
            "§8• §7Usa qualsiasi ascia",
            "§8• §7Tieni premuto SHIFT",
            "§8• §7Colpisci il tronco più basso",
            "§8• §7Animazione automatica dal basso",
            "",
            "§e➜ Click per informazioni"
        );
        treeCutterMeta.setLore(treeCutterLore);
        treeCutter.setItemMeta(treeCutterMeta);
        gui.setItem(11, treeCutter);
        
        // Meteo
        ItemStack weather = new ItemStack(Material.WATER_BUCKET);
        ItemMeta weatherMeta = weather.getItemMeta();
        weatherMeta.setDisplayName("§b§lMeteo");
        weatherMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        weatherMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> weatherLore = Arrays.asList(
            "§7Controlla il meteo del server",
            "",
            "§8• §7Sole",
            "§8• §7Pioggia",
            "§8• §7Tempesta",
            "",
            "§e➜ Click per aprire"
        );
        weatherMeta.setLore(weatherLore);
        weather.setItemMeta(weatherMeta);
        gui.setItem(13, weather);
        
        // Gamemode
        ItemStack gamemode = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta gamemodeMeta = gamemode.getItemMeta();
        gamemodeMeta.setDisplayName("§c§lGamemode");
        gamemodeMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        gamemodeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        List<String> gamemodeLore = Arrays.asList(
            "§7Cambia la tua modalità di gioco",
            "",
            "§8• §7Sopravvivenza",
            "§8• §7Creativa",
            "§8• §7Avventura",
            "§8• §7Spettatore",
            "",
            "§e➜ Click per aprire"
        );
        gamemodeMeta.setLore(gamemodeLore);
        gamemode.setItemMeta(gamemodeMeta);
        gui.setItem(15, gamemode);
        
        // Seconda riga (slot 19-25)
        // Chat
        ItemStack chat = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta chatMeta = chat.getItemMeta();
        chatMeta.setDisplayName("§a§lChat");
        chatMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        chatMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> chatLore = Arrays.asList(
            "§7Gestisci la chat del server",
            "",
            "§8• §7Attiva/Disattiva chat",
            "§8• §7Pulisci chat",
            "",
            "§e➜ Click per aprire"
        );
        chatMeta.setLore(chatLore);
        chat.setItemMeta(chatMeta);
        gui.setItem(20, chat);
        
        // Teleport
        ItemStack teleport = new ItemStack(Material.ENDER_PEARL);
        ItemMeta teleportMeta = teleport.getItemMeta();
        teleportMeta.setDisplayName("§5§lTeleport");
        teleportMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        teleportMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> teleportLore = Arrays.asList(
            "§7Gestisci i teletrasporti",
            "",
            "§8• §7Teletrasporto a",
            "§8• §7Teletrasporto qui",
            "",
            "§e➜ Click per aprire"
        );
        teleportMeta.setLore(teleportLore);
        teleport.setItemMeta(teleportMeta);
        gui.setItem(22, teleport);
        
        // Trasformazioni
        ItemStack transform = new ItemStack(Material.BONE);
        ItemMeta transformMeta = transform.getItemMeta();
        transformMeta.setDisplayName("§d§lTrasformazioni");
        transformMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        transformMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> transformLore = Arrays.asList(
            "§7Trasformati in un cane o",
            "§7siediti come una sedia",
            "",
            "§7Siediti su qualsiasi blocco"
        );
        transformMeta.setLore(transformLore);
        transform.setItemMeta(transformMeta);
        gui.setItem(24, transform);
        
        // Terza riga - Items e Addons (slot 28-34)
        // Items Custom
        ItemStack items = new ItemStack(Material.CHEST);
        ItemMeta itemsMeta = items.getItemMeta();
        itemsMeta.setDisplayName("§6§lItems Custom");
        itemsMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> itemsLore = Arrays.asList(
            "§7Visualizza e crafta gli",
            "§7items custom del server",
            "",
            "§8• §7Panino",
            "§8• §7Coca Cola",
            "§8• §7Patatine",
            "§8• §7Guinzaglio Magico",
            "",
            "§e➜ Click per aprire"
        );
        itemsMeta.setLore(itemsLore);
        items.setItemMeta(itemsMeta);
        gui.setItem(29, items);

        // Zaini (solo se BackpackPlus è presente)
        if (plugin.hasBackpackPlus()) {
            ItemStack backpack = new ItemStack(Material.BROWN_DYE);
            ItemMeta backpackMeta = backpack.getItemMeta();
            backpackMeta.setDisplayName("§6§lZaini");
            backpackMeta.setCustomModelData(800001);
            backpackMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            backpackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> backpackLore = Arrays.asList(
                "§7Visualizza e crafta gli",
                "§7zaini del server",
                "",
                "§8• §7Zaino Base",
                "§8• §7Zaino d'Oro",
                "§8• §7Zaino di Diamante",
                "",
                "§e➜ Click per aprire"
            );
            backpackMeta.setLore(backpackLore);
            backpack.setItemMeta(backpackMeta);
            gui.setItem(31, backpack);
        }

        // Bordo inferiore decorativo
        for(int i = 45; i < 54; i++) {
            gui.setItem(i, border);
        }
        
        // Dev Tools (solo per op)
        if (player.isOp()) {
            ItemStack devTools = new ItemStack(Material.COMMAND_BLOCK);
            ItemMeta devToolsMeta = devTools.getItemMeta();
            devToolsMeta.setDisplayName("§c§lDeveloper Tools");
            List<String> devLore = new ArrayList<>();
            devLore.add("§7Strumenti per gli sviluppatori");
            devLore.add("");
            if (plugin.isDeveloperMode()) {
                devLore.add("§c§lModalità Sviluppatore: §aAttiva");
                devLore.add("§7• Click per vedere i comandi");
                devToolsMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                devToolsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                devLore.add("§c§lModalità Sviluppatore: §cDisattivata");
                devLore.add("§7• Usa /developer-mode per attivarla");
            }
            devToolsMeta.setLore(devLore);
            devTools.setItemMeta(devToolsMeta);
            gui.setItem(49, devTools);
        }

        // Gestione Plugin
        ItemStack pluginManager = new ItemStack(Material.NETHER_STAR);
        ItemMeta pluginMeta = pluginManager.getItemMeta();
        pluginMeta.setDisplayName("§d§lGestione Plugin");
        pluginMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        pluginMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> pluginLore = new ArrayList<>();
        pluginLore.add("§7Gestisci e visualizza tutti");
        pluginLore.add("§7i plugin del server");
        pluginLore.add("");
        pluginLore.add("§7Plugin attivi: §a" + getActivePluginsCount() + "§8/§7" + getTotalPluginsCount());
        pluginLore.add("");
        pluginLore.add("§e➜ Click per aprire");
        pluginMeta.setLore(pluginLore);
        pluginManager.setItemMeta(pluginMeta);
        gui.setItem(33, pluginManager);

        // Riempi gli spazi vuoti con vetro nero
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        
        for(int i = 0; i < gui.getSize(); i++) {
            if(gui.getItem(i) == null) {
                gui.setItem(i, glass);
            }
        }
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "main");
        
        if (player.isOp()) {
            player.sendMessage("§6Debug: Menu aperto con successo");
        }
    }

    // Metodo rimosso - La funzionalità di protezione è stata sostituita con il TreeCutter
    public void openProtectionMenu(Player player) {
        // Reindirizza al nuovo menu TreeCutter
        openTreeCutterInfoMenu(player);
    }

    // Metodo per aprire il menu meteo
    public void openWeatherMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE + " §8» §bMeteo");
        
        // Sole
        ItemStack sun = new ItemStack(Material.SUNFLOWER);
        ItemMeta sunMeta = sun.getItemMeta();
        sunMeta.setDisplayName("§e§lSole");
        sunMeta.setLore(Arrays.asList(
            "§7Imposta il tempo sereno",
            "",
            "§e➜ Click per attivare"
        ));
        sun.setItemMeta(sunMeta);
        gui.setItem(11, sun);
        
        // Pioggia
        ItemStack rain = new ItemStack(Material.WATER_BUCKET);
        ItemMeta rainMeta = rain.getItemMeta();
        rainMeta.setDisplayName("§b§lPioggia");
        rainMeta.setLore(Arrays.asList(
            "§7Fa piovere nel server",
            "",
            "§e➜ Click per attivare"
        ));
        rain.setItemMeta(rainMeta);
        gui.setItem(13, rain);
        
        // Tempesta
        ItemStack storm = new ItemStack(Material.LIGHTNING_ROD);
        ItemMeta stormMeta = storm.getItemMeta();
        stormMeta.setDisplayName("§7§lTempesta");
        stormMeta.setLore(Arrays.asList(
            "§7Scatena una tempesta",
            "",
            "§e➜ Click per attivare"
        ));
        storm.setItemMeta(stormMeta);
        gui.setItem(15, storm);
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna Indietro");
        back.setItemMeta(backMeta);
        gui.setItem(26, back);
        
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "weather");
    }

    // Metodo per aprire il menu gamemode
    public void openGamemodeMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE + " §8» §aGamemode");
        
        // Sopravvivenza
        ItemStack survival = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta survivalMeta = survival.getItemMeta();
        survivalMeta.setDisplayName("§a§lSopravvivenza");
        survivalMeta.setLore(Arrays.asList(
            "§7Modalità sopravvivenza",
            "",
            "§e➜ Click per attivare"
        ));
        survival.setItemMeta(survivalMeta);
        gui.setItem(10, survival);
        
        // Creativa
        ItemStack creative = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta creativeMeta = creative.getItemMeta();
        creativeMeta.setDisplayName("§b§lCreativa");
        creativeMeta.setLore(Arrays.asList(
            "§7Modalità creativa",
            "",
            "§e➜ Click per attivare"
        ));
        creative.setItemMeta(creativeMeta);
        gui.setItem(12, creative);
        
        // Avventura
        ItemStack adventure = new ItemStack(Material.MAP);
        ItemMeta adventureMeta = adventure.getItemMeta();
        adventureMeta.setDisplayName("§e§lAvventura");
        adventureMeta.setLore(Arrays.asList(
            "§7Modalità avventura",
            "",
            "§e➜ Click per attivare"
        ));
        adventure.setItemMeta(adventureMeta);
        gui.setItem(14, adventure);
        
        // Spettatore
        ItemStack spectator = new ItemStack(Material.ENDER_EYE);
        ItemMeta spectatorMeta = spectator.getItemMeta();
        spectatorMeta.setDisplayName("§5§lSpettatore");
        spectatorMeta.setLore(Arrays.asList(
            "§7Modalità spettatore",
            "",
            "§e➜ Click per attivare"
        ));
        spectator.setItemMeta(spectatorMeta);
        gui.setItem(16, spectator);
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna Indietro");
        back.setItemMeta(backMeta);
        gui.setItem(26, back);
        
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "gamemode");
    }

    // Metodo per aprire il menu chat
    public void openChatMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE + " §8» §aChat");
        
        // Pulisci Chat
        ItemStack clear = new ItemStack(Material.PAPER);
        ItemMeta clearMeta = clear.getItemMeta();
        clearMeta.setDisplayName("§e§lPulisci Chat");
        clearMeta.setLore(Arrays.asList(
            "§7Pulisce la chat del server",
            "",
            "§e➜ Click per pulire"
        ));
        clear.setItemMeta(clearMeta);
        gui.setItem(11, clear);
        
        // Toggle Chat
        ItemStack toggle = new ItemStack(Material.REDSTONE_TORCH);
        ItemMeta toggleMeta = toggle.getItemMeta();
        toggleMeta.setDisplayName("§c§lToggle Chat");
        toggleMeta.setLore(Arrays.asList(
            "§7Attiva/Disattiva la chat",
            "",
            "§e➜ Click per cambiare"
        ));
        toggle.setItemMeta(toggleMeta);
        gui.setItem(15, toggle);
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna Indietro");
        back.setItemMeta(backMeta);
        gui.setItem(26, back);
        
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "chat");
    }

    // Metodo per aprire il menu teleport
    public void openTeleportMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, GUI_TITLE + " §8» §5Teleport");
        
        // Teletrasporto a giocatore (per tutti)
        ItemStack tp = new ItemStack(Material.ENDER_PEARL);
        ItemMeta tpMeta = tp.getItemMeta();
        tpMeta.setDisplayName("§5§lTeletrasporto a Giocatore");
        tpMeta.setLore(Arrays.asList(
            "§7Teletrasportati da un giocatore",
            "",
            "§7Comando: §f/tp <giocatore>",
            "",
            "§e➜ Click per info"
        ));
        tp.setItemMeta(tpMeta);
        gui.setItem(11, tp);
        
        // Teletrasporto qui
        ItemStack tphere = new ItemStack(Material.COMPASS);
        ItemMeta tphereMeta = tphere.getItemMeta();
        tphereMeta.setDisplayName("§5§lTeletrasporto Qui");
        tphereMeta.setLore(Arrays.asList(
            "§7Teletrasporta un giocatore da te",
            "",
            "§7Comando: §f/tpqui <giocatore>",
            "",
            "§e➜ Click per info"
        ));
        tphere.setItemMeta(tphereMeta);
        gui.setItem(15, tphere);
        
        // Sezione operatori
        if (player.isOp()) {
            // Separatore
            ItemStack separator = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
            ItemMeta separatorMeta = separator.getItemMeta();
            separatorMeta.setDisplayName("§5§l-- Comandi Operatore --");
            separator.setItemMeta(separatorMeta);
            
            for (int i = 18; i < 27; i++) {
                gui.setItem(i, separator);
            }
            
            // TP a Coordinate
            ItemStack tpCoords = new ItemStack(Material.MAP);
            ItemMeta tpCoordsMeta = tpCoords.getItemMeta();
            tpCoordsMeta.setDisplayName("§5§lTP a Coordinate");
            tpCoordsMeta.setLore(Arrays.asList(
                "§7Teletrasportati a coordinate specifiche",
                "",
                "§7Comando: §f/tp <x> <y> <z>",
                "",
                "§c§lSolo per operatori",
                "",
                "§e➜ Click per info"
            ));
            tpCoordsMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            tpCoordsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            tpCoords.setItemMeta(tpCoordsMeta);
            gui.setItem(29, tpCoords);
            
            // TP Player to Player
            ItemStack tpPlayerToPlayer = new ItemStack(Material.ENDER_EYE);
            ItemMeta tpPlayerToPlayerMeta = tpPlayerToPlayer.getItemMeta();
            tpPlayerToPlayerMeta.setDisplayName("§5§lTP Giocatore → Giocatore");
            tpPlayerToPlayerMeta.setLore(Arrays.asList(
                "§7Teletrasporta un giocatore da un altro",
                "",
                "§7Comando: §f/tp <giocatore> <destinazione>",
                "",
                "§c§lSolo per operatori",
                "",
                "§e➜ Click per info"
            ));
            tpPlayerToPlayerMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            tpPlayerToPlayerMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            tpPlayerToPlayer.setItemMeta(tpPlayerToPlayerMeta);
            gui.setItem(31, tpPlayerToPlayer);
            
            // TP Player to Coords
            ItemStack tpPlayerToCoords = new ItemStack(Material.END_CRYSTAL);
            ItemMeta tpPlayerToCoordsMeta = tpPlayerToCoords.getItemMeta();
            tpPlayerToCoordsMeta.setDisplayName("§5§lTP Giocatore → Coordinate");
            tpPlayerToCoordsMeta.setLore(Arrays.asList(
                "§7Teletrasporta un giocatore a coordinate",
                "",
                "§7Comando: §f/tp <giocatore> <x> <y> <z>",
                "",
                "§c§lSolo per operatori",
                "",
                "§e➜ Click per info"
            ));
            tpPlayerToCoordsMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            tpPlayerToCoordsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            tpPlayerToCoords.setItemMeta(tpPlayerToCoordsMeta);
            gui.setItem(33, tpPlayerToCoords);
        }
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna Indietro");
        back.setItemMeta(backMeta);
        gui.setItem(40, back);
        
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "teleport");
    }

    // Metodo per aprire il menu trasformazione
    public void openTransformMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE + " §8» §dTrasformazioni");
        
        // Siedi
        ItemStack sit = new ItemStack(Material.OAK_STAIRS);
        ItemMeta sitMeta = sit.getItemMeta();
        sitMeta.setDisplayName("§d§lSiedi");
        sitMeta.setLore(Arrays.asList(
            "§7Siediti come una sedia",
            "",
            "§e➜ Click per sederti"
        ));
        sit.setItemMeta(sitMeta);
        gui.setItem(11, sit);
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna Indietro");
        back.setItemMeta(backMeta);
        gui.setItem(26, back);
        
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "transform");
    }

    // Metodo per aprire il menu items
    public void openCustomItemsMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, GUI_TITLE + " §8» §6Items Custom");
        
        // Panino
        ItemStack panino = new ItemStack(Material.BEEF);
        ItemMeta paninoMeta = panino.getItemMeta();
        paninoMeta.setDisplayName("§6§lPanino");
        paninoMeta.setCustomModelData(800009);
        paninoMeta.setLore(Arrays.asList(
            "§7Un delizioso panino",
            "§7con carne di manzo",
            "",
            "§bEffetti:",
            "§7- +5 cuori",
            "§7- Fame al massimo",
            "",
            "§e➜ Click per la ricetta"
        ));
        panino.setItemMeta(paninoMeta);
        gui.setItem(11, panino);
        
        // Coca Cola
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
            "§7- +3 Fame",
            "",
            "§e➜ Click per la ricetta"
        ));
        cola.setItemMeta(colaMeta);
        gui.setItem(13, cola);
        
        // Patatine
        ItemStack patatine = new ItemStack(Material.POTATO);
        ItemMeta patatineMeta = patatine.getItemMeta();
        patatineMeta.setDisplayName("§6§lPatatine Fritte");
        patatineMeta.setCustomModelData(800014);
        patatineMeta.setLore(Arrays.asList(
            "§7Croccanti patatine fritte",
            "",
            "§bEffetti:",
            "§7- Velocità di scavo (60s)",
            "§7- +4 Fame",
            "",
            "§e➜ Click per la ricetta"
        ));
        patatine.setItemMeta(patatineMeta);
        gui.setItem(15, patatine);
        
        // Guinzaglio Magico
        ItemStack guinzaglio = new ItemStack(Material.LEAD);
        ItemMeta guinzaglioMeta = guinzaglio.getItemMeta();
        guinzaglioMeta.setDisplayName("§d§lGuinzaglio Magico");
        guinzaglioMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        guinzaglioMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        guinzaglioMeta.setLore(Arrays.asList(
            "§7Trasforma i giocatori in cani",
            "§7e controllali",
            "",
            "§e➜ Click per la ricetta"
        ));
        guinzaglio.setItemMeta(guinzaglioMeta);
        gui.setItem(22, guinzaglio);
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna Indietro");
        back.setItemMeta(backMeta);
        gui.setItem(31, back);
        
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "items");
    }

    // Metodo per mostrare le ricette degli item
    public void showItemRecipe(Player player, String item) {
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE + " §8» §6Ricetta");
        
        // Bordo decorativo superiore
        ItemStack border = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName("§e✦");
        border.setItemMeta(borderMeta);
        
        for(int i = 0; i < 9; i++) {
            gui.setItem(i, border);
        }
        
        Map<String, ItemStack> recipes = plugin.getRegisteredRecipes();
        ItemStack result = recipes.get(item);
        
        switch (item) {
            case "panino":
                // Ricetta del panino (B = BREAD, C = COOKED_BEEF)
                ItemStack bread = new ItemStack(Material.BREAD);
                ItemStack beef = new ItemStack(Material.COOKED_BEEF);
                
                gui.setItem(11, bread);
                gui.setItem(20, beef);
                gui.setItem(29, bread);
                gui.setItem(24, new ItemStack(Material.ARROW)); // Freccia decorativa
                gui.setItem(25, result);
                break;
                
            case "coca_cola":
                // Ricetta della Coca Cola
                ItemStack sugar = new ItemStack(Material.SUGAR);
                ItemStack water = new ItemStack(Material.POTION);
                ItemStack dye = new ItemStack(Material.BLACK_DYE);
                
                gui.setItem(11, sugar);
                gui.setItem(20, water);
                gui.setItem(29, dye);
                gui.setItem(24, new ItemStack(Material.ARROW)); // Freccia decorativa
                gui.setItem(25, result);
                break;
                
            case "patatine":
                // Ricetta delle patatine
                ItemStack potato = new ItemStack(Material.BAKED_POTATO);
                ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
                ItemStack paper = new ItemStack(Material.PAPER);
                
                gui.setItem(11, potato);
                gui.setItem(20, sword);
                gui.setItem(29, paper);
                gui.setItem(24, new ItemStack(Material.ARROW)); // Freccia decorativa
                gui.setItem(25, result);
                break;
                
            case "guinzaglio":
                // Ricetta del guinzaglio magico
                ItemStack gold = new ItemStack(Material.GOLD_INGOT);
                ItemStack diamond = new ItemStack(Material.DIAMOND);
                ItemStack lead = new ItemStack(Material.LEAD);
                
                gui.setItem(11, gold);
                gui.setItem(12, diamond);
                gui.setItem(13, gold);
                gui.setItem(20, diamond);
                gui.setItem(21, lead);
                gui.setItem(22, diamond);
                gui.setItem(29, gold);
                gui.setItem(30, diamond);
                gui.setItem(31, gold);
                gui.setItem(24, new ItemStack(Material.ARROW)); // Freccia decorativa
                gui.setItem(25, result);
                break;
        }
        
        // Bordo inferiore decorativo
        for(int i = 45; i < 54; i++) {
            gui.setItem(i, border);
        }
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna al Menu");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);
        
        // Riempi gli spazi vuoti con vetro nero
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "item_recipe");
    }

    // Metodo per aprire il menu addon
    public void openAddonManagerMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE + " §8» §dAddons");
        
        // Sistema Treni
        if (plugin.hasTrainAddon()) {
            ItemStack train = new ItemStack(Material.POWERED_RAIL);
            ItemMeta trainMeta = train.getItemMeta();
            trainMeta.setDisplayName("§6§lSistema Treni");
            trainMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            trainMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> trainLore = new ArrayList<>();
            trainLore.add("§7Plugin: §fGavaCore-TrainAddons");
            trainLore.add("§7Gestisci il sistema dei treni");
            trainLore.add("§7e delle stazioni");
            trainLore.add("");
            trainLore.add("§a§lSTATO: §aAttivo");
            trainLore.add("§7Versione: §f" + plugin.getTrainAddonVersion());
            trainLore.add("");
            trainLore.add("§e➜ Click per aprire");
            trainMeta.setLore(trainLore);
            train.setItemMeta(trainMeta);
            gui.setItem(10, train);
        }
        
        // Sistema Postale
        if (plugin.hasPosteAddon()) {
            ItemStack post = new ItemStack(Material.WRITABLE_BOOK);
            ItemMeta postMeta = post.getItemMeta();
            postMeta.setDisplayName("§e§lSistema Postale");
            postMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            postMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> postLore = new ArrayList<>();
            postLore.add("§7Plugin: §fGavaCore-PosteAddons");
            postLore.add("§7Sistema di posta tra");
            postLore.add("§7i giocatori");
            postLore.add("");
            postLore.add("§a§lSTATO: §aAttivo");
            postLore.add("§7Versione: §f" + plugin.getPosteAddonVersion());
            postLore.add("");
            postLore.add("§e➜ Click per aprire");
            postMeta.setLore(postLore);
            post.setItemMeta(postMeta);
            gui.setItem(12, post);
        }
        
        // BackpackPlus
        if (plugin.hasBackpackPlus()) {
            ItemStack backpack = new ItemStack(Material.BROWN_DYE);
            ItemMeta backpackMeta = backpack.getItemMeta();
            backpackMeta.setDisplayName("§6§lBackpackPlus");
            backpackMeta.setCustomModelData(800001);
            backpackMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            backpackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> backpackLore = new ArrayList<>();
            backpackLore.add("§7Plugin: §fBackpackPlus");
            backpackLore.add("§7Sistema di zaini");
            backpackLore.add("§7personalizzati");
            backpackLore.add("");
            backpackLore.add("§a§lSTATO: §aAttivo");
            backpackLore.add("§7Versione: §f" + plugin.getBackpackVersion());
            backpackLore.add("");
            backpackLore.add("§e➜ Click per aprire");
            backpackMeta.setLore(backpackLore);
            backpack.setItemMeta(backpackMeta);
            gui.setItem(14, backpack);
        }
        
        // Coins
        if (plugin.hasCoins()) {
            ItemStack coins = new ItemStack(Material.GOLD_INGOT);
            ItemMeta coinsMeta = coins.getItemMeta();
            coinsMeta.setDisplayName("§e§lCoins");
            coinsMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            coinsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> coinsLore = new ArrayList<>();
            coinsLore.add("§7Plugin: §fGavaCore-Coins");
            coinsLore.add("§7Sistema di economia");
            coinsLore.add("§7del server");
            coinsLore.add("");
            coinsLore.add("§a§lSTATO: §aAttivo");
            coinsLore.add("§7Versione: §f" + plugin.getCoinsVersion());
            coinsLore.add("");
            coinsLore.add("§e➜ Click per info");
            coinsMeta.setLore(coinsLore);
            coins.setItemMeta(coinsMeta);
            gui.setItem(16, coins);
        }
        
        // iConomy
        if (plugin.hasIConomy()) {
            ItemStack iconomy = new ItemStack(Material.EMERALD);
            ItemMeta iconomyMeta = iconomy.getItemMeta();
            iconomyMeta.setDisplayName("§a§liConomy");
            iconomyMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            iconomyMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> iconomyLore = new ArrayList<>();
            iconomyLore.add("§7Plugin: §fiConomyUnlocked");
            iconomyLore.add("§7Sistema di economia");
            iconomyLore.add("§7avanzato");
            iconomyLore.add("");
            iconomyLore.add("§a§lSTATO: §aAttivo");
            iconomyLore.add("§7Versione: §f" + plugin.getIconomyVersion());
            iconomyLore.add("");
            iconomyLore.add("§e➜ Click per info");
            iconomyMeta.setLore(iconomyLore);
            iconomy.setItemMeta(iconomyMeta);
            gui.setItem(28, iconomy);
        }
        
        // SkinRestorer
        if (plugin.hasSkinRestorer()) {
            ItemStack skin = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta skinMeta = skin.getItemMeta();
            skinMeta.setDisplayName("§e§lSkinRestorer");
            skinMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            skinMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> skinLore = new ArrayList<>();
            skinLore.add("§7Plugin: §fSkinsRestorer");
            skinLore.add("§7Sistema di gestione skin");
            skinLore.add("§7personalizzate");
            skinLore.add("");
            skinLore.add("§a§lSTATO: §aAttivo");
            skinLore.add("§7Versione: §f" + plugin.getSkinRestorerVersion());
            skinLore.add("");
            skinLore.add("§e➜ Click per info");
            skinMeta.setLore(skinLore);
            skin.setItemMeta(skinMeta);
            gui.setItem(30, skin);
        }
        
        // SmoothTimber
        if (plugin.hasSmoothTimber()) {
            ItemStack timber = new ItemStack(Material.DIAMOND_AXE);
            ItemMeta timberMeta = timber.getItemMeta();
            timberMeta.setDisplayName("§a§lSmooth Timber");
            timberMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            timberMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            List<String> timberLore = new ArrayList<>();
            timberLore.add("§7Plugin: §fSmoothTimber");
            timberLore.add("§7Taglia alberi interi");
            timberLore.add("§7istantaneamente");
            timberLore.add("");
            timberLore.add("§a§lSTATO: §aAttivo");
            timberLore.add("§7Versione: §f" + plugin.getSmoothTimberVersion());
            timberLore.add("");
            timberLore.add("§e➜ Click per info");
            timberMeta.setLore(timberLore);
            timber.setItemMeta(timberMeta);
            gui.setItem(32, timber);
        }
        
        // Plugin non installati
        ItemStack missing = new ItemStack(Material.BARRIER);
        ItemMeta missingMeta = missing.getItemMeta();
        missingMeta.setDisplayName("§c§lPlugin Mancanti");
        List<String> missingLore = new ArrayList<>();
        missingLore.add("§7Plugin non installati:");
        missingLore.add("");
        if (!plugin.hasTrainAddon()) missingLore.add("§c✘ §7GavaCore-TrainAddons");
        if (!plugin.hasPosteAddon()) missingLore.add("§c✘ §7GavaCore-PosteAddons");
        if (!plugin.hasBackpackPlus()) missingLore.add("§c✘ §7BackpackPlus");
        if (!plugin.hasCoins()) missingLore.add("§c✘ §7GavaCore-Coins");
        if (!plugin.hasIConomy()) missingLore.add("§c✘ §7iConomyUnlocked");
        if (!plugin.hasSkinRestorer()) missingLore.add("§c✘ §7SkinsRestorer");
        if (!plugin.hasSmoothTimber()) missingLore.add("§c✘ §7SmoothTimber");
        missingLore.add("");
        missingLore.add("§7Contatta un amministratore");
        missingLore.add("§7per installarli");
        missingMeta.setLore(missingLore);
        missing.setItemMeta(missingMeta);
        gui.setItem(40, missing);
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna Indietro");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);
        
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "addon_manager");
    }

    // Metodo per aprire il menu backpack
    public void openBackpackMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, GUI_TITLE + " §8» §bBackpack");
        
        // Leggi la configurazione di BackpackPlus
        File backpackConfigFile = new File(plugin.getServer().getPluginManager().getPlugin("BackpackPlus").getDataFolder(), "config.yml");
        if (!backpackConfigFile.exists()) {
            player.sendMessage("§c§l[GavaCore] §cErrore: File di configurazione BackpackPlus non trovato!");
            return;
        }

        FileConfiguration backpackConfig = YamlConfiguration.loadConfiguration(backpackConfigFile);
        
        // Zaino Base
        ItemStack basic = new ItemStack(Material.BROWN_DYE);
        ItemMeta basicMeta = basic.getItemMeta();
        basicMeta.setDisplayName(backpackConfig.getString("backpack-tiers.1.displayName", "§6§lZaino Base")
                .replace("#E3680E", "§6"));
        basicMeta.setCustomModelData(800001);
        List<String> basicLore = Arrays.asList(
            "§7Uno zaino base con",
            "§7" + backpackConfig.getInt("backpack-tiers.1.size") + " slot di spazio",
            "",
            "§e➜ Click per vedere la ricetta"
        );
        basicMeta.setLore(basicLore);
        basic.setItemMeta(basicMeta);
        gui.setItem(11, basic);
        
        // Zaino d'Oro
        ItemStack gold = new ItemStack(Material.BROWN_DYE);
        ItemMeta goldMeta = gold.getItemMeta();
        goldMeta.setDisplayName(backpackConfig.getString("backpack-tiers.2.displayName", "§6§lZaino d'Oro")
                .replace("#E0B715", "§6"));
        goldMeta.setCustomModelData(800002);
        List<String> goldLore = Arrays.asList(
            "§7Uno zaino medio con",
            "§7" + backpackConfig.getInt("backpack-tiers.2.size") + " slot di spazio",
            "",
            "§e➜ Click per vedere la ricetta"
        );
        goldMeta.setLore(goldLore);
        gold.setItemMeta(goldMeta);
        gui.setItem(13, gold);
        
        // Zaino di Diamante
        ItemStack diamond = new ItemStack(Material.BROWN_DYE);
        ItemMeta diamondMeta = diamond.getItemMeta();
        diamondMeta.setDisplayName(backpackConfig.getString("backpack-tiers.3.displayName", "§b§lZaino di Diamante")
                .replace("#00C2C6", "§b"));
        diamondMeta.setCustomModelData(800003);
        List<String> diamondLore = Arrays.asList(
            "§7Uno zaino grande con",
            "§7" + backpackConfig.getInt("backpack-tiers.3.size") + " slot di spazio",
            "",
            "§e➜ Click per vedere la ricetta"
        );
        diamondMeta.setLore(diamondLore);
        diamond.setItemMeta(diamondMeta);
        gui.setItem(15, diamond);
        
        // Aggiungi il pulsante per tornare indietro
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna Indietro");
        back.setItemMeta(backMeta);
        gui.setItem(31, back);
        
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "backpack");
    }

    // Metodo per mostrare le ricette dei backpack
    public void showBackpackRecipe(Player player, int modelData) {
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE + " §8» §6Crafting Zaino");
        
        // Bordo decorativo superiore
        ItemStack border = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName("§e✦");
        border.setItemMeta(borderMeta);
        
        for(int i = 0; i < 9; i++) {
            gui.setItem(i, border);
        }
        
        // Leggi la configurazione di BackpackPlus
        File backpackConfigFile = new File(plugin.getServer().getPluginManager().getPlugin("BackpackPlus").getDataFolder(), "config.yml");
        if (!backpackConfigFile.exists()) {
            player.sendMessage("§c§l[GavaCore] §cErrore: File di configurazione BackpackPlus non trovato!");
            return;
        }

        FileConfiguration backpackConfig = YamlConfiguration.loadConfiguration(backpackConfigFile);
        String tierId = String.valueOf((modelData - 800000));
        
        // Imposta gli ingredienti dalla configurazione
        String basePath = "backpack-tiers." + tierId + ".crafting";
        if (backpackConfig.contains(basePath)) {
            for (String slot : backpackConfig.getConfigurationSection(basePath).getKeys(false)) {
                String materialName = backpackConfig.getString(basePath + "." + slot);
                Material material = Material.valueOf(materialName.toUpperCase());
                int gridSlot = 10 + (Integer.parseInt(slot) - 1) + ((Integer.parseInt(slot) - 1) / 3) * 6;
                gui.setItem(gridSlot, new ItemStack(material));
            }
        }
        
        // Crea il risultato
        ItemStack result = new ItemStack(Material.BROWN_DYE);
        ItemMeta resultMeta = result.getItemMeta();
        resultMeta.setDisplayName(backpackConfig.getString("backpack-tiers." + tierId + ".displayName", "Zaino")
                .replace("#E3680E", "§6")
                .replace("#E0B715", "§6")
                .replace("#00C2C6", "§b"));
        resultMeta.setCustomModelData(modelData);
        List<String> lore = Arrays.asList(
            "§7Uno zaino con",
            "§7" + backpackConfig.getInt("backpack-tiers." + tierId + ".size") + " slot di spazio",
            "",
            "§e➜ Click per craftare"
        );
        resultMeta.setLore(lore);
        result.setItemMeta(resultMeta);
        
        // Aggiungi freccia e risultato
        gui.setItem(23, createArrow());
        gui.setItem(25, result);
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna Indietro");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);
        
        // Riempi gli spazi vuoti
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "backpack_recipe");
    }

    // Metodo per aprire il menu dev
    public void openDevCommandsGUI(Player player) {
        if (!player.isOp() || !plugin.isDeveloperMode()) {
            player.closeInventory();
            return;
        }
        
        Inventory gui = Bukkit.createInventory(null, 36, GUI_TITLE + " §8» §c§lDev Tools");
        
        // Block Info
        ItemStack blockInfo = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta blockMeta = blockInfo.getItemMeta();
        blockMeta.setDisplayName("§e§lBlock Info");
        blockMeta.setLore(Arrays.asList(
            "§7Mostra informazioni sui blocchi",
            "§7quando li colpisci",
            "",
            "§e➜ Click per attivare/disattivare"
        ));
        blockInfo.setItemMeta(blockMeta);
        gui.setItem(11, blockInfo);
        
        // Entity Info
        ItemStack entityInfo = new ItemStack(Material.ZOMBIE_HEAD);
        ItemMeta entityMeta = entityInfo.getItemMeta();
        entityMeta.setDisplayName("§a§lEntity Info");
        entityMeta.setLore(Arrays.asList(
            "§7Mostra informazioni sulle entità",
            "§7quando le colpisci",
            "",
            "§e➜ Click per attivare/disattivare"
        ));
        entityInfo.setItemMeta(entityMeta);
        gui.setItem(13, entityInfo);
        
        // Performance Monitor
        ItemStack performance = new ItemStack(Material.CLOCK);
        ItemMeta perfMeta = performance.getItemMeta();
        perfMeta.setDisplayName("§b§lPerformance");
        perfMeta.setLore(Arrays.asList(
            "§7Monitora le performance",
            "§7del server",
            "",
            "§e➜ Click per attivare/disattivare"
        ));
        performance.setItemMeta(perfMeta);
        gui.setItem(15, performance);
        
        // No Clip
        ItemStack noClip = new ItemStack(Material.FEATHER);
        ItemMeta noClipMeta = noClip.getItemMeta();
        noClipMeta.setDisplayName("§d§lNo Clip");
        noClipMeta.setLore(Arrays.asList(
            "§7Attraversa i blocchi",
            "§7in modalità spettatore",
            "",
            "§e➜ Click per attivare/disattivare"
        ));
        noClip.setItemMeta(noClipMeta);
        gui.setItem(21, noClip);
        
        // Debug
        ItemStack debug = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta debugMeta = debug.getItemMeta();
        debugMeta.setDisplayName("§c§lDebug");
        debugMeta.setLore(Arrays.asList(
            "§7Informazioni di debug",
            "§7sui plugin",
            "",
            "§e➜ Click per aprire"
        ));
        debug.setItemMeta(debugMeta);
        gui.setItem(23, debug);
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna Indietro");
        back.setItemMeta(backMeta);
        gui.setItem(31, back);
        
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "dev");
    }

    // Metodo rimosso - La funzionalità di protezione è stata sostituita con il TreeCutter
    public void handleProtectionMenuClick(Player player, ItemStack clicked) {
        // Reindirizza al menu TreeCutter
        openTreeCutterInfoMenu(player);
    }

    public void handleWeatherMenuClick(Player player, ItemStack clicked) {
        Material type = clicked.getType();
        
        if (type == Material.ARROW) {
            openMainMenu(player);
            return;
        }
        
        if (type == Material.SUNFLOWER) {
            player.performCommand("weather clear");
            player.closeInventory();
        } else if (type == Material.WATER_BUCKET) {
            player.performCommand("weather rain");
            player.closeInventory();
        } else if (type == Material.LIGHTNING_ROD) {
            player.performCommand("weather thunder");
            player.closeInventory();
        }
    }

    public void handleGamemodeMenuClick(Player player, ItemStack clicked) {
        Material type = clicked.getType();
        
        if (type == Material.ARROW) {
            openMainMenu(player);
            return;
        }
        
        if (type == Material.GRASS_BLOCK) {
            player.performCommand("gamemode survival");
            player.closeInventory();
        } else if (type == Material.FEATHER) {
            player.performCommand("gamemode creative");
            player.closeInventory();
        } else if (type == Material.ENDER_EYE) {
            player.performCommand("gamemode spectator");
            player.closeInventory();
        } else if (type == Material.BARRIER) {
            player.performCommand("gamemode adventure");
            player.closeInventory();
        }
    }

    public void handleChatMenuClick(Player player, ItemStack clicked) {
        Material type = clicked.getType();
        
        if (type == Material.ARROW) {
            openMainMenu(player);
            return;
        }
        
        if (type == Material.PAPER) {
            player.performCommand("chat clear");
            player.closeInventory();
        } else if (type == Material.REDSTONE) {
            player.performCommand("chat mute");
            player.closeInventory();
        } else if (type == Material.EMERALD) {
            player.performCommand("chat unmute");
            player.closeInventory();
        }
    }

    public void handleTeleportMenuClick(Player player, ItemStack clicked) {
        Material type = clicked.getType();
        
        if (type == Material.ARROW) {
            openMainMenu(player);
            return;
        }
        
        // Chiudi l'inventario prima di mostrare i tutorial
        player.closeInventory();
        
        if (type == Material.ENDER_PEARL) {
            // Tutorial Teletrasporto a
            player.sendMessage("§5§lTutorial Teletrasporto:");
            player.sendMessage("§7Per teletrasportarti da un altro giocatore:");
            player.sendMessage("§71. Usa §f/tp <giocatore> §7per inviare una richiesta");
            player.sendMessage("§72. Attendi che il giocatore accetti la richiesta");
            player.sendMessage("");
            player.sendMessage("§7Tip: La richiesta scade dopo " + 
                   (plugin.getConfig().contains("timings.teleport-request-timeout") ? 
                   plugin.getConfig().getInt("timings.teleport-request-timeout") : 60) + 
                   " secondi");
        } else if (type == Material.COMPASS) {
            // Tutorial Teletrasporto qui
            player.sendMessage("§5§lTutorial Teletrasporto Qui:");
            player.sendMessage("§7Per teletrasportare un giocatore da te:");
            player.sendMessage("§71. Usa §f/tpqui <giocatore> §7per inviare una richiesta");
            player.sendMessage("§72. Il giocatore dovrà accettare per essere teletrasportato");
            player.sendMessage("");
            player.sendMessage("§7Tip: La richiesta scade dopo " + 
                   (plugin.getConfig().contains("timings.teleport-request-timeout") ? 
                   plugin.getConfig().getInt("timings.teleport-request-timeout") : 60) + 
                   " secondi");
        } else if (type == Material.MAP && player.isOp()) {
            // Tutorial TP a Coordinate (solo operatori)
            player.sendMessage("§5§lTutorial TP a Coordinate:");
            player.sendMessage("§c§lSOLO PER OPERATORI");
            player.sendMessage("§7Per teletrasportarti a coordinate specifiche:");
            player.sendMessage("§71. Usa §f/tp <x> <y> <z>");
            player.sendMessage("§72. Verrai teletrasportato istantaneamente alle coordinate");
            player.sendMessage("");
            player.sendMessage("§7Esempio: §f/tp 100 64 -200");
        } else if (type == Material.ENDER_EYE && player.isOp()) {
            // Tutorial TP Giocatore → Giocatore (solo operatori)
            player.sendMessage("§5§lTutorial TP Giocatore → Giocatore:");
            player.sendMessage("§c§lSOLO PER OPERATORI");
            player.sendMessage("§7Per teletrasportare un giocatore verso un altro:");
            player.sendMessage("§71. Usa §f/tp <giocatore> <destinazione>");
            player.sendMessage("§72. Il giocatore verrà teletrasportato istantaneamente");
            player.sendMessage("");
            player.sendMessage("§7Esempio: §f/tp Steve Alex");
            player.sendMessage("§7Questo teletrasporterà Steve da Alex");
        } else if (type == Material.END_CRYSTAL && player.isOp()) {
            // Tutorial TP Giocatore → Coordinate (solo operatori)
            player.sendMessage("§5§lTutorial TP Giocatore → Coordinate:");
            player.sendMessage("§c§lSOLO PER OPERATORI");
            player.sendMessage("§7Per teletrasportare un giocatore a coordinate specifiche:");
            player.sendMessage("§71. Usa §f/tp <giocatore> <x> <y> <z>");
            player.sendMessage("§72. Il giocatore verrà teletrasportato istantaneamente");
            player.sendMessage("");
            player.sendMessage("§7Esempio: §f/tp Steve 100 64 -200");
            player.sendMessage("§7Questo teletrasporterà Steve alle coordinate specificate");
        } else if (type == Material.EMERALD) {
            // Tutorial Accetta teletrasporto
            player.sendMessage("§5§lTutorial Accetta Teletrasporto:");
            player.sendMessage("§7Quando ricevi una richiesta di teletrasporto:");
            player.sendMessage("§71. Usa §f/tpaccept <giocatore> §7per accettare la richiesta");
            player.sendMessage("§72. Verrai teletrasportato automaticamente");
            player.sendMessage("");
            player.sendMessage("§7Tip: La richiesta scade dopo " + 
                   (plugin.getConfig().contains("timings.teleport-request-timeout") ? 
                   plugin.getConfig().getInt("timings.teleport-request-timeout") : 60) + 
                   " secondi");
            player.sendMessage("§7Tip: Puoi rifiutare con §f/tpdeny <giocatore>");
        } else if (type == Material.REDSTONE) {
            // Tutorial Rifiuta teletrasporto
            player.sendMessage("§5§lTutorial Rifiuta Teletrasporto:");
            player.sendMessage("§7Per rifiutare una richiesta di teletrasporto:");
            player.sendMessage("§71. Usa §f/tpdeny <giocatore> §7per rifiutare la richiesta");
            player.sendMessage("§72. Il giocatore verrà notificato del rifiuto");
            player.sendMessage("");
            player.sendMessage("§7Tip: Le richieste vengono rifiutate automaticamente dopo " + 
                   (plugin.getConfig().contains("timings.teleport-request-timeout") ? 
                   plugin.getConfig().getInt("timings.teleport-request-timeout") : 60) + 
                   " secondi");
        }
    }

    public void handleTransformMenuClick(Player player, ItemStack clicked) {
        Material type = clicked.getType();
        
        if (type == Material.ARROW) {
            openMainMenu(player);
            return;
        }
        
        // Chiudi l'inventario prima di eseguire i comandi
        player.closeInventory();
        
        if (type == Material.OAK_STAIRS) {
            // Trasformazione in sedia (toggle)
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.performCommand("sedia");
            });
        }
    }

    public void handleItemsMenuClick(Player player, ItemStack clicked) {
        Material type = clicked.getType();
        
        if (type == Material.ARROW) {
            openMainMenu(player);
            return;
        }
        
        if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
            String displayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
            
            if (displayName.contains("coca cola")) {
                showItemRecipe(player, "coca_cola");
            } else if (displayName.contains("panino")) {
                showItemRecipe(player, "panino");
            } else if (displayName.contains("patatine")) {
                showItemRecipe(player, "patatine");
            } else if (displayName.contains("guinzaglio magico")) {
                showItemRecipe(player, "guinzaglio");
            }
        }
    }

    public void handleBackpackMenuClick(Player player, ItemStack clicked) {
        Material type = clicked.getType();
        
        if (type == Material.ARROW) {
            openMainMenu(player);
            return;
        }
        
        if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
            String displayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName().toLowerCase());
            
            if (displayName.contains("zaino base")) {
                showBackpackRecipe(player, 800001);
            } else if (displayName.contains("zaino d'oro")) {
                showBackpackRecipe(player, 800002);
            } else if (displayName.contains("zaino di diamante")) {
                showBackpackRecipe(player, 800003);
            }
        }
    }

    public void handlePluginsMenuClick(Player player, ItemStack clicked) {
        if (clicked == null) return;
        
        // Gestione del click sul bottone "Torna Indietro"
        if (clicked.getType() == Material.ARROW) {
            openMainMenu(player);
            return;
        }

        // Sistema Treni
        if (clicked.getType() == Material.POWERED_RAIL && 
            clicked.hasItemMeta() && 
            clicked.getItemMeta().getDisplayName().equals("§6§lSistema Treni")) {
            if (plugin.hasTrainAddon()) {
                player.closeInventory();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.performCommand("treni");
                });
            }
            return;
        }

        // Sistema Postale
        if (clicked.getType() == Material.WRITABLE_BOOK && 
            clicked.hasItemMeta() && 
            clicked.getItemMeta().getDisplayName().equals("§e§lSistema Postale")) {
            if (plugin.hasPosteAddon()) {
                player.closeInventory();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.performCommand("posta");
                });
            }
            return;
        }

        // BackpackPlus
        if (clicked.getType() == Material.BROWN_DYE && 
            clicked.hasItemMeta() && 
            clicked.getItemMeta().getDisplayName().equals("§6§lBackpackPlus")) {
            if (plugin.hasBackpackPlus()) {
                openBackpackMenu(player);
            }
            return;
        }

        // SkinRestorer
        if (clicked.getType() == Material.PLAYER_HEAD && 
            clicked.hasItemMeta() && 
            clicked.getItemMeta().getDisplayName().equals("§e§lSkinRestorer")) {
            if (plugin.hasSkinRestorer()) {
                player.closeInventory();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.performCommand("skin menu");
                });
            }
            return;
        }

        // Coins
        if (clicked.getType() == Material.GOLD_INGOT && 
            clicked.hasItemMeta() && 
            clicked.getItemMeta().getDisplayName().equals("§e§lCoins")) {
            if (plugin.hasCoins()) {
                openCoinsMenu(player);
            }
            return;
        }
    }

    public void openRecipeMenu(Player player, String itemName) {
        // Apri il menu della ricetta per l'item specificato
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE + " §8» §6Ricetta");
        
        // Riempi l'inventario con la ricetta appropriata
        String item = itemName.toLowerCase();
        if (item.equals("panino")) {
            showItemRecipe(player, "panino");
        } else if (item.equals("coca cola")) {
            showItemRecipe(player, "coca_cola");
        } else if (item.equals("patatine fritte")) {
            showItemRecipe(player, "patatine");
        } else if (item.equals("guinzaglio magico")) {
            showItemRecipe(player, "guinzaglio");
        }
    }

    public void openBackpackRecipeMenu(Player player) {
        // Apri il menu delle ricette degli zaini
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE + " §8» §6Ricette Zaini");
        
        // Mostra le ricette di tutti i tipi di zaino
        showBackpackRecipe(player, 800001); // Zaino base
        showBackpackRecipe(player, 800002); // Zaino d'oro
        showBackpackRecipe(player, 800003); // Zaino di diamante
    }

    public void openDevToolsMenu(Player player) {
        if (!player.hasPermission("gavacore.developer")) {
            player.sendMessage("§c§l[GavaCore] §cNon hai il permesso di accedere a questo menu!");
            return;
        }
        
        openDevCommandsGUI(player);
    }

    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            openMenus.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clicked = event.getCurrentItem();
        
        // Verifica se l'inventario appartiene al plugin
        if (!title.startsWith(GUI_TITLE) && !title.equals("§e§lInserisci l'importo")) return;
        
        // Cancella SEMPRE l'evento per tutti i menu del plugin
        event.setCancelled(true);
        
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // Menu principale
        if (title.equals(GUI_TITLE)) {
            Material type = clicked.getType();
            if (type == Material.DIAMOND_AXE) {
                openTreeCutterInfoMenu(player);
            } else if (type == Material.WATER_BUCKET) {
                openWeatherMenu(player);
            } else if (type == Material.DIAMOND_SWORD) {
                openGamemodeMenu(player);
            } else if (type == Material.WRITABLE_BOOK) {
                openChatMenu(player);
            } else if (type == Material.ENDER_PEARL) {
                openTeleportMenu(player);
            } else if (type == Material.BONE) {
                openTransformMenu(player);
            } else if (type == Material.CHEST) {
                openCustomItemsMenu(player);
            } else if (type == Material.BROWN_DYE && clicked.getItemMeta().hasCustomModelData() && clicked.getItemMeta().getCustomModelData() == 800001) {
                openBackpackMenu(player);
            } else if (type == Material.NETHER_STAR) {
                openPluginsMenu(player);
            } else if (type == Material.COMMAND_BLOCK && player.isOp()) {
                if (plugin.isDeveloperMode()) {
                    openDevCommandsGUI(player);
                } else {
                    player.sendMessage("§c§l[GavaCore] §cAttiva prima la modalità sviluppatore con /developer-mode");
                    player.closeInventory();
                }
            }
            return;
        }
        
        // Menu informazioni sul TreeCutter
        if (title.equals(GUI_TITLE + " §8» §aTaglialegna")) {
            if (clicked.getType() == Material.BARRIER) {
                openMainMenu(player);
            }
            return;
        }
        
        // Menu meteo
        if (title.equals(GUI_TITLE + " §8» §bMeteo")) {
            handleWeatherMenuClick(player, clicked);
            return;
        }
        
        // Menu gamemode
        if (title.equals(GUI_TITLE + " §8» §aGamemode")) {
            handleGamemodeMenuClick(player, clicked);
            return;
        }
        
        // Menu chat
        if (title.equals(GUI_TITLE + " §8» §aChat")) {
            handleChatMenuClick(player, clicked);
            return;
        }
        
        // Menu teleport
        if (title.equals(GUI_TITLE + " §8» §5Teleport")) {
            handleTeleportMenuClick(player, clicked);
            return;
        }
        
        // Menu trasformazioni
        if (title.equals(GUI_TITLE + " §8» §dTrasformazioni")) {
            handleTransformMenuClick(player, clicked);
            return;
        }
        
        // Menu items custom
        if (title.equals(GUI_TITLE + " §8» §6Items Custom")) {
            handleItemsMenuClick(player, clicked);
            return;
        }
        
        // Menu ricetta item
        if (title.equals(GUI_TITLE + " §8» §6Ricetta")) {
            if (clicked.getType() == Material.BARRIER || clicked.getType() == Material.ARROW) {
                openCustomItemsMenu(player);
            }
            return;
        }
        
        // Menu backpack
        if (title.equals(GUI_TITLE + " §8» §bBackpack")) {
            if (clicked.getType() == Material.BROWN_DYE && clicked.hasItemMeta()) {
                ItemMeta meta = clicked.getItemMeta();
                if (meta.hasCustomModelData()) {
                    int modelData = meta.getCustomModelData();
                    if (modelData >= 800001 && modelData <= 800003) {
                        showBackpackRecipe(player, modelData);
                        return;
                    }
                }
            } else if (clicked.getType() == Material.ARROW) {
                openMainMenu(player);
                return;
            }
            event.setCancelled(true);
            return;
        }
        
        // Menu ricetta backpack
        if (title.equals(GUI_TITLE + " §8» §6Crafting Zaino")) {
            if (clicked.getType() == Material.ARROW) {
                openBackpackMenu(player);
            }
            event.setCancelled(true);
            return;
        }
        
        // Menu plugins
        if (title.equals(GUI_TITLE + " §8» §5Plugins")) {
            handlePluginsMenuClick(player, clicked);
            return;
        }
        
        // Menu dev tools
        if (title.equals(GUI_TITLE + " §8» §c§lDev Tools")) {
            if (clicked.getType() == Material.ARROW) {
                openMainMenu(player);
                return;
            }
            
            Material type = clicked.getType();
            if (type == Material.GRASS_BLOCK) { // Block Info
                player.performCommand("dev blockinfo");
                player.closeInventory();
            } else if (type == Material.ZOMBIE_HEAD) { // Entity Info
                player.performCommand("dev entityinfo");
                player.closeInventory();
            } else if (type == Material.CLOCK) { // Performance
                player.performCommand("dev performance");
                player.closeInventory();
            } else if (type == Material.FEATHER) { // No Clip
                player.performCommand("dev noclip");
                player.closeInventory();
            } else if (type == Material.COMMAND_BLOCK) { // Debug
                player.performCommand("dev debug");
                player.closeInventory();
            }
        }

        // Menu Coins
        if (title.equals(GUI_TITLE + " §8» §eCoins")) {
            handleCoinsMenuClick(player, clicked);
            event.setCancelled(true);
            return;
        }
        
        // Menu importo monete
        if (title.equals("§e§lInserisci l'importo")) {
            if (!clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;
            
            UUID playerId = player.getUniqueId();
            if (!amountInputs.containsKey(playerId)) {
                amountInputs.put(playerId, new StringBuilder());
            }
            StringBuilder currentInput = amountInputs.get(playerId);
            
            String displayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            
            // Gestione dei numeri
            if (clicked.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
                try {
                    Integer.parseInt(displayName);
                    if (currentInput.length() < 9) {
                        currentInput.append(displayName);
                        updateDisplay(event.getInventory(), currentInput.toString());
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    }
                } catch (NumberFormatException ignored) {}
            }
            // Gestione cancella tutto
            else if (clicked.getType() == Material.RED_STAINED_GLASS_PANE) {
                currentInput.setLength(0);
                updateDisplay(event.getInventory(), "0");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            }
            // Gestione cancella ultima cifra
            else if (clicked.getType() == Material.ORANGE_STAINED_GLASS_PANE) {
                if (currentInput.length() > 0) {
                    currentInput.setLength(currentInput.length() - 1);
                    String newAmount = currentInput.length() > 0 ? currentInput.toString() : "0";
                    updateDisplay(event.getInventory(), newAmount);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.8f);
                }
            }
            // Gestione conferma
            else if (clicked.getType() == Material.LIME_STAINED_GLASS_PANE) {
                String amount = currentInput.toString();
                if (amount.length() > 0 && isValidNumber(amount)) {
                    player.closeInventory();
                    player.sendMessage("§a§lConversione in corso...");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    String finalAmount = amount;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.performCommand("monete-fisiche " + finalAmount);
                    }, 1L);
                    amountInputs.remove(playerId);
                } else {
                    player.sendMessage("§c§l[GavaCore] §cInserisci un numero valido!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                }
            }
            // Gestione torna indietro
            else if (clicked.getType() == Material.ARROW) {
                amountInputs.remove(playerId);
                openCoinsMenu(player);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.5f);
            }
            return;
        }
    }

    private void handleMainMenuClick(Player player, ItemStack clicked) {
        Material type = clicked.getType();
        if (type == Material.DIAMOND_AXE) {
            openTreeCutterInfoMenu(player);
        } else if (type == Material.WATER_BUCKET) {
            openWeatherMenu(player);
        } else if (type == Material.DIAMOND_SWORD) {
            openGamemodeMenu(player);
        } else if (type == Material.WRITABLE_BOOK) {
            openChatMenu(player);
        } else if (type == Material.ENDER_PEARL) {
            openTeleportMenu(player);
        } else if (type == Material.BONE) {
            openTransformMenu(player);
        } else if (type == Material.CHEST) {
            openCustomItemsMenu(player);
        } else if (type == Material.BROWN_DYE && plugin.hasBackpackPlus()) {
            openBackpackMenu(player);
        } else if (type == Material.NETHER_STAR) {
            openPluginsMenu(player);
        } else if (type == Material.COMMAND_BLOCK) {
            if (player.hasPermission("mioplugin.devtools")) {
                openDevToolsMenu(player);
            }
        }
    }
    
    // Metodo per aprire il menu informazioni sul TreeCutter
    public void openTreeCutterInfoMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE + " §8» §aTaglialegna");
        
        // Icona principale
        ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        axeMeta.setDisplayName("§a§lTreeCutter");
        axeMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        axeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        List<String> axeLore = Arrays.asList(
            "§7Il sistema TreeCutter ti permette",
            "§7di abbattere interi alberi con",
            "§7un solo colpo mentre sei in shift.",
            "",
            "§7Funziona con qualsiasi tipo di ascia!"
        );
        axeMeta.setLore(axeLore);
        axe.setItemMeta(axeMeta);
        gui.setItem(4, axe);
        
        // Come funziona
        ItemStack howTo = new ItemStack(Material.BOOK);
        ItemMeta howToMeta = howTo.getItemMeta();
        howToMeta.setDisplayName("§e§lCome Funziona");
        List<String> howToLore = Arrays.asList(
            "§71. §fEquipaggia un'ascia",
            "§72. §fPremi SHIFT (accovacciati)",
            "§73. §fColpisci il tronco più basso dell'albero",
            "§74. §fL'albero sarà abbattuto automaticamente",
            "§7   con un'animazione dal basso verso l'alto",
            "",
            "§cNota: §7Più durabile è l'ascia, più",
            "§7alberi potrai tagliare. L'ascia perderà",
            "§7durabilità in base alle dimensioni dell'albero."
        );
        howToMeta.setLore(howToLore);
        howTo.setItemMeta(howToMeta);
        gui.setItem(11, howTo);
        
        // Velocità di taglio
        ItemStack speed = new ItemStack(Material.CLOCK);
        ItemMeta speedMeta = speed.getItemMeta();
        speedMeta.setDisplayName("§b§lVelocità di Taglio");
        List<String> speedLore = Arrays.asList(
            "§7La velocità di taglio dipende dal",
            "§7tipo di ascia utilizzata:",
            "",
            "§6Ascia d'Oro: §fVelocissima",
            "§bAscia di Diamante: §fVeloce",
            "§fAscia di Ferro: §fNormale",
            "§8Ascia di Pietra: §fLenta",
            "§eAscia di Legno: §fMolto lenta"
        );
        speedMeta.setLore(speedLore);
        speed.setItemMeta(speedMeta);
        gui.setItem(13, speed);
        
        // Tipi di alberi
        ItemStack trees = new ItemStack(Material.OAK_LOG);
        ItemMeta treesMeta = trees.getItemMeta();
        treesMeta.setDisplayName("§2§lTipi di Alberi");
        List<String> treesLore = Arrays.asList(
            "§7Funziona con tutti i tipi di alberi:",
            "",
            "§8• §fQuercia",
            "§8• §fBetulla",
            "§8• §fAbete",
            "§8• §fGiungla",
            "§8• §fAcacia",
            "§8• §fFunghi giganti",
            "§8• §fAlberi di Azalea"
        );
        treesMeta.setLore(treesLore);
        trees.setItemMeta(treesMeta);
        gui.setItem(15, trees);
        
        // Bottone per tornare al menu principale
        gui.setItem(22, createBackButton());
        
        // Riempi gli spazi vuoti con vetro nero
        fillWithGlass(gui);
        
        // Apri il menu
        player.openInventory(gui);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
        
        // Tieni traccia del menu aperto
        openMenus.put(player.getUniqueId(), "treecutter_info");
    }

    public void openPluginsMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE + " §8» §5Plugins");
        
        // Sistema Treni
        if (plugin.hasTrainAddon()) {
            ItemStack train = new ItemStack(Material.POWERED_RAIL);
            ItemMeta trainMeta = train.getItemMeta();
            trainMeta.setDisplayName("§6§lSistema Treni");
            trainMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            trainMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> trainLore = new ArrayList<>();
            trainLore.add("§7Plugin: §fGavaCore-TrainAddons");
            trainLore.add("§7Gestisci il sistema dei treni");
            trainLore.add("§7e delle stazioni");
            trainLore.add("");
            trainLore.add("§a§lSTATO: §aAttivo");
            trainLore.add("§7Versione: §f" + plugin.getTrainAddonVersion());
            trainLore.add("");
            trainLore.add("§e➜ Click per aprire");
            trainMeta.setLore(trainLore);
            train.setItemMeta(trainMeta);
            gui.setItem(10, train);
        }
        
        // Sistema Postale
        if (plugin.hasPosteAddon()) {
            ItemStack post = new ItemStack(Material.WRITABLE_BOOK);
            ItemMeta postMeta = post.getItemMeta();
            postMeta.setDisplayName("§e§lSistema Postale");
            postMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            postMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> postLore = new ArrayList<>();
            postLore.add("§7Plugin: §fGavaCore-PosteAddons");
            postLore.add("§7Sistema di posta tra");
            postLore.add("§7i giocatori");
            postLore.add("");
            postLore.add("§a§lSTATO: §aAttivo");
            postLore.add("§7Versione: §f" + plugin.getPosteAddonVersion());
            postLore.add("");
            postLore.add("§e➜ Click per aprire");
            postMeta.setLore(postLore);
            post.setItemMeta(postMeta);
            gui.setItem(12, post);
        }
        
        // BackpackPlus
        if (plugin.hasBackpackPlus()) {
            ItemStack backpack = new ItemStack(Material.BROWN_DYE);
            ItemMeta backpackMeta = backpack.getItemMeta();
            backpackMeta.setDisplayName("§6§lBackpackPlus");
            backpackMeta.setCustomModelData(800001);
            backpackMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            backpackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> backpackLore = new ArrayList<>();
            backpackLore.add("§7Plugin: §fBackpackPlus");
            backpackLore.add("§7Sistema di zaini");
            backpackLore.add("§7personalizzati");
            backpackLore.add("");
            backpackLore.add("§a§lSTATO: §aAttivo");
            backpackLore.add("§7Versione: §f" + plugin.getBackpackVersion());
            backpackLore.add("");
            backpackLore.add("§e➜ Click per aprire");
            backpackMeta.setLore(backpackLore);
            backpack.setItemMeta(backpackMeta);
            gui.setItem(14, backpack);
        }
        
        // Coins
        if (plugin.hasCoins()) {
            ItemStack coins = new ItemStack(Material.GOLD_INGOT);
            ItemMeta coinsMeta = coins.getItemMeta();
            coinsMeta.setDisplayName("§e§lCoins");
            coinsMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            coinsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> coinsLore = new ArrayList<>();
            coinsLore.add("§7Plugin: §fGavaCore-Coins");
            coinsLore.add("§7Sistema di economia");
            coinsLore.add("§7del server");
            coinsLore.add("");
            coinsLore.add("§a§lSTATO: §aAttivo");
            coinsLore.add("§7Versione: §f" + plugin.getCoinsVersion());
            coinsLore.add("");
            coinsLore.add("§e➜ Click per info");
            coinsMeta.setLore(coinsLore);
            coins.setItemMeta(coinsMeta);
            gui.setItem(16, coins);
        }
        
        // iConomy
        if (plugin.hasIConomy()) {
            ItemStack iconomy = new ItemStack(Material.EMERALD);
            ItemMeta iconomyMeta = iconomy.getItemMeta();
            iconomyMeta.setDisplayName("§a§liConomy");
            iconomyMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            iconomyMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> iconomyLore = new ArrayList<>();
            iconomyLore.add("§7Plugin: §fiConomyUnlocked");
            iconomyLore.add("§7Sistema di economia");
            iconomyLore.add("§7avanzato");
            iconomyLore.add("");
            iconomyLore.add("§a§lSTATO: §aAttivo");
            iconomyLore.add("§7Versione: §f" + plugin.getIconomyVersion());
            iconomyLore.add("");
            iconomyLore.add("§e➜ Click per info");
            iconomyMeta.setLore(iconomyLore);
            iconomy.setItemMeta(iconomyMeta);
            gui.setItem(28, iconomy);
        }
        
        // SkinRestorer
        if (plugin.hasSkinRestorer()) {
            ItemStack skin = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta skinMeta = skin.getItemMeta();
            skinMeta.setDisplayName("§e§lSkinRestorer");
            skinMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            skinMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> skinLore = new ArrayList<>();
            skinLore.add("§7Plugin: §fSkinsRestorer");
            skinLore.add("§7Sistema di gestione skin");
            skinLore.add("§7personalizzate");
            skinLore.add("");
            skinLore.add("§a§lSTATO: §aAttivo");
            skinLore.add("§7Versione: §f" + plugin.getSkinRestorerVersion());
            skinLore.add("");
            skinLore.add("§e➜ Click per info");
            skinMeta.setLore(skinLore);
            skin.setItemMeta(skinMeta);
            gui.setItem(30, skin);
        }
        
        // SmoothTimber
        if (plugin.hasSmoothTimber()) {
            ItemStack timber = new ItemStack(Material.DIAMOND_AXE);
            ItemMeta timberMeta = timber.getItemMeta();
            timberMeta.setDisplayName("§a§lSmooth Timber");
            timberMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            timberMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            List<String> timberLore = new ArrayList<>();
            timberLore.add("§7Plugin: §fSmoothTimber");
            timberLore.add("§7Taglia alberi interi");
            timberLore.add("§7istantaneamente");
            timberLore.add("");
            timberLore.add("§a§lSTATO: §aAttivo");
            timberLore.add("§7Versione: §f" + plugin.getSmoothTimberVersion());
            timberLore.add("");
            timberLore.add("§e➜ Click per info");
            timberMeta.setLore(timberLore);
            timber.setItemMeta(timberMeta);
            gui.setItem(32, timber);
        }
        
        // Plugin non installati
        ItemStack missing = new ItemStack(Material.BARRIER);
        ItemMeta missingMeta = missing.getItemMeta();
        missingMeta.setDisplayName("§c§lPlugin Mancanti");
        List<String> missingLore = new ArrayList<>();
        missingLore.add("§7Plugin non installati:");
        missingLore.add("");
        if (!plugin.hasTrainAddon()) missingLore.add("§c✘ §7GavaCore-TrainAddons");
        if (!plugin.hasPosteAddon()) missingLore.add("§c✘ §7GavaCore-PosteAddons");
        if (!plugin.hasBackpackPlus()) missingLore.add("§c✘ §7BackpackPlus");
        if (!plugin.hasCoins()) missingLore.add("§c✘ §7GavaCore-Coins");
        if (!plugin.hasIConomy()) missingLore.add("§c✘ §7iConomyUnlocked");
        if (!plugin.hasSkinRestorer()) missingLore.add("§c✘ §7SkinsRestorer");
        if (!plugin.hasSmoothTimber()) missingLore.add("§c✘ §7SmoothTimber");
        missingLore.add("");
        missingLore.add("§7Contatta un amministratore");
        missingLore.add("§7per installarli");
        missingMeta.setLore(missingLore);
        missing.setItemMeta(missingMeta);
        gui.setItem(40, missing);
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna Indietro");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);
        
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "plugins");
    }

    private int getActivePluginsCount() {
        int count = 0;
        if (plugin.hasTrainAddon()) count++;
        if (plugin.hasPosteAddon()) count++;
        if (plugin.hasBackpackPlus()) count++;
        if (plugin.hasCoins()) count++;
        if (plugin.hasIConomy()) count++;
        if (plugin.hasSkinRestorer()) count++;
        if (plugin.hasSmoothTimber()) count++;
        return count;
    }

    private int getTotalPluginsCount() {
        return 7; // Numero totale di plugin supportati
    }

    // Aggiungi questo nuovo metodo per il menu di crafting degli item personalizzati
    public void openCustomItemsCraftingMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE + " §8» §6Crafting Items");
        
        // Bordo decorativo superiore
        ItemStack border = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName("§e✦");
        border.setItemMeta(borderMeta);
        
        for(int i = 0; i < 9; i++) {
            gui.setItem(i, border);
        }
        
        // Ottieni tutte le ricette registrate
        Map<String, ItemStack> recipes = plugin.getRegisteredRecipes();
        
        // Panino
        ItemStack panino = recipes.get("panino");
        gui.setItem(10, new ItemStack(Material.BREAD));
        gui.setItem(19, new ItemStack(Material.COOKED_BEEF));
        gui.setItem(28, new ItemStack(Material.BREAD));
        gui.setItem(16, panino);
        gui.setItem(15, createArrow());
        
        // Coca Cola
        ItemStack cocaCola = recipes.get("coca_cola");
        gui.setItem(12, new ItemStack(Material.SUGAR));
        gui.setItem(21, new ItemStack(Material.POTION));
        gui.setItem(30, new ItemStack(Material.BLACK_DYE));
        gui.setItem(25, cocaCola);
        gui.setItem(24, createArrow());
        
        // Patatine
        ItemStack patatine = recipes.get("patatine");
        gui.setItem(14, new ItemStack(Material.BAKED_POTATO));
        gui.setItem(23, new ItemStack(Material.WOODEN_SWORD));
        gui.setItem(32, new ItemStack(Material.PAPER));
        gui.setItem(34, patatine);
        gui.setItem(33, createArrow());
        
        // Guinzaglio Magico
        ItemStack guinzaglio = recipes.get("guinzaglio");
        gui.setItem(37, new ItemStack(Material.GOLD_INGOT));
        gui.setItem(38, new ItemStack(Material.DIAMOND));
        gui.setItem(39, new ItemStack(Material.GOLD_INGOT));
        gui.setItem(40, new ItemStack(Material.DIAMOND));
        gui.setItem(41, new ItemStack(Material.LEAD));
        gui.setItem(42, new ItemStack(Material.DIAMOND));
        gui.setItem(43, new ItemStack(Material.GOLD_INGOT));
        gui.setItem(44, new ItemStack(Material.DIAMOND));
        gui.setItem(45, new ItemStack(Material.GOLD_INGOT));
        gui.setItem(52, guinzaglio);
        gui.setItem(51, createArrow());
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna al Menu");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);
        
        // Riempi gli spazi vuoti con vetro nero
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "crafting_items");
    }

    private ItemStack createArrow() {
        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta arrowMeta = arrow.getItemMeta();
        arrowMeta.setDisplayName("§e➜");
        arrow.setItemMeta(arrowMeta);
        return arrow;
    }

    // Aggiungi questo nuovo metodo per il menu di crafting degli zaini
    public void openBackpackCraftingMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE + " §8» §6Crafting Zaini");
        
        // Bordo decorativo superiore
        ItemStack border = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName("§e✦");
        border.setItemMeta(borderMeta);
        
        for(int i = 0; i < 9; i++) {
            gui.setItem(i, border);
        }
        
        // Leggi la configurazione di BackpackPlus
        File backpackConfigFile = new File(plugin.getServer().getPluginManager().getPlugin("BackpackPlus").getDataFolder(), "config.yml");
        FileConfiguration backpackConfig = YamlConfiguration.loadConfiguration(backpackConfigFile);
        
        // Zaino Base (Tier 1)
        setupBackpackRecipe(gui, backpackConfig, "1", 10, 16);
        
        // Zaino d'Oro (Tier 2)
        setupBackpackRecipe(gui, backpackConfig, "2", 28, 34);
        
        // Zaino di Diamante (Tier 3)
        setupBackpackRecipe(gui, backpackConfig, "3", 46, 52);
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna al Menu");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);
        
        // Riempi gli spazi vuoti con vetro nero
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "crafting_backpacks");
    }

    private void setupBackpackRecipe(Inventory gui, FileConfiguration config, String tier, int startSlot, int resultSlot) {
        String basePath = "backpack-tiers." + tier;
        
        // Crea il risultato
        ItemStack result = new ItemStack(Material.BROWN_DYE);
        ItemMeta resultMeta = result.getItemMeta();
        resultMeta.setDisplayName(config.getString(basePath + ".displayName", "Zaino")
                .replace("#E3680E", "§6")
                .replace("#E0B715", "§6")
                .replace("#00C2C6", "§b"));
        resultMeta.setCustomModelData(config.getInt(basePath + ".id", 800000 + Integer.parseInt(tier)));
        List<String> lore = Arrays.asList(
            "§7Uno zaino con",
            "§7" + config.getInt(basePath + ".size") + " slot di spazio"
        );
        resultMeta.setLore(lore);
        result.setItemMeta(resultMeta);
        
        // Posiziona gli ingredienti
        for (String craftSlot : config.getConfigurationSection(basePath + ".crafting").getKeys(false)) {
            String materialName = config.getString(basePath + ".crafting." + craftSlot);
            Material material = Material.valueOf(materialName.toUpperCase());
            int slot = startSlot + (Integer.parseInt(craftSlot) - 1);
            gui.setItem(slot, new ItemStack(material));
        }
        
        // Aggiungi la freccia e il risultato
        gui.setItem(resultSlot - 1, createArrow());
        gui.setItem(resultSlot, result);
    }

    // Aggiungi questo metodo per aprire la GUI Coins
    public void openCoinsMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE + " §8» §eCoins");
        
        // Monete Fisiche
        ItemStack monete = new ItemStack(Material.GOLD_INGOT);
        ItemMeta moneteMeta = monete.getItemMeta();
        moneteMeta.setDisplayName("§e§lMonete Fisiche");
        moneteMeta.setLore(Arrays.asList(
            "§7Converti i tuoi soldi virtuali",
            "§7in monete fisiche",
            "",
            "§e➜ Click per inserire l'importo"
        ));
        moneteMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        moneteMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        monete.setItemMeta(moneteMeta);
        gui.setItem(13, monete);
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna Indietro");
        back.setItemMeta(backMeta);
        gui.setItem(26, back);
        
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "coins");
    }

    // Aggiungi questo metodo per gestire l'input dell'importo
    public void openAmountInput(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "§e§lInserisci l'importo");
        
        // Display dell'importo
        ItemStack display = new ItemStack(Material.PAPER);
        ItemMeta displayMeta = display.getItemMeta();
        displayMeta.setDisplayName("§e§lImporto: §f0");
        displayMeta.setLore(Arrays.asList(
            "§7L'importo verrà convertito",
            "§7in monete fisiche"
        ));
        display.setItemMeta(displayMeta);
        gui.setItem(4, display);
        
        // Tastierino numerico
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        int[] slots = {19, 20, 21, 28, 29, 30, 37, 38, 39, 33};
        
        for (int i = 0; i < numbers.length; i++) {
            ItemStack number = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
            ItemMeta numberMeta = number.getItemMeta();
            numberMeta.setDisplayName("§b§l" + numbers[i]);
            numberMeta.setLore(Arrays.asList("§7Click per aggiungere"));
            number.setItemMeta(numberMeta);
            gui.setItem(slots[i], number);
        }
        
        // Pulsanti di controllo
        // Cancella tutto
        ItemStack clear = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta clearMeta = clear.getItemMeta();
        clearMeta.setDisplayName("§c§lCancella Tutto");
        clearMeta.setLore(Arrays.asList("§7Click per cancellare tutto"));
        clear.setItemMeta(clearMeta);
        gui.setItem(41, clear);
        
        // Cancella ultima cifra
        ItemStack backspace = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta backspaceMeta = backspace.getItemMeta();
        backspaceMeta.setDisplayName("§6§lCancella Ultima Cifra");
        backspaceMeta.setLore(Arrays.asList("§7Click per cancellare l'ultima cifra"));
        backspace.setItemMeta(backspaceMeta);
        gui.setItem(40, backspace);
        
        // Conferma
        ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§a§lConferma");
        confirmMeta.setLore(Arrays.asList("§7Click per confermare"));
        confirm.setItemMeta(confirmMeta);
        gui.setItem(43, confirm);
        
        // Torna indietro
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§c§lTorna Indietro");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);
        
        fillWithGlass(gui);
        
        player.openInventory(gui);
        openMenus.put(player.getUniqueId(), "coins_amount");
    }

    // Aggiungi questo metodo per gestire i click nel menu Coins
    public void handleCoinsMenuClick(Player player, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            openPluginsMenu(player);
            return;
        }
        
        if (clicked.getType() == Material.GOLD_INGOT) {
            openAmountInput(player);
        }
    }

    // Metodo helper per validare l'input numerico
    private boolean isValidNumber(String input) {
        try {
            int amount = Integer.parseInt(input);
            return amount > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Metodo per aggiornare il display
    private void updateDisplay(Inventory inventory, String amount) {
        ItemStack display = inventory.getItem(4);
        if (display != null && display.hasItemMeta()) {
            ItemMeta meta = display.getItemMeta();
            meta.setDisplayName("§e§lImporto: §f" + amount);
            List<String> lore = Arrays.asList(
                "§7L'importo verrà convertito",
                "§7in monete fisiche",
                "",
                "§7Importo attuale: §e" + amount + " monete",
                "",
                "§e• Click sui numeri per inserire",
                "§e• Click su Cancella Ultima Cifra per rimuovere",
                "§e• Click su Cancella Tutto per azzerare",
                "§e• Click su Conferma per convertire"
            );
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
    }
} 