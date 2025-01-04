import java.io.*;
import java.net.*;
import java.nio.file.*;
import javax.swing.*;
import java.util.Properties;
import org.json.JSONObject;

public class UpdateChecker {
    private static final String VERSION_CHECK_URL = "https://raw.githubusercontent.com/tuorepository/gavacore/main/version.json";
    private static final String CURRENT_VERSION = "1.0.0";
    private static final String APP_NAME = "GavaCore Installer";
    
    public static void checkForUpdates(JFrame parent) {
        try {
            // Leggi la versione più recente dal server
            JSONObject versionInfo = fetchVersionInfo();
            String latestVersion = versionInfo.getString("version");
            String downloadUrl = versionInfo.getString("download_url");
            String changelog = versionInfo.getString("changelog");
            
            if (isNewerVersion(latestVersion, CURRENT_VERSION)) {
                showUpdateDialog(parent, latestVersion, downloadUrl, changelog);
            }
        } catch (Exception e) {
            System.out.println("Errore nel controllo aggiornamenti: " + e.getMessage());
        }
    }
    
    private static JSONObject fetchVersionInfo() throws Exception {
        URL url = new URL(VERSION_CHECK_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            return new JSONObject(response.toString());
        }
    }
    
    private static boolean isNewerVersion(String latest, String current) {
        String[] latestParts = latest.split("\\.");
        String[] currentParts = current.split("\\.");
        
        for (int i = 0; i < Math.min(latestParts.length, currentParts.length); i++) {
            int latestPart = Integer.parseInt(latestParts[i]);
            int currentPart = Integer.parseInt(currentParts[i]);
            
            if (latestPart > currentPart) {
                return true;
            } else if (latestPart < currentPart) {
                return false;
            }
        }
        
        return latestParts.length > currentParts.length;
    }
    
    private static void showUpdateDialog(JFrame parent, String newVersion, String downloadUrl, String changelog) {
        String message = String.format(
            "È disponibile una nuova versione di %s!\n\n" +
            "Versione attuale: %s\n" +
            "Nuova versione: %s\n\n" +
            "Novità:\n%s\n\n" +
            "Vuoi scaricare l'aggiornamento?",
            APP_NAME, CURRENT_VERSION, newVersion, changelog
        );
        
        int result = JOptionPane.showConfirmDialog(
            parent,
            message,
            "Aggiornamento Disponibile",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            downloadAndInstallUpdate(parent, downloadUrl, newVersion);
        }
    }
    
    private static void downloadAndInstallUpdate(JFrame parent, String downloadUrl, String newVersion) {
        try {
            // Crea una directory temporanea per il download
            Path tempDir = Files.createTempDirectory("gavacore_update");
            Path updateFile = tempDir.resolve("GavaCoreInstaller_" + newVersion + ".jar");
            
            // Mostra una finestra di progresso
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            
            JDialog progressDialog = new JDialog(parent, "Download Aggiornamento", true);
            progressDialog.setLayout(new BorderLayout(10, 10));
            progressDialog.add(new JLabel("Download in corso..."), BorderLayout.NORTH);
            progressDialog.add(progressBar, BorderLayout.CENTER);
            progressDialog.setSize(300, 100);
            progressDialog.setLocationRelativeTo(parent);
            
            // Avvia il download in background
            SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
                @Override
                protected Void doInBackground() throws Exception {
                    URL url = new URL(downloadUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    int fileSize = connection.getContentLength();
                    
                    try (InputStream in = new BufferedInputStream(connection.getInputStream());
                         OutputStream out = new BufferedOutputStream(Files.newOutputStream(updateFile))) {
                        
                        byte[] buffer = new byte[8192];
                        int count;
                        long downloaded = 0;
                        
                        while ((count = in.read(buffer)) != -1) {
                            out.write(buffer, 0, count);
                            downloaded += count;
                            
                            // Aggiorna la barra di progresso
                            int progress = (int) ((downloaded * 100) / fileSize);
                            publish(progress);
                        }
                    }
                    return null;
                }
                
                @Override
                protected void process(java.util.List<Integer> chunks) {
                    int progress = chunks.get(chunks.size() - 1);
                    progressBar.setValue(progress);
                }
                
                @Override
                protected void done() {
                    try {
                        get();
                        progressDialog.dispose();
                        
                        // Avvia il nuovo installer
                        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
                        ProcessBuilder pb = new ProcessBuilder(
                            javaBin,
                            "-jar",
                            updateFile.toString()
                        );
                        pb.start();
                        
                        // Chiudi l'applicazione corrente
                        System.exit(0);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(
                            parent,
                            "Errore durante l'aggiornamento: " + e.getMessage(),
                            "Errore",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            };
            
            worker.execute();
            progressDialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                parent,
                "Errore durante l'aggiornamento: " + e.getMessage(),
                "Errore",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
} 