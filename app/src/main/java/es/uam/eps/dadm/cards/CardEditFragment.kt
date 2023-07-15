package es.uam.eps.dadm.cards
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.FragmentCardEditBinding
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class CardEditFragment : Fragment() {
    private val executor = Executors.newSingleThreadExecutor()
    lateinit var binding: FragmentCardEditBinding
    lateinit var card: Card
    lateinit var question: String
    lateinit var answer: String

    private val viewModel: CardEditViewModel by lazy {
        ViewModelProvider(this).get(CardEditViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_card_edit,
            container,
            false
        )

        val args = CardEditFragmentArgs.fromBundle(requireArguments())

        viewModel.loadCardId(args.cardId)
        viewModel.card.observe(viewLifecycleOwner) {
            card = it
            question = it.question
            answer = it.answer
            binding.card = it
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val questionTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                card.question = s.toString()
            }
        }

        binding.questionEditText.addTextChangedListener(questionTextWatcher)

        val answerTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                card.answer = s.toString()
            }
        }

        binding.answerEditText.addTextChangedListener(answerTextWatcher)

        binding.acceptCardEditButton.setOnClickListener {
            executor.execute {
                CardDatabase.getInstance(requireContext()).cardDao.updateCard(card)
            }
            it.findNavController()
                .navigate(CardEditFragmentDirections.actionCardEditFragmentToCardListFragment())
        }

        binding.cancelCardEditButton.setOnClickListener {
            card.question = question
            card.answer = answer
            if (card.question == "" || card.answer == "")
                CardsApplication.deleteCard(card.id)
            it.findNavController()
                .navigate(CardEditFragmentDirections.actionCardEditFragmentToCardListFragment())
        }
        binding.deleteButton.setOnClickListener { //denne knappen funker ikke ass
            lifecycleScope.launch {
                CardDatabase.getInstance(requireContext()).cardDao.deleteCard(card)
            }
            it.findNavController().navigate(R.id.action_cardEditFragment_to_cardListFragment)
        }
    }
}
