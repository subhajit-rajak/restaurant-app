package com.gloomdev.restaurantapp.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.gloomdev.restaurantapp.R
import com.gloomdev.restaurantapp.databinding.FragmentProfileBinding
import com.gloomdev.restaurantapp.ui.activities.MainActivity
import com.gloomdev.restaurantapp.ui.activities.RegisterActivity
import com.gloomdev.restaurantapp.ui.dataclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class Profile : Fragment() {

    private lateinit var database: DatabaseReference
    private var userId: String? = null
    private lateinit var binding: FragmentProfileBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var profileImageView: ImageView
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        profileImageView = binding.profileImage

        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        loadUserProfilePic()

        //Logout Button
        binding.logoutButton.setOnClickListener {
            signOut()
        }
        fetchUserData()

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
        binding.profileImage.setOnClickListener {
            chooseAndUploadImage()
        }
        return binding.root
    }

    // Load user's profile picture from Firestore
    private fun loadUserProfilePic() {
        userId?.let {
            firestore.collection("users").document(it).get()
                .addOnSuccessListener { documentSnapshot ->
                    val profileImageUrl = documentSnapshot.getString("profileImageUrl")
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Picasso.get().load(profileImageUrl).into(profileImageView)
                    }
                }
                .addOnFailureListener { exception ->
                    showToast("Error: ${exception.message}")
                }
        }
    }

    // Choose image from gallery
    fun chooseAndUploadImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Upload image to Firebase Storage and Firestore
    private fun uploadImageToFirebase(imageUri: Uri) {
        userId?.let { uid ->
            val fileReference = storageReference.child("profile_pics/$uid.jpg")

            fileReference.putFile(imageUri)
                .addOnSuccessListener {
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        saveImageUrlToFirestore(uid, uri.toString())
                    }
                }
                .addOnFailureListener {
                    showToast("Upload failed: ${it.message}")
                }
        }
    }

    private fun saveImageUrlToFirestore(userId: String, imageUrl: String) {
        val userMap = hashMapOf("profileImageUrl" to imageUrl)
        firestore.collection("users").document(userId).set(userMap)
            .addOnSuccessListener {
                showToast("Profile picture updated")
            }
            .addOnFailureListener {
                showToast("Error: ${it.message}")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            Picasso.get().load(imageUri).into(profileImageView)
            uploadImageToFirebase(imageUri)
        }
    }

    // Sign out from Firebase and clear user session
    private fun signOut() {
        // Sign out from FirebaseAuth
        mAuth.signOut()

        // Clear "Remember Me" data from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Clears all saved data including email, password, and rememberMe flag
        editor.apply()

        // Redirect to LoginActivity
        val intent = Intent(requireContext(), RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clears the backstack
        startActivity(intent)

        // Close the current activity to prevent returning after logout
        MainActivity().finish()
    }

    // Fetch user data from Firebase
    private fun fetchUserData() {
        userId?.let { uid ->
            database.child("Customers").child("customerDetails").child(uid)
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

    // Edit dialogs for updating user fields
    private fun showEditDialogName(field: String) {
        showEditDialog(field, "username", "Invalid Name", ::isValidName)
    }

    private fun showEditDialogEmail(field: String) {
        showEditDialog(field, "email", "Invalid Email Address", ::isValidEmail)
    }

    private fun showEditDialogNumber(field: String) {
        showEditDialog(field, "mobile", "Invalid Phone Number", ::isValidPhone)
    }

    // Generic dialog for editing fields
    private fun showEditDialog(field: String, fieldName: String, errorText: String, validation: (String) -> Boolean) {
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog, null)
        val editText = dialogView.findViewById<EditText>(R.id.editText)
        val submitButton = dialogView.findViewById<Button>(R.id.submitButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        val alertDialog = dialogBuilder.setView(dialogView).create()

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (!validation(text)) {
                    editText.error = errorText
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        submitButton.setOnClickListener {
            val newText = editText.text.toString().trim()
            updateFirebaseField(fieldName, newText)
            alertDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    // Validate email, phone, and name formats
    fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPhone(phone: String): Boolean = phone.length == 10 && phone.all { it.isDigit() }

    fun isValidName(name: String): Boolean {
        val nameRegex = "^[a-zA-Z\\s]+$"
        return name.matches(Regex(nameRegex)) && name.length in 1..30
    }

    // Update Firebase field with new data
    private fun updateFirebaseField(field: String, newText: String) {
        userId?.let {
            database.child("Customers").child("customerDetails").child(it).child(field)
                .setValue(newText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Updated $field successfully!")
                        fetchUserData()
                    } else {
                        showToast("Failed to update $field")
                    }
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}





