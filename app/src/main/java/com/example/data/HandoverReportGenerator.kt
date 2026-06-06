package com.example.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object HandoverReportGenerator {

    /**
     * Formats timestamp into readable German locale date-time format.
     */
    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY)
        return sdf.format(Date(timestamp))
    }

    /**
     * Formatter 1: Structured clinical text for medical chart entries (Kurveneinträge).
     */
    fun generateChartEntry(report: HandoverReport): String {
        val dateStr = formatTimestamp(report.timestamp)
        val zimmer = if (report.roomNumber.trim().isNotEmpty()) report.roomNumber.trim() else "Keine Angabe"
        
        return """
            === CLINICAL HANDOVER CHART ENTRY (KJP) ===
            Interventionszeitpunkt: $dateStr Uhr
            Zimmer / Ort: $zimmer
            Deeskalationsstufe: ${report.phase}
            Verdachtsdiagnose / ICD: ${report.diagnosis}
            Gewählte Regulationsmethoden: ${report.strategies}
            Beurteilter Verlauf: ${report.result.take(200)}
            Eskalationswarnung / Status: ${report.outcome}
            --------------------------------------------------
            [DSGVO-Hinweis: Dieser Bericht enthält zwecks Datenschutz keine Klarnamen oder Geburtsdaten.]
            ==================================================
        """.trimIndent()
    }

    /**
     * Formatter 2: Short, shareable notification card for messenger applications (WhatsApp / Signal).
     */
    fun generateMessengerShareText(report: HandoverReport): String {
        val dateStr = formatTimestamp(report.timestamp)
        val zimmer = if (report.roomNumber.trim().isNotEmpty()) report.roomNumber.trim() else "N/A"
        
        val phaseEmoji = when (report.phase.uppercase()) {
            "ROT" -> "🔴"
            "GELB" -> "🟡"
            "WEISS_GRUEN", "WEISS", "GRUEN" -> "🟢"
            "BLAU" -> "🔵"
            else -> "ℹ️"
        }

        return """
            $phaseEmoji *KJP-AKUTMELDUNG (SCHICHTÜBERGABE)*
            🕒 *Zeit:* $dateStr Uhr
            🔑 *Zimmer:* $zimmer
            📌 *Phase:* ${report.phase}
            🧠 *Diagnostischer Kontext:* ${report.diagnosis}
            🛠️ *Arbeitsstrategien:* ${report.strategies}
            📝 *Verlauf:* ${report.result.take(200)}
            🚨 *Eskaliert:* ${report.outcome}
        """.trimIndent()
    }
}
