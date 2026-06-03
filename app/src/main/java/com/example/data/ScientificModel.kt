package com.example.data

data class PhaseDetails(
    val id: String,
    val name: String,
    val deName: String,
    val colorHex: String,
    val textColorHex: String,
    val subtitle: String,
    val summary: String,
    val neuroBasics: String,
    val keyInteractions: List<Pair<String, String>>, // list of pairs (Step, Action)
    val donts: List<String>,
    val additionalTips: String = ""
)

data class DiagnosisDetails(
    val id: String,
    val name: String,
    val dynamik: String,
    val absicherung: String,
    val klaerung: String,
    val aufloesung: String
)

data class NeuroArticle(
    val title: String,
    val content: String,
    val bulletPoints: List<Pair<String, String>> = emptyList()
)

data class CommunicationArticle(
    val title: String,
    val description: String,
    val details: List<Pair<String, String>> = emptyList(),
    val quotes: List<String> = emptyList()
)

object ScientificContent {

    val phases = listOf(
        PhaseDetails(
            id = "WEISS",
            name = "White",
            deName = "WEISS (Grundlage)",
            colorHex = "#F1F5F9",
            textColorHex = "#334155",
            subtitle = "Was vor jedem Trigger passiert",
            summary = "Die WEISS-Phase ist die Voraussetzung für alle anderen Phasen. Was hier geleistet wird, bestimmt die Häufigkeit und Intensität aller zukünftigen Krisen. Investitionen in dieser Phase reduzieren GELB, ROT und BLAU messbar.",
            neuroBasics = "Neurobiologische Grundlagen verstehen, professionelle Haltung entwickeln, Patienten im Detail kennenlernen (Krisenpläne, individuelle Trigger, Frühwarnzeichen, Ressourcen, Abwertungsvermeidung) und eine teamübergreifende fehlerfreundliche Kultur etablieren.",
            keyInteractions = listOf(
                "DBT PLEASE für das Team" to "Körperliche Erkrankungen ernst nehmen, regelmäßige Mahlzeiten und Pausen einhalten auch unter Stress, ausreichend Schlaf sichern (Schlafmangel erhöht Amygdala-Reaktivität um bis zu 60%), und körperliche Bewegung nach belastenden Schichten als physiologischen Kortisol-Abbau nutzen.",
                "DBT Cope Ahead" to "Wenn im Vorhinein bekannt ist, dass heute ein besonders schwieriges Gespräch oder ein belastender Termin ansteht - das Team mental vorbereiten. Wer mental vorbereitet ist, greift unter Druck auf bewusste Regulationsstrategien (Wise Mind) zurück, statt unwillkürlich zu reagieren."
            ),
            donts = listOf(
                "Kein willkürliches Reagieren ohne vorherige Absprache.",
                "Kein Ignorieren von eigenen Erschöpfungszeichen im Team."
            ),
            additionalTips = "Regelmäßiges Abgleichen der individuellen Patientenkrisenpläne sorgt für verlässliche Reaktionsmuster im gesamten multiprofessionellen Team."
        ),
        PhaseDetails(
            id = "GRUEN",
            name = "Green",
            deName = "GRÜN (Prävention)",
            colorHex = "#DCFCE7",
            textColorHex = "#166534",
            subtitle = "Sicherheit im Alltag verankern",
            summary = "Das Safewards-Modell zeigt: Strukturierte Interventionen in dieser Phase reduzieren Zwangsmaßnahmen um bis zu 20 %. Eine verlässliche Tagesstruktur, vorhersehbare Abläufe und transparente Entscheidungen reduzieren die Grundspannung.",
            neuroBasics = "Bindungstheoretischer Aspekt: Für traumatisch belastete Jugendliche ist Vorhersehbarkeit keine Komfortfunktion, sondern physiologische Sicherheit.",
            keyInteractions = listOf(
                "Tagesstruktur & Klarheit" to "Tagesabläufe, Visitenzeiten und Übergabeprozesse transparent gestalten.",
                "Krisenplan" to "Enthält individuelle Trigger, Frühwarnzeichen, hilfreiche Maßnahmen, was die Krise verschlimmert, Wunsch des Patienten für den Krisenaufenthalt und die vereinbarte Teamreaktion. Wird konsequent von allen Berufsgruppen umgesetzt.",
                "Bindungsverlässlichkeit" to "Jugendliche mit unsicheren Bindungsmustern testen die Verlässlichkeit des Teams durch wiederholtes Grenzetesten. Das ist Bindungsverhalten, kein bösartiger Widerstand. Das Team gewinnt Vertrauen durch wiederholte konsistente Reaktionen."
            ),
            donts = listOf(
                "Keine personalabhängigen Ausnahmen bei vereinbarten Regeln (untergräbt Sicherheitsgefühl).",
                "Keine unvorhersehbaren Planänderungen ohne transparente und zeitnahe Erklärung."
            )
        ),
        PhaseDetails(
            id = "GELB",
            name = "Yellow",
            deName = "GELB (Frühwarnung)",
            colorHex = "#FEF3C7",
            textColorHex = "#92400E",
            subtitle = "Der günstigste Interventionszeitpunkt",
            summary = "Der präfrontale Kortex ist noch teilweise aktiv. Eine wirksame Intervention in Phase GELB ist um ein Vielfaches effektiver und weniger belastend als eine Krisenintervention in Phase ROT.",
            neuroBasics = "Amygdala wird zunehmend aktiver, aber der rationale Verstand ist noch über Validation und Deeskalationstechniken erreichbar.",
            keyInteractions = listOf(
                "Schritt 1: Absicherung" to "Selbstkontrolle aktivieren (STOP-Skill) · Raum einschätzen · andere Patienten diskret herausführen (lassen) · seitlich versetzt aufstellen in 1,5-2m Abstand · Fluchtweg unbedingt frei halten.",
                "Schritt 2: Kommunikation" to "GFK Schritte 1-3 anwenden · Validation als unerschütterliches Fundament nutzen · Stimme senken · offene, nicht-bedrohliche Körperhaltung einnehmen.",
                "Scham-Wut-Spirale erkennen" to "Plötzlicher Rückzug, Vermeidung von Blickkontakt oder gereizte Reaktion nach einem sozialen Vorfall signalisiert starke Scham. Wut oder Provokation dient dann oft der unbewussten Abwehr dieses extrem schmerzhaften Gefühls."
            ),
            donts = listOf(
                "Auf keinen Fall diskutieren, erklären, belehren, rational überzeugen wollen oder Konsequenzen lautstark ankündigen.",
                "Keine körperliche Nähe aufzwingen oder Fluchtwege versperren."
            ),
            additionalTips = "Grenze GELB -> ROT: Sobald körperliche Gewalt gegen sich, andere oder herbeieilende Gegenstände auftritt oder eine schwere dissoziative Erstarrung einsetzt, tritt das Stationsprotokoll in Kraft."
        ),
        PhaseDetails(
            id = "ROT",
            name = "Red",
            deName = "ROT (Akutkrise)",
            colorHex = "#FEE2E2",
            textColorHex = "#991B1B",
            subtitle = "Sicherheit hat absoluten Vorrang",
            summary = "In Phase ROT dominiert der untere Stammhirnbereich (Kampf/Flucht/Erstarrung). Der Patient befindet sich im Amygdala Hijack - der denkende Kortex ist effektiv offline. Klärung ist physiologisch unmöglich.",
            neuroBasics = "Ausschließlich Sicherheit, Reizreduktion und physiologische Co-Regulation herstellen. Deeskalation läuft flankierend als Sicherheitssignal über die Spiegelneuronen des Personals.",
            keyInteractions = listOf(
                "Paced Breathing (DBT TIP)" to "Bewusst 4 Sek. einatmen, 8 Sek. ausatmen für das Team und zur Co-Regulation. Synchronisation senkt den Herzschlag.",
                "Grounding bei Dissoziation" to "Ruhige Orientierung: 'Du bist hier auf Station. Du bist in Sicherheit. Ich bin bei dir.' Keine unangekündigter Körperkontakt.",
                "Reizreduktion" to "Licht dimmen, Lärmquellen abstellen, andere Patienten zügig entfernen, Reizquellen ausschalten.",
                "Teamkoordination (Wichtig)" to "Genau eine Person spricht ('Lead'). Die zweite sichert diskret den Raum. Die dritte Person koordiniert im Hintergrund und kontaktiert bei Bedarf den Arzt. Niemals sprechen zwei Personen gleichzeitig auf den Patienten ein!"
            ),
            donts = listOf(
                "Keinerlei Erklärungen, moralische Appelle, Belehrungen oder lautstarke Diskussionen führen.",
                "Keine unüberlegten Berührungen oder plötzliche, unangekündigte Körperbewegungen."
            ),
            additionalTips = "Rechtliches: Freiheitsbeschränkende Maßnahmen bedürfen einer klaren Rechtsgrundlage, akuter Selbst-/Fremdgefährdung und ärztlicher Anordnung. Auslöser, Maßnahmen, Dauer, Reaktion und Vitalwerte sind lückenlos zu dokumentieren."
        ),
        PhaseDetails(
            id = "BLAU",
            name = "Blue",
            deName = "BLAU (Nachbereitung)",
            colorHex = "#DBEAFE",
            textColorHex = "#1E40AF",
            subtitle = "Beziehung und Würde wiederherstellen",
            summary = "Phase BLAU beginnt erst, wenn der Jugendliche wieder vollständig im grünen Zustand ist. Achtung: Die Kortisol-Latenz nach starkem Stress beträgt 20-60 Minuten, in denen das System übermäßig empfindlich bleibt. Warten ist klinisch notwendig!",
            neuroBasics = "Der ventrale Vagus is wieder aktiv. Ein klärendes Gespräch ist nun physiologisch wieder möglich.",
            keyInteractions = listOf(
                "Zuhören & Validieren" to "Vollständig zuhören ohne zu unterbrechen · Gefühle spiegeln · Das Bedürfnis hinter dem Verhalten erfragen · Schweigen aushalten · Keine voreiligen Lösungen erzwingen.",
                "Krisenplan aktualisieren" to "Freundliche Fragen stellen: 'Was hat dich so aufgewühlt?' (nicht: 'Warum hast du das getan?') · Neuen Plan abstimmen, Timing bestimmt der Patient mit.",
                "Verbindung herstellen" to "Echte Wahlmöglichkeiten für die nächsten Schritte anbieten · Erforderliche Konsequenzen ruhig, begründet und immer im privaten Rahmen benennen · Stärken wertschätzen: 'Du hast da enorme Kraft spüren lassen, wie können wir diese positiv lenken?' · Die persönliche Würde vollständig wiederherstellen.",
                "Regulation für das Team" to "Nach intensiven Krisen brauchen Teammitglieder Zeit zur eigenen physiologischen Regulation. Kurzes Ausatmen, Trinken, Gehen - das ist keine Schwätzchenzeit, sondern professioneller Erhalt der Handlungsfähigkeit."
            ),
            donts = listOf(
                "Kein überstürztes Klären direkt nach der Krise (Gefahr der Re-Eskalation wegen Kortisol-Latenz).",
                "Niemals den Patienten vor anderen Maßregeln oder eine Demütigung (Scham-Trigger) verursachen."
            )
        )
    )

