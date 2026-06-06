package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import android.util.Log

@JsonClass(generateAdapter = true)
data class Strategy(
    @Json(name = "name") val name: String,
    @Json(name = "schritte") val schritte: List<String>,
    @Json(name = "dauer") val dauer: String
)

@JsonClass(generateAdapter = true)
data class MatrixNode(
    @Json(name = "phase") val phase: String,
    @Json(name = "diagnose") val diagnose: String,
    @Json(name = "trigger") val trigger: String,
    @Json(name = "strategien") val strategien: List<Strategy>,
    @Json(name = "vermeiden") val vermeiden: List<String>,
    @Json(name = "eskalation") val eskalation: String
)

object OfflineMatrixEngine {
    private const val TAG = "OfflineMatrixEngine"

    private val moshi = Moshi.Builder()
        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()

    // 1. Structured Decision Matrix as JSON String
    private val MATRIX_JSON = """
    [
      {
        "phase": "ROT",
        "diagnose": "PTBS",
        "trigger": "Dissoziation",
        "strategien": [
          {
            "name": "5-4-3-2-1 Grounding (Re-Reorientierung)",
            "schritte": [
              "Laut und beruhigend mit Namen des Patienten ansprechen.",
              "5 sichtbare Gegenstände im Raum laut aufzählen lassen.",
              "4 spürbare Berührungspunkte wahrnehmen lassen (z.B. feste Fußsohlen spüren).",
              "3 hörbare Schallquellen benennen.",
              "2 Riech- oder Duftreize anbieten (z.B. Riech-Stäbchen, Kälteakku).",
              "1 Geschmacksempfinden wecken."
            ],
            "dauer": "3 min"
          },
          {
            "name": "Atemanker Co-Regulation",
            "schritte": [
              "Lerneigenes Paced-Breathing demonstrieren (4s ein, 8s aus).",
              "Atembewegung synchronisieren ohne physische Nähe zu erzwingen."
            ],
            "dauer": "2 min"
          }
        ],
        "vermeiden": [
          "Unerwarteter Körperkontakt (Retraumatisierungspotenzial)",
          "Einkesseln oder Versperren des Fluchtwegs im Raum",
          "Kognitiv überfordernde moralische Diskurse"
        ],
        "eskalation": "Wenn nach 5 min Grounding keine kognitive Re-Orientierung einsetzt, sofort Oberarzt (OA) informieren und stationsinternes Notfall-Sicherheitsprotokoll aktivieren!"
      },
      {
        "phase": "ROT",
        "diagnose": "ADHS",
        "trigger": "Aggression",
        "strategien": [
          {
            "name": "Motorisches Ventil (Physische Entlastung)",
            "schritte": [
              "Erlaube kontrolliertes Gehen oder Begehen des Flurs im sicheren Radius.",
              "Schicke Zuschauer weg, um sekundäre Aufmerksamkeits-Verstärker zu minimieren.",
              "Biete stressabbauende Handlungsalternativen (Knetball, Kissen werfen)."
            ],
            "dauer": "4 min"
          },
          {
            "name": "Ein-Satz-Vorgabe (Syllable Limit)",
            "schritte": [
              "Verwende ultrakurze Sätze, maximal 5-7 Wörter.",
              "Präzise Verhaltensanweisung statt Fragen stellen: 'Mach bitte zwei Schritte zurück.'"
            ],
            "dauer": "1 min"
          }
        ],
        "vermeiden": [
          "Physische Blockaden oder Einkesseln im Flur",
          "Lange verbale Vorträge oder moralisierende Drohungen",
          "Überreizung durch mehrere zeitgleiche Ansprachen"
        ],
        "eskalation": "Bei Fremdgefährdung oder aggressivem Werfen schwerer Gegenstände sofort OA hinzuziehen, Teamschutz aktivieren."
      },
      {
        "phase": "ROT",
        "diagnose": "EIPS",
        "trigger": "Impulsivität",
        "strategien": [
          {
            "name": "Reizloser Schutzraum & DBT-Validation",
            "schritte": [
              "Felsenfestes, emotionsneutrales Auftreten der Betreuungsperson.",
              "Mikro-Validation: 'Ich höre, wie unerträglich schmerzhaft deine innere Spannung gerade ist...'",
              "Grenze unaufgeregt aber kompromisslos setzen: 'Ich bleibe hier, damit du sicher bist.'"
            ],
            "dauer": "5 min"
          },
          {
            "name": "Harte Reize (TIPP-Skills)",
            "schritte": [
              "Ammoniak-Riechstäbchen oder Eispack (Kältereiz) anbieten.",
              "Durchführung von schmerzfreien aber intensiven sensorischen Impulsen zur kognitiven Unterbrechung."
            ],
            "dauer": "2 min"
          }
        ],
        "vermeiden": [
          "Auf Splitting-Versuche eingehen (Diskussionen über Kollegen verbieten)",
          "Eigene emotionale Gegenübertragung (Wut, Panik spiegeln)",
          "Unerwartetes Nachgeben vereinbarter Stationsregeln"
        ],
        "eskalation": "Bei drohender oder aktiver unkontrollierbarer Selbstverletzung sofort OA informieren und physische Absicherung im Team einleiten."
      },
      {
        "phase": "ROT",
        "diagnose": "Autismus",
        "trigger": "Overload",
        "strategien": [
          {
            "name": "Aggressive Reizreduktion",
            "schritte": [
              "Sofort Raum abdunkeln, Vorhänge schließen, Geräuschquellen deaktivieren.",
              "Zuseher, Mitpatienten und redundante Teammitglieder evakuieren."
            ],
            "dauer": "5 min"
          },
          {
            "name": "Binäre Entscheidungsangebote",
            "schritte": [
              "Einfache binäre Wahlmöglichkeiten ohne sprachlichen Schnörkel stellen: 'Willst du sitzen oder stehen?'",
              "Metaphern, Witze und Ironie komplett unterlassen!"
            ],
            "dauer": "2 min"
          }
        ],
        "vermeiden": [
          "Direkten Augenkontakt erzwingen",
          "Stereotype Bewegungen (Körperwiegen, Wedeln) fälschlicherweise einschränken",
          "Berührungen jeglicher Art im sensorischen Overload"
        ],
        "eskalation": "Wenn der Overload in schwere fremd- oder autoaggressive Panik ausartet, OA kontaktieren und reizisolierten Krisenplatz vorbereiten."
      },
      {
        "phase": "ROT",
        "diagnose": "Psychose",
        "trigger": "Paranoia",
        "strategien": [
          {
            "name": "Realitätsanker auf Emotionsebene",
            "schritte": [
              "Wahninhalte weder bestreiten noch verifizieren (beides verfestigt Panik).",
              "Gefühl paranoider Erschütterung validieren: 'Ich sehe, dass Sie das gerade in schreckliche Angst versetzt. Hier sind Sie sicher.'",
              "Erkläre jeden kleinsten physischen Schritt, bevor Du handelst (Sicherheitsanker)."
            ],
            "dauer": "4 min"
          }
        ],
        "vermeiden": [
          "Wahn ausdiskutieren oder logisch widerlegen wollen",
          "Flüstern im Sichtfeld des Patienten (verstärkt paranoiden Bias)",
          "Plötzliche, unangekündigte Körperbewegungen im Raum"
        ],
        "eskalation": "Bei imperativem Stimmenhören mit akuter Handlungsaufforderung zur Selbst- oder Fremdverletzung: Sofort OA benachrichtigen."
      },
      {
        "phase": "ROT",
        "diagnose": "Allgemein",
        "trigger": "Allgemein",
        "strategien": [
          {
            "name": "Deeskalative Co-Regulation",
            "schritte": [
              "Feste 'Lead-Stimme' bestimmen. Nur ein Behandler führt das Gespräch.",
              "Alle anderen Teammitglieder sichern schweigend im Hintergrund ab.",
              "Sprechtempo drastisch drosseln und Tonlage tief halten."
            ],
            "dauer": "5 min"
          }
        ],
        "vermeiden": [
          "Durcheinanderreden (erzeugt Reizüberflutung)",
          "Drohgebärden oder Drohung mit Disziplinarmaßnahmen",
          "Hektische Flur-Bewegungen"
        ],
        "eskalation": "Bei Eskalation ohne Deeskalationserfolg nach 5 Minuten: stationsspezifischen Dienst- und Oberarzt verständigen."
      },
      {
        "phase": "GELB",
        "diagnose": "PTBS",
        "trigger": "Dissoziation",
        "strategien": [
          {
            "name": "Entschleunigte Ressourcen-Mobilisierung",
            "schritte": [
              "Dem Jugendlichen einen vertrauten Trost- oder Regulationsgegenstand reichen.",
              "Achtsame, leise Ansprache im Sitzen auf Augenhöhe (oder leicht darunter).",
              "Sichere Umgebung verbal bestätigen."
            ],
            "dauer": "5 min"
          }
        ],
        "vermeiden": [
          "Druck ausüben ('reiß dich zusammen')",
          "Hektisches Herumwedeln",
          "Körperliche Annäherung unter 1,5 Metern ohne Absprache"
        ],
        "eskalation": "Sollte der Zustand in eine tiefe, therapieresistente, stuporöse Dissoziation übergehen, Stationsarzt informieren."
      },
      {
        "phase": "GELB",
        "diagnose": "ADHS",
        "trigger": "Trotz",
        "strategien": [
          {
            "name": "Kontrollierte Selbstwirksamkeit (Choices)",
            "schritte": [
              "Gib konkrete, deeskalative Handlungsoptionen vor: 'Möchtest du das Medikament jetzt nehmen oder in 5 Minuten im Ruheraum?'",
              "Verlassen der Akut-Schnittstelle zur Einigung unter vier Augen."
            ],
            "dauer": "3 min"
          }
        ],
        "vermeiden": [
          "Machtkämpfe führen oder auf Wortgefechte eingehen",
          "Sanktionen androhen, die nicht unmittelbar konsistent umsetzbar sind"
        ],
        "eskalation": "Droht der Übergang in impulsive Sachbeschädigung (Phase ROT), Team als Backup formieren."
      },
      {
        "phase": "GELB",
        "diagnose": "EIPS",
        "trigger": "Impulsivität",
        "strategien": [
          {
            "name": "Spannungsregulation (Wahlmöglichkeiten)",
            "schritte": [
              "Fokus weg vom Regelstreit, hin zur inneren Anspannung lenken.",
              "Reizfreien Rückzugsort anbieten: 'Lass uns rübergehen, um die Anspannung abzubauen.'",
              "Nutzung des vereinbarten Krisenkoffers stimulieren."
            ],
            "dauer": "4 min"
          }
        ],
        "vermeiden": [
          "Diskussionen über Stationsordnungen im Flur",
          "Patienten emotional zurückweisen"
        ],
        "eskalation": "Frühzeitig Absprachen im Team dokumentieren, um Splitting zu vermeiden."
      },
      {
        "phase": "GELB",
        "diagnose": "Autismus",
        "trigger": "Overload",
        "strategien": [
          {
            "name": "Umsorgender Trigger-Stopp",
            "schritte": [
              "Finde die reizerzeugende Quelle (z.B. flackerndes Licht, laute Lüftung, klopfende Schritte) und schalte sie aus.",
              "Biete geräuschunterdrückende Kopfhörer (ANC) oder dämpfende Brille an."
            ],
            "dauer": "3 min"
          }
        ],
        "vermeiden": [
          "Metaphorische Beschreibungen verwenden ('komm mal runter')",
          "Verweigerung als bösen Willen missverstehen"
        ],
        "eskalation": "Achte auf motorisches Ersticken von Reizen. Rechtzeitig ruhigen Rückzugsort anweisen."
      },
      {
        "phase": "GELB",
        "diagnose": "Psychose",
        "trigger": "Paranoia",
        "strategien": [
          {
            "name": "Beruhigende Reizkontrolle",
            "schritte": [
              "Einen überschaubaren, hellen, aber reizarmen Raum aufsuchen.",
              "Sicherheit strukturiert vermitteln: 'Die Türen sind verschlossen. Wir passen auf Sie auf.'"
            ],
            "dauer": "5 min"
          }
        ],
        "vermeiden": [
          "Flüstern, Schreiben oder Getuschel im Beisein des Patienten",
          "Heimlich Gegenstände bewegen oder entfernen"
        ],
        "eskalation": "Sollten sich imperative Tendenzen abzeichnen, sofortige ärztliche Überprüfung anfordern."
      },
      {
        "phase": "GELB",
        "diagnose": "Allgemein",
        "trigger": "Allgemein",
        "strategien": [
          {
            "name": "Frühzeitiges Deeskalatives Angebot",
            "schritte": [
              "Puls der beteiligten Fachkräfte prüfen und beruhigen.",
              "Einbeziehung von bekannten, positiv besetzten Co-Regulatoren."
            ],
            "dauer": "4 min"
          }
        ],
        "vermeiden": [
          "Ignorieren erster Warnsignale",
          "Überreizung oder Erhöhung der Stimmlautstärke"
        ],
        "eskalation": "Bei Anstieg der Erregung Übergang in Phase ROT vorbereiten."
      },
      {
        "phase": "WEISS_GRUEN",
        "diagnose": "Allgemein",
        "trigger": "Allgemein",
        "strategien": [
          {
            "name": "Beziehungsarbeit & Krisenplanung (Cope Ahead)",
            "schritte": [
              "Individuelle Frühwarnzeichen im ruhigen Zustand besprechen und eintragen.",
              "Regelmäßige Beziehungsangebote (Beteiligung an Stationsaktivitäten, Spiele, Gespräche) pflegen.",
              "Einen individuellen Notfallkoffer (Skills, Telefonnummern, Düfte) packen."
            ],
            "dauer": "10 min"
          }
        ],
        "vermeiden": [
          "Beziehungsabbruch bei stabiler Phase",
          "Vorbehalte oder frühere Konflikte nachtragen"
        ],
        "eskalation": "Normaler Stationsdienst. Keine Eskalation indiziert."
      },
      {
        "phase": "BLAU",
        "diagnose": "Allgemein",
        "trigger": "Allgemein",
        "strategien": [
          {
            "name": "Kortisollatenz & Bindungsreparatur",
            "schritte": [
              "Nach sichtbarer Beruhigung mindestens 20-30 Minuten Erholungszeit gewähren (Kortisolspiegel sinkt langsam!).",
              "Unter vier Augen das Geschehene wertfrei besprechen (Schutz der Würde, Vermeidung von Scham).",
              "Beziehung bewusst flicken: 'Ich bin froh, dass wir die Situation jetzt sicher überstanden haben.'",
              "Krisenplan anpassen oder um neu entdeckte Trigger ergänzen."
            ],
            "dauer": "15 min"
          }
        ],
        "vermeiden": [
          "Sofortige Standpauken oder pädagogische Konsequenzen nach der Krise",
          "Schamgefühle triggern oder Vorwürfe wegen des Fehlverhaltens machen",
          "Mitpatienten im Beisein des Betroffenen befragen"
        ],
        "eskalation": "Normalisierung der Beziehungsdynamik. Bei anhaltendem emotionalen Stupor Nachuntersuchung durch Arzt."
      }
    ]
    """.trimIndent()

