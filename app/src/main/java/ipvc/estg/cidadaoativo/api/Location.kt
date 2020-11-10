package ipvc.estg.cidadaoativo.api

import android.view.inspector.IntFlagMapping

data class Location(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val descricao: String,
    val photo: String,
    val user: User
)

data class User(
    val id: Int,
    val username: String
)