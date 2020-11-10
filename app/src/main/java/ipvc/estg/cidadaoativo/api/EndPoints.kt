package ipvc.estg.cidadaoativo.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @GET("locations/")
    fun getLocations(): Call<List<Location>>

    @GET("locations/{id}")
    fun getLocationById(@Path("id") id: Int): Call<Location>

    @FormUrlEncoded
    @POST("locations/")
    fun createLocation(@Field("latitude") latitude: Double,
                       @Field("longitude") longitude: Double,
                       @Field("descricao") descricao: String,
                       @Field("photo") photo: String,
                       @Field("users_id") user_id: Int): Call<OutputPost>
}