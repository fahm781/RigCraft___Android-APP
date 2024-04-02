package com.fahm781.rigcraft.ebayServices

data class SearchResult(
    val href: String,
    val total: Int,
    val itemSummaries: List<ItemSummary>
)
