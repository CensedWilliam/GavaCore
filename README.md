# 🎮 GavaCore - La Base per i Server Minecraft

![Version](https://img.shields.io/badge/version-0.5.5-blue.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.20.2-green.svg)
![Author](https://img.shields.io/badge/author-GavaTech-orange.svg)

GavaCore è un plugin completo per server Minecraft che offre un'ampia gamma di funzionalità essenziali e avanzate, progettato per migliorare l'esperienza di gioco con animazioni spettacolari, sistemi di gestione intuitivi e ottimizzazioni delle performance.

## ✨ Caratteristiche Principali

### 🎭 Sistema Animazioni
- **Animazione di Primo Accesso**: Spettacolare animazione cinematografica per i nuovi giocatori
- **Animazione Teletrasporto**: Effetti visivi stile GTA per i teletrasporti
- **Effetto Rotazione Luna**: Animazione della luna durante il sonno dei giocatori

### 🛠️ Strumenti di Gestione
- **TreeCutter**: Taglia interi alberi con un solo colpo mentre sei in shift
- **Sistema Soprannomi**: Personalizza il tuo nome nel gioco
- **Protezione Chest**: Blocca e condividi le tue casse con altri giocatori
- **GUI Menu Interattivo**: Interfaccia grafica intuitiva per accedere a tutte le funzionalità

### 🌦️ Controllo Tempo e Meteo
- Comandi in italiano: `/giorno`, `/notte`, `/alba`, `/tramonto`
- Gestione meteo: `/sereno`, `/pioggia`, `/temporale`
- Effetti sonori e particelle personalizzabili
- Sistema di notifiche per cambiamenti meteo

### 🍔 Items Personalizzati
- **Panino Gourmet**: +5 cuori, fame al massimo
- **Coca Cola**: Respirazione subacquea e visione notturna
- **Patatine Fritte**: Velocità di scavo aumentata
- **Sedia**: Siediti ovunque nel mondo

### 💬 Sistema Chat Avanzato
- Anti-spam con timeout automatico
- Filtro parolacce e CAPS lock
- Messaggi privati con `/msg` e `/r`
- Gestione chat globale

### 🚀 Performance e Ottimizzazioni
- **Cache Manager**: Sistema di cache intelligente
- **Backup Automatico**: Salvataggio automatico delle configurazioni
- **Operazioni Asincrone**: Nessun lag sul server principale

### 🔧 Modalità Sviluppatore
- Comandi debug avanzati
- Monitoraggio performance in tempo reale
- Block/Entity info
- NoClip e controllo velocità

## 📋 Requisiti

- **Minecraft Server**: Spigot/Paper 1.20.2+
- **Java**: 8 o superiore
- **RAM**: Minimo 2GB raccomandata
- **Plugin Opzionali Supportati**:
  - BackpackPlus
  - SkinsRestorer
  - Vault
  - PlaceholderAPI

## 📦 Installazione

1. Scarica l'ultima versione di GavaCore.jar
2. Inserisci il file nella cartella `plugins` del tuo server
3. Riavvia il server
4. Configura il plugin modificando `plugins/GavaCore/config.yml`

## 🔨 Compilazione da Sorgente

### Prerequisiti
- Java Development Kit (JDK) 8+
- Maven 3.6+
- Git

### Passaggi per la Compilazione

```bash
# Clona il repository
git clone https://github.com/GavaTech/GavaCore.git

# Entra nella directory del progetto
cd GavaCore

# Compila il plugin
mvn clean package

# Il file JAR compilato sarà in:
# gavacore/target/GavaCore.jar
```

## 📝 Comandi Principali

### Menu e Interfacce
- `/gavacore` - Apre il menu principale
- `/craft` - Apre il banco da lavoro (richiede uno zaino)

### Tempo e Meteo
- `/giorno [mondo]` - Imposta il giorno
- `/notte [mondo]` - Imposta la notte
- `/alba [mondo]` - Imposta l'alba
- `/tramonto [mondo]` - Imposta il tramonto
- `/sereno [mondo]` - Tempo sereno
- `/pioggia [mondo]` - Inizia la pioggia
- `/temporale [mondo]` - Scatena un temporale
- `/meteo [mondo]` - Info meteo

### Gestione Giocatori
- `/soprannome <nome>` - Imposta un soprannome
- `/sedia` - Siediti dove sei
- `/tp <giocatore>` - Richiedi teletrasporto
- `/tpqui <giocatore>` - Teletrasporta qui
- `/msg <giocatore> <messaggio>` - Messaggio privato
- `/r <messaggio>` - Rispondi all'ultimo messaggio

### Chest e Protezioni
- `/blocca` - Blocca una chest
- `/sblocca` - Sblocca una chest
- `/condividi <giocatore>` - Condividi una chest
- `/lista-condivisioni` - Vedi le condivisioni

### Modalità di Gioco
- `/gmc [giocatore]` - Modalità creativa
- `/gms [giocatore]` - Modalità sopravvivenza
- `/gmsp [giocatore]` - Modalità spettatore
- `/gma [giocatore]` - Modalità avventura

### Amministrazione
- `/developer-mode` - Attiva/disattiva modalità sviluppatore
- `/dev <comando>` - Comandi sviluppatore
- `/chat <on/off>` - Gestisci la chat globale
- `/puliscichat` - Pulisci la chat

## 🔐 Permessi

```yaml
# Permessi base
gavacore.gamemode - Cambiare modalità di gioco
gavacore.chat - Gestire la chat del server
gavacore.nickname - Usare i soprannomi

# Permessi tempo/meteo
gavacore.time.day - Impostare il giorno
gavacore.time.night - Impostare la notte
gavacore.time.dawn - Impostare l'alba
gavacore.time.sunset - Impostare il tramonto
gavacore.weather.clear - Tempo sereno
gavacore.weather.rain - Pioggia
gavacore.weather.thunder - Temporale

# Permessi sviluppatore
gavacore.developer - Accesso modalità sviluppatore
gavacore.animation - Gestire animazioni
gavacore.resolvebugs - Risolvere bug animazioni
```

## ⚙️ Configurazione

Il file `config.yml` offre centinaia di opzioni personalizzabili:

- **Colori e Prefissi**: Personalizza tutti i messaggi
- **Animazioni**: Abilita/disabilita e configura ogni animazione
- **Items Custom**: Modifica effetti e ricette
- **Performance**: Ottimizza cache e operazioni asincrone
- **Storage**: Configura backup automatici e salvataggi

## 📊 Statistiche e Privacy

GavaCore utilizza bStats per raccogliere statistiche anonime sull'utilizzo. Questo ci aiuta a migliorare il plugin. Puoi disabilitare le statistiche in `plugins/bStats/config.yml`.

## 🤝 Supporto e Contributi

- **Sito Web**: [www.gavatech.org](https://www.gavatech.org)
- **Segnalazione Bug**: Apri una issue su GitHub
- **Richieste Funzionalità**: Contattaci sul nostro sito

## 📜 Crediti

**Sviluppato da**: GavaTech  
**Versione**: 1.5.0  
**Compatibilità**: Minecraft 1.20.2+  

---

Made with ❤️ by **GavaTech** - www.gavatech.org!
