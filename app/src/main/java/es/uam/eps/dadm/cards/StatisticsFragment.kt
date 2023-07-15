package es.uam.eps.dadm.cards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.snackbar.Snackbar
import es.uam.eps.dadm.cards.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {
    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel: StatisticsViewModel by lazy {
        ViewModelProvider(this).get(StatisticsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_statistics,
            container,
            false
        )

        return binding.root
    }

    private fun updatePieChart(data: ArrayList<Float>) {
        val entries = mutableListOf<PieEntry>()

        entries.add(PieEntry(data[0], "Easy"))
        entries.add(PieEntry(data[1], "Doubt"))
        entries.add(PieEntry(data[2], "Difficult"))

        val set = PieDataSet(entries, "Card responses")
        binding.pieChart.data = PieData(set)

        val colorFirst = context?.let { ContextCompat.getColor(it, R.color.pie_first_color) }
        val colorSecond = context?.let { ContextCompat.getColor(it, R.color.pie_second_color) }
        val colorThird = context?.let { ContextCompat.getColor(it, R.color.pie_third_color) }

        set.colors = mutableListOf(colorFirst, colorSecond, colorThird)

        binding.pieChart.invalidate()
    }

    private fun cardQualityPercentage(cards: List<Card>): ArrayList<Float> {
        val percent = arrayListOf(0F, 0F, 0F)
        for (c in cards) {
            if (c.answered)
                when (c.quality) {
                    5 -> percent[0]++
                    3 -> percent[1]++
                    0 -> percent[2]++
                }
        }
        return percent
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*viewModel.decks.observe(viewLifecycleOwner) {
            var message = String()
            for (deck in it)
                message += "The deck named ${deck.deck.name} has ${deck.cards.size} cards\n"
            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
        }*/

        viewModel.loadDeckId(2)
        viewModel.deckWithCards.observe(viewLifecycleOwner) {
            if (it.size == 0)
                return@observe
            val message = "Deck named ${it[0].deck.name} has ${it[0].cards.size} cards"
            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.cards.observe(viewLifecycleOwner) {
            val percent = cardQualityPercentage(it)
            updatePieChart(percent)
            binding.reviewedCardsNumber.text = percent.sum().toInt().toString()
        }
    }
}
