package com.example.androidprojectma23

object IconMapping {
    val iconIdToDocIdMap = mapOf(
        R.id.icon_music to "qWAkLQAUlXIuJCn45ChZ",
        R.id.icon_sports to "9NPO76LYaq9hl5KOCtC4",
        R.id.icon_movies to "AYpDbrWtQOUOt7rBDXDH",
        R.id.icon_art to "iglkcuMPG8egGg4scETR",
        R.id.icon_books to "YmMikFDeggctuiqSYrmw",
        R.id.icon_wine to "YvVGXixVaQSsMhAZaCy2",
        R.id.icon_cooking to "HPQHhJeFC7wQaHyAFSnU",
        R.id.icon_travel to "M9RqxG3Caa0JNT9h6ZTX",
        R.id.icon_festival to "EymGn10U227Gf5xmducS",
        R.id.icon_fashion to "vs5sifFqzkrCyVILxya6",
        R.id.icon_dance to "zcz594bv81UYIWjhWgTy",
        R.id.icon_games to "SM8Oh6Hnba6Gzjpn77RJ",
        R.id.icon_yoga to "6O7GXIC0DWKz6T8wXCJa",
        R.id.icon_camping to "HvJnJ1QKuS2l9IAzYkGa",
        R.id.icon_fika to "0YB3cpO2ducwVQeuCmHC",
        R.id.icon_training to "xZ4sv4Th1Rx3xmUWPr7C",
        R.id.icon_animals to "YeByZ6w5see5N6GfBPYI",
        R.id.icon_garden to "zBgJksLJY1Fa0s3oUSvg",
        R.id.icon_photography to "ftWLcl8ag7pabyuSSJih",
        R.id.icon_technology to "GTPROJYniNOrFBivL7wE"
    )

    fun getIconName(id:Int): String? {
        return iconIdToDocIdMap[id]
    }
}