    val diagnoses = listOf(
        DiagnosisDetails(
            id = "ADHS",
            name = "ADHS (Aufmerksamkeitsdefizit-Hyperaktivitätsstörung)",
            dynamik = "Eskalationen entstehen blitzschnell und hochemotional. Der Weg von einem leichten Trigger bis zum unkontrollierten Ausbruch ist extrem kurz (Impulskontrollschwäche). Die aggressive Energie entlädt sich heftig, verebbt aber oft ebenso schnell. Zurück bleibt im Anschluss häufig ein tiefes Schamgefühl über den Kontrollverlust. Jugendliche mit ADHS testen Grenzen permanent - nicht aus strategischer Manipulation, sondern als Folge impulsiven Handlungsdrangs und zur Suche nach klaren, unmittelbaren Bezugspunkten. Verzögerte Konsequenzen werden neurobiologisch nicht adäquat verarbeitet.",
            absicherung = "Kurze, klare Anweisungen geben (maximal 1 Satz) · Ausreichend Bewegung im Raum erlauben · Andere Patienten diskret herausführen, den Patienten keinesfalls festhalten.",
            klaerung = "Gespräch maximal 3-5 Minuten halten, bevorzugt im Stehen oder im Gehen · Konkrete, fokussierte Frage stellen: 'What was the hardest part just now?' · Stärken und Fähigkeiten unmittelbar benennen.",
            aufloesung = "Sofort umsetzbare, kurze Vereinbarungen treffen · Logische Konsequenzen ohne jegliche zeitliche Verzögerung umsetzen · Lob immer sofort und hochspezifisch aussprechen (nicht pauschal)."
        ),
        DiagnosisDetails(
            id = "EIPS",
            name = "EIPS (Emotional instabile Persönlichkeitsstörung / Borderline)",
            dynamik = "EIPS ist primär eine neurobiologische Störung der Emotionseinstellung: Eine extrem niedrige Reizschwelle, enorme Intensität der Affekte und eine stark eingeschränkte Fähigkeit zur selbständigen Beruhigung prägen das Bild. Rasch wechselnde emotionale Zustände, ausgeprägtes Schwarz-Weiß-Denken und Selbstverletzungen als physiologische Spannungsregulation sind Symptome der Erkrankung - nicht willentliche Manipulation. Das Testen des Personals auf Zuverlässigkeit ist ein verzweifelter bindungstheoretischer Testlauf. Das Phänomen des 'Splittings' (Aufspaltung des Teams) dient dem Schutz zerbrechlicher innerer Objektbeziehungen und erfordert absolute, geschlossene Teamkonsistenz.",
            absicherung = "Ruhig, unaufgeregt und felsenfest auftreten · Keinerlei Anzeichen von Panik oder Überforderung zeigen · Nicht penetrant auf direktem Blickkontakt bestehen · Körperliche Augenhöhe wahren · Lückenlos konsistente Teamhaltung wahren.",
            klaerung = "Hocheffektive DBT-Validation anwenden: Die Intensität des Gefühls vollkommen anerkennen, bevor Lösungen gesucht werden · Ambivalenz einführen ('Ein Teil von dir fühlt sich so, ein anderer...') · Keinesfalls invalidieren ('Das ist doch nicht so schlimm.').",
            aufloesung = "Sorgfältig formulierte, echte Wahlmöglichkeiten anbieten (keine Scheinoptionen) · Transparenz wahren über alle weiteren Schritte · Kein Nachgeben oder Einknicken aus Erschöpfung - dies verstärkt das dysfunktionale Eskalationsmuster langfristig."
        ),
        DiagnosisDetails(
            id = "PTBS",
            name = "PTBS (Posttraumatische Belastungsstörung)",
            dynamik = "Eskalationsreaktionen wirken auf das Team oft völlig unvorhersehbar, willkürlich oder unbegründet. Der Auslöser (Trigger) reaktiviert jedoch ein im autonomen Nervensystem gespeichertes Trauma. Körperkontakt, bestimmte Stimmlagen, enge Räume, plötzliche Annäherungen von hinten oder sensorische Reize können Flashbacks auslösen, die klinisch wie psychotische Zustände wirken können. Dissoziation ist eine biologisch determinierte Überlebensreaktion (Freeze/Collapse), kein Trotz. Körperlicher Kontakt ohne explizites Einverständnis stellt eine akute Gefahr der Retraumatisierung dar.",
            absicherung = "Körperlichen Kontakt strikt vermeiden, außer im Notfall mit Ankündigung · Den Patienten niemals unangekündigt von hinten ansprechen · Grounding (Erdung) hat oberste Priorität: 'Spüre den festen Boden unter dir.' · Trigger präventiv im Krisenplan erfassen.",
            klaerung = "Gespräche ausnahmslos im grünen Zustand führen · Erdungstechniken voranstellen · Tempo drastisch entschleunigen, kein Druck aufbauen · Keinerlei ungeplante Konfrontation mit Trauma-Inhalten.",
            aufloesung = "Die Rückgabe von Kontrolle ist die wirksamste deeskalierende Medizin · Jede sichere Wahlentscheidung beim Patienten belassen · Detaillierte Pflege des individuellen Krisenplans."
        ),
        DiagnosisDetails(
            id = "ASS",
            name = "ASS (Autismus-Spektrum-Störung)",
            dynamik = "Die primäre Eskalationsursache ist fast immer eine massive sensorische oder kognitive Reizüberflutung (Overload) - nicht interpersoneller Trotz. Geräusche, grelles Licht, Berührungen oder Gerüche überlasten die neuronale Filterkapazität. Gesprochene Sprache wird absolut wörtlich und unkontextualisiert verarbeitet: Metaphern, Ironie oder indirekte Bitten führen zu echten, stressvollen Missverständnissen. Abrupte Brüche gewohnter Abläufe oder Routinen lösen Panik aus. Stereotypien wie rhythmisches Schlagen dienen oft der notwendigen neuronalen Reizunterdrückung und Selbstregulation.",
            absicherung = "Körperkontakt unter allen Umständen vermeiden · Umgehende Reizreduktion einleiten (Licht dimmen, Lärmquellen reduzieren, Anzahl anwesender Personen minimieren) · Einsatz von visuellen Hilfen (Zeichnungen, Ablaufpläne, Timer) · Ultra-klare, einsilbige Kommunikation.",
            klaerung = "Rein visuelle Systeme bevorzugen · Keine Redewendungen, Metaphern oder impliziten Erwartungen · Exakt identische Formulierungen geduldig wiederholen · Gefühlszustände über Skalen oder standardisierte Gefühlskarten erfassen.",
            aufloesung = "Das zeitliche Ende der Aktivität konkret und sichtlich verankern (z.B. Sanduhr oder Timer) · Routinen rasch wiederherstellen ('Danach kommt standardmäßig...') · Reizarmen Rückzugsraum (Snoezelen/Seclusion) konstant zugänglich halten."
        ),
        DiagnosisDetails(
            id = "Psychose",
            name = "Psychotische Episode / Schizophrenie",
            dynamik = "In einer akuten psychotischen Episode werden Sinneseindrücke, Gedanken und Bedrohungen durch Halluzinationen oder Wahnformationen massiv verzerrt. Für das Team unbegreifliche Handlungen folgen einer zwingenden inneren Überlebenslogik des Patienten. Akute psychotische Agitation stellt einen medizinischen/psychiatrischen Notfall dar und bedarf umgehender ärztlicher und oft medikamentöser Abklärung. Direkte Konfrontation mit dem Wahn (Widersprechen, Korrigieren, Diskutieren) wird vom Gehirn als akuter Bedrohungsreiz verarbeitet und treibt die Eskalationsspirale unmittelbar an.",
            absicherung = "Maximal ruhige, verlässliche und strukturierende Präsenz einnehmen · Kontinuierliche Orientierung anbieten (Wer bin ich, welcher Ort, welche Uhrzeit) · Den Wahninhalt weder bestätigen noch bekämpfen · Arzt umgehend verständigen.",
            klaerung = "Rein auf der emotionalen Beziehungsebene andocken: 'Ich höre, dass Ihnen das gerade furchtbare Angst macht' statt inhaltlich zu streiten · Kurze, syntaktisch einfache Sätze formulieren (keine Schachtelsätze).",
            aufloesung = "Absolute Sicherheit vermitteln · Nächste konkrete Teilschritte ankündigen: 'Jetzt gehen wir gemeinsam...' · Keine ermüdenden Verhandlungen über die Wahnrealität führen."
        ),
        DiagnosisDetails(
            id = "Sozialverhalten",
            name = "Störung des Sozialverhaltens",
            dynamik = "Gezielte Provokationen, instrumentelle Aggressionen und Manipulationsversuche des Teams sind meist das Ergebnis einer langen, verfestigten Lerngeschichte, in der aggressives Verhalten erfolgreich war, gepaart mit einer veränderten Empathieverarbeitung. Das multiprofessionelle Team wird permanent auf seine Risse, Inkonsistenzen und Ausnahmen hin abgeklopft. Jede personalabhängige Sonderregelung oder eine emotionale Reaktion auf Provokationen signalisiert dem Jugendlichen, dass das System steuerbar ist. Das Team schützt sich und den Patienten durch lückenlose, wohlwollende und absolut konsistente Regelbeachtung.",
            absicherung = "Provokationen vollkommen ins Leere laufen lassen - keinerlei emotionale Beteiligung signalisieren · Grenzen absolut ruhig, bestimmt und emotionsneutral benennen · Nicht in Machtkämpfe verstricken lassen.",
            klaerung = "Zuerst eine tragfähige persönliche Beziehung aufbauen, bevor Grenzen verhandelt werden · Aufrichtiges, unvoreingenommenes Interesse an der Person zeigen · Die Funktion der Provokation verstehen (Sicherheitsvergewisserung oder Beziehungsprüfung) und das Verhalten von der Wertschätzung der Person trennen.",
            aufloesung = "Regeln und logische Konsequenzen felsenfest, berechenbar, aber völlig empathisch ohne Triumphgefühl oder Vorwürfe durchsetzen · Das Team rückt bei Regelüberschreitungen eng zusammen, um Splitting-Effekte sofort unwirksam zu machen."
        )
    )

