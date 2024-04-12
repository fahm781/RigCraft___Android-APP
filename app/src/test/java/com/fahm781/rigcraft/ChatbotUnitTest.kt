package com.fahm781.rigcraft

import android.util.Log
import com.fahm781.rigcraft.chatbotServices.*
import org.junit.Test
import org.junit.Assert.assertEquals

class ChatbotUnitTest {

    //test if the chatbot can respond to a valid question
    @Test
    fun testUnrelatedQuestion() {
        val chatbotRepo = ChatbotRepository()
        val prompt = "Answer queries only related to PC building and such. Otherwise, say 'I can only answer queries related to PC building'"
        val msg = "What is the capital of France?"
        chatbotRepo.getResponse(msg, prompt) {
            assertEquals(it, "I can only answer queries related to PC building") // the chatbot should respond with this message
            println(it)
        }
    }
}