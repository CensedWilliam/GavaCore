package library;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TreeCutter {
    private final Main plugin;
    private final Set<Material> LOG_TYPES = new HashSet<>(Arrays.asList(
            Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, 
            Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG, Material.CHERRY_LOG, Material.CRIMSON_STEM, 
            Material.WARPED_STEM, Material.STRIPPED_OAK_LOG, Material.STRIPPED_SPRUCE_LOG,
            Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_JUNGLE_LOG, 
            Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_DARK_OAK_LOG,
            Material.STRIPPED_MANGROVE_LOG, Material.STRIPPED_CHERRY_LOG,
            Material.STRIPPED_CRIMSON_STEM, Material.STRIPPED_WARPED_STEM
    ));
    
    private final Set<Material> LEAF_TYPES = new HashSet<>(Arrays.asList(
            Material.OAK_LEAVES, Material.SPRUCE_LEAVES, Material.BIRCH_LEAVES,
            Material.JUNGLE_LEAVES, Material.ACACIA_LEAVES, Material.DARK_OAK_LEAVES,
            Material.MANGROVE_LEAVES, Material.CHERRY_LEAVES, Material.NETHER_WART_BLOCK,
            Material.WARPED_WART_BLOCK
    ));
    
    private final Map<Material, Material> LOG_TO_LEAF_MAP = new HashMap<Material, Material>() {{
        put(Material.OAK_LOG, Material.OAK_LEAVES);
        put(Material.STRIPPED_OAK_LOG, Material.OAK_LEAVES);
        put(Material.SPRUCE_LOG, Material.SPRUCE_LEAVES);
        put(Material.STRIPPED_SPRUCE_LOG, Material.SPRUCE_LEAVES);
        put(Material.BIRCH_LOG, Material.BIRCH_LEAVES);
        put(Material.STRIPPED_BIRCH_LOG, Material.BIRCH_LEAVES);
        put(Material.JUNGLE_LOG, Material.JUNGLE_LEAVES);
        put(Material.STRIPPED_JUNGLE_LOG, Material.JUNGLE_LEAVES);
        put(Material.ACACIA_LOG, Material.ACACIA_LEAVES);
        put(Material.STRIPPED_ACACIA_LOG, Material.ACACIA_LEAVES);
        put(Material.DARK_OAK_LOG, Material.DARK_OAK_LEAVES);
        put(Material.STRIPPED_DARK_OAK_LOG, Material.DARK_OAK_LEAVES);
        put(Material.MANGROVE_LOG, Material.MANGROVE_LEAVES);
        put(Material.STRIPPED_MANGROVE_LOG, Material.MANGROVE_LEAVES);
        put(Material.CHERRY_LOG, Material.CHERRY_LEAVES);
        put(Material.STRIPPED_CHERRY_LOG, Material.CHERRY_LEAVES);
        put(Material.CRIMSON_STEM, Material.NETHER_WART_BLOCK);
        put(Material.STRIPPED_CRIMSON_STEM, Material.NETHER_WART_BLOCK);
        put(Material.WARPED_STEM, Material.WARPED_WART_BLOCK);
        put(Material.STRIPPED_WARPED_STEM, Material.WARPED_WART_BLOCK);
    }};
    
    // Mappa per i colori delle particelle in base al tipo di legno
    private final Map<Material, Color> LOG_COLORS = new HashMap<Material, Color>() {{
        put(Material.OAK_LOG, Color.fromRGB(133, 94, 66));
        put(Material.STRIPPED_OAK_LOG, Color.fromRGB(133, 94, 66));
        put(Material.SPRUCE_LOG, Color.fromRGB(91, 67, 46));
        put(Material.STRIPPED_SPRUCE_LOG, Color.fromRGB(91, 67, 46));
        put(Material.BIRCH_LOG, Color.fromRGB(207, 206, 201));
        put(Material.STRIPPED_BIRCH_LOG, Color.fromRGB(207, 206, 201));
        put(Material.JUNGLE_LOG, Color.fromRGB(150, 111, 51));
        put(Material.STRIPPED_JUNGLE_LOG, Color.fromRGB(150, 111, 51));
        put(Material.ACACIA_LOG, Color.fromRGB(169, 89, 49));
        put(Material.STRIPPED_ACACIA_LOG, Color.fromRGB(169, 89, 49));
        put(Material.DARK_OAK_LOG, Color.fromRGB(69, 50, 31));
        put(Material.STRIPPED_DARK_OAK_LOG, Color.fromRGB(69, 50, 31));
        put(Material.MANGROVE_LOG, Color.fromRGB(117, 54, 48));
        put(Material.STRIPPED_MANGROVE_LOG, Color.fromRGB(117, 54, 48));
        put(Material.CHERRY_LOG, Color.fromRGB(227, 153, 130));
        put(Material.STRIPPED_CHERRY_LOG, Color.fromRGB(227, 153, 130));
        put(Material.CRIMSON_STEM, Color.fromRGB(148, 63, 97));
        put(Material.STRIPPED_CRIMSON_STEM, Color.fromRGB(148, 63, 97));
        put(Material.WARPED_STEM, Color.fromRGB(58, 178, 165));
        put(Material.STRIPPED_WARPED_STEM, Color.fromRGB(58, 178, 165));
    }};
    
    // Colori delle foglie
    private final Map<Material, Color> LEAF_COLORS = new HashMap<Material, Color>() {{
        put(Material.OAK_LEAVES, Color.fromRGB(60, 192, 41));
        put(Material.SPRUCE_LEAVES, Color.fromRGB(25, 110, 14));
        put(Material.BIRCH_LEAVES, Color.fromRGB(128, 167, 85));
        put(Material.JUNGLE_LEAVES, Color.fromRGB(85, 167, 50));
        put(Material.ACACIA_LEAVES, Color.fromRGB(96, 161, 50));
        put(Material.DARK_OAK_LEAVES, Color.fromRGB(77, 106, 40));
        put(Material.MANGROVE_LEAVES, Color.fromRGB(74, 130, 44));
        put(Material.CHERRY_LEAVES, Color.fromRGB(242, 214, 221));
        put(Material.NETHER_WART_BLOCK, Color.fromRGB(180, 8, 8));
        put(Material.WARPED_WART_BLOCK, Color.fromRGB(22, 119, 121));
    }};
    
    // Velocità di taglio per tipo di ascia
    private final Map<Material, Integer> AXE_SPEEDS = new HashMap<Material, Integer>() {{
        put(Material.WOODEN_AXE, 5);      // Più lenta
        put(Material.STONE_AXE, 4);       // Lenta
        put(Material.IRON_AXE, 3);        // Media
        put(Material.GOLDEN_AXE, 1);      // La più veloce
        put(Material.DIAMOND_AXE, 2);     // Veloce
        put(Material.NETHERITE_AXE, 2);   // Veloce
    }};
    
    // Suoni casuali di taglio del legno
    private final Sound[] CHOPPING_SOUNDS = {
        Sound.BLOCK_WOOD_BREAK,
        Sound.BLOCK_WOOD_PLACE,
        Sound.BLOCK_WOOD_STEP,
        Sound.BLOCK_WOOD_HIT
    };
    
    private final Set<Material> AXE_TYPES = new HashSet<>(Arrays.asList(
            Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
            Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE
    ));
    
    // Mappa per tenere traccia degli alberi in fase di taglio
    private final Map<UUID, Boolean> cuttingTrees = new HashMap<>();
    
    // Mappa per tenere traccia di tutti gli alberi in fase di taglio per ogni giocatore
    private final Map<UUID, Set<Vector>> playerActiveTrees = new HashMap<>();
    
    // Limite massimo di alberi simultanei per giocatore
    private final int MAX_SIMULTANEOUS_TREES = 3;
    
    // Distanza minima tra alberi per considerarli distinti
    private final double MIN_TREE_DISTANCE = 5.0;
    
    public TreeCutter(Main plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Controlla se un blocco è un tronco di un albero
     */
    public boolean isLog(Block block) {
        return LOG_TYPES.contains(block.getType());
    }
    
    /**
     * Controlla se un blocco è una foglia
     */
    public boolean isLeaf(Block block) {
        return LEAF_TYPES.contains(block.getType());
    }
    
    /**
     * Controlla se un tronco è stato piazzato da un giocatore
     * I tronchi piazzati dai giocatori hanno caratteristiche diverse da quelli generati naturalmente
     */
    private boolean isPlayerPlacedLog(Block block) {
        if (!isLog(block)) return false;
        
        // Controlla se il blocco è isolato (non fa parte di un albero naturale)
        boolean hasAdjacentLogs = false;
        boolean hasAdjacentLeaves = false;
        
        // Controlla i blocchi adiacenti per vedere se ci sono altri tronchi o foglie naturali
        for (BlockFace face : new BlockFace[] {
            BlockFace.UP, BlockFace.DOWN, 
            BlockFace.NORTH, BlockFace.SOUTH, 
            BlockFace.EAST, BlockFace.WEST
        }) {
            Block adjacent = block.getRelative(face);
            if (isLog(adjacent)) {
                hasAdjacentLogs = true;
            } else if (isLeaf(adjacent) && isNaturalLeaf(adjacent)) {
                hasAdjacentLeaves = true;
            }
        }
        
        // Controlla il blocco sotto il tronco
        Block below = block.getRelative(BlockFace.DOWN);
        Material belowType = below.getType();
        
        // Un tronco naturale solitamente ha altri tronchi o foglie adiacenti
        // e/o è su un blocco naturale (terra, erba, ecc.)
        boolean onNaturalGround = belowType == Material.DIRT || 
                                  belowType == Material.GRASS_BLOCK || 
                                  belowType == Material.PODZOL ||
                                  belowType == Material.MOSS_BLOCK ||
                                  belowType == Material.MYCELIUM;
        
        // Se il tronco è isolato (nessun altro tronco o foglia adiacente)
        // e non è su terra naturale, probabilmente è stato piazzato da un giocatore
        return (!hasAdjacentLogs && !hasAdjacentLeaves && !onNaturalGround) ||
               // Oppure se è su un blocco non naturale ed è isolato
               (!onNaturalGround && !hasAdjacentLeaves);
    }
    
    /**
     * Controlla se l'item è un'ascia
     */
    public boolean isAxe(ItemStack item) {
        return item != null && AXE_TYPES.contains(item.getType());
    }
    
    /**
     * Ottiene il materiale di legno corrispondente al tronco
     */
    private Material getWoodFromLog(Material logType) {
        switch (logType) {
            case OAK_LOG:
            case STRIPPED_OAK_LOG:
                return Material.OAK_WOOD;
            case SPRUCE_LOG:
            case STRIPPED_SPRUCE_LOG:
                return Material.SPRUCE_WOOD;
            case BIRCH_LOG:
            case STRIPPED_BIRCH_LOG:
                return Material.BIRCH_WOOD;
            case JUNGLE_LOG:
            case STRIPPED_JUNGLE_LOG:
                return Material.JUNGLE_WOOD;
            case ACACIA_LOG:
            case STRIPPED_ACACIA_LOG:
                return Material.ACACIA_WOOD;
            case DARK_OAK_LOG:
            case STRIPPED_DARK_OAK_LOG:
                return Material.DARK_OAK_WOOD;
            case MANGROVE_LOG:
            case STRIPPED_MANGROVE_LOG:
                return Material.MANGROVE_WOOD;
            case CHERRY_LOG:
            case STRIPPED_CHERRY_LOG:
                return Material.CHERRY_WOOD;
            case CRIMSON_STEM:
            case STRIPPED_CRIMSON_STEM:
                return Material.CRIMSON_HYPHAE;
            case WARPED_STEM:
            case STRIPPED_WARPED_STEM:
                return Material.WARPED_HYPHAE;
            default:
                return Material.OAK_WOOD;
        }
    }
    
    /**
     * Ottieni il tipo di foglie corrispondente al tipo di legno
     */
    private Material getMatchingLeafType(Material logType) {
        return LOG_TO_LEAF_MAP.getOrDefault(logType, Material.OAK_LEAVES);
    }
    
    /**
     * Ottieni il colore associato a un tipo di tronco
     */
    private Color getLogColor(Material logType) {
        return LOG_COLORS.getOrDefault(logType, Color.fromRGB(133, 94, 66)); // Colore di default
    }
    
    /**
     * Ottieni il colore associato a un tipo di foglia
     */
    private Color getLeafColor(Material leafType) {
        return LEAF_COLORS.getOrDefault(leafType, Color.fromRGB(60, 192, 41)); // Colore di default
    }
    
    /**
     * Ottieni la velocità di taglio per un tipo di ascia
     */
    private int getAxeSpeed(Material axeType) {
        return AXE_SPEEDS.getOrDefault(axeType, 3); // Velocità di default
    }
    
    /**
     * Tenta di tagliare un albero quando un giocatore rompe un blocco di legno mentre è in shift
     */
    public boolean tryChopTree(Player player, Block block) {
        UUID playerUUID = player.getUniqueId();
        
        // Inizializza il set di alberi attivi per questo giocatore se non esiste
        playerActiveTrees.putIfAbsent(playerUUID, new HashSet<>());
        
        // Limita il numero di alberi attivi contemporaneamente
        if (playerActiveTrees.get(playerUUID).size() >= MAX_SIMULTANEOUS_TREES) {
            // Se il giocatore sta tagliando troppi alberi, informa il giocatore
            player.sendMessage("§6§l[TreeCutter] §cStai già tagliando il numero massimo di alberi!");
            return false;
        }
        
        // Controlla se il giocatore sta usando un'ascia
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (!isAxe(handItem)) {
            return false;
        }
        
        // Controlla se il blocco è un tronco
        if (!isLog(block)) {
            return false;
        }
        
        // Controlla se il tronco è stato piazzato da un giocatore
        if (isPlayerPlacedLog(block)) {
            // Non tagliare i tronchi piazzati dai giocatori
            return false;
        }
        
        // Ottiene i blocchi dell'albero
        TreeStructure treeStructure = findTreeStructure(block);
        if (treeStructure.getLogBlocks().size() <= 1) {
            // Non è un albero, è solo un singolo blocco di legno
            return false;
        }
        
        // Calcola il centro dell'albero
        Vector treeCenter = calculateTreeCenter(treeStructure.getLogBlocks());
        
        // Controlla se l'albero è già in fase di taglio
        for (Vector activeTree : playerActiveTrees.get(playerUUID)) {
            if (activeTree.distance(treeCenter) < MIN_TREE_DISTANCE) {
                // Albero già in fase di taglio (o troppo vicino ad un albero già in fase di taglio)
                player.sendMessage("§6§l[TreeCutter] §eStai già tagliando un albero qui vicino!");
                return false;
            }
        }
        
        // Controlla la durabilità dell'ascia
        int maxDurability = getAxeMaxDurability(handItem.getType());
        int currentDurability = ((Damageable) handItem.getItemMeta()).getDamage();
        int remainingDurability = maxDurability - currentDurability;
        
        // Limita i blocchi alla durabilità dell'ascia
        List<Block> logsToBreak = new ArrayList<>(treeStructure.getLogBlocks());
        if (logsToBreak.size() > remainingDurability) {
            logsToBreak = logsToBreak.subList(0, remainingDurability);
            treeStructure.setLeafBlocks(new ArrayList<>()); // Niente foglie se non abbiamo durabilità per tutti i tronchi
        }
        
        // Aggiungi l'albero ai tagli attivi del giocatore
        playerActiveTrees.get(playerUUID).add(treeCenter);
        
        // Aggiorna lo stato del giocatore
        cuttingTrees.put(playerUUID, true);
        
        // Inizia l'animazione di taglio
        startChoppingAnimation(player, logsToBreak, treeStructure.getLeafBlocks(), handItem, block.getType(), treeCenter);
        
        return true;
    }
    
    /**
     * Calcola il centro geometrico di un albero dai suoi blocchi
     */
    private Vector calculateTreeCenter(List<Block> blocks) {
        if (blocks.isEmpty()) {
            return new Vector(0, 0, 0);
        }
        
        double totalX = 0;
        double totalY = 0;
        double totalZ = 0;
        
        for (Block block : blocks) {
            totalX += block.getX();
            totalY += block.getY();
            totalZ += block.getZ();
        }
        
        return new Vector(
            totalX / blocks.size(),
            totalY / blocks.size(),
            totalZ / blocks.size()
        );
    }
    
    /**
     * Rappresenta la struttura completa di un albero con tronchi e foglie
     */
    private class TreeStructure {
        private List<Block> logBlocks;
        private List<Block> leafBlocks;
        
        public TreeStructure(List<Block> logBlocks, List<Block> leafBlocks) {
            this.logBlocks = logBlocks;
            this.leafBlocks = leafBlocks;
        }
        
        public List<Block> getLogBlocks() {
            return logBlocks;
        }
        
        public List<Block> getLeafBlocks() {
            return leafBlocks;
        }
        
        public void setLeafBlocks(List<Block> leafBlocks) {
            this.leafBlocks = leafBlocks;
        }
    }
    
    /**
     * Controlla se un blocco è una foglia naturale (non piazzata da un giocatore)
     * Le foglie naturali hanno la proprietà "persistent" impostata a false
     */
    private boolean isNaturalLeaf(Block block) {
        if (!isLeaf(block)) return false;
        
        try {
            // Verifica la proprietà "persistent" delle foglie
            if (block.getBlockData() instanceof org.bukkit.block.data.type.Leaves) {
                org.bukkit.block.data.type.Leaves leaves = (org.bukkit.block.data.type.Leaves) block.getBlockData();
                return !leaves.isPersistent();
            }
        } catch (Exception e) {
            // In caso di errore (versioni di Bukkit diverse), 
            // torniamo al controllo semplice del tipo di blocco
        }
        
        // Fallback al vecchio metodo se non possiamo controllare la persistenza
        return true;
    }
    
    /**
     * Trova tutti i blocchi che compongono l'albero, incluse le foglie correlate
     */
    private TreeStructure findTreeStructure(Block startBlock) {
        Set<Block> logBlocks = new HashSet<>();
        Set<Block> leafBlocks = new HashSet<>();
        Set<Block> visited = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        
        Material logType = startBlock.getType();
        Material leafType = getMatchingLeafType(logType);
        
        // Fase 1: trova tutti i tronchi connessi usando BFS con ricerca migliorata per strutture ramificate
        queue.add(startBlock);
        visited.add(startBlock);
        
        // Array di direzioni per la ricerca - ora incluse anche le diagonali per alberi con strutture complesse
        BlockFace[] primaryDirections = new BlockFace[] {
            BlockFace.UP, BlockFace.DOWN, 
            BlockFace.NORTH, BlockFace.SOUTH, 
            BlockFace.EAST, BlockFace.WEST
        };
        
        // Aggiungi struttura per tracciare i "rami" (percorsi di tronchi distinti)
        List<List<Block>> branches = new ArrayList<>();
        List<Block> mainTrunk = new ArrayList<>();
        branches.add(mainTrunk);
        
        // Tiene traccia della connettività per l'analisi dei rami
        Map<Block, Set<Block>> connectionMap = new HashMap<>();
        
        while (!queue.isEmpty()) {
            Block current = queue.poll();
            
            if (isLog(current)) {
                logBlocks.add(current);
                mainTrunk.add(current);
                
                // Aggiungi le connessioni per questo blocco
                connectionMap.putIfAbsent(current, new HashSet<>());
                
                // Prima verifica le direzioni primarie (più probabile che siano parte del tronco principale)
                for (BlockFace face : primaryDirections) {
                    Block neighbor = current.getRelative(face);
                    
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        
                        if (isLog(neighbor)) {
                            // Registra la connessione nei due sensi
                            connectionMap.get(current).add(neighbor);
                            connectionMap.putIfAbsent(neighbor, new HashSet<>());
                            connectionMap.get(neighbor).add(current);
                            
                            queue.add(neighbor);
                        }
                    }
                }
                
                // Poi verifica le diagonali - importante per alberi con strutture complesse come quelli della giungla
                // Verifica tutte le diagonali possibili (8 diagonali in piano e 16 diagonali in 3D)
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            // Ignora il centro e le direzioni primarie già controllate
                            if ((x == 0 && y == 0 && z == 0) || 
                               (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1)) {
                                continue;
                            }
                            
                            Block diagonalNeighbor = current.getRelative(x, y, z);
                            
                            if (!visited.contains(diagonalNeighbor)) {
                                visited.add(diagonalNeighbor);
                                
                                // Verifica se è dello stesso tipo di legno
                                if (isLog(diagonalNeighbor) && diagonalNeighbor.getType() == logType) {
                                    // Registra la connessione diagonale
                                    connectionMap.get(current).add(diagonalNeighbor);
                                    connectionMap.putIfAbsent(diagonalNeighbor, new HashSet<>());
                                    connectionMap.get(diagonalNeighbor).add(current);
                                    
                                    queue.add(diagonalNeighbor);
                                    
                                    // Per le diagonali, controlla se potrebbe essere l'inizio di un ramo
                                    if (connectionMap.get(current).size() > 2) {
                                        // Questo potrebbe essere un punto di diramazione
                                        List<Block> newBranch = new ArrayList<>();
                                        newBranch.add(diagonalNeighbor);
                                        branches.add(newBranch);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Fase 2: Analizza la struttura dell'albero per identificare meglio i rami
        // Gli alberi della giungla e altri alberi complessi hanno spesso strutture separate
        // ma connesse che formano rami distinti
        
        // Trova il punto più alto e più basso dell'albero
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        Block highestLog = null;
        
        for (Block log : logBlocks) {
            int y = log.getY();
            if (y < minY) minY = y;
            if (y > maxY) {
                maxY = y;
                highestLog = log;
            }
        }
        
        // La base dell'albero (il punto più basso)
        Block treeBase = null;
        for (Block log : logBlocks) {
            if (log.getY() == minY) {
                treeBase = log;
                break;
            }
        }
        
        if (treeBase == null || highestLog == null) {
            // Qualcosa è andato storto, restituisci quello che abbiamo finora
            return new TreeStructure(new ArrayList<>(logBlocks), new ArrayList<>());
        }
        
        // Fase 3: Verifica che l'albero sia su terra o un blocco naturale
        // Questo aiuta a distinguere strutture artificiali da alberi naturali
        Block groundBlock = treeBase.getRelative(BlockFace.DOWN);
        Material groundType = groundBlock.getType();
        boolean isOnNaturalGround = groundType == Material.DIRT || 
                                  groundType == Material.GRASS_BLOCK || 
                                  groundType == Material.PODZOL ||
                                  groundType == Material.MOSS_BLOCK ||
                                  groundType == Material.MYCELIUM;
        
        // Fase 4: Analisi avanzata della struttura ramificata
        // Identifica punti di diramazione (blocchi con più di 2 connessioni)
        Set<Block> branchPoints = new HashSet<>();
        for (Block log : logBlocks) {
            if (connectionMap.containsKey(log) && connectionMap.get(log).size() > 2) {
                branchPoints.add(log);
            }
        }
        
        // Fase 5: Traccia i rami a partire dai punti di diramazione
        // Questo è importante per gli alberi della giungla che hanno strutture distinte
        if (!branchPoints.isEmpty()) {
            for (Block branchPoint : branchPoints) {
                for (Block connection : connectionMap.get(branchPoint)) {
                    if (!mainTrunk.contains(connection)) {
                        // Segui questo ramo fino alla fine
                        List<Block> branch = new ArrayList<>();
                        branch.add(connection);
                        
                        Queue<Block> branchQueue = new LinkedList<>();
                        Set<Block> branchVisited = new HashSet<>();
                        branchQueue.add(connection);
                        branchVisited.add(connection);
                        branchVisited.add(branchPoint); // Evita di tornare indietro
                        
                        while (!branchQueue.isEmpty()) {
                            Block current = branchQueue.poll();
                            
                            if (connectionMap.containsKey(current)) {
                                for (Block neighbor : connectionMap.get(current)) {
                                    if (!branchVisited.contains(neighbor)) {
                                        branch.add(neighbor);
                                        branchVisited.add(neighbor);
                                        branchQueue.add(neighbor);
                                    }
                                }
                            }
                        }
                        
                        if (branch.size() > 1) {
                            branches.add(branch);
                        }
                    }
                }
            }
        }
        
        // Fase 6: Calcola il raggio dell'albero in base all'altezza e alla complessità dei rami
        int treeHeight = maxY - minY;
        int branchComplexity = branches.size();
        
        // Alberi con più rami hanno tipicamente chiome più ampie
        int leafSearchRadius = Math.min(5, 2 + (treeHeight / 3) + (branchComplexity / 2)); 
        
        // Fase 7: Verifica ulteriore per alberi della giungla
        // Gli alberi della giungla spesso hanno "mini-alberi" connessi lungo il tronco
        boolean isJungleTree = logType == Material.JUNGLE_LOG || logType == Material.STRIPPED_JUNGLE_LOG;
        
        if (isJungleTree && branchPoints.size() > 0) {
            // Gli alberi della giungla hanno spesso strutture più complesse con rami distanziati
            leafSearchRadius = Math.max(leafSearchRadius, 4);
        }
        
        // Fase 8: Crea un ID univoco per questo albero specifico
        // L'ID è basato sulla posizione del tronco base e sulla struttura generale dell'albero
        String treeId = treeBase.getX() + "_" + treeBase.getY() + "_" + treeBase.getZ() + "_" + logBlocks.size() + "_" + branches.size();
        
        // Fase 9: Cerca foglie a partire dai log più alti dell'albero (dove tipicamente si trova la chioma)
        // in genere, le foglie sono connesse ai tronchi più in alto
        visited.clear();
        
        // Fase 9.1: Analizza la distribuzione spaziale dei tronchi per determinare il centro dell'albero
        double centerX = 0, centerZ = 0;
        for (Block log : logBlocks) {
            centerX += log.getX();
            centerZ += log.getZ();
        }
        centerX /= logBlocks.size();
        centerZ /= logBlocks.size();
        
        // Fase 9.2: Calcola il centro geometrico della chioma (parte superiore dell'albero)
        double topCenterX = 0, topCenterZ = 0;
        int topLogCount = 0;
        
        for (Block log : logBlocks) {
            if (log.getY() >= maxY - 3) { // Considera solo i tronchi vicini alla cima
                topCenterX += log.getX();
                topCenterZ += log.getZ();
                topLogCount++;
            }
        }
        
        if (topLogCount > 0) {
            topCenterX /= topLogCount;
            topCenterZ /= topLogCount;
        } else {
            topCenterX = centerX;
            topCenterZ = centerZ;
        }
        
        // Fase 10: Ricerca delle foglie migliorata
        // Prima cerca foglie partendo dalle estremità di ogni ramo
        for (List<Block> branch : branches) {
            // Usa gli ultimi blocchi di ogni ramo (le estremità dove sono più probabili le foglie)
            for (int i = Math.max(0, branch.size() - 3); i < branch.size(); i++) {
                Block log = branch.get(i);
                visited.add(log);
                
                // Adatta il raggio di ricerca in base all'altezza relativa del log nell'albero
                double relativeHeight = (double)(log.getY() - minY) / treeHeight;
                int adjustedRadius = (int) (leafSearchRadius * (0.5 + 0.5 * relativeHeight));
                
                // Per alberi della giungla, aumenta il raggio per i rami laterali
                if (isJungleTree && log != treeBase && branch != mainTrunk) {
                    adjustedRadius += 1;
                }
                
                // Cerca foglie intorno a questo log con il raggio adattato
                for (int x = -adjustedRadius; x <= adjustedRadius; x++) {
                    for (int y = -adjustedRadius; y <= adjustedRadius; y++) {
                        for (int z = -adjustedRadius; z <= adjustedRadius; z++) {
                            // Usa la distanza euclidea per limitare la ricerca a una sfera
                            if (x*x + y*y + z*z > adjustedRadius*adjustedRadius) continue;
                            
                            Block potential = log.getRelative(x, y, z);
                            if (!visited.contains(potential) && isLeaf(potential) && potential.getType() == leafType) {
                                // Verifica aggiuntiva: controlla se è una foglia naturale (non piazzata da un giocatore)
                                if (!isNaturalLeaf(potential)) continue;
                                
                                // Verifica se la foglia è nel raggio della chioma rispetto al centro geometrico
                                double distFromTopCenter = Math.sqrt(
                                    Math.pow(potential.getX() - topCenterX, 2) + 
                                    Math.pow(potential.getZ() - topCenterZ, 2)
                                );
                                
                                // Per foglie molto distanti dal centro, aumenta il livello di controllo
                                int verificationDepth = distFromTopCenter > leafSearchRadius ? 3 : 2;
                                
                                // Per alberi della giungla, aumenta la profondità di verifica
                                if (isJungleTree) {
                                    verificationDepth++;
                                }
                                
                                // Calcola la distanza dal tronco più vicino con verifica in profondità
                                if (isLeafConnectedToTree(potential, logBlocks, visited, verificationDepth)) {
                                    visited.add(potential);
                                    leafBlocks.add(potential);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Poi cerca foglie partendo da tutti i tronchi dell'albero nella metà superiore
        for (Block log : logBlocks) {
            // Verifica solo i log che sono nella parte superiore dell'albero
            // Ora con una distribuzione più precisa basata sull'altezza dell'albero
            if (log.getY() < minY + (treeHeight * 0.4)) { // Ignora il 40% inferiore dell'albero
                continue;
            }
            
            // Salta i blocchi già visitati
            if (visited.contains(log)) {
                continue;
            }
            
            visited.add(log);
            
            // Adatta il raggio di ricerca in base all'altezza relativa del log nell'albero
            double relativeHeight = (double)(log.getY() - minY) / treeHeight;
            int adjustedRadius = (int) (leafSearchRadius * (0.5 + 0.5 * relativeHeight));
            
            // Calcola la distanza dal centro della chioma per adattare ulteriormente il raggio
            double distFromCenter = Math.sqrt(Math.pow(log.getX() - topCenterX, 2) + Math.pow(log.getZ() - topCenterZ, 2));
            
            // Modifica il raggio in base alla distanza dal centro (i tronchi al centro hanno un raggio maggiore)
            adjustedRadius = Math.max(2, adjustedRadius - (int)(distFromCenter * 0.5));
            
            // Cerca foglie intorno a questo log con il raggio adattato
            for (int x = -adjustedRadius; x <= adjustedRadius; x++) {
                for (int y = -adjustedRadius; y <= adjustedRadius; y++) {
                    for (int z = -adjustedRadius; z <= adjustedRadius; z++) {
                        // Usa la distanza euclidea per limitare la ricerca a una sfera
                        if (x*x + y*y + z*z > adjustedRadius*adjustedRadius) continue;
                        
                        Block potential = log.getRelative(x, y, z);
                        if (!visited.contains(potential) && isLeaf(potential) && potential.getType() == leafType) {
                            // Verifica aggiuntiva: controlla se è una foglia naturale (non piazzata da un giocatore)
                            if (!isNaturalLeaf(potential)) continue;
                            
                            // Verifica se la foglia è nel raggio della chioma rispetto al centro geometrico
                            double distFromTopCenter = Math.sqrt(
                                Math.pow(potential.getX() - topCenterX, 2) + 
                                Math.pow(potential.getZ() - topCenterZ, 2)
                            );
                            
                            // Per foglie molto distanti dal centro, aumenta il livello di controllo
                            int verificationDepth = distFromTopCenter > leafSearchRadius ? 3 : 2;
                            
                            // Calcola la distanza dal tronco più vicino con verifica in profondità
                            if (isLeafConnectedToTree(potential, logBlocks, visited, verificationDepth)) {
                                visited.add(potential);
                                leafBlocks.add(potential);
                            }
                        }
                    }
                }
            }
        }
        
        // Fase 11: BFS a partire dalle foglie trovate per trovare foglie aggiuntive che sono connesse
        Queue<Block> leafQueue = new LinkedList<>(leafBlocks);
        Set<Block> processedLeaves = new HashSet<>(leafBlocks);
        
        while (!leafQueue.isEmpty()) {
            Block currentLeaf = leafQueue.poll();
            
            // Controlla solo i blocchi direttamente adiacenti alle foglie
            for (BlockFace face : primaryDirections) {
                Block neighbor = currentLeaf.getRelative(face);
                
                if (!processedLeaves.contains(neighbor) && isLeaf(neighbor) && neighbor.getType() == leafType) {
                    // Verifica aggiuntiva: controlla se è una foglia naturale
                    if (isNaturalLeaf(neighbor)) {
                        // Verifica che la foglia non sia troppo distante dal centro della chioma
                        double distFromTopCenter = Math.sqrt(
                            Math.pow(neighbor.getX() - topCenterX, 2) + 
                            Math.pow(neighbor.getZ() - topCenterZ, 2)
                        );
                        
                        // Limita l'espansione delle foglie per evitare di catturare altri alberi vicini
                        double expansionLimit = leafSearchRadius * 1.5;
                        
                        // Per alberi della giungla, permetti un'espansione maggiore
                        if (isJungleTree) {
                            expansionLimit *= 1.2;
                        }
                        
                        if (distFromTopCenter <= expansionLimit) {
                            processedLeaves.add(neighbor);
                            leafBlocks.add(neighbor);
                            leafQueue.add(neighbor);
                        }
                    }
                }
            }
        }
        
        // Debug: verifica quanti rami sono stati identificati
        // System.out.println("Albero con " + logBlocks.size() + " blocchi di legno, " + branches.size() + " rami, e " + leafBlocks.size() + " foglie");
        
        return new TreeStructure(new ArrayList<>(logBlocks), new ArrayList<>(leafBlocks));
    }
    
    /**
     * Controlla se una foglia è connessa all'albero (direttamente o tramite altre foglie)
     */
    private boolean isLeafConnectedToTree(Block leaf, Set<Block> logBlocks, Set<Block> visited, int maxDepth) {
        if (maxDepth <= 0) return false;
        
        // Controlla se la foglia è adiacente a un tronco
        for (BlockFace face : new BlockFace[] {
                BlockFace.UP, BlockFace.DOWN, 
                BlockFace.NORTH, BlockFace.SOUTH, 
                BlockFace.EAST, BlockFace.WEST
        }) {
            Block neighbor = leaf.getRelative(face);
            
            // Se tocca direttamente un tronco dell'albero
            if (logBlocks.contains(neighbor)) {
                return true;
            }
            
            // Se tocca un'altra foglia che non abbiamo visitato
            if (!visited.contains(neighbor) && isLeaf(neighbor)) {
                // Marca come visitato per evitare cicli
                visited.add(neighbor);
                // Ricorsione con profondità ridotta
                if (isLeafConnectedToTree(neighbor, logBlocks, visited, maxDepth - 1)) {
                    return true;
                }
            }
        }
        
        // Controlla anche le foglie in diagonale per alberi più complessi
        if (maxDepth >= 2) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        // Salta il blocco centrale (la foglia stessa) e le direzioni già controllate
                        if ((x == 0 && y == 0 && z == 0) || 
                            (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1)) {
                            continue;
                        }
                        
                        Block diagonalNeighbor = leaf.getRelative(x, y, z);
                        
                        if (logBlocks.contains(diagonalNeighbor)) {
                            return true;
                        }
                        
                        if (!visited.contains(diagonalNeighbor) && isLeaf(diagonalNeighbor)) {
                            visited.add(diagonalNeighbor);
                            // Usa una profondità ridotta per le diagonali
                            if (isLeafConnectedToTree(diagonalNeighbor, logBlocks, visited, maxDepth - 2)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Avvia l'animazione di taglio dell'albero
     */
    private void startChoppingAnimation(Player player, List<Block> logs, List<Block> leaves, ItemStack axe, Material logType, Vector treeCenter) {
        UUID playerUUID = player.getUniqueId();
        
        // Ordina i blocchi dal basso verso l'alto
        logs.sort(Comparator.comparingInt(Block::getY));
        
        // Ordina le foglie dall'alto verso il basso (per effetto visivo migliore)
        leaves.sort(Comparator.comparingInt(Block::getY).reversed());
        
        // Calcola il danno all'ascia
        Damageable axeMeta = (Damageable) axe.getItemMeta();
        int totalDamage = Math.min(logs.size(), getAxeMaxDurability(axe.getType()) - axeMeta.getDamage());
        
        // Ottieni la velocità in base al tipo di ascia (delay tra i tick)
        int axeSpeed = getAxeSpeed(axe.getType());
        
        // Colori per le particelle
        Color logColor = getLogColor(logType);
        Material leafType = getMatchingLeafType(logType);
        Color leafColor = getLeafColor(leafType);
        
        // Verifica se è un albero grande
        boolean isLarge = isLargeTree(logs);
        
        // Per gli alberi grandi, crea una sequenza cinematica
        if (isLarge) {
            // Notifica il giocatore
            player.sendMessage("§6§l[TreeCutter] §eHai iniziato a tagliare un grande albero!");
            
            // Effetti iniziali speciali
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.3f, 0.5f);
            
            // Notifica i giocatori vicini
            for (Player nearby : player.getWorld().getPlayers()) {
                if (nearby != player && nearby.getLocation().distance(player.getLocation()) < 50) {
                    nearby.sendMessage("§6§l[TreeCutter] §eUn grande albero sta per essere abbattuto nelle vicinanze!");
                }
            }
            
            // Pausa drammatica prima di iniziare
            new BukkitRunnable() {
                private int count = 0;
                
                @Override
                public void run() {
                    if (count >= 3 || !player.isOnline()) {
                        cancel();
                        // Avvia l'animazione di taglio principale dopo la pausa
                        startMainCuttingSequence(player, logs, leaves, axe, logType, axeSpeed, totalDamage, logColor, leafColor, isLarge, treeCenter);
                        return;
                    }
                    
                    // Effetti di vibrazione e tensione
                    Block baseLog = logs.get(0);
                    player.getWorld().playSound(baseLog.getLocation(), Sound.BLOCK_WOOD_STEP, 1.0f, 0.5f);
                    
                    // Particelle che simulano la tensione
                    for (int i = 0; i < 3; i++) {
                        Block log = logs.get(Math.min(i, logs.size() - 1));
                        player.getWorld().spawnParticle(
                                Particle.CRIT, 
                                log.getLocation().add(0.5, 0.5, 0.5),
                                10, 0.5, 0.2, 0.5, 0.1);
                    }
                    
                    count++;
                }
            }.runTaskTimer(plugin, 10L, 20L);
        } else {
            // Per alberi normali, avvia direttamente la sequenza di taglio
            startMainCuttingSequence(player, logs, leaves, axe, logType, axeSpeed, totalDamage, logColor, leafColor, false, treeCenter);
        }
    }
    
    /**
     * Avvia la sequenza principale di taglio
     */
    private void startMainCuttingSequence(Player player, List<Block> logs, List<Block> leaves, 
                                        ItemStack axe, Material logType, int axeSpeed, 
                                        int totalDamage, Color logColor, Color leafColor, boolean isLarge, Vector treeCenter) {
        UUID playerUUID = player.getUniqueId();
        
        // Ottieni il tipo di foglia corrispondente al tipo di tronco
        Material leafType = getMatchingLeafType(logType);
        
        // Prepara effetti sonori avanzati per alberi grandi
        Sound[] largeTreeSounds = {
            Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR,
            Sound.BLOCK_WOOD_BREAK,
            Sound.ENTITY_GENERIC_EXPLODE,
            Sound.BLOCK_WOOD_HIT
        };
        
        // Per alberi grandi, crea un effetto di scossa iniziale
        if (isLarge) {
            for (Player nearbyPlayer : player.getWorld().getPlayers()) {
                if (nearbyPlayer.getLocation().distance(player.getLocation()) < 50) {
                    // Effetto scossa solo per chi è vicino all'albero
                    nearbyPlayer.spawnParticle(
                        VersionCompatibility.getExplosionParticle(), 
                        logs.get(0).getLocation().add(0.5, 0.5, 0.5),
                        5, 0.2, 0.2, 0.2, 0.1);
                }
            }
        }
        
        // Avvia l'animazione
        new BukkitRunnable() {
            private int logIndex = 0;
            private int leafIndex = 0;
            private boolean processingLogs = true;
            private final Random random = ThreadLocalRandom.current();
            private int specialEffectCounter = 0;
            private double tiltAngle = 0;
            private boolean fallingAnimation = false;
            private int fallingStep = 0;
            
            @Override
            public void run() {
                // Se il player non è più online, interrompi tutto
                if (!player.isOnline()) {
                    finishTreeCutting(playerUUID, treeCenter);
                    cancel();
                    return;
                }
                
                // Effetti speciali per alberi grandi - inclinazione simulata avanzata
                if (isLarge && processingLogs) {
                    // Aumenta il contatore degli effetti speciali ad ogni taglio
                    if (logIndex > 0 && logIndex % 5 == 0) {
                        specialEffectCounter++;
                        
                        // Simula l'inclinazione progressiva dell'albero
                        tiltAngle += 0.01;
                        simulateAdvancedTreeTilting(player, logs, logIndex, tiltAngle);
                        
                        // Aggiungi suoni casuali di scricchiolio
                        if (random.nextBoolean()) {
                            Sound crackSound = largeTreeSounds[random.nextInt(largeTreeSounds.length)];
                            float volume = 0.7f + random.nextFloat() * 0.3f;
                            float pitch = 0.7f + random.nextFloat() * 0.3f;
                            
                            player.getWorld().playSound(
                                logs.get(Math.min(logIndex + 5, logs.size() - 1)).getLocation(),
                                crackSound, volume, pitch);
                        }
                        
                        // Notifica drammatica quando si è tagliato circa il 70% dell'albero
                        if (logIndex > logs.size() * 0.7 && !fallingAnimation && logs.size() > 50) {
                            player.sendMessage("§c§lL'albero sta per cadere!");
                            fallingAnimation = true;
                            
                            // Effetto di scossa per tutti i giocatori nelle vicinanze
                            for (Player nearbyPlayer : player.getWorld().getPlayers()) {
                                if (nearbyPlayer.getLocation().distance(player.getLocation()) < 50) {
                                    nearbyPlayer.playSound(nearbyPlayer.getLocation(), 
                                        Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 0.8f);
                                }
                            }
                        }
                    }
                    
                    // Animazione di caduta per alberi molto grandi
                    if (fallingAnimation) {
                        fallingStep++;
                        simulateTreeFalling(player, logs, logIndex, fallingStep);
                    }
                }
                
                // Prima processa tutti i tronchi, poi le foglie
                if (processingLogs) {
                    if (logIndex >= logs.size()) {
                        processingLogs = false;
                        
                        // Effetto di transizione spettacolare tra tronchi e foglie per alberi grandi
                        if (isLarge) {
                            // Esplosione drammatica al termine del taglio dei tronchi
                            player.getWorld().playSound(player.getLocation(), 
                                Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.4f, 0.8f);
                            
                            // Aumenta l'effetto visivo dell'esplosione
                            player.getWorld().spawnParticle(
                                VersionCompatibility.getExplosionEmitterParticle(), 
                                logs.get(logs.size()-1).getLocation().add(0.5, 1.0, 0.5),
                                5, 1.0, 1.0, 1.0, 0.1);
                            
                            // Aggiunge particelle di detriti per un effetto più realistico
                            for (int i = 0; i < 3; i++) {
                                Block log = logs.get(Math.max(0, logs.size() - i - 1));
                                player.getWorld().spawnParticle(
                                    VersionCompatibility.getBlockParticle(),
                                    log.getLocation().add(0.5, 0.5, 0.5),
                                    30, 2.0, 2.0, 2.0, 0.1,
                                    log.getBlockData());
                            }
                            
                            // Per alberi veramente grandi, crea un effetto di oscillazione del terreno
                            if (logs.size() > 80) {
                                for (Player nearby : player.getWorld().getPlayers()) {
                                    if (nearby.getLocation().distance(player.getLocation()) < 50) {
                                        nearby.sendMessage("§6§l[TreeCutter] §eUn enorme albero è stato abbattuto!");
                                    }
                                }
                            }
                        } else {
                            player.getWorld().playSound(player.getLocation(), 
                                Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
                        }
                        return;
                    }
                    
                    Block block = logs.get(logIndex);
                    
                    // Effetti visivi avanzati
                    // 1. Particelle di crack del blocco (standard)
                    player.getWorld().spawnParticle(
                            VersionCompatibility.getBlockParticle(), 
                            block.getLocation().add(0.5, 0.5, 0.5), 
                            15, 0.5, 0.5, 0.5, 0, 
                            block.getBlockData());
                    
                    // 2. Particelle colorate in base al tipo di legno
                    player.getWorld().spawnParticle(
                            VersionCompatibility.getDustParticle(), 
                            block.getLocation().add(0.5, 0.5, 0.5),
                            8, 0.5, 0.5, 0.5, 0,
                            new DustOptions(logColor, 1.5f));
                    
                    // 3. Particelle di fumo che volano via
                    Vector direction = new Vector(
                            random.nextDouble() - 0.5, 
                            random.nextDouble() * 0.5, 
                            random.nextDouble() - 0.5).normalize().multiply(0.15);
                    
                    player.getWorld().spawnParticle(
                            VersionCompatibility.getSmokeParticle(), 
                            block.getLocation().add(0.5, 0.5, 0.5),
                            5, 0.3, 0.3, 0.3, 0.05);
                    
                    // 4. Scintille per alberi grandi (effetto più drammatico)
                    if (isLarge && random.nextInt(4) == 0) {
                        player.getWorld().spawnParticle(
                                Particle.CRIT, 
                                block.getLocation().add(0.5, 0.5, 0.5),
                                10, 0.5, 0.5, 0.5, 0.2);
                    }
                    
                    // 5. Suoni casuali di taglio con variazioni
                    Sound choppingSound = CHOPPING_SOUNDS[random.nextInt(CHOPPING_SOUNDS.length)];
                    float volume = 0.8f;
                    float pitch = random.nextFloat() * 0.4f + 0.8f; // Pitch casuale
                    
                    // Volume più alto per alberi grandi
                    if (isLarge) {
                        volume = 1.0f;
                        // Pitch più basso per simulare tronchi più grossi
                        pitch = random.nextFloat() * 0.3f + 0.6f;
                    }
                    
                    player.getWorld().playSound(
                            block.getLocation(), 
                            choppingSound, 
                            volume, 
                            pitch);
                    
                    // Aggiungi una vibrazione quando il blocco viene rotto
                    if (random.nextInt(4) == 0) {
                        player.getWorld().playSound(
                                block.getLocation(),
                                Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,
                                0.2f,
                                1.5f);
                    }
                    
                    // Scuoti gli alberi grandi periodicamente
                    if (isLarge && random.nextInt(3) == 0) {
                        shakeNearbyLeaves(block, leafType, player);
                    }
                    
                    // Rimuovi il blocco e droppa l'item
                    block.breakNaturally();
                    
                    // Particelle più intense dopo la rottura
                    player.getWorld().spawnParticle(
                            Particle.CLOUD, 
                            block.getLocation().add(0.5, 0.5, 0.5),
                            isLarge ? 6 : 3, 0.2, 0.2, 0.2, 0.02);
                    
                    logIndex++;
                    
                    // Danneggia l'ascia progressivamente
                    if (logIndex % 3 == 0 || logIndex == logs.size()) {
                        int currentDamage = (int) Math.ceil((double) totalDamage * logIndex / logs.size());
                        damageAxe(player, axe, currentDamage);
                    }
                } else {
                    // Processa le foglie dopo aver finito con i tronchi
                    if (leafIndex >= leaves.size()) {
                        // Rimuovi questo albero dalla lista degli alberi attivi
                        finishTreeCutting(playerUUID, treeCenter);
                        
                        // Effetto finale quando finisce tutto il taglio
                        if (isLarge) {
                            // Effetto speciale per alberi grandi
                            player.getWorld().playSound(player.getLocation(), 
                                Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.8f);
                            
                            // Esplosione di particelle più intensa
                            player.getWorld().spawnParticle(
                                    VersionCompatibility.getExplosionEmitterParticle(), 
                                    player.getLocation().add(0, 1, 0),
                                    8, 1.5, 1.5, 1.5, 0.2);
                            
                            // Aggiunge effetto di spostamento d'aria
                            player.getWorld().spawnParticle(
                                    Particle.CLOUD, 
                                    player.getLocation().add(0, 1, 0),
                                    20, 2.0, 1.0, 2.0, 0.1);
                            
                            player.sendMessage("§6§l[TreeCutter] §a§lHai abbattuto un grande albero!");
                            
                            // Notifica anche i giocatori vicini
                            for (Player nearby : player.getWorld().getPlayers()) {
                                if (nearby != player && nearby.getLocation().distance(player.getLocation()) < 50) {
                                    nearby.sendMessage("§6§l[TreeCutter] §aUn grande albero è stato abbattuto nelle vicinanze!");
                                }
                            }
                        } else {
                            player.getWorld().playSound(player.getLocation(), 
                                Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 0.6f);
                            player.getWorld().spawnParticle(
                                    VersionCompatibility.getExplosionParticle(), 
                                    player.getLocation().add(0, 1, 0),
                                    3, 0.5, 0.5, 0.5, 0.1);
                        }
                        
                        cancel();
                        return;
                    }
                    
                    // Processa più foglie per tick per velocizzare l'effetto
                    int leavesToProcess = Math.min(isLarge ? 12 : 5, leaves.size() - leafIndex);
                    for (int i = 0; i < leavesToProcess; i++) {
                        Block block = leaves.get(leafIndex);
                        
                        // Verifica che la foglia esista ancora
                        if (isLeaf(block)) {
                            // Effetti visivi migliorati per le foglie
                            // 1. Particelle di crack del blocco
                            player.getWorld().spawnParticle(
                                    VersionCompatibility.getBlockParticle(), 
                                    block.getLocation().add(0.5, 0.5, 0.5), 
                                    8, 0.5, 0.5, 0.5, 0, 
                                    block.getBlockData());
                            
                            // 2. Particelle colorate in base al tipo di foglia
                            player.getWorld().spawnParticle(
                                    VersionCompatibility.getDustParticle(), 
                                    block.getLocation().add(0.5, 0.5, 0.5),
                                    5, 0.5, 0.5, 0.5, 0,
                                    new DustOptions(leafColor, 1.0f));
                            
                            // 3. Effetto di particelle che cadono con variazioni
                            if (random.nextBoolean()) {
                                player.getWorld().spawnParticle(
                                        Particle.FALLING_DUST, 
                                        block.getLocation().add(0.5, 0.5, 0.5),
                                        3, 0.4, 0.4, 0.4, 0,
                                        block.getBlockData());
                                
                                // Per alberi grandi, aggiungi particelle extra
                                if (isLarge && random.nextInt(5) == 0) {
                                    // Particelle che simulano piccoli insetti/detriti che volano via
                                    player.getWorld().spawnParticle(
                                            Particle.CRIT, 
                                            block.getLocation().add(0.5, 0.5, 0.5),
                                            2, 0.7, 0.7, 0.7, 0.1);
                                }
                            }
                            
                            // 4. Suono di foglie che si rompono con variazioni
                            if (i == 0 || random.nextInt(3) == 0) {
                                Sound leafSound = random.nextBoolean() ? 
                                        Sound.BLOCK_GRASS_BREAK : Sound.BLOCK_AZALEA_LEAVES_BREAK;
                                        
                                player.getWorld().playSound(
                                        block.getLocation(), 
                                        leafSound, 
                                        0.3f, 
                                        random.nextFloat() * 0.5f + 0.8f);
                            }
                            
                            // 5. Per alberi grandi, occasionalmente aggiungi suoni di uccelli/animali che scappano
                            if (isLarge && random.nextInt(20) == 0) {
                                Sound wildlifeSound = random.nextBoolean() ? 
                                        Sound.ENTITY_BAT_TAKEOFF : Sound.ENTITY_PARROT_FLY;
                                
                                player.getWorld().playSound(
                                        block.getLocation(),
                                        wildlifeSound,
                                        0.2f,
                                        random.nextFloat() * 0.4f + 0.8f);
                            }
                            
                            // Rimuovi il blocco e droppa l'item
                            block.breakNaturally();
                        }
                        
                        leafIndex++;
                        
                        if (leafIndex >= leaves.size()) {
                            break;
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 2L, isLarge ? Math.max(1, axeSpeed - 1) : axeSpeed);
    }
    
    /**
     * Simula un effetto avanzato di inclinazione dell'albero durante il taglio
     */
    private void simulateAdvancedTreeTilting(Player player, List<Block> logs, int currentIndex, double tiltAngle) {
        // Controllo di sicurezza per evitare IndexOutOfBoundsException
        if (logs.isEmpty() || currentIndex >= logs.size()) {
            return;
        }
        
        // Calcola il centro dell'albero
        Vector center = new Vector(0, 0, 0);
        int minY = Integer.MAX_VALUE;
        
        // Trova la base dell'albero e il centro
        for (Block log : logs) {
            center.add(new Vector(log.getX(), 0, log.getZ()));
            if (log.getY() < minY) {
                minY = log.getY();
            }
        }
        center.multiply(1.0 / logs.size());
        
        // Seleziona tutti i log al di sopra del punto di taglio
        List<Block> affectedLogs = new ArrayList<>();
        for (Block log : logs) {
            if (log.getY() > logs.get(currentIndex).getY()) {
                affectedLogs.add(log);
            }
        }
        
        // Limita per non sovraccaricare il server
        if (affectedLogs.size() > 30) {
            Collections.shuffle(affectedLogs);
            affectedLogs = affectedLogs.subList(0, 30);
        }
        
        // Gestisce il caso in cui non ci sono log affetti
        if (affectedLogs.isEmpty()) {
            return;
        }
        
        // Calcola la direzione di inclinazione (in modo realistico verso il lato tagliato)
        Vector baseDirection = new Vector(
            logs.get(Math.min(currentIndex, logs.size() - 1)).getX() - center.getX(),
            0,
            logs.get(Math.min(currentIndex, logs.size() - 1)).getZ() - center.getZ()
        ).normalize();
        
        // Se la direzione è un vettore nullo (può accadere con alberi perfettamente simmetrici),
        // usa una direzione predefinita
        if (baseDirection.lengthSquared() < 0.01) {
            baseDirection = new Vector(1, 0, 0);
        }
        
        // Ruota leggermente la direzione per renderla meno prevedibile
        double rotationAngle = Math.toRadians(15 * Math.sin(tiltAngle * 5));
        double cosRot = Math.cos(rotationAngle);
        double sinRot = Math.sin(rotationAngle);
        double newX = baseDirection.getX() * cosRot - baseDirection.getZ() * sinRot;
        double newZ = baseDirection.getX() * sinRot + baseDirection.getZ() * cosRot;
        baseDirection = new Vector(newX, 0, newZ).normalize();
        
        // Intensità dell'inclinazione basata sull'angolo corrente
        double intensity = Math.sin(tiltAngle) * 0.1;
        
        // Crea effetti visivi che simulano l'inclinazione
        Random random = ThreadLocalRandom.current();
        
        for (Block log : affectedLogs) {
            // Calcola l'intensità relativa in base all'altezza
            // Usa un indice sicuro per ottenere il log più alto
            int lastLogIndex = logs.size() - 1;
            double relativeHeight = (log.getY() - minY) / 
                                    (double)(logs.get(Math.min(lastLogIndex, logs.size() - 1)).getY() - minY);
            double scaledIntensity = intensity * relativeHeight * 2.0;
            
            // Calcola lo spostamento in base alla direzione e all'intensità
            Vector displacement = baseDirection.clone().multiply(scaledIntensity);
            
            // Aggiungi una variazione casuale per un effetto più naturale
            displacement.add(new Vector(
                (random.nextDouble() - 0.5) * 0.05,
                (random.nextDouble() - 0.5) * 0.05,
                (random.nextDouble() - 0.5) * 0.05
            ));
            
            // Crea particelle che simulano lo spostamento
            player.getWorld().spawnParticle(
                VersionCompatibility.getBlockParticle(),
                log.getLocation().add(0.5 + displacement.getX(), 0.5, 0.5 + displacement.getZ()),
                3, 0.2, 0.2, 0.2, 0,
                log.getBlockData()
            );
            
            // Per i log più alti, simula foglie/detriti che cadono
            if (relativeHeight > 0.7 && random.nextInt(5) == 0) {
                player.getWorld().spawnParticle(
                    Particle.FALLING_DUST,
                    log.getLocation().add(0.5 + displacement.getX(), 0.5, 0.5 + displacement.getZ()),
                    1, 0.1, 0.1, 0.1, 0,
                    log.getBlockData()
                );
            }
        }
        
        // Usa un indice sicuro per il suono
        int soundIndex = Math.min(currentIndex, logs.size() - 1);
        
        // Suoni che simulano lo stress del legno
        player.getWorld().playSound(
            logs.get(soundIndex).getLocation(),
            Sound.BLOCK_WOOD_PLACE,
            0.8f,
            0.5f + (float)(tiltAngle * 0.2)
        );
    }
    
    /**
     * Simula la caduta di un albero grande in modo spettacolare
     */
    private void simulateTreeFalling(Player player, List<Block> logs, int currentIndex, int step) {
        // Controllo di sicurezza
        if (logs.isEmpty() || currentIndex >= logs.size() - 10) return;
        
        if (step % 5 != 0) return;
        
        // Trova il blocco più alto ancora in piedi
        Block highestLog = null;
        int highestY = 0;
        
        for (int i = currentIndex; i < logs.size(); i++) {
            Block log = logs.get(i);
            if (log.getY() > highestY) {
                highestY = log.getY();
                highestLog = log;
            }
        }
        
        if (highestLog == null) return;
        
        // Calcola il centro dell'albero
        Vector center = new Vector(0, 0, 0);
        int validLogs = 0;
        
        for (int i = 0; i < Math.min(currentIndex, logs.size()); i++) {
            Block log = logs.get(i);
            center.add(new Vector(log.getX(), 0, log.getZ()));
            validLogs++;
        }
        
        if (validLogs == 0) return;
        
        center.multiply(1.0 / validLogs);
        
        // Direzione di caduta
        Vector fallDirection = new Vector(
            highestLog.getX() - center.getX(),
            0,
            highestLog.getZ() - center.getZ()
        ).normalize();
        
        // Se la direzione è un vettore nullo, usa una direzione predefinita
        if (fallDirection.lengthSquared() < 0.01) {
            fallDirection = new Vector(1, 0, 0);
        }
        
        // Intensità crescente con il passare del tempo
        double fallIntensity = Math.min(1.0, step / 30.0);
        
        // Calcola l'arco di caduta
        double fallAngle = fallIntensity * Math.PI / 3; // massimo 60 gradi
        
        // Distanza dipendente dall'altezza
        double maxDistance = (highestY - logs.get(0).getY()) * fallIntensity;
        
        // Suono di legno che scricchiola
        player.getWorld().playSound(
            highestLog.getLocation(),
            Sound.BLOCK_BAMBOO_BREAK,
            1.0f,
            0.5f
        );
        
        // Effetti particellari intensi - usa step per limitare il numero di particelle
        int step2 = step % 4 + 2; // Varia da 2 a 5
        for (int i = currentIndex; i < logs.size(); i += step2) {
            Block log = logs.get(i);
            
            // Calcola la posizione relativa rispetto al punto di taglio
            double relHeight = (log.getY() - logs.get(0).getY()) / (double)(highestY - logs.get(0).getY());
            if (Double.isNaN(relHeight)) continue; // Evita divisione per zero
            
            // Simula un movimento ad arco
            double distanceFactor = relHeight * maxDistance;
            
            // Calcola lo spostamento in base all'angolo e all'altezza
            Vector displacement = fallDirection.clone().multiply(distanceFactor * Math.sin(fallAngle));
            displacement.setY(distanceFactor * (1 - Math.cos(fallAngle)));
            
            // Crea particelle di crack per simulare il movimento
            player.getWorld().spawnParticle(
                VersionCompatibility.getBlockParticle(),
                log.getLocation().add(0.5 + displacement.getX(), 0.5 - displacement.getY(), 0.5 + displacement.getZ()),
                5, 0.2, 0.2, 0.2, 0,
                log.getBlockData()
            );
            
            // Per i blocchi più in alto, aggiungi particelle d'impatto che anticipano la caduta
            if (relHeight > 0.8 && step % 10 == 0) {
                try {
                    // Calcola dove "atterrerà" il blocco
                    Vector landingPos = new Vector(
                        log.getX() + fallDirection.getX() * maxDistance * 2, 
                        log.getY(), 
                        log.getZ() + fallDirection.getZ() * maxDistance * 2
                    );
                    
                    Block landingBlock = player.getWorld().getBlockAt(
                        (int)landingPos.getX(), 
                        player.getWorld().getHighestBlockYAt((int)landingPos.getX(), (int)landingPos.getZ()), 
                        (int)landingPos.getZ()
                    );
                    
                    // Crea particelle di polvere al punto di impatto previsto
                    player.getWorld().spawnParticle(
                        VersionCompatibility.getBlockParticle(),
                        landingBlock.getLocation().add(0.5, 1.0, 0.5),
                        10, 1.0, 0.2, 1.0, 0,
                        landingBlock.getBlockData()
                    );
                } catch (Exception e) {
                    // Gestisce eventuali errori nel calcolo del punto di impatto
                    // Può succedere se si tenta di calcolare l'altezza di un chunk non caricato
                }
            }
        }
    }
    
    /**
     * Danneggia l'ascia in base al numero di blocchi tagliati
     */
    private void damageAxe(Player player, ItemStack axe, int damage) {
        if (axe == null) return;
        
        ItemMeta meta = axe.getItemMeta();
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            int currentDamage = damageable.getDamage();
            int maxDurability = getAxeMaxDurability(axe.getType());
            
            int newDamage = currentDamage + damage;
            
            // Controlla se l'ascia si rompe
            if (newDamage >= maxDurability) {
                player.getInventory().setItemInMainHand(null);
                player.getWorld().playSound(player.getLocation(), 
                        Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                
                // Aggiungi effetti di rottura più impressionanti
                player.getWorld().spawnParticle(
                        VersionCompatibility.getItemParticle(), 
                        player.getLocation().add(0, 1, 0),
                        20, 0.3, 0.3, 0.3, 0.1,
                        axe);
            } else {
                damageable.setDamage(newDamage);
                axe.setItemMeta(meta);
            }
        }
    }
    
    /**
     * Restituisce la durabilità massima di un'ascia
     */
    private int getAxeMaxDurability(Material axeType) {
        switch (axeType) {
            case WOODEN_AXE:
                return 59;
            case STONE_AXE:
                return 131;
            case IRON_AXE:
                return 250;
            case GOLDEN_AXE:
                return 32;
            case DIAMOND_AXE:
                return 1561;
            case NETHERITE_AXE:
                return 2031;
            default:
                return 100;
        }
    }
    
    /**
     * Controlla se un giocatore sta tagliando un albero
     */
    public boolean isPlayerCuttingTree(UUID playerUUID) {
        return cuttingTrees.containsKey(playerUUID) && cuttingTrees.get(playerUUID);
    }

    private boolean isLargeTree(List<Block> logs) {
        // Un albero è considerato grande se ha più di 30 blocchi di tronco
        // o se è alto più di 10 blocchi
        if (logs.size() > 30) return true;
        
        // Trova l'altezza dell'albero
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        for (Block log : logs) {
            int y = log.getY();
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        
        return (maxY - minY) > 10;
    }

    /**
     * Fa vibrare le foglie vicine per simulare il movimento dell'albero
     */
    private void shakeNearbyLeaves(Block center, Material leafType, Player player) {
        Random random = ThreadLocalRandom.current();
        
        // Cerca le foglie in un raggio di 3 blocchi
        for (int x = -3; x <= 3; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    if (random.nextInt(5) != 0) continue; // Solo alcune foglie casuali
                    
                    Block leaf = center.getRelative(x, y, z);
                    if (leaf.getType() == leafType) {
                        // Particelle che simulano le foglie che si muovono
                        player.getWorld().spawnParticle(
                                VersionCompatibility.getBlockParticle(),
                                leaf.getLocation().add(0.5, 0.5, 0.5),
                                2, 0.4, 0.4, 0.4, 0,
                                leaf.getBlockData());
                        
                        // Occasionalmente aggiungi una particella di foglia che cade
                        if (random.nextInt(10) == 0) {
                            player.getWorld().spawnParticle(
                                    Particle.FALLING_DUST,
                                    leaf.getLocation().add(0.5, 0.2, 0.5),
                                    1, 0.2, 0.1, 0.2, 0,
                                    leaf.getBlockData());
                        }
                    }
                }
            }
        }
    }

    /**
     * Rimuove un albero dalla lista degli alberi attivi di un giocatore
     */
    private void finishTreeCutting(UUID playerUUID, Vector treeCenter) {
        // Rimuovi questo albero specifico dai tagli attivi
        if (playerActiveTrees.containsKey(playerUUID)) {
            Set<Vector> activeTrees = playerActiveTrees.get(playerUUID);
            
            // Trova l'albero più vicino al centro specificato
            Vector treeToRemove = null;
            double minDistance = Double.MAX_VALUE;
            
            for (Vector tree : activeTrees) {
                double distance = tree.distance(treeCenter);
                if (distance < minDistance) {
                    minDistance = distance;
                    treeToRemove = tree;
                }
            }
            
            if (treeToRemove != null) {
                activeTrees.remove(treeToRemove);
            }
            
            // Se non ci sono più alberi attivi, imposta lo stato del giocatore a falso
            if (activeTrees.isEmpty()) {
                cuttingTrees.put(playerUUID, false);
            }
        }
    }

    /**
     * Ottiene il numero di alberi che un giocatore sta tagliando attualmente
     */
    public int getPlayerActiveTreeCount(UUID playerUUID) {
        if (!playerActiveTrees.containsKey(playerUUID)) {
            return 0;
        }
        return playerActiveTrees.get(playerUUID).size();
    }
} 
