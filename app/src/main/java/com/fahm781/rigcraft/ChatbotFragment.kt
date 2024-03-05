package com.fahm781.rigcraft

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fahm781.rigcraft.ChatbotServices.BOT_MSG
import com.fahm781.rigcraft.ChatbotServices.ChatbotRepository
import com.fahm781.rigcraft.ChatbotServices.MY_MSG
import com.google.firebase.firestore.FirebaseFirestore
import com.fahm781.rigcraft.ChatbotServices.Msg
import com.fahm781.rigcraft.ChatbotServices.MsgAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query


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
    private lateinit var msgList: ArrayList<Msg>
    private lateinit var msgAdapter: MsgAdapter
    private var chatbotRepository = ChatbotRepository()
    private val db = FirebaseFirestore.getInstance()

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
        msgList = ArrayList()

        //setup recycler view
        msgAdapter = MsgAdapter(msgList)
        chatRecyclerView.adapter = msgAdapter
        LinearLayoutManager(requireContext()).also { linearLayoutManager ->
            linearLayoutManager.stackFromEnd = true
            chatRecyclerView.layoutManager = linearLayoutManager
        }

        buttonSend.setOnClickListener {
            val query = editTextMessage.text.toString().trim()
            if (query.isNotEmpty()) {
                addMessage(query, MY_MSG)
                editTextMessage.setText("")
                val prompt = "Answer queries only related to PC building and such. Otherwise, say 'I can only answer queries related to PC building'"

                welcomeText.visibility = View.GONE
                chatbotRepository.getResponse(query, prompt) { result ->
                    addMessage(result, BOT_MSG)
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a query", Toast.LENGTH_SHORT).show()
            }
        }
      loadMessagesFromFirestore()
        return view
    }

    //add message to the recycler view/MsgAdapter
    private fun addMessage(message: String, sentBy: String) {
        activity?.runOnUiThread {
            saveMessageToFirestore(sentBy, message)
            msgList.add(Msg(sentBy, message))
            msgAdapter.notifyDataSetChanged()
            chatRecyclerView.smoothScrollToPosition(msgAdapter.itemCount - 1)
        }
    }
    //this method saves the messages to the firestore database
    private fun saveMessageToFirestore(sentBy: String, message: String) {

        val msg = hashMapOf(
            "sentBy" to sentBy,
            "message" to message,
            "User UID" to FirebaseAuth.getInstance().currentUser?.uid,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("ChatbotMessages")
            .add(msg)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }

    //this method will load the previous messages (if any) from the firestore database
    private fun loadMessagesFromFirestore() {
        //need to check first if there are any ChatbotMessages in the database
        db.collection("ChatbotMessages")
            .whereEqualTo("User UID", FirebaseAuth.getInstance().currentUser?.uid)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limit(150) //can change later to 90
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    return@addOnSuccessListener // if no documents are found return from the method
                }
                for (document in documents) {
                    val sentBy = document.getString("sentBy") ?: ""
                    val message = document.getString("message") ?: ""
                    msgList.add(Msg(sentBy, message))
                }
                // If the total number of messages exceeds 90, delete the oldest messages (uncomment the following loop if any problems)
                val excess = documents.size() - 90
                if (excess > 0) {
                    // If the total number of messages exceeds 90, delete the oldest messages
                    val messagesToDelete = documents.documents.take(excess)
                    for (message in messagesToDelete) {
                        db.collection("ChatbotMessages").document(message.id).delete()
                    }
                }
                welcomeText.visibility = View.GONE
                msgAdapter.notifyDataSetChanged()
                chatRecyclerView.smoothScrollToPosition(msgAdapter.itemCount - 1)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }


    }



