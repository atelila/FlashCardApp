package es.uam.eps.dadm.cards.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.uam.eps.dadm.cards.Card

class CardListFirebaseViewModel : ViewModel() {

    private val _cards = MutableLiveData<List<Card>>()
    val cards : LiveData<List<Card>>
        get() = _cards
    private var reference = FirebaseDatabase.getInstance().getReference("cards")

    init {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfCards = mutableListOf<Card>()

                for (child in snapshot.children) {
                    child.getValue(Card::class.java)?.let {
                        listOfCards.add(it)
                    }
                }

                _cards.value = listOfCards
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}