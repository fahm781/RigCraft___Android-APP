package com.fahm781.rigcraft.sharedBuildPackage

data class SharedBuild(
    val buildData: Map<String, Any> = mapOf(),
    val userEmail: String = "",
    val buildIdentifier: String = "",
    var likes: Number = 0,
    var comment: String = "",
    var buildName: String = ""
//    val likedByCurrentUser: Boolean
) {

}
