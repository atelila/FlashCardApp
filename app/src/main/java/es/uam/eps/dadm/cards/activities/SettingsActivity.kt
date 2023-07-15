package es.uam.eps.dadm.cards.activities

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.CardAdapter
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.database.CardDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class SettingsActivity : AppCompatActivity() {
    private lateinit var uploadButton: Button
    private lateinit var syncButton: Button
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        adapter = CardAdapter()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, true)

        // UPLOAD BUTTON  (vurdere aa kun bruke samme parameterverdier som den under)
        uploadButton = findViewById(R.id.uploadButton)
        uploadButton.setOnClickListener {
            uploadToFirebase(view = it, context = applicationContext, user_id = Firebase.auth.currentUser!!.uid, lifecycleOwner = this)
        }
        // SYNC BUTTON (ikke ferdig)
        syncButton = findViewById(R.id.syncButton)
        syncButton.setOnClickListener {
            syncFromFirebase(view = it, applicationContext, user_id = Firebase.auth.currentUser!!.uid, lifecycleOwner = this)
        }

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    companion object {
        const val MAX_NUMBER_CARDS_KEY = "max_number_cards"
        const val MAX_NUMBER_CARDS_DEFAULT = "20"
        const val LOGGED_IN_KEY = "logged_in_key"
        const val BOARD_KEY = "board_preference"
        const val BOARD_DEFAULT = false
        const val EASINESS_OR_REPETITION_KEY = "sort_by_easiness_or_repetitions"
        const val EASINESS_OR_REPETITION_DEFAULT = false
        private lateinit var adapter: CardAdapter

        fun getMaximumNumberOfCards(context: Context): String? {
            return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(MAX_NUMBER_CARDS_KEY, MAX_NUMBER_CARDS_DEFAULT)
        }

        fun setLoggedIn(context: Context, loggedin: Boolean) {
            val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putBoolean(LOGGED_IN_KEY, loggedin)
            editor.apply()
        }

        fun isBoardAvailable(context: Context): Boolean {
            return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(BOARD_KEY, BOARD_DEFAULT)
        }

        fun isSortedEasinessOrRepetitons(context: Context): Boolean {
            return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(EASINESS_OR_REPETITION_KEY, EASINESS_OR_REPETITION_DEFAULT)
        }

        private fun uploadToFirebase(view: View, context: Context,user_id:String, lifecycleOwner: LifecycleOwner) {
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val reference = database.getReference("cards").child(user_id)
            val cardDao = CardDatabase.getInstance(context).cardDao
            cardDao.getCards(user_id).observe(lifecycleOwner) { cards ->
                reference.setValue(cards).addOnSuccessListener {
                    Snackbar.make(view, "Upload successful", Snackbar.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Snackbar.make(view, "Upload failed", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        private fun syncFromFirebase(view: View, context: Context, user_id: String, lifecycleOwner: LifecycleOwner) {
            val executor = Executors.newSingleThreadExecutor()
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val cardDao = CardDatabase.getInstance(context).cardDao
            val reference = database.getReference("cards").child(user_id)
            adapter = CardAdapter()

            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                val cards = mutableListOf<Card>()

                override fun onDataChange(snapshot: DataSnapshot) {
                    cards.clear()
                    snapshot.children.forEach {
                        it.getValue(Card::class.java)?.let { card ->
                            cards.add(card)
                        }
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        cardDao.deleteAllCards()
                        cardDao.insertCards(cards)
                    }

                    adapter.notifyDataSetChanged()
                    Toast.makeText(context, "Download successful", Toast.LENGTH_LONG).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Download canceled", Toast.LENGTH_LONG).show()
                }
            })
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }



}


