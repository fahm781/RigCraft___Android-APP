package com.fahm781.rigcraft

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    private lateinit var logoutButton: Button
    private lateinit var changePwdButton: Button
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmNewPassword: EditText
    private lateinit var userEmailTextView: TextView
    private lateinit var feedbackSubButton: Button
    private lateinit var feedbackMessage: EditText
    private lateinit var feedbackTitle: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Logging out User
        logoutButton = view.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }


        changePwdButton = view.findViewById(R.id.changePwdButton)
        newPassword = view.findViewById(R.id.newPassword)
        oldPassword = view.findViewById(R.id.oldPassword)
        confirmNewPassword = view.findViewById(R.id.confirmNewPassword)

        changePwdButton.setOnClickListener{
            changePassword()
        }

        // Get the TextView by its ID
        userEmailTextView = view.findViewById(R.id.userEmail)

        // Get the current user from FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Set the user's email to the TextView, if the user is not null
        currentUser?.let {
            userEmailTextView.text = "${it.email}"
        } ?: run {
            userEmailTextView.text = "User: not logged in"
        }

        //Submitting User Feedback
        feedbackSubButton = view.findViewById(R.id.feedbackSubButton)
        feedbackSubButton.setOnClickListener{
            sendFeedback(view)
        }
        return view
    }

    private fun sendFeedback(view: View){
        feedbackMessage = view.findViewById(R.id.feedbackMessage)
        feedbackTitle = view.findViewById(R.id.feedbackTitle)
        val message = feedbackMessage.text.toString()
        val title = feedbackTitle.text.toString()
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email.toString()

        if (message.isEmpty() || title.isEmpty()) {
            Toast.makeText(context, "Enter Both Fields", Toast.LENGTH_SHORT).show()
            return
        }

//        val emailIntent = Intent(Intent.ACTION_SEND).apply {
//            type = "message/rfc822"
//            putExtra(Intent.EXTRA_EMAIL, arrayOf("farazahmed9910@gmail.com")) // Receiver's email address
//            putExtra(Intent.EXTRA_SUBJECT, "RigCraft Feedback (User Email: $email)\n$title") // Subject of the email
//            putExtra(Intent.EXTRA_TEXT, message) // Body of the email
//        }
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, arrayOf("farazahmed9910@gmail.com")) // Receiver's email address
            putExtra(Intent.EXTRA_SUBJECT, "RigCraft Feedback (User Email: $email)\n$title") // Subject of the email
            putExtra(Intent.EXTRA_TEXT, message) // Body of the email
        }
        try {
            startActivity(Intent.createChooser(emailIntent, "Send feedback..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, "No email client installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changePassword() {
            val user = FirebaseAuth.getInstance().currentUser
            val oldPwd = oldPassword.text.toString()
            val newPwd = newPassword.text.toString()
            val confirmPwd = confirmNewPassword.text.toString()

            if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                Toast.makeText(
                    context,
                    "Make sure all Password fields are filled",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            if (newPwd != confirmPwd) {
                Toast.makeText(
                    context,
                    "New password and confirm password do not match.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            // Password should be at least 8 characters long
            if (newPwd.length < 8) {
                Toast.makeText(
                    context,
                    "Password should be at least 8 characters long.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            if (user != null) {
                // Reauthenticate the user
                val credential = EmailAuthProvider.getCredential(user.email!!, oldPwd)
                user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // Proceed with password update
                        user.updatePassword(newPwd).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Password updated successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Clear the fields
                                oldPassword.text.clear()
                                newPassword.text.clear()
                                confirmNewPassword.text.clear()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to update password: ${updateTask.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Re-authentication failed: ${reauthTask.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
