package br.com.aranoua.pokedex.data.network

import br.com.aranoua.pokedex.data.model.PokemonDetailResponse
import br.com.aranoua.pokedex.data.model.PokemonListResponse
import br.com.aranoua.pokedex.data.model.TypeDetailResponse
import br.com.aranoua.pokedex.data.model.TypeListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    @GET("pokemon")
    fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Call<PokemonListResponse>

    @GET("pokemon/{name}")
    fun getPokemonDetail(
        @Path("name") name: String
    ): Call<PokemonDetailResponse>

    @GET("type")
    fun getTypeList(): Call<TypeListResponse>

    @GET("type/{name}")
    fun getPokemonsByType(
        @Path("name") name: String
    ): Call<TypeDetailResponse>

}
