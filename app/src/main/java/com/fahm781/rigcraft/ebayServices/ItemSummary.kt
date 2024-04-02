package com.fahm781.rigcraft.ebayServices

data class ItemSummary(
    val itemId: String,
    val title: String,
    val price: Price,
    val image: Image,
    val itemWebUrl: String
    // Add other fields as needed
)
data class Price(
    val value: String,
    val currency: String
)

data class Image(
    val imageUrl: String
)

