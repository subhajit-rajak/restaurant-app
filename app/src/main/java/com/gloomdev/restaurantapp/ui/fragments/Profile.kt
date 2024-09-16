package com.gloomdev.restaurantapp.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.databinding.FragmentProfileBinding
import com.gloomdev.restaurantapp.ui.activities.RegisterActivity
import com.gloomdev.restaurantapp.ui.dataclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class Profile : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference
    private var userId: String? = null
    private lateinit var binding: FragmentProfileBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var profileImageView: ImageView
    private lateinit var uploadButton: ImageView
    private val PICK_IMAGE_REQUEST = 1
   // private lateinit var uid: String

    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore

    private lateinit var progressBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        profileImageView = binding.profileImage
        uploadButton = binding.editProfileImage

        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "-")
        database = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()
        progressBar = binding.progressBar
       // uid = mAuth.currentUser?.uid.toString()
        loadUserProfilePic()
        //Logout Button
        binding.logoutButton.setOnClickListener {
            signOutAndClearPreferences()
        }
        fetchUserData(binding.root)

        //Edit Button for Name, Email & Phone
        binding.editName.setOnClickListener {
            showEditDialogName("username")
        }
        binding.editEmail.setOnClickListener {
            showEditDialogEmail("email")
        }
        binding.editPhoneNumber.setOnClickListener {
            showEditDialogNumber("number")
        }

        // Handle upload button click (upload the image)
        binding.editProfileImage.setOnClickListener {
            chooseAndUploadImage()
        }
        return binding.root
    }
    //
