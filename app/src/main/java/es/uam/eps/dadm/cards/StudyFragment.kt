package es.uam.eps.dadm.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import es.uam.eps.dadm.cards.activities.SettingsActivity
import es.uam.eps.dadm.cards.databinding.FragmentStudyBinding

class StudyFragment: Fragment() {
    private lateinit var binding: FragmentStudyBinding
    private val viewModel: StudyViewModel by lazy {
        ViewModelProvider(this)[StudyViewModel::class.java]
    }

    private var listener = View.OnClickListener { v ->
        val quality = when (v?.id) {
            R.id.hard_button -> 0
            R.id.doubt_button -> 3
            R.id.easy_button -> 5
            else -> throw Exception("Unknown quality")
        }
        viewModel.update(quality)

        if (viewModel.card == null) {
            Toast.makeText(activity, R.string.no_more_cards_toast_message, Toast.LENGTH_SHORT).show()
                  }

        viewModel.card?.answered = false

        binding.invalidateAll()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_study,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.dueCard.observe(viewLifecycleOwner) {
            viewModel.card= it
            binding.invalidateAll()
        }
        binding.apply {
            answerButton.setOnClickListener {
                viewModel?.card?.answered = true
                invalidateAll()
            }
        }
        binding.easyButton.setOnClickListener(listener)
        binding.doubtButton.setOnClickListener(listener)
        binding.hardButton.setOnClickListener(listener)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (SettingsActivity.isBoardAvailable(requireContext()))
            binding.boardView?.visibility = View.VISIBLE
        else
            binding.boardView?.visibility = View.GONE
    }
}
