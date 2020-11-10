package ipvc.estg.cidadaoativo.api

import android.view.inspector.IntFlagMapping

data class Location(
    val id: Int,
    val latitude: Float,
    val longitude: Float,
    val descricao: String,
    val photo: String,
    val user: User
)

data class User(
    val id: Int,
    val username: String
)