//    Functions
//     Function to load the user's profile picture from Firestore
    private fun loadUserProfilePic() {
        val currentUser = userId
        if (currentUser != null ) {
            setInProgress(true)
            val userId = currentUser // Get current logged-in user's ID
            // Fetch the user document from Firestore
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Get the profile image URL from the Firestore document
                        val profileImageUrl = documentSnapshot.getString("profileImageUrl")

                        if (!profileImageUrl.isNullOrEmpty()) {
                            // Load the image using Picasso
                            Picasso.get().load(profileImageUrl).into(profileImageView)
                        } else {
                            //showToast("No profile image found")
                        }
                    } else {
                        //showToast("User data not found")
                    }
                    setInProgress(false)
                }
                .addOnFailureListener { exception ->
                    showToast("\"Error: ${exception.message}\",")
                }
        } else {
            //showToast("User not logged in")
        }

    }
    fun chooseAndUploadImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    private fun uploadImageToFirebase(imageUri: Uri) {
        val currentUser = userId
        if (currentUser != null) {
            val userId = currentUser // Get the logged-in user's ID
            val fileReference = storageReference.child("profile_pics/$userId.jpg")  // Save image with user ID

            fileReference.putFile(imageUri)
                .addOnSuccessListener {
                    // Get the download URL and save it to Firestore
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        saveImageUrlToFirestore(userId, uri.toString())
                    }
                }
                .addOnFailureListener {
                    showToast("Upload failed: ${it.message}")
                }
        } else {
            showToast("User not logged in")
        }
    }
    // Upload the image to Firebase Storage and save its URL to Firestore
    private fun saveImageUrlToFirestore(userId: String, imageUrl: String) {
        val userMap = hashMapOf("profileImageUrl" to imageUrl)
        firestore.collection("users").document(userId).set(userMap)
            .addOnSuccessListener {
                showToast("Profile picture updated")
            }
            .addOnFailureListener {
                showToast("Error: ${it.message}")
            }
        setInProgress(false)
    }
    // Handle result of image picker and upload
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            Picasso.get().load(imageUri).into(profileImageView)  // Preview selected image
            // Upload the image to Firebase Storage
            uploadImageToFirebase(imageUri)
        }
    }

    private fun signOutAndClearPreferences() {
        // Clear shared preferences
        val sharedPreferences =
            requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Sign out from Firebase
        mAuth.signOut()

        // Navigate to RegisterActivity
        val intent = Intent(requireContext(), RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Finish the current activity
        requireActivity().finish()
    }
    private fun fetchUserData(root: View) {
        userId?.let { id ->
            database.child("Customers").child("customerDetails").child(id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val user = snapshot.getValue(User::class.java)
                            user?.let {
                                binding.name.text = it.username
                                binding.email.text = it.email
                                binding.number.text = it.mobile
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showToast("Failed to fetch user data: ${error.message}")
                    }
                })
        }
    }


    private fun showEditDialogName(field: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog, null)
        val editText = dialogView.findViewById<EditText>(R.id.editText)
        val submitButton = dialogView.findViewById<Button>(R.id.submitButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        val alertDialog = dialogBuilder.setView(dialogView).create()
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val name = s.toString()
                if (!isValidName(name)) {
                    editText.error = "Invalid Name"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        submitButton.setOnClickListener {
            val newText = editText.text.toString().trim()
            updateField(field, newText)
            alertDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
    private fun showEditDialogEmail(field: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog, null)
        val editText = dialogView.findViewById<EditText>(R.id.editText)
        val submitButton = dialogView.findViewById<Button>(R.id.submitButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        val alertDialog = dialogBuilder.setView(dialogView).create()
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (!isValidEmail(email)) {
                    editText.error = "Invalid Email Address"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        submitButton.setOnClickListener {
            val newText = editText.text.toString().trim()
            updateField(field, newText)
            alertDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
    private fun showEditDialogNumber(field: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog, null)
        val editText = dialogView.findViewById<EditText>(R.id.editText)
        val submitButton = dialogView.findViewById<Button>(R.id.submitButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        val alertDialog = dialogBuilder.setView(dialogView).create()

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val phone = s.toString()
                if (!isValidPhone(phone)) {
                    editText.error = "Invalid Phone Number"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        submitButton.setOnClickListener {
            val newText = editText.text.toString().trim()
            updateField(field, newText)
            alertDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
    private fun updateField(field: String, newText: String) {
        when (field) {
            "username" -> {
                val emailTextView: TextView? = view?.findViewById(R.id.name)
                // Check if the new email is already registered with another phone number
                checkNameExist(newText) { exists ->
                    if (!exists) {
                        updateFirebaseField("username", newText)
                    } else {
                        showToast("Name already registered with another phone number!")
                        emailTextView?.text = sharedPreferences.getString("username", "")
                    }
                }
            }
            "number" -> {
                val phoneTextView: TextView? = view?.findViewById(R.id.number)
                // Check if the new phone number is already registered with another email
                checkPhoneNumberExists(newText) { exists ->
                    if (!exists) {
                        updateFirebaseField("mobile", newText)
                    } else {
                        showToast("Phone number already registered with another email!")
                        // Revert UI change if validation fails
                        phoneTextView?.text = sharedPreferences.getString("mobile", "")
                    }
                }
            }

            "email" -> {
                val emailTextView: TextView? = view?.findViewById(R.id.email)
                // Check if the new email is already registered with another phone number
                checkEmailExists(newText) { exists ->
                    if (!exists) {
                        updateFirebaseField("email", newText)
                    } else {
                        showToast("Email already registered with another phone number!")
                        emailTextView?.text = sharedPreferences.getString("email", "")

                    }
                }
            }
        }
    }
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun isValidPhone(phone: String): Boolean {
        return phone.length == 10 && phone.all { it.isDigit() }
    }
    fun isValidName(name: String): Boolean {
        val nameRegex = "^[a-zA-Z\\s]+$"
        return name.matches(Regex(nameRegex)) && name.length in 1..30
    }

    private fun checkNameExist(name: String, callback: (Boolean) -> Unit) {
        database.child("Customers").child("customerDetails")
            .orderByChild("username").equalTo(name)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Name checking email: ${error.message}")
                    callback(false)
                }
            })
    }

    private fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        database.child("Customers").child("customerDetails")
            .orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Error checking email: ${error.message}")
                    callback(false)
                }
            })
    }

    private fun checkPhoneNumberExists(phoneNumber: String, callback: (Boolean) -> Unit) {
        database.child("Customers").child("customerDetails")
            .orderByChild("mobile").equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Error checking phone number: ${error.message}")
                    callback(false)
                }
            })
    }


    private fun updateFirebaseField(field: String, newText: String) {
        userId?.let {
            database.child("Customers").child("customerDetails").child(it).child(field)
                .setValue(newText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        when (field) {
                            "username" -> view?.findViewById<TextView>(R.id.name)?.text = newText
                            "phone" -> view?.findViewById<TextView>(R.id.number)?.text =
                                newText

                            "email" -> view?.findViewById<TextView>(R.id.email)?.text = newText
                        }

                        fetchUserData(requireView())

                        sharedPreferences.edit().apply {
                            when (field) {
                                "username" -> putString("username", newText)
                                "phone" -> putString("mobile", newText)
                                "email" -> putString("email", newText)
                            }
                            apply()
                        }
                        showToast("Updated $field in Database successfully!")
                    } else {
                        showToast("Failed to update $field in Database")
                    }
                }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar.visibility = View.VISIBLE

        } else {
            progressBar.visibility = View.GONE
        }
    }
}




