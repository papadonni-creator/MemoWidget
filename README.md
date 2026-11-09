# MemoWidget — Android 13

Widget nativo Android per appuntamenti con e senza data.

## Compilazione senza Android Studio
1. Crea un repository GitHub vuoto, preferibilmente privato.
2. Carica **tutti i file e le cartelle** di questo progetto nella radice del repository, compresa `.github/workflows/android.yml` (la cartella `.github` può essere nascosta).
3. Vai su **Actions → Compila MemoWidget APK → Run workflow**. In alternativa, il caricamento sul branch `main` avvia automaticamente la compilazione.
4. Apri l'esecuzione completata e scarica l'artifact `MemoWidget-Android13-APK`.
5. Estrai `app-debug.apk` dallo ZIP scaricato, trasferiscilo al telefono Android 13 e installalo, autorizzando l'installazione da quella fonte se richiesto.
6. Aggiungi il widget dalla schermata Home: pressione lunga su uno spazio vuoto → Widget → MemoWidget.

## Funzioni
- Aggiunta, modifica e cancellazione con conferma.
- Data facoltativa; senza data in cima, poi date decrescenti.
- Giorno e mese abbreviati, giorno della settimana sotto.
- Lista scorrevole; l'altezza effettiva del widget è regolabile dal launcher.
- Colori globali tramite valori esadecimali, colori individuali con palette rapida, caratteri 14 sp predefiniti.
- Salvataggio locale persistente con SharedPreferences.

## Limitazioni della versione iniziale
- I widget Android basati su RemoteViews non offrono un evento standard affidabile di pressione lunga sulle singole righe: toccare una riga apre il menu Modifica/Elimina. Nell'app principale funziona anche la pressione lunga.
- L'altezza delle righe è dinamica ma il testo nel widget è limitato a due righe, con ellissi se eccede.
- Il launcher controlla le dimensioni del widget; per mostrare circa cinque righe occorre ridimensionarlo.
- I colori personalizzati del widget vengono applicati come sfondi pieni; gli angoli arrotondati iniziali possono non essere conservati quando si cambia colore.
- L'APK di debug è destinato alle prove. Per distribuire aggiornamenti stabili servirà una firma release.

Il progetto non richiede account cloud né permessi Internet nell'app. Compatibilità minima Android 8, destinazione Android 13.
