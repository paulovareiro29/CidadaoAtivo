package ipvc.estg.cidadaoativo.api

data class LocationPost (
    val latitude: Double,
    val longitude: Double,
    val descricao: String,
    val photo: String,
    val user: String
)

