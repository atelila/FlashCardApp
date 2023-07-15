package es.uam.eps.dadm.cards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import es.uam.eps.dadm.cards.databinding.ListItemCardBinding

class CardAdapter : RecyclerView.Adapter<CardAdapter.CardHolder>() {

    var data = listOf<Card>()
    private lateinit var binding: ListItemCardBinding

    inner class CardHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var local = binding
        fun bind(card: Card) {
            local.card = card
            itemView.setOnClickListener {
                it.findNavController()
                    .navigate(CardListFragmentDirections.actionCardListFragmentToCardEditFragment(card.id))
            }
            local.listItemDetails.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    local.listItemDetailsText.run {
                        visibility = View.VISIBLE
                        text = createDetailsSting(card)
                    }
                } else {
                    local.listItemDetailsText.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = ListItemCardBinding.inflate(layoutInflater, parent, false)
        return CardHolder(binding.root)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.bind(data[position])
    }

    private fun createDetailsSting(card: Card) = String.format(
        "Quality: %d\nRepetitions: %d\nEasiness: %.2f\nNext date: %s",
        card.quality,
        card.repetitions,
        card.easiness,
        card.nextPracticeDate.substring(0, 10)
    )
}