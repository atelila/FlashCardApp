package es.uam.eps.dadm.cards
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.activities.SettingsActivity
import es.uam.eps.dadm.cards.activities.TitleActivity
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.FragmentCardListBinding
import java.util.concurrent.Executors

class CardListFragment: Fragment() {
    private lateinit var adapter: CardAdapter
    private val executor = Executors.newSingleThreadExecutor()
    private val cardListViewModel by lazy {
        ViewModelProvider(this).get(CardListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentCardListBinding>(
            inflater,
            R.layout.fragment_card_list,
            container,
            false
        )

        adapter = CardAdapter()

        binding.cardListRecyclerView.adapter = adapter

        binding.cardListStudyButton?.setOnClickListener { view ->
            if (CardsApplication.numberOfDueCards() > 0)
                view.findNavController()
                    .navigate(R.id.action_cardListFragment_to_studyFragment)
            else
                Toast.makeText(
                    requireActivity(),
                    R.string.no_more_cards_toast_message,
                    Toast.LENGTH_LONG
                ).show()
        }

        binding.newCardFab.setOnClickListener {
            val card = Card("","",Firebase.auth.currentUser!!.uid)
            executor.execute {
                CardDatabase.getInstance(requireContext()).cardDao.addCard(card)
            }
            it.findNavController().navigate(CardListFragmentDirections.actionCardListFragmentToCardEditFragment(card.id))
        }

        cardListViewModel.getCards(Firebase.auth.currentUser!!.uid).observe(viewLifecycleOwner) { cards ->
            val sortedCards = when {
                SettingsActivity.isSortedEasinessOrRepetitons(requireContext()) -> {
                    cards.sortedBy { it.easiness }
                }
                else -> cards
            }
            adapter.data = sortedCards
            adapter.notifyDataSetChanged()
        }

        SettingsActivity.setLoggedIn(requireContext(), true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_card_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
            }
            R.id.log_out -> {
                Firebase.auth.signOut()
                findNavController().navigate(R.id.loginFragment,null,NavOptions.Builder().setPopUpTo(R.id.loginFragment,true).build())
//                startActivity(Intent(requireContext(), TitleActivity::class.java))
            }
        }
        return true
    }
}
