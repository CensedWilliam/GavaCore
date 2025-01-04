import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.zip.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.security.NoSuchAlgorithmException;
import java.net.HttpURLConnection;
import javax.swing.border.*;
import javax.imageio.ImageIO;
import java.awt.geom.*;
import javax.swing.Timer;
import java.util.Properties;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.Map;
import java.util.ArrayList;
import java.util.Enumeration;

public class ModpackInstaller extends JFrame {
    private static final String MINECRAFT_PATH = getDefaultMinecraftPath();
    private static final String GAVACORE_VERSION = "1.20.1";
    private static final String GAVACORE_ZIP_URL = "https://www.dropbox.com/scl/fi/xrwr1syyhuaf8096w27p1/GavaCore.zip?rlkey=s8o3hc1azudzqolrugxxgdk7o&st=h9mxtwhp&dl=1"; // Sostituisci con l'URL reale del tuo ZIP
    private static final String ICON_16 = "/images/icon-16.png";
    private static final String ICON_32 = "/images/icon-32.png";
    private static final String TITLE_IMAGE = "/images/title.png";
    private static final String CUSTOM_FONT = "/fonts/Minecraft.ttf";  // Font stile Minecraft
    private Font customFont;
    private static final Color ORANGE_MAIN = new Color(255, 140, 0);
    private static final Color ORANGE_DARK = new Color(255, 103, 0);
    private static final Color DARK_BG = new Color(20, 20, 20);
    private static final Color DARK_PANEL = new Color(30, 30, 30);
    private static final Color HOVER_COLOR = new Color(40, 40, 40);
    private static final Color TEXT_COLOR = new Color(240, 240, 240);
    
    // Aggiungi colori personalizzati
    private static final Color BACKGROUND_COLOR = new Color(36, 36, 36);
    private static final Color FOREGROUND_COLOR = new Color(230, 230, 230);
    private static final Color ACCENT_COLOR = new Color(64, 169, 243);
    
    private JProgressBar progressBar;
    private JButton selectButton;
    private JButton installButton;
    private JLabel statusLabel;
    private File selectedFile;
    private JPanel modpackPanel;
    private Image gavaIcon;
    private float buttonAlpha = 0.8f;
    private Timer pulseTimer;

    // Aggiungi costanti per le icone dei launcher
    private static final String TLAUNCHER_ICON = "/images/tlauncher.png";
    private static final String PRISM_ICON = "/images/prism.png";

    // Aggiungi queste variabili di classe
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private SwingWorker<Void, Integer> currentInstallation;
    private Path currentInstallPath;
    private JButton cancelButton;
    private String selectedLauncher = null;
    private JLabel pathLabel;
    private static final String PRISM_ZIP_URL = "https://example.com/prism-gavacore.zip"; // URL per Prism

    // Aggiungi questa costante per il nome della pagina di download
    private static final String DOWNLOAD_PAGE = "DOWNLOAD";

    // Aggiungi queste costanti
    private static final int NUM_PARTICLES = 50;
    private List<Particle> particles = new ArrayList<>();
    private Timer particleTimer;

    // Aggiungi questa classe interna per le particelle
    private class Particle {
        float x, y;
        float speedX, speedY;
        float size;
        float alpha;
        
        public Particle() {
            reset();
            x = (float)(Math.random() * getWidth());
            y = (float)(Math.random() * getHeight());
        }
        
        void reset() {
            x = (float)(Math.random() * getWidth());
            y = getHeight() + size;
            speedX = (float)(Math.random() * 2 - 1);
            speedY = (float)(Math.random() * -2 - 1);
            size = (float)(Math.random() * 4 + 2);
            alpha = (float)(Math.random() * 0.5 + 0.5);
        }
        
        void update() {
            x += speedX;
            y += speedY;
            alpha -= 0.005f;
            
            if (y < -size || alpha <= 0) {
                reset();
            }
        }
    }

    public ModpackInstaller() {
        super("GavaCore Installer");
        
        // Carica il font personalizzato
        loadCustomFont();
        
        // Inizializza i componenti UI base
        progressBar = new JProgressBar(0, 100);
        statusLabel = new JLabel("Pronto per l'installazione");
        
        // Imposta la dimensione minima della finestra
        setMinimumSize(new Dimension(600, 400));
        
        // Configura direttamente l'interfaccia
        setupGUI();
        setupLookAndFeel();
        setupParticles();
        
        // Controlla gli aggiornamenti
        SwingUtilities.invokeLater(() -> {
            UpdateChecker.checkForUpdates(this);
        });
    }

