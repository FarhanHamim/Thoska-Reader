package de.thoska.pulse.model

/**
 * Thuringian Higher Education Institutions participating in the thoska system.
 */
enum class ThuringiaUniversity(
    val id: String,
    val shortName: String,
    val fullName: String,
    val primaryColorHex: Long
) {
    EAH_JENA(
        id = "EAH_JENA",
        shortName = "EAH JENA",
        fullName = "Ernst-Abbe-Hochschule Jena",
        primaryColorHex = 0xFF005A5B
    ),
    FSU_JENA(
        id = "FSU_JENA",
        shortName = "FSU JENA",
        fullName = "Friedrich-Schiller-Universität Jena",
        primaryColorHex = 0xFF002F5D
    ),
    TU_ILMENAU(
        id = "TU_ILMENAU",
        shortName = "TU ILMENAU",
        fullName = "Technische Universität Ilmenau",
        primaryColorHex = 0xFF006633
    ),
    BAUHAUS_WEIMAR(
        id = "BAUHAUS_WEIMAR",
        shortName = "BAUHAUS WEIMAR",
        fullName = "Bauhaus-Universität Weimar",
        primaryColorHex = 0xFFE3000B
    ),
    UNI_ERFURT(
        id = "UNI_ERFURT",
        shortName = "UNI ERFURT",
        fullName = "Universität Erfurt",
        primaryColorHex = 0xFFA51E37
    ),
    FH_ERFURT(
        id = "FH_ERFURT",
        shortName = "FH ERFURT",
        fullName = "Fachhochschule Erfurt",
        primaryColorHex = 0xFF00854A
    ),
    HS_SCHMALKALDEN(
        id = "HS_SCHMALKALDEN",
        shortName = "HS SCHMALKALDEN",
        fullName = "Hochschule Schmalkalden",
        primaryColorHex = 0xFF005478
    ),
    HS_NORDHAUSEN(
        id = "HS_NORDHAUSEN",
        shortName = "HS NORDHAUSEN",
        fullName = "Hochschule Nordhausen",
        primaryColorHex = 0xFF004B93
    ),
    HFM_WEIMAR(
        id = "HFM_WEIMAR",
        shortName = "HFM WEIMAR",
        fullName = "Hochschule für Musik Franz Liszt Weimar",
        primaryColorHex = 0xFF8B0000
    ),
    DHGE(
        id = "DHGE",
        shortName = "DHGE",
        fullName = "Duale Hochschule Gera-Eisenach",
        primaryColorHex = 0xFF003366
    );

    companion object {
        fun fromId(id: String?): ThuringiaUniversity {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: EAH_JENA
        }
    }
}