    private val nodes: List<MatrixNode> by lazy {
        try {
            val type = Types.newParameterizedType(List::class.java, MatrixNode::class.java)
            val adapter = moshi.adapter<List<MatrixNode>>(type)
            adapter.fromJson(MATRIX_JSON) ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing offline matrix JSON", e)
            emptyList()
        }
    }

    /**
     * Filters the native decision matrix for the specific phase, diagnosis, and trigger/situation,
     * returning a beautifully structured Markdown clinical advice document.
     */
    fun matchAdvice(
        phase: String,
        diagnosisId: String,
        situationText: String
    ): String {
        val normPhase = phase.uppercase().trim()
        val normDiagnosis = diagnosisId.uppercase().trim()
        val normSituation = situationText.lowercase().trim()

        // Match diagnosis key (mapping custom subcategories to main database categories if necessary)
        val targetCategory = when {
            normDiagnosis.contains("PTBS") || normDiagnosis.contains("TRAUMA") || normDiagnosis.contains("6B40") -> "PTBS"
            normDiagnosis.contains("ADHS") || normDiagnosis.contains("HYPER") || normDiagnosis.contains("6A05") -> "ADHS"
            normDiagnosis.contains("EIPS") || normDiagnosis.contains("BORDER") || normDiagnosis.contains("6D11") -> "EIPS"
            normDiagnosis.contains("AUTISM") || normDiagnosis.contains("ASS") || normDiagnosis.contains("6A02") -> "Autismus"
            normDiagnosis.contains("PSYCHOSE") || normDiagnosis.contains("SCHIZO") || normDiagnosis.contains("6A20") -> "Psychose"
            else -> "Allgemein"
        }

        // Try exact combination: phase + targetCategory
        var matched = nodes.firstOrNull { 
            it.phase == normPhase && it.diagnose.uppercase() == targetCategory.uppercase()
        }

        // Fallback 1: If no exact diagnosis match for that phase, fall back to "Allgemein" for that same phase
        if (matched == null) {
            matched = nodes.firstOrNull { 
                it.phase == normPhase && it.diagnose.uppercase() == "ALLGEMEIN"
            }
        }

        // Fallback 2: General fallback across the entire matrix (safety first)
        if (matched == null) {
            matched = nodes.firstOrNull { it.phase == "ROT" && it.diagnose.uppercase() == "ALLGEMEIN" }
        }

        val node = matched ?: return "Fehler: Keine Deeskalationsmatrix für diese Eingaben lokal im System hinterlegt."

        // Check for crisis trigger or warning words in current situation to append warnings
        val hasAggressiveTrigger = normSituation.contains("messer") || 
                                   normSituation.contains("scherbe") || 
                                   normSituation.contains("angriff") || 
                                   normSituation.contains("waffe") ||
                                   normSituation.contains("schlagen") || 
                                   normSituation.contains("erwürgen") ||
                                   normSituation.contains("töten")

        val triggerWarning = if (hasAggressiveTrigger) {
            "⚠️ **[NOTFALL-ALARM] WARNUNG:** Akute Bedrohung / Fremdgefährdung im Raum erkannt! Unverzüglich Eigenschutz priorisieren, lautstark Verstärkung anfordern und medizinischen Dienstarzt / Oberarzt hinzuziehen!\n\n"
        } else ""

        // Process markdown formulation
        val sb = java.lang.StringBuilder()
        sb.append(triggerWarning)
        sb.append("### ℹ️ KLINISCHER OFFLINE-SCHEIN (Deeskalations-Entscheidungsmatrix)\n\n")
        sb.append("**Deeskalationsstufe:** `${node.phase}` (KJP-Zustand: ${
            when (node.phase) {
                "WEISS_GRUEN" -> "Grün - Prävention & Allianz"
                "GELB" -> "Gelb - Prä-Krise & Frühwarnung"
                "ROT" -> "Rot - Akutkrise / Amygdala-Hijack"
                "BLAU" -> "Blau - Nachsorge & Kortisollatenz"
                else -> node.phase
            }
        })\n")
        sb.append("**Diagnostischer Fokus:** `${node.diagnose}` (Spezifische Methodik)\n\n")

        sb.append("#### ✅ EMPFOHLENE STATIONÄRE STRATEGIEN:\n")
        node.strategien.forEach { strat ->
            sb.append("• **${strat.name}** (Dauer ca. *${strat.dauer}*)\n")
            strat.schritte.forEachIndexed { idx, step ->
                sb.append("  ${idx + 1}. **$step**\n")
            }
            sb.append("\n")
        }

        sb.append("#### ❌ ABSOLUT ZU VERMEIDEN (Sicherheitsrisiken):\n")
        node.vermeiden.forEach { v ->
            sb.append("- **$v**\n")
        }
        sb.append("\n")

        sb.append("#### 🚨 SOFORTIGE ESKALATIONSEBENE:\n")
        sb.append("*${node.eskalation}*\n\n")
        
        sb.append("*(Hinweis: Bei fehlender Internetverbindung greift diese wissenschaftlich-pädagogische Notfall-Entscheidungsmatrix lokal direkt auf Ihrem Gerät.)*")

        return sb.toString()
    }
}
