package com.fahm781.rigcraft

import android.os.Bundle
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
//import com.fahm781.rigcraft.ChatbotServices.BOT_MSG
import com.fahm781.rigcraft.ChatbotServices.ChatbotRepository
import com.fahm781.rigcraft.ChatbotServices.MY_MSG
//import com.fahm781.rigcraft.ChatbotServices.MY_MSG
import com.fahm781.rigcraft.ChatbotServices.Msg
import com.fahm781.rigcraft.ChatbotServices.MsgAdapter


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
     var chatbotRepository = ChatbotRepository()
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
        msgList =  ArrayList()

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
                welcomeText.visibility = View.GONE
                chatbotRepository.getResponse(query) { result ->
                    addMessage(result, BOT_MSG)
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a query", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun addMessage(message: String, sentBy: String) {
        activity?.runOnUiThread {
            val content = if (sentBy == BOT_MSG) {
                // Parse the message to extract the content
                message.substringAfter("content=").trimEnd(')')
            } else {
                message
            }
            msgList.add(Msg(sentBy, content))
            msgAdapter.notifyDataSetChanged()
            chatRecyclerView.smoothScrollToPosition(msgAdapter.itemCount - 1)
        }
    }

    }



