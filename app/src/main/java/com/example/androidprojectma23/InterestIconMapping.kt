package com.example.androidprojectma23

object IconMapping {

    val userInterests = listOf(
        R.id.icon_music, R.id.icon_sports, R.id.icon_movies, R.id.icon_art,
        R.id.icon_books, R.id.icon_wine, R.id.icon_cooking, R.id.icon_travel,
        R.id.icon_festival, R.id.icon_fashion, R.id.icon_dance, R.id.icon_games,
        R.id.icon_yoga, R.id.icon_camping, R.id.icon_fika, R.id.icon_training,
        R.id.icon_animals, R.id.icon_garden, R.id.icon_photography, R.id.icon_technology
    )
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

    val docIdToIconResMap = mapOf(
        "qWAkLQAUlXIuJCn45ChZ" to R.drawable.icon_music,
        "9NPO76LYaq9hl5KOCtC4" to R.drawable.icon_sports,
        "AYpDbrWtQOUOt7rBDXDH" to R.drawable.icon_movies,
        "iglkcuMPG8egGg4scETR" to R.drawable.icon_art,
        "YmMikFDeggctuiqSYrmw" to R.drawable.icon_books,
        "YvVGXixVaQSsMhAZaCy2" to R.drawable.icon_wine,
        "HPQHhJeFC7wQaHyAFSnU" to R.drawable.icon_cooking,
        "M9RqxG3Caa0JNT9h6ZTX" to R.drawable.icon_travel,
        "EymGn10U227Gf5xmducS" to R.drawable.icon_festival,
        "vs5sifFqzkrCyVILxya6" to R.drawable.icon_fashion,
        "zcz594bv81UYIWjhWgTy" to R.drawable.icon_dance,
        "SM8Oh6Hnba6Gzjpn77RJ" to R.drawable.icon_games,
        "6O7GXIC0DWKz6T8wXCJa" to R.drawable.icon_yoga,
        "HvJnJ1QKuS2l9IAzYkGa" to R.drawable.icon_camping,
        "0YB3cpO2ducwVQeuCmHC" to R.drawable.icon_fika,
        "xZ4sv4Th1Rx3xmUWPr7C" to R.drawable.icon_training,
        "YeByZ6w5see5N6GfBPYI" to R.drawable.icon_animals,
        "zBgJksLJY1Fa0s3oUSvg" to R.drawable.icon_garden,
        "ftWLcl8ag7pabyuSSJih" to R.drawable.icon_photography,
        "GTPROJYniNOrFBivL7wE" to R.drawable.icon_technology
    )

    val imageViewIdProfileCard = listOf(
        R.id.interestImageView, R.id.interestImageView2,
        R.id.interestImageView3, R.id.interestImageView4,
        R.id.interestImageView5
    )

    fun getIconName(id:String): String {
        return docIdToIconResMap[id].toString()
    }

//    fun getIconId(docId: String): Int? {
//        return docIdToIconResMap[docId]
//    }

}