    val neuroArticles = listOf(
        NeuroArticle(
            title = "2.1 Polyvagal-Theorie (Sicherheitswarnsystem)",
            content = "Die Polyvagal-Theorie (Porges, 2011) beschreibt das autonome Nervensystem als hierarchisches Sicherheitssystem, das ununterbrochen die Umgebung auf Signale von Sicherheit oder Gefahr scannt – schneller als jede bewusste Wahrnehmung. Porges nennt diesen Prozess Neurozeption. Das Nervensystem reagiert nicht auf logische Absichten, sondern auf biologische Signale: Ein ruhig klingendes Gespräch kann das Nervensystem eines traumatisierten Jugendlichen trotzdem in den Alarmzustand versetzen, wenn Tonhöhe, Körperhaltung oder räumliche Nähe als biologische Gefahr registriert werden. Das menschliche Nervensystem besitzt drei hierarchische Stufen der Aktivierung:",
            bulletPoints = listOf(
                "Ventraler Vagus (Soziale Verbundenheit / Grün)" to "Sicherheit und Verbundenheit sind aktiv. Freie Sprache, Mimikspiel, Empathie und differenziertes Denken sind möglich. Einzig in diesem Zustand ist Lernen und therapeutische Klärung wirksam.",
                "Sympathikus (Kampf-Flucht / Gelb)" to "Mobilisierung bei Gefahr. Herzschlag beschleunigt sich, Atmung flach. Logische Argumente verpuffen, der Patient scannt nach Fluchtwegen oder bereitet einen Angriff vor.",
                "Dorsaler Vagus (Erstarrung-Kollaps / Rot)" to "Urzeitlicher Schutz bei auswegloser Bedrohung. Erstarrung, dissoziation, Taubheitsgefühl, Abwesenheit, Verlangsamung. Kommunikation ist hier fast unmöglich; benötigt ausschließlich Grounding (Erdung) und absolute Reizarmut."
            )
        ),
        NeuroArticle(
            title = "2.2 Amygdala Hijack & Kortisol-Latenz",
            content = "Bei starkem Stress überlagert die Amygdala (Mandelkern) innerhalb extrem kurzer Zeit (17–100 Millisekunden) alle anderen Hirnareale und schaltet den präfrontalen Kortex effektiv stumm. Das Gehirn schaltet auf reines Überlebensprogramm um. Ein Jugendlicher im Amygdala-Overload (Phase ROT) IST physiologisch nicht in der Lage, rationale Argumente zu erfassen. Zudem gilt: Sobald sich ein Patient äußerlich beruhigt hat, bleibt der Kortisolspiegel im Blut noch circa 20-60 Minuten lang massiv erhöht (Kortisol-Latenz). Wird in diesem Zeitraum voreilig geklärt, kommt es fast immer zu einer heftigen Re-Eskalation.",
            bulletPoints = listOf(
                "Phase GELB (Vor Hijack)" to "Die Amygdala wird zunehmend aktiver, aber der Kortex ist über gezielte Validation und Beziehungsangebote noch teilweise erreichbar. Letztes Zeitfenster für den STOP-Skill! Keine Erklärungen, keine Konfrontation.",
                "Phase ROT (Hijack aktiv)" to "Vollständiger Hijack. Amygdala übernimmt vollständig, Kortex ist offline. Jedes Argument wird als Bedrohung gewertet. Ausschließlich Sicherheit herstellen, keine Klärung, keine Argumente.",
                "Post-Hijack (Sichtbar ruhig)" to "Amygdala beruhigt sich langsam, Kortex beginnt die Rückkehr. Aber Achtung: Der Kortisolspiegel bleibt noch mindestens 20-60 Minuten lang massiv erhöht. Klärungsgespräche in dieser Phase können sofort einen zweiten Eskalationszyklus auslösen! Abwarten ist eine klinische Notwendigkeit, keine Gleichgültigkeit.",
                "Phase BLAU (Kortisol abgeklungen)" to "Volle ventrale Vagus-Aktivierung. Jetzt ist ein Klärungsgespräch physiologisch möglich und sinnvoll."
            )
        ),
        NeuroArticle(
            title = "2.3 Co-Regulation & Spiegelneuronen",
            content = "Spiegelneuronen bilden das neurobiologische Gelenk für interpersonelles Verhalten: Das Nervensystem einer Person beeinflusst das Nervensystem einer anderen direkt, automatisch, unwillkürlich und schneller als jede bewusste Wahrnehmung. Die wirksamen Signale sind nicht primär Worte, sondern Atemrhythmus, Bewegungstempo, Stimmfrequenz, Muskelspannung und Gesichtsausdruck. Ein dysreguliertes Teammitglied kann einen Patienten unmöglich ko-regulieren. Die wichtigsten Steuerungskanäle:",
            bulletPoints = listOf(
                "Atemrhythmus" to "Synchronisation aktiviert den Parasympathikus beim Jugendlichen. Verlangsamtes, tiefes Atmen – in Phase GELB sichtbar als Regulationsmodell einsetzen.",
                "Bewegungstempo" to "Motorische Entschleunigung überträgt sich auf den Beobachter. Bewege dich langsamer, als die Situation es gefühlt erfordert – überzeugend langsam.",
                "Stimmfrequenz" to "Eine tiefe Stimmlage aktiviert den ventralen Vagus beim Zuhörer. Stimme bewusst in den Brustbereich senken – tief und gleichmäßig fließen lassen, nicht flüstern.",
                "Muskeltonus" to "Erhöhte Körperspannung wird unwillkürlich gespiegelt. Schultern senken, Hände öffnen, Gesicht entspannen – bewusst vor dem Eintreten ausführen.",
                "Mimik" to "Gesichtsausdrücke werden in unter 200 ms neuronal gespiegelt. Neutral-freundlich ist eine aktive therapeutische Wahl, da sich gereizte Mimik immer überträgt."
            )
        ),
        NeuroArticle(
            title = "2.4 Mentalisierung: Den anderen von innen verstehen",
            content = "Mentalisierung (Fonagy et al., 2004) beschreibt die Fähigkeit, das eigene und das Verhalten anderer als durch innere Zustände gesteuert zu verstehen – d.h. Gefühle, Bedürfnisse, Gedanken, Träume und Absichten hinter dem sichtbaren Verhalten wahrzunehmen. Unter akutem Stress bricht diese Fähigkeit bei adoleszenten Patienten (extremes Schwarz-Weiß-Denken, globale Abwertungen) und gleichermaßen beim erschöpften Personal abrupt zusammen. Mentalisierung wieder zu aktivieren bedeutet, echte Neugier zu zeigen statt zu korrigieren:",
            bulletPoints = listOf(
                "Therapeutische Neugier" to "Die Frage 'Was könnte in diesem Moment in ihm vorgehen?' beruhigt sofort das eigene Nervensystem und verändert die klinische Reaktion.",
                "Wichtigste Lernformel" to "Im Post-Incident-Review ist es der entscheidende Hebel: 'Was könnte in dem Jugendlichen vorgegangen sein, als der Vorfall begann?' statt 'Warum hat er das getan?'",
                "Spaltungsprävention" to "Verständnis der inneren Logik des Kindes verhindert, dass das Team in Feindseligkeit oder Ohnmacht rutscht."
            )
        ),
        NeuroArticle(
            title = "2.5 Wahrnehmungsfehler des Teams unter Stress",
            content = "Unter physiologischem Stress ist das Gehirn des Personals kognitiven Verzerrungen unterworfen. Diese Fehler zu kennen schützt das Team vor voreiligen Fehlentscheidungen:",
            bulletPoints = listOf(
                "Fundamental Attribution Error" to "Das eskalative Verhalten wird fälschlich der stabilen Persönlichkeit zugeschrieben ('Er ist manipulativ/aggressiv') statt dem akuten neurobiologischen Erregungszustand ('Er befindet sich in Phase ROT').",
                "Confirmation Bias" to "Informationen werden so interpretiert, dass sie bestehende Überzeugungen bestätigen. Frühwarnsignale bei vermeintlich 'schwierigen' Patienten werden dadurch als 'normales Alltagsverhalten' abgetan und übersehen.",
                "Perceptual Narrowing (Tunnelblick/Filter)" to "Die visuelle und mentale Aufmerksamkeit verengt sich unter Stress massiv auf den eskalierenden Jugendlichen. Der restliche Raum, andere Patienten sowie sekundäre Gefahrenquellen werden klinisch nicht mehr wahrgenommen.",
                "Der innere Interventions-Check" to "Vor jeder Intervention 3 Fragen stellen: 1. Wie ist mein Atemrhythmus? 2. Sind meine Schultern entspannt? 3. Bin ich im Wise Mind oder im Emotion Mind? Fällt die Antwort zweimal negativ aus: Erst drei tiefe Atemzüge nehmen, dann herantreten."
            )
        )
    )

