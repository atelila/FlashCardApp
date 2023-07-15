package es.uam.eps.dadm.cards
// ELISE
import android.app.Application
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.Card
import timber.log.Timber
import java.time.LocalDateTime
import java.util.concurrent.Executors

class CardsApplication : Application() {
    private val executor = Executors.newSingleThreadExecutor()

    init {
        cards.add(Card("To wake up", "Despertarse","123"))
        cards.add(Card("To rule out", "Descartar","123"))
        cards.add(Card("To turn down", "Rechazar","123"))
    }

    override fun onCreate() {
        super.onCreate()
        val cardDatabase = CardDatabase.getInstance(applicationContext)
        /*executor.execute {
            cardDatabase.cardDao.addDeck(Deck(deckId = 1, "English"))
            cardDatabase.cardDao.addDeck(Deck(deckId = 2, "Frech"))

            cardDatabase.cardDao.addCard(
                Card("To wake up", "Despertarse", deckId = 1)
            )
            cardDatabase.cardDao.addCard(
                Card("To rule out", "Descartar", deckId = 1)
            )
            cardDatabase.cardDao.addCard(
                Card("To turn down", "Rechazar", deckId = 1)
            )
            cardDatabase.cardDao.addCard(
                Card("La voiture", "El coche", deckId = 2)
            )
            cardDatabase.cardDao.addCard(
                Card("J'ai faim", "Tengo hambre", deckId = 2)
            )
        }*/
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        var cards: MutableList<Card> = mutableListOf()
        fun numberOfDueCards() = cards.filter { it.isDue(LocalDateTime.now()) }.size
        fun getCard(id: String) = cards.find { it.id == id }
        fun addCard(card: Card) {
            cards.add(card)
        }

        fun deleteCard(id: String) {
            cards.remove(getCard(id))
        }
    }
}