    private static String getDefaultMinecraftPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return System.getenv("APPDATA") + "\\.minecraft";
        } else if (os.contains("mac")) {
            return System.getProperty("user.home") + "/Library/Application Support/minecraft";
        } else { // Linux
            return System.getProperty("user.home") + "/.minecraft";
        }
    }

    private void checkTLauncherPath() {
        Path tlauncherPath = Paths.get(MINECRAFT_PATH);
        if (!Files.exists(tlauncherPath)) {
            int result = JOptionPane.showConfirmDialog(this,
                "Il percorso di TLauncher non è stato trovato in:\n" + MINECRAFT_PATH + 
                "\nVuoi selezionare manualmente la cartella .minecraft?",
                "TLauncher non trovato",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                selectTLauncherPath();
            } else {
                System.exit(0);
            }
        }
    }

    private void selectTLauncherPath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Seleziona la cartella .minecraft");
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            if (isValidMinecraftFolder(selected)) {
                // Usa la classe Config per salvare il percorso
                Config.setMinecraftPath(selected.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(this,
                    "La cartella selezionata non sembra essere una cartella .minecraft valida",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    private boolean isValidMinecraftFolder(File folder) {
        return folder.exists() &&
               new File(folder, "versions").exists() &&
               new File(folder, "assets").exists();
    }

    private void setupLookAndFeel() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("mac")) {
                // Usa il look and feel nativo di Mac
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("apple.awt.application.name", "GavaCore Installer");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {
                // Usa Nimbus per Windows e Linux
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Personalizza i colori dell'interfaccia
        UIManager.put("Panel.background", DARK_BG);
        UIManager.put("OptionPane.background", DARK_BG);
        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("Button.background", ORANGE_MAIN);
        UIManager.put("ProgressBar.foreground", ORANGE_MAIN);
    }

    private void setupGUI() {
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(DARK_BG);
        
        // Usa CardLayout per gestire le diverse pagine
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(DARK_BG);
        
        // Pagina principale
        JPanel mainPanel = createMainPanel();
        // Pagina selezione launcher
        JPanel launcherPanel = createLauncherSelectionPanel();
        // Pagina selezione versione
        JPanel versionPanel = createVersionSelectionPanel();
        // Pagina download
        JPanel downloadPanel = createDownloadPanel();
        
        contentPanel.add(mainPanel, "MAIN");
        contentPanel.add(launcherPanel, "LAUNCHER");
        contentPanel.add(versionPanel, "VERSION");
        contentPanel.add(downloadPanel, "DOWNLOAD");
        
        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "MAIN"); // Mostra direttamente la pagina principale
        setSize(700, 500);
        setLocationRelativeTo(null);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(DARK_PANEL);
        
        // Carica e mostra il logo principale
        JLabel logoLabel = new JLabel();
        try {
            InputStream titleStream = getClass().getResourceAsStream(TITLE_IMAGE);
            if (titleStream != null) {
                Image titleImage = ImageIO.read(titleStream);
                int maxWidth = 350;
                int originalWidth = titleImage.getWidth(null);
                int originalHeight = titleImage.getHeight(null);
                int newWidth = maxWidth;
                int newHeight = (maxWidth * originalHeight) / originalWidth;
                
                Image scaledImage = titleImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaledImage));
            }
        } catch (Exception e) {
            System.out.println("Impossibile caricare il logo principale: " + e.getMessage());
        }
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Pannello principale con effetto hover
        JPanel mainPanel = createHoverPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        // Pannello contenitore per le due colonne (rinominato per evitare conflitti)
        JPanel columnsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        columnsPanel.setOpaque(false);
        
        // Colonna sinistra - Dettagli
        JPanel leftPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        leftPanel.setOpaque(false);
        
        // Colonna destra - Lista mod
        JPanel rightPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        rightPanel.setOpaque(false);
        
        // Titolo principale
        JLabel titleLabel = createAnimatedLabel("GavaCore " + GAVACORE_VERSION + " ⚡", 24);
        
        // Descrizione principale
        JLabel descLabel = createAnimatedLabel("<html><div style='text-align: center;'>" +
            "ModPack creato da GavaTech/CensedWilliam<br>" +
            "Ufficiale" +
            "</div></html>", 16);
        
        // Aggiungi i dettagli alla colonna sinistra
        leftPanel.add(titleLabel);
        leftPanel.add(descLabel);
        
        // Features principali
        JLabel featuresLabel = createAnimatedLabel("<html><div style='text-align: left;'>" +
            "<b>Mod Principali:</b><br>" +
            "• Create e addons (Create, Create Additions, Create Deco, etc.)<br>" +
            "• Biomes O' Plenty<br>" +
            "• Farmer's Delight<br>" +
            "• Immersive Engineering<br>" +
            "• Macaw's Mods (Windows, Doors, Bridges, etc.)<br>" +
            "• Sophisticated Backpacks<br>" +
            "• Better Combat<br>" +
            "• Performance Mods (Embeddium, ModernFix, etc.)<br>" +
            "• E molte altre..." +
            "</div></html>", 14);
        
        // Aggiungi la lista delle mod alla colonna destra
        rightPanel.add(featuresLabel);
        
        // Nota informativa
        JLabel noteLabel = new JLabel(
            "<html><div style='text-align: center; color: #FFA500;'>" +
            "Scegli il tuo launcher preferito nella prossima schermata" +
            "</div></html>"
        );
        
        leftPanel.add(noteLabel);
        
        // Aggiungi le colonne al pannello contenitore
        columnsPanel.add(leftPanel);
        columnsPanel.add(rightPanel);
        
        mainPanel.add(logoLabel, BorderLayout.NORTH);
        mainPanel.add(columnsPanel, BorderLayout.CENTER);
        
        // Pulsante installazione
        JButton installButton = createStyledButton("Inizia Installazione 🚀");
        installButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(ModpackInstaller.this.contentPanel, "LAUNCHER");
            }
        });
        mainPanel.add(installButton, BorderLayout.SOUTH);
        
        panel.add(mainPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHoverPanel() {
        return new JPanel(new BorderLayout()) {
            private float alpha = 0f;
            private Timer fadeTimer;

            {
                setOpaque(false);
                setBackground(DARK_PANEL);
                
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        startFade(true);
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        startFade(false);
                    }
                });
            }

            private void startFade(boolean fadeIn) {
                if (fadeTimer != null && fadeTimer.isRunning()) {
                    fadeTimer.stop();
                }
                
                fadeTimer = new Timer(20, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (fadeIn) {
                            alpha = Math.min(1f, alpha + 0.1f);
                        } else {
                            alpha = Math.max(0f, alpha - 0.1f);
                        }
                        
                        if ((fadeIn && alpha >= 1f) || (!fadeIn && alpha <= 0f)) {
                            fadeTimer.stop();
                        }
                        repaint();
                    }
                });
                fadeTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sfondo base
                g2d.setColor(DARK_PANEL);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                
                // Effetto hover
                if (alpha > 0) {
                    g2d.setColor(new Color(HOVER_COLOR.getRed(), HOVER_COLOR.getGreen(), 
                                         HOVER_COLOR.getBlue(), (int)(alpha * 255)));
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                }
                
                // Bordo luminoso
                g2d.setStroke(new BasicStroke(2f));
                g2d.setColor(new Color(ORANGE_MAIN.getRed(), ORANGE_MAIN.getGreen(), 
                                     ORANGE_MAIN.getBlue(), 50));
                g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 20, 20));
                
                g2d.dispose();
            }
        };
    }

    private JLabel createAnimatedLabel(String text, int fontSize) {
        // Rimuovi le emoji dal testo principale
        String textWithoutEmoji = text.replaceAll("[\\x{1F300}-\\x{1F9FF}]", "");
        String emojiOnly = text.replaceAll("[^\\x{1F300}-\\x{1F9FF}]", "");
        
        String message = "<html>" +
            "<div style='text-align: center;'>" +
            textWithoutEmoji +
            "</div>" +
            "</html>";

        JLabel label = new JLabel(message) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Disegna il testo principale con il font Minecraft
                g2d.setFont(customFont.deriveFont(Font.PLAIN, fontSize));
                super.paintComponent(g2d);
                
                // Disegna le emoji con il font di sistema
                if (!emojiOnly.isEmpty()) {
                    g2d.setFont(new Font("Dialog", Font.PLAIN, fontSize));
                    FontMetrics fm = g2d.getFontMetrics();
                    g2d.drawString(emojiOnly, getWidth() - fm.stringWidth(emojiOnly) - 10, fm.getAscent());
                }
                
                g2d.dispose();
            }
        };
        
        label.setForeground(Color.WHITE);
        label.setFont(customFont.deriveFont(Font.PLAIN, fontSize));
        
        // Animazione fade-in
        label.setVisible(false);
        Timer timer = new Timer(50, null);
        timer.setInitialDelay(fontSize * 20);
        timer.addActionListener(new ActionListener() {
            float alpha = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha = Math.min(1, alpha + 0.1f);
                label.setForeground(new Color(1f, 1f, 1f, alpha));
                if (alpha >= 1) {
                    timer.stop();
                }
            }
        });
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                label.setVisible(true);
                timer.start();
            }
        });
        
        return label;
    }

    private JButton createStyledButton(String text) {
        // Separa emoji e testo
        String textWithoutEmoji = text.replaceAll("[\\x{1F300}-\\x{1F9FF}]", "");
        String emojiOnly = text.replaceAll("[^\\x{1F300}-\\x{1F9FF}]", "");
        
        return new JButton(textWithoutEmoji) {
            private float glowAlpha = 0f;
            private Timer glowTimer;
            
            {
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setFont(customFont.deriveFont(Font.PLAIN, 16));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setForeground(Color.WHITE);
                
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        startGlowEffect(true);
                    }
                    public void mouseExited(MouseEvent e) {
                        startGlowEffect(false);
                    }
                });
            }
            
            private void startGlowEffect(boolean fadeIn) {
                if (glowTimer != null && glowTimer.isRunning()) {
                    glowTimer.stop();
                }
                
                glowTimer = new Timer(20, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (fadeIn) {
                            glowAlpha = Math.min(1f, glowAlpha + 0.1f);
                        } else {
                            glowAlpha = Math.max(0f, glowAlpha - 0.1f);
                        }
                        
                        if ((fadeIn && glowAlpha >= 1f) || (!fadeIn && glowAlpha <= 0f)) {
                            glowTimer.stop();
                        }
                        repaint();
                    }
                });
                glowTimer.start();
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Disegna il pulsante con gradiente arancione
                GradientPaint gradient = new GradientPaint(
                    0, 0, ORANGE_MAIN,
                    0, getHeight(), ORANGE_DARK
                );
                g2d.setPaint(gradient);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                
                // Effetto hover con bagliore
                if (glowAlpha > 0) {
                    for (int i = 5; i > 0; i--) {
                        float alpha = (glowAlpha * 0.3f) / i;
                        g2d.setColor(new Color(1f, 1f, 1f, alpha * 0.5f));
                        g2d.fill(new RoundRectangle2D.Float(i, i, 
                                getWidth()-2*i, getHeight()-2*i, 20, 20));
                    }
                }
                
                // Aggiungi un sottile bordo luminoso
                g2d.setStroke(new BasicStroke(1f));
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 20, 20));
                
                // Disegna il testo con il font Minecraft
                g2d.setFont(getFont());
                FontMetrics fmText = g2d.getFontMetrics();
                Rectangle2D rText = fmText.getStringBounds(textWithoutEmoji, g2d);
                
                // Disegna le emoji con il font di sistema
                g2d.setFont(new Font("Dialog", Font.PLAIN, getFont().getSize()));
                FontMetrics fmEmoji = g2d.getFontMetrics();
                Rectangle2D rEmoji = fmEmoji.getStringBounds(emojiOnly, g2d);
                
                // Calcola la larghezza totale
                int totalWidth = (int)(rText.getWidth() + (emojiOnly.isEmpty() ? 0 : rEmoji.getWidth() + 5));
                
                // Calcola le posizioni x
                int x = (getWidth() - totalWidth) / 2;
                int y = (getHeight() - (int)rText.getHeight()) / 2 + fmText.getAscent();
                
                // Aggiungi un leggero effetto ombra al testo
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.setFont(getFont());
                g2d.drawString(textWithoutEmoji, x+1, y+1);
                
                // Disegna il testo principale
                g2d.setColor(getForeground());
                g2d.drawString(textWithoutEmoji, x, y);
                
                // Disegna le emoji
                if (!emojiOnly.isEmpty()) {
                    g2d.setFont(new Font("Dialog", Font.PLAIN, getFont().getSize()));
                    g2d.drawString(emojiOnly, x + (int)rText.getWidth() + 5, y);
                }
                
                g2d.dispose();
            }
        };
    }

    private Image loadImage(String path) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void installGavaCore() {
        statusLabel.setText("Preparazione installazione GavaCore...");
        progressBar.setValue(0);

        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                // 1. Crea le cartelle necessarie
                Path versionsDir = Paths.get(MINECRAFT_PATH, "versions");
                Path gavaCoreDir = versionsDir.resolve("GavaCore");
                Files.createDirectories(gavaCoreDir);
                publish(10);

                // 2. Download del file ZIP di GavaCore
                statusLabel.setText("Download GavaCore...");
                Path tempDir = Files.createTempDirectory("gavacore_temp");
                Path gavaCoreZip = downloadFile(GAVACORE_ZIP_URL, tempDir.resolve("gavacore.zip"));
                publish(50);

                // 3. Estrazione del file ZIP nella cartella GavaCore
                statusLabel.setText("Installazione GavaCore...");
                extractGavaCore(gavaCoreZip, gavaCoreDir);
                publish(90);

                // Pulizia
                Files.walk(tempDir)
                     .sorted(Comparator.reverseOrder())
                     .forEach(path -> {
                         try {
                             Files.delete(path);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     });

                publish(100);
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                int progress = chunks.get(chunks.size() - 1);
                progressBar.setValue(progress);
            }

            @Override
            protected void done() {
                try {
                    get();
                    statusLabel.setText("GavaCore installato con successo! Seleziona il profilo 'GavaCore' nel launcher");
                } catch (Exception e) {
                    statusLabel.setText("Errore: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private void extractGavaCore(Path zipFile, Path gavaCoreDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    // Crea il percorso completo per il file
                    Path filePath = gavaCoreDir.resolve(entry.getName());
                    
                    // Crea le directory necessarie
                    Files.createDirectories(filePath.getParent());
                    
                    // Estrai il file
                    try (OutputStream out = Files.newOutputStream(filePath)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    private void createVersionJson(Path gavaCoreDir, String forgeVersion) throws IOException {
        String jsonContent = String.format("{\n" +
            "    \"id\": \"GavaCore\",\n" +
            "    \"inheritsFrom\": \"1.20.1\",\n" +
            "    \"releaseTime\": \"2024-01-02T12:00:00+00:00\",\n" +
            "    \"time\": \"2024-01-02T12:00:00+00:00\",\n" +
            "    \"type\": \"release\",\n" +
            "    \"mainClass\": \"net.minecraft.launchwrapper.Launch\",\n" +
            "    \"arguments\": {\n" +
            "        \"game\": [\n" +
            "            \"--tweakClass\",\n" +
            "            \"net.minecraftforge.fml.common.launcher.FMLTweaker\"\n" +
            "        ]\n" +
            "    },\n" +
            "    \"libraries\": [\n" +
            "        {\n" +
            "            \"name\": \"net.minecraftforge:forge:%s\"\n" +
            "        }\n" +
            "    ]\n" +
            "}", forgeVersion);

        Files.write(gavaCoreDir.resolve("GavaCore.json"), jsonContent.getBytes());
    }

    private void extractMods(Path modsZip, Path modsDir) throws IOException {
        // Assicurati che la directory esista
        Files.createDirectories(modsDir);
        
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(modsZip))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".jar")) {
                    // Prendi solo il nome del file, ignora eventuali sottocartelle
                    String fileName = Paths.get(entry.getName()).getFileName().toString();
                    Path modFile = modsDir.resolve(fileName);
                    
                    // Crea le directory padre se necessario
                    Files.createDirectories(modFile.getParent());
                    
                    // Copia il file
                    try (OutputStream out = Files.newOutputStream(modFile)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    private Path downloadFile(String urlString, Path destination) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        long fileSize = connection.getContentLengthLong();
        
        try (InputStream in = new BufferedInputStream(connection.getInputStream());
             OutputStream out = new BufferedOutputStream(Files.newOutputStream(destination))) {
            
            byte[] buffer = new byte[8192];
            long downloaded = 0;
            int count;
            
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
                downloaded += count;
                int progress = (int) ((downloaded * 100) / fileSize);
                SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
            }
        }
        
        return destination;
    }

    private void selectModpack() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".zip");
            }
            public String getDescription() {
                return "File ZIP (*.zip)";
            }
        });

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            try {
                // Verifica l'integrità del file
                String checksum = SecurityUtils.calculateChecksum(selectedFile);
                statusLabel.setText("Modpack selezionato: " + selectedFile.getName() + " (Checksum: " + checksum.substring(0, 8) + "...)");
                installButton.setEnabled(true);
            } catch (Exception e) {
                statusLabel.setText("Errore nella verifica del file: " + e.getMessage());
                installButton.setEnabled(false);
            }
        }
    }

    private void installModpack() {
        installButton.setEnabled(false);
        selectButton.setEnabled(false);

        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Verifica la firma del file prima dell'installazione
                if (!verifyFile(selectedFile)) {
                    throw new SecurityException("Il file potrebbe essere stato modificato");
                }

                // Backup della cartella mods esistente
                backupModsFolder();
                
                // Verifica il contenuto del ZIP
                if (!verifyZipContents(selectedFile.toPath())) {
                    throw new SecurityException("Il contenuto del modpack non è sicuro");
                }
                
                // Estrazione del modpack
                extractModpack(selectedFile.toPath(), MINECRAFT_PATH);
                
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Controlla eventuali eccezioni
                    statusLabel.setText("Installazione completata con successo!");
                } catch (Exception e) {
                    statusLabel.setText("Errore: " + e.getMessage());
                }
                installButton.setEnabled(true);
                selectButton.setEnabled(true);
                progressBar.setValue(0);
            }
        };

        worker.execute();
    }

    private void backupModsFolder() throws IOException {
        Path modsPath = Paths.get(MINECRAFT_PATH, "mods");
        if (Files.exists(modsPath)) {
            Path backupDir = getBackupDirectory();
            Files.createDirectories(backupDir);
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path backupPath = backupDir.resolve("mods_backup_" + timestamp);
            Files.move(modsPath, backupPath);
        }
    }

    private void extractModpack(Path zipFile, String destinationPath) throws IOException {
        long totalSize = Files.size(zipFile);
        long currentSize = 0;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.toFile()))) {
            ZipEntry entry;
            byte[] buffer = new byte[4096];

            while ((entry = zis.getNextEntry()) != null) {
                Path path = Paths.get(destinationPath, entry.getName());
                
                if (entry.isDirectory()) {
                    Files.createDirectories(path);
                } else {
                    Files.createDirectories(path.getParent());
                    try (OutputStream os = Files.newOutputStream(path)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            os.write(buffer, 0, len);
                            currentSize += len;
                            int progress = (int) ((currentSize * 100) / totalSize);
                            SwingUtilities.invokeLater(() -> {
                                progressBar.setValue(progress);
                                statusLabel.setText("Estrazione: " + progress + "%");
                            });
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    private boolean verifyFile(File file) {
        try {
            // Verifica base del file
            if (file.length() > 1024 * 1024 * 500) { // Max 500MB
                return false;
            }

            // Verifica estensione
            if (!file.getName().toLowerCase().endsWith(".zip")) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean verifyZipContents(Path zipFile) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.toFile()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName().toLowerCase();
                
                // Blocca file potenzialmente pericolosi
                if (name.endsWith(".exe") || name.endsWith(".dll") || 
                    name.endsWith(".bat") || name.endsWith(".cmd") ||
                    name.endsWith(".sh") || name.contains("../")) {
                    return false;
                }

                // Verifica percorsi validi
                if (!isValidPath(name)) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isValidPath(String path) {
        // Verifica che il percorso sia sicuro
        return !path.contains("..") && 
               !path.startsWith("/") && 
               !path.startsWith("\\") &&
               path.matches("^[a-zA-Z0-9/\\\\._-]+$");
    }

    private void loadGavaIcon() {
        try {
            // Carica l'icona 32x32 come predefinita
            InputStream iconStream = getClass().getResourceAsStream(ICON_32);
            if (iconStream == null) {
                // Se non trova l'icona 32x32, prova con quella 16x16
                iconStream = getClass().getResourceAsStream(ICON_16);
            }
            
            if (iconStream != null) {
                gavaIcon = ImageIO.read(iconStream);
            } else {
                // Se non trova nessuna icona, crea un'icona di fallback
                gavaIcon = createFallbackIcon();
            }
        } catch (Exception e) {
            System.out.println("Impossibile caricare l'icona: " + e.getMessage());
            gavaIcon = createFallbackIcon();
        }
    }

    private Image createFallbackIcon() {
        // Crea un'icona semplice come fallback
        BufferedImage fallbackImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = fallbackImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Disegna un cerchio con il colore arancione
        g2d.setColor(ORANGE_MAIN);
        g2d.fillOval(4, 4, 56, 56);
        
        // Aggiungi una "G" al centro
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString("G", 
            32 - fm.stringWidth("G")/2, 
            32 + fm.getHeight()/3);
        
        g2d.dispose();
        return fallbackImage;
    }

    private void setupAnimations() {
        pulseTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonAlpha = 0.8f + (float)(Math.sin(System.currentTimeMillis() / 1000.0) + 1) * 0.1f;
                if (installButton != null) {
                    installButton.repaint();
                }
            }
        });
        pulseTimer.start();
    }

    private void createFileAssociation() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            // Registra l'app come handler per i file .gavapack su Mac
            try {
                String[] script = new String[] {
                    "defaults write com.apple.LaunchServices LSHandlers -array-add '{" +
                    "LSHandlerContentType = \"org.gavatech.modpack\";" +
                    "LSHandlerRoleAll = \"" + getClass().getName() + "\";" +
                    "}'"}; 
                Runtime.getRuntime().exec(String.join(" ", script));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Path getBackupDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            return Paths.get(System.getProperty("user.home"), 
                           "Library/Application Support/GavaCore/backups");
        } else {
            return Paths.get(MINECRAFT_PATH, "backups");
        }
    }

    private void updateVersionJson(Path jsonPath) throws IOException {
        // Usa Files.readAllBytes e crea una nuova String per compatibilità con Java 8
        String jsonContent = new String(Files.readAllBytes(jsonPath));
        
        // Aggiorna l'ID e il nome del jar
        jsonContent = jsonContent
            .replace("\"id\": \"1.20.1-forge-47.2.0\"", "\"id\": \"GavaCore\"")
            .replace("1.20.1-forge-47.2.0.jar", "GavaCore.jar");
        
        // Usa Files.write per compatibilità con Java 8
        Files.write(jsonPath, jsonContent.getBytes());
    }

    private String getJavaPath() throws IOException {
        String javaHome = System.getProperty("java.home");
        String os = System.getProperty("os.name").toLowerCase();
        Path javaPath;
        
        if (os.contains("win")) {
            javaPath = Paths.get(javaHome, "bin", "java.exe");
        } else {
            javaPath = Paths.get(javaHome, "bin", "java");
        }
        
        if (!Files.exists(javaPath)) {
            throw new IOException("Java non trovato in: " + javaPath);
        }
        
        return javaPath.toString();
    }

    // Aggiungi questo metodo per il debug
    private void logSystemInfo() {
        System.out.println("=== System Info ===");
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Java Home: " + System.getProperty("java.home"));
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        System.out.println("Minecraft Path: " + MINECRAFT_PATH);
        System.out.println("=================");
    }

    private JPanel createLauncherSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(DARK_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titolo
        JLabel titleLabel = new JLabel("Seleziona il tuo launcher 🎮", JLabel.CENTER);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Pannello per i launcher
        JPanel launcherPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        launcherPanel.setBackground(DARK_PANEL);

        // Crea prima entrambi i pulsanti
        JButton tlauncherBtn = createLauncherButton("TLauncher", TLAUNCHER_ICON);
        JButton prismBtn = createLauncherButton("Prism Launcher", PRISM_ICON);
        
        // TLauncher
        tlauncherBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectLauncher("tlauncher", tlauncherBtn, prismBtn);
            }
        });

        // Prism Launcher con overlay
        JPanel prismOverlayPanel = new JPanel();
        prismOverlayPanel.setLayout(new OverlayLayout(prismOverlayPanel));
        prismOverlayPanel.setBackground(DARK_PANEL);
        
        prismBtn.setEnabled(false);
        
        // Pannello overlay con X e "Prossimamente"
        JPanel overlayContent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sfondo semi-trasparente
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // X grande
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(3));
                int padding = 40;
                g2d.drawLine(padding, padding, getWidth()-padding, getHeight()-padding);
                g2d.drawLine(getWidth()-padding, padding, padding, getHeight()-padding);
                
                // Testo "Prossimamente"
                g2d.setFont(customFont.deriveFont(Font.BOLD, 20));
                String text = "Prossimamente";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2d.drawString(text, 
                    (getWidth() - textWidth) / 2,
                    (getHeight() + textHeight) / 2);
            }
        };
        overlayContent.setOpaque(false);
        
        prismOverlayPanel.add(overlayContent);
        prismOverlayPanel.add(prismBtn);

        launcherPanel.add(tlauncherBtn);
        launcherPanel.add(prismOverlayPanel);

        // Pannello info percorso
        JPanel pathPanel = new JPanel(new BorderLayout(5, 5));
        pathPanel.setBackground(DARK_PANEL);
        
        pathLabel = new JLabel("Seleziona un launcher per vedere il percorso di installazione");
        pathLabel.setForeground(TEXT_COLOR);
        pathLabel.setHorizontalAlignment(JLabel.CENTER);
        pathPanel.add(pathLabel, BorderLayout.CENTER);

        // Pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(DARK_PANEL);

        JButton backButton = createStyledButton("⮜ Indietro");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "MAIN");
            }
        });

        JButton continueButton = createStyledButton("Continua ⮞");
        continueButton.setEnabled(false); // Disabilitato finché non viene selezionato un launcher
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedLauncher != null) {
                    cardLayout.show(contentPanel, "VERSION");
                }
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);

        // Layout principale
        JPanel centerPanel = new JPanel(new BorderLayout(10, 20));
        centerPanel.setBackground(DARK_PANEL);
        centerPanel.add(launcherPanel, BorderLayout.CENTER);
        centerPanel.add(pathPanel, BorderLayout.SOUTH);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JButton createLauncherButton(String text, String iconPath) {
        JButton button = new JButton(text);
        try {
            InputStream is = getClass().getResourceAsStream(iconPath);
            if (is != null) {
                Image img = ImageIO.read(is);
                Image scaledImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImg));
            }
        } catch (Exception e) {
            System.out.println("Impossibile caricare l'icona del launcher: " + e.getMessage());
        }
        
        button.setBackground(DARK_BG);
        button.setForeground(TEXT_COLOR);
        button.setBorder(BorderFactory.createLineBorder(ORANGE_MAIN, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(DARK_BG);
            }
        });
        
        return button;
    }

    // Crea un nuovo pannello per la selezione della versione
    private JPanel createVersionSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(DARK_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titolo
        JLabel titleLabel = new JLabel("Versione 📦", JLabel.CENTER);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Pannello centrale con messaggio
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(DARK_PANEL);
        
        JLabel infoLabel = new JLabel(
            "<html><center>" +
            "🚧 Selezione versione in sviluppo 🚧<br><br>" +
            "Al momento è disponibile solo la versione 1.20.1<br>" +
            "Altre versioni saranno disponibili prossimamente</center></html>",
            JLabel.CENTER);

        infoLabel.setForeground(TEXT_COLOR);
        infoLabel.setFont(infoLabel.getFont().deriveFont(14f));
        centerPanel.add(infoLabel, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(DARK_PANEL);

        JButton backButton = createStyledButton("⮜ Indietro");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "MAIN");
            }
        });

        JButton installButton = createStyledButton("Installa 1.20.1 ⮞");
        installButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startInstallation("1.20.1");
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(installButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Modifica il metodo installGavaCore per supportare l'annullamento
    private void startInstallation(String version) {
        if (selectedLauncher == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleziona prima un launcher!", 
                "Errore", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mostra la pagina di download
        cardLayout.show(contentPanel, "DOWNLOAD");
        progressBar.setValue(0);
        statusLabel.setText("Preparazione download...");

        // Avvia il download
        currentInstallation = new DownloadWorker(version);
        currentInstallation.execute();
    }

    // Aggiungi questa classe interna
    private class DownloadWorker extends SwingWorker<Void, Integer> {
        private String version;
        private volatile boolean cancelled = false;
        
        public DownloadWorker(String version) {
            this.version = version;
        }
        
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
        
        private boolean isInstallationCancelled() {
            return cancelled;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            try {
                // Crea la cartella di destinazione
                Path destinationPath;
                if (selectedLauncher.equals("tlauncher")) {
                    destinationPath = Paths.get(MINECRAFT_PATH, "versions", "GavaCore");
                } else {
                    destinationPath = Paths.get(System.getProperty("user.home"), ".prism", "instances", "GavaCore");
                }
                currentInstallPath = destinationPath;
                
                // Scarica e estrai il modpack
                downloadAndExtract(GAVACORE_ZIP_URL, destinationPath);
                
                return null;
            } catch (Exception e) {
                throw new Exception("Errore durante l'installazione: " + e.getMessage());
            }
        }
        
        @Override
        protected void process(List<Integer> chunks) {
            if (!chunks.isEmpty()) {
                int progress = chunks.get(chunks.size() - 1);
                progressBar.setValue(progress);
                progressBar.setString(progress + "%");
            }
        }
        
        @Override
        protected void done() {
            try {
                get();
                statusLabel.setText("Installazione completata con successo! ✅");
                progressBar.setValue(100);
                progressBar.setString("100%");
                
                // Mostra un pulsante per tornare alla home dopo 2 secondi
                Timer timer = new Timer(2000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cardLayout.show(contentPanel, "MAIN");
                        ((Timer)e.getSource()).stop();
                    }
                });
                timer.setRepeats(false);
                timer.start();
                
            } catch (Exception e) {
                if (e.getCause() instanceof InterruptedException) {
                    statusLabel.setText("Installazione annullata ❌");
                } else {
                    statusLabel.setText("Errore: " + e.getMessage() + " ❌");
                }
            }
        }

        private void downloadAndExtract(String url, Path destinationPath) throws IOException {
            Path tempDir = Files.createTempDirectory("gavacore_temp");
            Path tempFile = tempDir.resolve("download.zip");
            
            try {
                // Download del file
                URL downloadUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
                int fileSize = connection.getContentLength();
                
                try (InputStream in = connection.getInputStream();
                     FileOutputStream out = new FileOutputStream(tempFile.toFile())) {
                    
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalBytesRead = 0;
                    
                    while ((bytesRead = in.read(buffer)) != -1) {
                        if (isInstallationCancelled()) {
                            throw new InterruptedException("Installazione annullata dall'utente");
                        }
                        
                        out.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                        
                        // Aggiorna il progresso
                        int progress = (int)((totalBytesRead * 50) / fileSize);
                        publish(progress);
                    }
                }
                
                // Estrazione del file ZIP
                Files.createDirectories(destinationPath);
                
                try (ZipInputStream zis = new ZipInputStream(new FileInputStream(tempFile.toFile()))) {
                    ZipEntry entry;
                    long totalSize = 0;
                    long extractedSize = 0;
                    
                    // Prima calcola la dimensione totale
                    try (ZipFile zipFile = new ZipFile(tempFile.toFile())) {
                        Enumeration<? extends ZipEntry> entries = zipFile.entries();
                        while (entries.hasMoreElements()) {
                            totalSize += entries.nextElement().getSize();
                        }
                    }
                    
                    while ((entry = zis.getNextEntry()) != null) {
                        if (isInstallationCancelled()) {
                            throw new InterruptedException("Installazione annullata dall'utente");
                        }
                        
                        Path filePath = destinationPath.resolve(entry.getName());
                        
                        if (entry.isDirectory()) {
                            Files.createDirectories(filePath);
                        } else {
                            Files.createDirectories(filePath.getParent());
                            Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);
                            extractedSize += entry.getSize();
                        }
                        
                        // Aggiorna il progresso dell'estrazione (50-100%)
                        int progress = 50 + (int)((extractedSize * 50) / totalSize);
                        publish(progress);
                        
                        zis.closeEntry();
                    }
                }
                
            } catch (Exception e) {
                // In caso di errore o annullamento, pulisci i file temporanei
                try {
                    Files.deleteIfExists(tempFile);
                    Files.deleteIfExists(tempDir);
                } catch (IOException ignored) {}
                
                if (e instanceof InterruptedException) {
                    try {
                        Files.deleteIfExists(tempFile);
                        Files.deleteIfExists(tempDir);
                    } catch (IOException ignored) {}
                }
                throw new IOException("Errore durante il download/estrazione: " + e.getMessage(), e);
            }
        }
    }

    // Aggiungi il metodo per annullare l'installazione
    private void cancelInstallation() {
        if (currentInstallation != null && !currentInstallation.isDone()) {
            ((DownloadWorker)currentInstallation).setCancelled(true);
            
            // Rimuovi i file creati
            if (currentInstallPath != null) {
                try {
                    Files.walk(currentInstallPath)
                         .sorted(Comparator.reverseOrder())
                         .forEach(path -> {
                             try {
                                 Files.delete(path);
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                         });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            // Torna alla pagina principale
            cardLayout.show(contentPanel, "MAIN");
            // Resetta lo stato
            statusLabel.setText("Installazione annullata ❌");
            progressBar.setValue(0);
            progressBar.setString("0%");
        }
    }

    // Aggiungi questo metodo per gestire la selezione del launcher
    private void selectLauncher(String launcher, JButton selectedBtn, JButton otherBtn) {
        selectedLauncher = launcher;
        
        // Aggiorna lo stile dei pulsanti
        selectedBtn.setBorder(BorderFactory.createLineBorder(ORANGE_MAIN, 3));
        otherBtn.setBorder(BorderFactory.createLineBorder(ORANGE_MAIN, 1));
        
        // Aggiorna il percorso mostrato
        String path;
        if (launcher.equals("tlauncher")) {
            path = Paths.get(MINECRAFT_PATH, "versions", "GavaCore").toString();
        } else {
            path = Paths.get(System.getProperty("user.home"), ".prism", "instances", "GavaCore").toString();
        }
        pathLabel.setText("<html>Percorso di installazione:<br>" + path + "</html>");
        
        // Abilita il pulsante continua
        Component[] components = ((JPanel)selectedBtn.getParent().getParent()).getParent().getComponents();
        for (Component c : components) {
            if (c instanceof JPanel) {
                for (Component btn : ((JPanel)c).getComponents()) {
                    if (btn instanceof JButton && ((JButton)btn).getText().contains("Continua")) {
                        btn.setEnabled(true);
                    }
                }
            }
        }
    }

    // Aggiungi questo metodo per creare la pagina di download
    private JPanel createDownloadPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(DARK_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titolo
        JLabel titleLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "Download in corso... 📥" +
            "</div></html>",
            JLabel.CENTER);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Pannello centrale con barra di progresso e stato
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(DARK_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        // Barra di progresso
        progressBar.setPreferredSize(new Dimension(400, 25));
        progressBar.setStringPainted(true);
        progressBar.setFont(progressBar.getFont().deriveFont(Font.BOLD));
        centerPanel.add(progressBar, gbc);

        // Label di stato
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(14f));
        centerPanel.add(statusLabel, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Pulsante annulla
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(DARK_PANEL);
        cancelButton = createStyledButton("❌ Annulla");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelInstallation();
            }
        });
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadCustomFont() {
        try {
            InputStream is = getClass().getResourceAsStream(CUSTOM_FONT);
            customFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (Exception e) {
            System.out.println("Impossibile caricare il font personalizzato: " + e.getMessage());
            // Usa un font di fallback
            customFont = new Font("Arial", Font.PLAIN, 12);
        }
    }

    private void applyCustomFont(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel) {
                c.setFont(customFont.deriveFont(c.getFont().getSize2D()));
            } else if (c instanceof JButton) {
                c.setFont(customFont.deriveFont(16f));
            } else if (c instanceof Container) {
                applyCustomFont((Container) c);
            }
        }
    }

    private void setupParticles() {
        // Inizializza le particelle
        for (int i = 0; i < NUM_PARTICLES; i++) {
            particles.add(new Particle());
        }
        
        // Crea il timer per l'animazione
        particleTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Particle particle : particles) {
                    particle.update();
                }
                repaint();
            }
        });
        particleTimer.start();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ModpackInstaller installer = new ModpackInstaller();
                    installer.setVisible(true);
                }
            });
        } catch (Exception e) {
            String errorMessage = "Si è verificato un errore durante l'avvio:\n" + 
                                 e.getMessage() + "\n\n" +
                                 "Dettagli tecnici:\n" + 
                                 e.toString();
            
            JOptionPane.showMessageDialog(null,
                errorMessage,
                "Errore",
                JOptionPane.ERROR_MESSAGE);
            
            e.printStackTrace();
        }
    }
} 