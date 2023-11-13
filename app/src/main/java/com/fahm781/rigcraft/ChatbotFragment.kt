package com.fahm781.rigcraft

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatbotFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatbotFragment : Fragment() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: ImageButton
    private lateinit var welcomeText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_chatbot, container, false)

        // Initialize the button and set the click listener
        buttonSend = view.findViewById(R.id.buttonSend)
        editTextMessage = view.findViewById(R.id.editTextMessage)
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        welcomeText = view.findViewById(R.id.welcomeText)

        buttonSend.setOnClickListener {
            val message = editTextMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessageToChatbot(message)
                //reset the text in the edit text
                //send a short toast message to the user with the message
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                editTextMessage.setText("")
            }
        }

        return view
    }

    private fun sendMessageToChatbot(message: String) {
        //  chatbot message sending logic
    }




}