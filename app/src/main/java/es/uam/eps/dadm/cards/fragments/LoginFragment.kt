package es.uam.eps.dadm.cards.fragments
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.databinding.FragmentLoginBinding
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment: Fragment() {
    lateinit var binding: FragmentLoginBinding
    var username: String = ""
    var password: String = ""


    override fun onResume()
    {
        super.onResume()
        Firebase.auth.currentUser?.let {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_cardListFragment)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val userTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                username = s.toString().trim().lowercase()
            }
            override fun afterTextChanged(s: Editable?) {
                username = s.toString().trim().lowercase()
            }
        }
        val passwordTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                password = s.toString().trim().lowercase()
            }
            override fun afterTextChanged(s: Editable?) {
                password = s.toString().trim().lowercase()
            }
        }
        binding.usernameEditText.addTextChangedListener(userTextWatcher)
        binding.passwordEditText.addTextChangedListener(passwordTextWatcher)

        binding.apply {

            this.createUserButton.setOnClickListener {
                if (username.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(context, "Username cannot be empty. Try again", Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(context, "Please Wait", Toast.LENGTH_LONG).show()
                    Firebase.auth.createUserWithEmailAndPassword(username,password).addOnCompleteListener {

                        if (it.isSuccessful)
                        {
                            Toast.makeText(context, "you have created a new user!", Toast.LENGTH_LONG).show()
                            view?.findNavController()?.navigate(R.id.action_loginFragment_to_cardListFragment)
                        }
                        else{
                            Toast.makeText(context, it.exception!!.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            this.loginButton.setOnClickListener {
                Toast.makeText(context, "Please Wait", Toast.LENGTH_LONG).show()
                Firebase.auth.signInWithEmailAndPassword(username,password).addOnCompleteListener {

                    if (it.isSuccessful)
                    {
                        Toast.makeText(context, "Welcome $username", Toast.LENGTH_LONG).show()
                        view?.findNavController()?.navigate(R.id.action_loginFragment_to_cardListFragment)
                    }
                    else{
                        Toast.makeText(context, "Invalid username or password", Toast.LENGTH_LONG).show()
                    }
                }

            }

        }
    }
}