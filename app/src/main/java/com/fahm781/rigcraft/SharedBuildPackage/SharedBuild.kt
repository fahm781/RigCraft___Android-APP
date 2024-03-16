package com.fahm781.rigcraft.SharedBuildPackage

data class SharedBuild(
    val buildData: Map<String, Any> = mapOf(),
    val userEmail: String = "",
    val buildIdentifier: String = ""
)
