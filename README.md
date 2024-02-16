# DHBW - Fortgeschrittene Systementwicklung I
##DatabaseSystemTechnology-master

# Übung Implementierung MitgliederDB

Die MitgliederDB setzt eine nicht segmentierte Blockzuordnung der Datensätze um. Die MitgliederDB kann in zwei Organisationsformen (unordered / sorted) gestartet werden. Die Implementierung finden Sie unter [diesem Link](https://github.com/sturc/dbfileorga).

## Aufgabe

Vervollständigen Sie die Implementierung der MitgliederDB. Erzeugen Sie hierzu zwei Versionen der MitgliederDB:

- Einmal mit einer Unordered File / HEAP File Organisation der Datensätze
- Einmal mit einer geordneten / Sorted File Organisation der Datensätze

### Unordered File (HEAP File)

Implementieren Sie folgende Funktionen:

- `read and findPos` (SearchTerm = MitgliederNr)
- `Insert` (Hans Meier)
- `modify` (ID95 Steffi Brahms wird zu ID 95 Steffi Bach)
- `delete` (ID 95 Steffi Bach)

In der Datei `StartMitgliederDB.java` finden Sie die entsprechenden Testfälle zur Überprüfung Ihrer Implementierung.

### Sorted File

Implementieren Sie folgende Funktionen:

- `read and findPos` (SearchTerm = MitgliederNr) Ohne binäre Suche!!!
- `Insert` (Hans Meier)
- `modify` (ID 95 Steffi Brahms wird zu ID 95 Steffi Bach)
- `delete` (ID 97 Theo Krapp)

In der Datei `StartMitgliederDBOrdered.java` finden Sie die entsprechenden Testfälle zur Überprüfung Ihrer Implementierung.

## Abgabe

Laden Sie den fertiggestellten vollständigen und lauffähigen Programmcode in Moodle als Zip-Datei bis zum angegebenen Abgabezeitpunkt hoch (inklusive kurzer Erläuterung, wie Ihr Programm zu starten ist).

## Bewertungskriterien

- Funktionalität
- Vollständigkeit
- Implementierungseleganz und Behandlung von Spezialfällen

## Mögliche Punktzahl
10 Punkte
