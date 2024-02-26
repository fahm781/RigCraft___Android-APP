package com.fahm781.rigcraft.EbayServices

data class SearchResult(
    val href: String,
    val total: Int,
    val itemSummaries: List<ItemSummary>
)
