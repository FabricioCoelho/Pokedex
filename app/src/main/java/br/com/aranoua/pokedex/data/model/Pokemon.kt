package br.com.aranoua.pokedex.data.model

import com.google.gson.annotations.SerializedName

// ---------------- Modelos para a Lista de Pokémon ----------------

data class PokemonListResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<PokemonResult>
)

data class PokemonResult(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)

// ---------------- Modelos para os Detalhes do Pokémon ----------------

data class PokemonDetailResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("height") val height: Int,
    @SerializedName("weight") val weight: Int,
    @SerializedName("types") val types: List<PokemonTypeSlot>,
    @SerializedName("stats") val stats: List<PokemonStatSlot>
)

data class PokemonTypeSlot(
    @SerializedName("slot") val slot: Int,
    @SerializedName("type") val type: PokemonTypeInfo
)

data class PokemonTypeInfo(
    @SerializedName("name") val name: String
)

data class PokemonStatSlot(
    @SerializedName("base_stat") val baseStat: Int,
    @SerializedName("stat") val stat: PokemonStatInfo
)

data class PokemonStatInfo(
    @SerializedName("name") val name: String
)

// ---------------- Modelos para o Filtro de Tipo ----------------

data class TypeListResponse(
    @SerializedName("results") val results: List<TypeResult>
)

data class TypeResult(
    @SerializedName("name") val name: String
)

data class TypeDetailResponse(
    @SerializedName("pokemon") val pokemon: List<PokemonSlot>
)

data class PokemonSlot(
    @SerializedName("pokemon") val pokemon: PokemonResult
)
