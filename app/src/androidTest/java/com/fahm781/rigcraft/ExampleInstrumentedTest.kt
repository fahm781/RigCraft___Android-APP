package com.fahm781.rigcraft

import android.util.Log
import android.view.ViewGroup
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fahm781.rigcraft.chatbotServices.ChatbotRepository
import com.fahm781.rigcraft.ebayServices.ItemSummary

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.fahm781.rigcraft", appContext.packageName)
    }
    @Test
    fun testUnrelatedQuestion() {
        val latch = CountDownLatch(1)
        val chatbotRepo = ChatbotRepository()
        val prompt = "Answer queries only related to PC building and such. Otherwise, say 'I can only answer queries related to PC building'"
        val msg = "What is the capital of France?"
        var actualResponse: String? = null
        chatbotRepo.getResponse(msg, prompt) {
            actualResponse = it
            latch.countDown()
        }
        latch.await(5, TimeUnit.SECONDS)
        assertEquals("I can only answer queries related to PC building.", actualResponse) // this line will be executed after getResponse has completed
    }

    @Test
    fun testSpecialCharacterQuery() {
        val latch = CountDownLatch(1)
        val chatbotRepo = ChatbotRepository()
        val prompt = "Answer queries only related to PC building and such. Otherwise, say 'I can only answer queries related to PC building'"
        val msg = "@#$%^&*()"
        var actualResponse: String? = null
        chatbotRepo.getResponse(msg, prompt) {
            actualResponse = it
            latch.countDown()
        }
        latch.await(5, TimeUnit.SECONDS)
        assertEquals("I can only answer queries related to PC building", actualResponse)
    }

//    @Test
//    fun onCreateViewHolder_returnsNonNullViewHolder() {
//        val parent = mock(ViewGroup::class.java)
//        val viewType = 0
//        val itemSummaries = listOf<ItemSummary>()
//        val productType = "testProductType"
//        val adapter = ItemSummaryAdapter(itemSummaries, productType)
//
//        val viewHolder = adapter.onCreateViewHolder(parent, viewType)
//
//        assertNotNull(viewHolder)
//        assertNotNull(viewHolder.titleTextView)
//        assertNotNull(viewHolder.priceTextView)
//        assertNotNull(viewHolder.imageView)
//        assertNotNull(viewHolder.itemWebUrlButton)
//        assertNotNull(viewHolder.moreDetailsButton)
//        assertNotNull(viewHolder.addToBuildButton)
//    }
//
//    @Test
//    fun getItemCount_returnsCorrectSize() {
//        val itemSummaries = listOf<ItemSummary>(Mockito.mock(ItemSummary::class.java), mock(ItemSummary::class.java))
//        val productType = "testProductType"
//        val adapter = ItemSummaryAdapter(itemSummaries, productType)
//
//        val itemCount = adapter.itemCount
//
//        assertEquals(2, itemCount)
//    }
}