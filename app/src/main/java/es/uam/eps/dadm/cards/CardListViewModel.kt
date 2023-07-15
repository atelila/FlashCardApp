package es.uam.eps.dadm.cards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import es.uam.eps.dadm.cards.database.CardDatabase

class CardListViewModel(application: Application): AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    fun getCards(userId:String) =  CardDatabase.getInstance(context).cardDao.getCards(userId)
}