    val commArticles = listOf(
        CommunicationArticle(
            title = "3.1 Professionelle Haltung: Respekt & Trauma-Brille",
            description = "Deeskalierende Kommunikation ist kein isoliertes Werkzeug, sondern ausdruck einer tiefen Haltung. Die traumapädagogische Brille verändert die grundlegende klinische Frage: Statt 'Was stimmt nicht mit dir?' fragen wir 'Was ist dir widerfahren, dass dieses Verhalten damals deine beste Überlebensstrategie war?'. Aggression ist oft ein Hilferuf in einer erlernten, dysfunktionalen Sprache. Die Würde des Jugendlichen ist unter allen Umständen unantastbar.",
            quotes = listOf(
                "Aggressives Verhalten ist oft ein Hilferuf in erlernten Mustern. Das Team, das das Verhalten vom wahren Kern des Jugendlichen trennt, deeskaliert nachhaltig."
            )
        ),
        CommunicationArticle(
            title = "3.2 GFK: Gewaltfreie Kommunikation in der Krise",
            description = "Die GFK (Rosenberg) strukturiert Gespräche in emotionalen Schieflagen entlang von vier glasklaren Schritten, um Eskalationen wirksam abzufangen:",
            details = listOf(
                "1. Beobachtung" to "Wertungsfrei beschreiben, was faktisch der Fall ist: 'Ich sehe, dass du seit einer halben Stunde am Fenster stehst und die Hände ballst.' (Nicht: 'Du bist wieder aggressiv.')",
                "2. Gefühl" to "Gefühle behutsam spiegeln und erfragen: 'Kann es sein, dass du gerade extrem verzweifelt und aufgebracht bist?' (Nicht: 'Du musst dich beruhigen.')",
                "3. Bedürfnis" to "Das hinter dem Verhalten liegende, oft verborgene Bedürfnis ergründen: 'Brauchst du im Moment einfach etwas Ruhe und Abstand?'",
                "4. Bitte" to "Erst im grünen/blauen Zustand eine konkrete, erfüllbare Bitte formulieren: 'Magst du mit mir in den Garten gehen und einen Schluck Wasser trinken?'"
            )
        ),
        CommunicationArticle(
            title = "3.3 Scham als zerstörerischer Eskalationsmotor",
            description = "Scham ist für Jugendliche die unerträglichste aller Emotionen und wird neurobiologisch wie physischer Schmerz verarbeitet. Um Scham abzuwehren, schlägt das Gehirn oft in explosive Wut um (Scham-Wut-Spirale). Wir müssen schamsensible Pflege im Alltag implementieren:",
            details = listOf(
                "Scham-Trigger auf Station" to "Kritik oder Grenzziehungen vor der Patientengruppe · Erzwingen von Entschuldigungen · Vorwürfe im Flur · Ignorieren oder abschätzige Mimik.",
                "Schamsensible Kultur" to "Sämtliche Kritik, Korrekturen oder Konsequenzen werden unter vier Augen in einem separaten Raum besprochen · Entschuldigungen als freiwillige Geste ermöglichen · Lob öffentlich machen, Kritik privat halten."
            )
        ),
        CommunicationArticle(
            title = "3.4 DBT STOP, Wise Mind & Check the Facts",
            description = "Ehe wir intervenieren, müssen wir unsere eigenen Impulse einfrieren (STOP), um unbewusste eskalative Signale zu unterbinden, und uns im Wise Mind zentrieren:",
            details = listOf(
                "S - Stop" to "Innehalten! Nicht sofort reagieren. Den Körper einfrieren, um unwillkürliche Reaktionen zu stoppen.",
                "T - Take a Step Back" to "Einen Schritt physisch und mental zurücktreten. Tief ausatmen, um das sympathische Erregungsniveau zu senken.",
                "O - Observe" to "Was geschieht gerade? Was fühlt das Team? Was braucht der Patient? Beobachte ohne Bewertung.",
                "P - Proceed Mindfully" to "Bewusst handeln aus dem Wise Mind (dem weisen Verstand, der Emotion Mind / Emotionale Überwältigung und Reasonable Mind / Protokollartige Kälte harmonisiert).",
                "Check the Facts-Skill" to "Vor der Reaktion kurze innere Überprüfung: Was ist das konkrete, beobachtbare Verhalten? Was interpretiere ich dahinter? Ist das die einzig mögliche Interpretation? Was ist die neurobiologische Erklärung?",
                "Voraussetzung für Präsenz" to "Klärungsgespräche aus dem reinen Reasonable Mind klingen wie Protokoll; solche aus dem reinen Emotion Mind kippen in Verstrickung. Wise Mind ist der Zustand professioneller Präsenz: 'Wie der Boden am Grund eines Sees – an der Oberfläche stürmische Wellen, unten immer ruhig.'"
            )
        ),
        CommunicationArticle(
            title = "3.5 Verachtung als unbewusster Eskalationsverstärker",
            description = "Verachtung (Gottman, 1994) ist die giftigste interpersonelle Kommunikation. Sie bewertet nicht das Verhalten, sondern die Person als Ganzes. Im anstrengenden Stationsalltag entsteht sie selten böswillig, sondern fast immer als Signal für Erschöpfung und chronischen Stress (Erschöpfungsfalle).",
            details = listOf(
                "Subtile Signale" to "Ein Seufzen beim Betreten des Zimmers, Augenrollen, herablassender Tonfall, Gespräche über den Jugendlichen im Beisein Dritter.",
                "Auslöser für Schmerz" to "Adoleszente registrieren diese Signale mit äußerster Sensibilität (Entwicklungsphase ist auf soziale Ausschlusssignale fokussiert). Verachtung aktiviert dieselben Hirnareale wie physischer Schmerz und ist ein direkter physischer Eskalationsauslöser.",
                "Erschöpfungsfalle" to "Sichtbare Verachtungsmuster sind KEIN moralisches Fehlverhalten, sondern ein Hilferuf nach kollegialer Unterstützung und struktureller Entlastung."
            )
        ),
        CommunicationArticle(
            title = "3.6 Validation: Gefühle anerkennen ohne zu bewerten",
            description = "Validation ist die klinisch wirksamste Kurzintervention in der Deeskalation. Sie signalisiert dem Nervensystem, dass seine subjektive Wahrnehmung nicht falsch ist, was die Amygdala-Erregung dämpft. Validation bedeutet nicht, unakzeptables Verhalten gutzuheißen, sondern die emotionale Realität anzuerkennen.",
            details = listOf(
                "Validation (So klingt es)" to "• „Ich verstehe, dass du gerade sehr wütend bist.“\n• „Das klingt wirklich belastend.“\n• „Es macht Sinn, dass du so reagierst.“\n• „Ich höre, dass dir das gerade sehr viel wird.“\n• „Du musst das nicht alleine tragen.“",
                "Nicht-Validation (Aktiviert Amygdala!)" to "• „Das ist doch nicht so schlimm.“\n• „Du überreagierst völlig.“\n• „Andere haben es viel schwerer.“\n• „Jetzt reiß dich mal zusammen.“\n• „Das kenne ich, mir geht es auch manchmal so.“"
            )
        )
    )

