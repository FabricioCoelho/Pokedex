package br.com.aranoua.pokedex.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import br.com.aranoua.pokedex.data.model.PokemonDetailResponse
import br.com.aranoua.pokedex.data.network.RetrofitClient
import br.com.aranoua.pokedex.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    companion object {
        const val EXTRA_POKEMON_NAME = "EXTRA_POKEMON_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Habilita o botão de "voltar" (Up button) na ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val pokemonName = intent.getStringExtra(EXTRA_POKEMON_NAME)
        if (pokemonName != null) {
            fetchPokemonDetails(pokemonName)
        } else {
            Log.e("DetailActivity", "Nome do Pokémon não fornecido")
            finish()
        }
    }

    // Trata o clique nos itens da ActionBar (incluindo o botão "voltar")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // Finaliza a activity atual, retornando para a anterior (MainActivity)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchPokemonDetails(name: String) {
        RetrofitClient.instance.getPokemonDetail(name)
            .enqueue(object : Callback<PokemonDetailResponse> {
                override fun onResponse(call: Call<PokemonDetailResponse>, response: Response<PokemonDetailResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { bindPokemonData(it) }
                    } else {
                        Log.e("DetailActivity", "Erro ao buscar detalhes: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<PokemonDetailResponse>, t: Throwable) {
                    Log.e("DetailActivity", "Falha na requisição de detalhes", t)
                }
            })
    }

    private fun bindPokemonData(pokemon: PokemonDetailResponse) {
        // Define o título da ActionBar com o nome do Pokémon
        supportActionBar?.title = pokemon.name.replaceFirstChar { it.uppercase() }

        binding.detailPokemonName.text = "#${String.format("%03d", pokemon.id)}"
        binding.detailPokemonHeight.text = "Altura\n${pokemon.height / 10.0}m"
        binding.detailPokemonWeight.text = "Peso\n${pokemon.weight / 10.0}kg"

        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemon.id}.png"
        Glide.with(this).load(imageUrl).into(binding.detailPokemonImage)

        binding.chipGroupTypes.removeAllViews()
        pokemon.types.forEach { typeSlot ->
            val chip = Chip(this)
            chip.text = typeSlot.type.name.replaceFirstChar { it.uppercase() }
            binding.chipGroupTypes.addView(chip)
        }

        pokemon.stats.forEach { statSlot ->
            when (statSlot.stat.name) {
                "hp" -> binding.statHp.text = statSlot.baseStat.toString()
                "attack" -> binding.statAttack.text = statSlot.baseStat.toString()
                "defense" -> binding.statDefense.text = statSlot.baseStat.toString()
            }
        }
    }
}
