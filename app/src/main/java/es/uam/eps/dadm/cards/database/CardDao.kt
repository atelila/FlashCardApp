package es.uam.eps.dadm.cards.database

import androidx.lifecycle.LiveData
import androidx.room.*
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.Deck
import es.uam.eps.dadm.cards.DeckWithCards
import es.uam.eps.dadm.cards.User

@Dao
interface CardDao {
    @Query("SELECT * FROM cards_table where user_id=:userId")
    fun getCards(userId:String): LiveData<List<Card>>

    @Query("SELECT * FROM cards_table WHERE id = :id")
    fun getCard(id: String): LiveData<Card?>

    @Insert
    fun addCard(card: Card)

    @Delete
    suspend fun deleteCard(card: Card?)

    @Query("SELECT * FROM cards_table")
    suspend fun getAllCards(): List<Card>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<Card>)

    @Query("DELETE FROM cards_table")
    suspend fun deleteAllCards()

    @Update
    fun updateCard(card: Card)

    @Update
    suspend fun updateCardsTable(cards: List<Card>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDeck(deck: Deck)

    @Transaction
    @Query("SELECT * FROM decks_table")
    fun getDecksWithCards(): LiveData<List<DeckWithCards>>

    @Transaction
    @Query("SELECT * FROM decks_table WHERE deckId = :deckId")
    fun getDeckWithCards(deckId: Long): LiveData<List<DeckWithCards>>

    //users
    @Query("SELECT * FROM users_table WHERE username = :username")
    fun getUserByUsername(username: String): LiveData<User?>


    @Query("SELECT * FROM users_table WHERE username = :username AND password = :pass LIMIT 1")
    fun getUser(username: String,pass:String): User?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addUser(user: User)

    @Update
    fun updateUser(user: User?)

    @Delete
    fun deleteUser(user: User?)}