    val teamArticles = listOf(
        NeuroArticle(
            title = "6.1 Rollenverteilung in Akutsituationen",
            content = "Ein unkoordiniertes Team eskaliert Situationen unfreiwillig. In einer Krise gelten eiserne Regeln:",
            bulletPoints = listOf(
                "Rolle 1: Der Sprecher (Lead)" to "Genau eine Person führt die verbale Kommunikation. Alle anderen Teammitglieder schweigen im Raum, um Reizüberflutung und widersprüchliche Signale (Doppelbotschaften) beim Jugendlichen zu verhindern. Das ist meist das Teammitglied mit der besten Beziehung zum Patienten.",
                "Rolle 2: Die Absicherung" to "Sichert diskret den Raum, hält den Fluchtweg frei, schützt andere unbeteiligte Jugendliche.",
                "Rolle 3: Der Koordinator" to "Hält im Hintergrund Fäden zusammen, bereitet Räumlichkeiten vor, alarmiert bei Bedarf den ärztlichen Dienst und dokumentiert die Zeiten."
            )
        ),
        NeuroArticle(
            title = "6.2 Splitting & Teamdynamik",
            content = "Splitting is ein unbewusster Abwehrmechanismus von Jugendlichen mit tiefen Bindungsstörungen: Teammitglieder werden in 'gut' (rettend, nachgiebig) und 'böse' (bestrafend, streng) eingeteilt. Splitting zeigt sich oft durch plötzliche Meinungsverschiedenheiten oder intensive Diskussionen innerhalb des Teams über einen bestimmten Patienten.",
            bulletPoints = listOf(
                "Gegenmaßnahme" to "Lückenloser Informationsaustausch bei jeder Übergabe. Keine Ausnahmen von vereinbarten Regeln zulassen, egal wie gut gemeint. Splitting im Team nicht persönlich nehmen, sondern als klinisches Symptom in der Supervision thematisieren."
            )
        ),
        NeuroArticle(
            title = "6.3 Das Post-Incident-Review (Lernkultur)",
            content = "Ein systematisches Debriefing innerhalb von 24–48 Stunden nach jeder Krisenintervention schützt vor Sekundärtraumatisierung und Burnout auf Station:",
            bulletPoints = listOf(
                "1. Daten & Fakten" to "Was ist konkret und wertungsfrei vorgefallen? Wer war beteiligt? Wie herrschte die Gruppendynamik?",
                "2. Trigger-Analyse" to "Was war der Auslöser – was hat die Situation begünstigt? Polyvagal-Perspektive und Neurozeption einbeziehen.",
                "3. Team-Wertschätzung" to "Was hat das Team gut gemacht? Stärken benennen, nicht überspringen!",
                "4. Lessons Learned" to "Was würden wir beim nächsten Mal anders machen? – konkret und umsetzbar.",
                "5. Befinden des Teams" to "Wie geht es den betroffenen Teammitgliedern? – ernst fragen, nicht rhetorisch.",
                "6. Support-Check" to "Braucht jemand zusätzliche Unterstützung? Supervision, Einzelgespräch?",
                "7. Krisenplan-Update" to "Was ergänzen wir im individuellen Krisenplan des Jugendlichen?",
                "8. Systemische Hebel" to "Welche strukturellen Änderungen helfen künftig auf Station?"
            )
        ),
        NeuroArticle(
            title = "6.4 Supervision & Fehlerkultur als Strukturaufgabe",
            content = "Klinische Supervision ist kein Zeichen von Schwäche, sondern ein unverzichtbares Strukturelement in psycho-sozialen Arbeitsfeldern, in denen die Konfrontation mit menschlichem Leid zur Realität gehört. Eine fehlerfreundliche Kultur schützt das Team und die Patienten gleichermaßen.",
            bulletPoints = listOf(
                "Klinische Supervision" to "Frequenz: Mindestens 1x monatlich (verpflichtend). Funktion: Verarbeitung belastender Fälle, eigene Reaktionen, Splitting-Konstellationen, Erschöpfungsprävention.",
                "Post-Incident-Review" to "Frequenz: Innerhalb von 24–48 Std. nach jedem Vorfall. Funktion: Systematisches Lernformat, Krisenplan-Update, Team-Reflexion und Sichern des Teambefindens.",
                "Multiprofessionelle Fallbesprechung" to "Frequenz: Mindestens 1x wöchentlich. Funktion: Gesamtbild herstellen, Therapieplanung verfeinern, Krisenpläne reviewen, Rollenverteilung schärfen.",
                "Schichtübergabe (Krisenrelevant)" to "Frequenz: Zu jeder Schichtübergabe. Funktion: Aktuelle Frühwarnzeichen, Krisenplan-Status und Besonderheiten lückenlos mitteilen.",
                "Fehlerbehandlung" to "Eine Kultur, die Schuld produziert, schadet allen. Fehler müssen als neurobiologisch erklärbare und systemisch begünstigte Ereignisse behandelt werden, um Kompetenzentwicklung zu ermöglichen."
            )
        ),
        NeuroArticle(
            title = "6.5 Selbstfürsorge als klinische Pflicht",
            content = "Selbstfürsorge ist eine direkte Frage der Patientensicherheit! Ein Teammitglied ohne physiologischen Kortisol-Abbau trägt die biologische Erregung (Aktivierung) in den nächsten Dienst. Schlafmangel erhöht die Amygdala-Reaktivität um bis zu 60 % und gefährdet das Umfeld.",
            bulletPoints = listOf(
                "Akut (Während/Direkt nach Krise)" to "Verlängerte Ausatmung anwenden (4 Sek. ein, 8 Sek. aus) · Füße fest auf dem Boden spüren (Grounding für sich selbst) · Kolleginnen/Kollegen einbeziehen oder sich ablösen lassen · Bewusst langsamer bewegen als nötig.",
                "1–2 Stunden nach der Krise" to "Kurzes Nachgespräch mit einer Kollegin oder einem Kollegen · Körperliche Bewegung (auch kurz) aktivieren · Ausreichend trinken und essen, um Glukosespiegel zu stabilisieren · Supervisorin/Supervisor ansprechen bei anhaltender Belastung.",
                "Strukturell (Teamebene)" to "Regelmäßige Supervision (mindestens 1x monatlich als Dienstpflicht) · Post-Incident-Review nach intensiven Vorfällen · Rotation bei extrem belastenden Patientenkonstellationen · Etablierung einer lebendigen Fehlerkultur ohne Schuldzuweisungen · Dienstplangestaltung mit ausreichenden Erholungszeiten."
            )
        )
    )

    val references = listOf(
        "Bowers, L. et al. (2014) - Safewards: Reducing conflict and containment in acute psychiatric wards. Social Science & Medicine, 114, 99-108.",
        "Porges, S. W. (2011) - The Polyvagal Theory: Neurophysiological Foundations of Emotions, Attachment, Communication, and Self-regulation. W. W. Norton & Company.",
        "Linehan, M. M. (2015) - DBT Skills Training Manual. Guilford Publications.",
        "Rosenberg, M. B. (2016) - Gewaltfreie Kommunikation: Eine Sprache des Lebens. Junfermann Verlag.",
        "Fonagy, P. et al. (2004) - Affect Regulation, Mentalization, and the Development of the Self. Other Press.",
        "Gottman, J. M. (1994) - What Predicts Divorce? The Relationship Between Marital Processes and Marital Outcomes. Lawrence Erlbaum Associates.",
        "Bowlby, J. (1988) - A Secure Base: Parent-Child Attachment and Healthy Human Development. Basic Books."
    )
}
