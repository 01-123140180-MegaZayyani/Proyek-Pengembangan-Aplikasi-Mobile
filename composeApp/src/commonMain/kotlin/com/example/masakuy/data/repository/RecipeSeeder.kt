package com.example.masakuy.data.repository

import com.example.masakuy.data.local.database.MasakuyDatabase

class RecipeSeeder(private val database: MasakuyDatabase) {
    fun seedIfEmpty() {
        val count = database.recipeQueries.getAllRecipes().executeAsList().size
        if (count > 0) return

        val recipes = listOf(
            listOf("1", "Nasi Telur Kecap", "", 11000L, 15L, "Mudah",
                "Nasi,Telur,Kecap manis,Bawang putih,Minyak goreng",
                "1.Goreng telur|2.Tumis bawang putih|3.Tambahkan kecap manis|4.Sajikan dengan nasi"),
            listOf("2", "Mi Goreng Sayur", "", 13500L, 20L, "Mudah",
                "Mi instan,Sawi,Wortel,Bawang putih,Telur,Kecap",
                "1.Rebus mi|2.Tumis bawang putih|3.Masukkan sayur|4.Tambahkan mi dan kecap|5.Aduk rata"),
            listOf("3", "Tumis Tahu Pedas", "", 12000L, 15L, "Mudah",
                "Tahu,Cabai merah,Bawang putih,Kecap manis,Garam",
                "1.Goreng tahu|2.Tumis bumbu|3.Masukkan tahu|4.Tambahkan kecap dan garam"),
            listOf("4", "Nasi Goreng Sederhana", "", 15000L, 20L, "Mudah",
                "Nasi,Telur,Bawang putih,Kecap manis,Garam,Minyak",
                "1.Tumis bawang putih|2.Masukkan nasi|3.Tambahkan telur|4.Beri kecap dan garam"),
            listOf("5", "Sayur Bayam Bening", "", 8000L, 15L, "Mudah",
                "Bayam,Jagung,Bawang putih,Garam,Air",
                "1.Rebus air|2.Masukkan jagung|3.Tambahkan bayam|4.Beri garam secukupnya"),
            listOf("6", "Tempe Orek", "", 10000L, 20L, "Mudah",
                "Tempe,Cabai,Bawang putih,Kecap manis,Gula,Minyak",
                "1.Goreng tempe|2.Tumis bumbu|3.Masukkan tempe|4.Tambahkan kecap dan gula"),
            listOf("7", "Sup Tahu Wortel", "", 14000L, 25L, "Mudah",
                "Tahu,Wortel,Kentang,Bawang putih,Garam,Merica",
                "1.Rebus air|2.Masukkan sayur|3.Tambahkan tahu|4.Beri bumbu secukupnya"),
            listOf("8", "Cah Kangkung", "", 9000L, 10L, "Mudah",
                "Kangkung,Bawang putih,Cabai,Saus tiram,Garam",
                "1.Tumis bawang dan cabai|2.Masukkan kangkung|3.Tambahkan saus tiram|4.Aduk rata")
        )

        recipes.forEach { r ->
            database.recipeQueries.insertRecipe(
                r[0] as String, r[1] as String, r[2] as String,
                r[3] as Long, r[4] as Long, r[5] as String,
                r[6] as String, r[7] as String, 0L
            )
        }
    }
}