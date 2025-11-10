package br.com.aranoua.pokedex

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.aranoua.pokedex.data.model.PokemonListResponse
import br.com.aranoua.pokedex.data.model.PokemonResult
import br.com.aranoua.pokedex.data.model.TypeDetailResponse
import br.com.aranoua.pokedex.data.model.TypeListResponse
import br.com.aranoua.pokedex.data.network.RetrofitClient
import br.com.aranoua.pokedex.databinding.ActivityMainBinding
import br.com.aranoua.pokedex.ui.list.PokemonAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class UiState { LOADING, SUCCESS, ERROR, EMPTY }

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PokemonAdapter

    private val generationList = mutableListOf<PokemonResult>()
    private var typeFilteredList: List<PokemonResult>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchView()
        setupFilterButtons()
        fetchPokemonDataByGeneration(151, 0)
    }

    private fun setupRecyclerView() {
        adapter = PokemonAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val listToFilter = typeFilteredList ?: generationList
                val filteredList = if (newText.isNullOrEmpty()) {
                    listToFilter
                } else {
                    listToFilter.filter { it.name.contains(newText, ignoreCase = true) }
                }
                adapter.submitList(filteredList)
                // Mostra o estado de lista vazia se a busca não encontrar nada
                if(filteredList.isEmpty()) showState(UiState.EMPTY) else showState(UiState.SUCCESS)
                return true
            }
        })
    }

    private fun setupFilterButtons() {
        binding.buttonFilterType.setOnClickListener { showTypeFilterDialog() }
        binding.buttonFilterGeneration.setOnClickListener { showGenerationFilterDialog() }
    }

    private fun showTypeFilterDialog() {
        // O estado de loading é tratado dentro do callback para evitar piscar a tela
        RetrofitClient.instance.getTypeList().enqueue(object : Callback<TypeListResponse> {
            override fun onResponse(call: Call<TypeListResponse>, response: Response<TypeListResponse>) {
                if (response.isSuccessful) {
                    val types = response.body()?.results?.map { it.name.replaceFirstChar { c -> c.uppercase() } }?.toMutableList() ?: mutableListOf()
                    types.add(0, "Todos os Tipos")

                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Filtrar por Tipo")
                        .setItems(types.toTypedArray()) { _, which ->
                            binding.searchView.setQuery("", false)
                            val selectedType = types[which]
                            if (selectedType == "Todos os Tipos") {
                                typeFilteredList = null
                                adapter.submitList(generationList)
                                if(generationList.isEmpty()) showState(UiState.EMPTY) else showState(UiState.SUCCESS)
                            } else {
                                fetchPokemonsByType(selectedType.lowercase())
                            }
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                } else {
                    showState(UiState.ERROR)
                }
            }
            override fun onFailure(call: Call<TypeListResponse>, t: Throwable) {
                showState(UiState.ERROR)
            }
        })
    }

    private fun showGenerationFilterDialog() {
        val generations = arrayOf("Geração I", "Geração II", "Geração III", "Geração IV", "Geração V")
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Filtrar por Geração")
            .setItems(generations) { _, which ->
                binding.searchView.setQuery("", false)
                typeFilteredList = null
                when (which) {
                    0 -> fetchPokemonDataByGeneration(151, 0)
                    1 -> fetchPokemonDataByGeneration(100, 151)
                    2 -> fetchPokemonDataByGeneration(135, 251)
                    3 -> fetchPokemonDataByGeneration(107, 386)
                    4 -> fetchPokemonDataByGeneration(156, 493)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun fetchPokemonsByType(typeName: String) {
        showState(UiState.LOADING)
        RetrofitClient.instance.getPokemonsByType(typeName).enqueue(object : Callback<TypeDetailResponse> {
            override fun onResponse(call: Call<TypeDetailResponse>, response: Response<TypeDetailResponse>) {
                if (response.isSuccessful) {
                    val pokemonsFromType = response.body()?.pokemon?.map { it.pokemon } ?: emptyList()
                    typeFilteredList = pokemonsFromType
                    adapter.submitList(pokemonsFromType)
                    if(pokemonsFromType.isEmpty()) showState(UiState.EMPTY) else showState(UiState.SUCCESS)
                } else {
                    showState(UiState.ERROR)
                }
            }
            override fun onFailure(call: Call<TypeDetailResponse>, t: Throwable) {
                showState(UiState.ERROR)
            }
        })
    }

    private fun fetchPokemonDataByGeneration(limit: Int, offset: Int) {
        showState(UiState.LOADING)
        binding.searchView.setQuery("", false)
        RetrofitClient.instance.getPokemonList(limit, offset)
            .enqueue(object : Callback<PokemonListResponse> {
                override fun onResponse(call: Call<PokemonListResponse>, response: Response<PokemonListResponse>) {
                    response.body()?.results?.let {
                        generationList.clear()
                        generationList.addAll(it)
                        adapter.submitList(generationList)
                        if(generationList.isEmpty()) showState(UiState.EMPTY) else showState(UiState.SUCCESS)
                    } ?: showState(UiState.ERROR)
                }
                override fun onFailure(call: Call<PokemonListResponse>, t: Throwable) {
                    showState(UiState.ERROR)
                }
            })
    }

    private fun showState(state: UiState) {
        binding.progressBar.visibility = if (state == UiState.LOADING) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (state == UiState.SUCCESS) View.VISIBLE else View.GONE
        binding.textViewError.visibility = if (state == UiState.ERROR) View.VISIBLE else View.GONE
        binding.textViewEmpty.visibility = if (state == UiState.EMPTY) View.VISIBLE else View.GONE
    }
}
