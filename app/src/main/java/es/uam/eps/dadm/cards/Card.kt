package es.uam.eps.dadm.cards
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*
import kotlin.math.max
import kotlin.math.roundToLong

@Entity(tableName = "cards_table")
open class Card() {
    @ColumnInfo(name = "card_question")
    var question: String = ""
    var answer: String = ""
    var user_id: String = ""
    var date: String = LocalDateTime.now().toString()
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var deckId: Long = 0
    var answered = false
    var quality = 0
    var repetitions = 0
    var interval = 1L
    var nextPracticeDate = date
    var easiness = 2.5

    constructor(
        question: String,
        answer: String,
        user_id: String,
        date: String = LocalDateTime.now().toString(),
        id: String = UUID.randomUUID().toString(),
        deckId: Long = 0
    ) : this() {
        this.question = question
        this.answer = answer
        this.user_id = user_id
        this.date = date
        this.id = id
        this.deckId = deckId
    }




    fun show() {
        print("  $question (INTRO para ver respuesta) ")
        readLine()
        print("  $answer (Teclea 0 -> Difícil 3 -> Dudo 5 -> Fácil): ")
        quality = readLine()?.toInt() ?: throw InputMismatchException()
    }

    fun update(currentDate: LocalDateTime) {
        val value: Double = easiness + 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)
        easiness = max(1.3, value)

        if (quality < 3)
            repetitions = 0
        else
            repetitions += 1

        interval = if (repetitions <= 1) 1L
        else if (repetitions == 2) 6L
        else (easiness * interval).roundToLong()

        nextPracticeDate = currentDate.plusDays(interval).toString()
    }

    fun details() {
        print("  eas = ${"%.2f".format(easiness)} ")
        print("rep = $repetitions int = $interval ")
        println("next = ${nextPracticeDate.substring(0, 10)}")
    }

    fun step(date: LocalDateTime) {
        show()
        update(date)
        details()
    }

    fun isDue(date: LocalDateTime): Boolean {
        val next = LocalDateTime.parse(nextPracticeDate)
        val result = date.compareTo(next)
        return result >= 0
    }

    fun simulate(period: Long) {
        println("Simulación de la tarjeta $question:")
        var now = LocalDateTime.now()

        for (i in 0..period) {
            println("Fecha: ${LocalDateTime.now().plusDays((i)).toString().substring(0, 10)}")
            if (isDue(now)) {
                step(now)
            }
            now = now.plusDays(1)
        }
    }
}

fun main() {
    val card1 = Card("To wake up", "Levantarse","123")
    card1.simulate(10)
}

