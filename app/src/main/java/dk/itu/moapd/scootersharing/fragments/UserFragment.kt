package dk.itu.moapd.scootersharing.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.BUCKET_URL
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.TextValidator
import dk.itu.moapd.scootersharing.databinding.FragmentUserProfileBinding

class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var storage: FirebaseStorage

    private lateinit var nameValidator: TextValidator

    /**
     * A component to validate the user's email.
     */
    private lateinit var emailValidator: TextValidator


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        storage = Firebase.storage(BUCKET_URL)

        with(binding) {

            nameValidator = TextValidator(username)
            username.addTextChangedListener(nameValidator)

            emailValidator = TextValidator(userEmail)
            userEmail.addTextChangedListener(emailValidator)

            if (!nameValidator.isValidName) {
                username.error = getString(R.string.error)
            }

            if (!emailValidator.isValidEmail) {
                userEmail.error = getString(R.string.error)
            }

            username.text = auth.currentUser?.displayName
            userEmail.text = auth.currentUser?.email
        }

        return binding.root
    }


}