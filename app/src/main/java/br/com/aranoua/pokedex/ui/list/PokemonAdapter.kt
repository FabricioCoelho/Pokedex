package br.com.aranoua.pokedex.ui.list

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.aranoua.pokedex.R
import br.com.aranoua.pokedex.data.model.PokemonResult
import br.com.aranoua.pokedex.ui.detail.DetailActivity
import com.bumptech.glide.Glide

//  O Adapter herda de ListAdapter para performance e animações automáticas.
class PokemonAdapter : ListAdapter<PokemonResult, PokemonAdapter.PokemonViewHolder>(PokemonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pokemon_list_item, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        // getItem() é um método do ListAdapter que pega o item na posição correta.
        val pokemon = getItem(position)
        holder.bind(pokemon)
    }

    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.pokemon_name)
        private val imageView: ImageView = itemView.findViewById(R.id.pokemon_image)

        fun bind(pokemon: PokemonResult) {
            nameTextView.text = pokemon.name.replaceFirstChar { it.uppercase() }

            val id = pokemon.url.split("/").filter { it.isNotEmpty() }.last()
            val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher) // Imagem de fallback
                .into(imageView)

            itemView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_POKEMON_NAME, pokemon.name)
                }
                context.startActivity(intent)
            }
        }
    }
}

// O DiffUtil calcula a diferença entre duas listas e permite que o ListAdapter

class PokemonDiffCallback : DiffUtil.ItemCallback<PokemonResult>() {
    override fun areItemsTheSame(oldItem: PokemonResult, newItem: PokemonResult): Boolean {
        // A URL é o identificador único de cada Pokémon na lista.
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: PokemonResult, newItem: PokemonResult): Boolean {
        // Como nosso item só tem nome e url, se as urls são iguais, o conteúdo é o mesmo.
        return oldItem == newItem
    }
}
