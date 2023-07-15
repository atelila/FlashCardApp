package es.uam.eps.dadm.cards

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks_table")
class Deck(
    @PrimaryKey val deckId: Long,
    val name: String)

