package com.fahm781.rigcraft

import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.fahm781.rigcraft.chatbotServices.ChatbotRepository
import com.fahm781.rigcraft.ebayServices.Image
import com.fahm781.rigcraft.ebayServices.ItemSummary
import com.fahm781.rigcraft.ebayServices.Price
import com.fahm781.rigcraft.partPickerPackage.ItemSummaryAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        FirebaseApp.initializeApp(context)
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testUnrelatedQuestion() {
        val latch = CountDownLatch(1)
        val chatbotRepo = ChatbotRepository()
        val prompt =
            "Answer queries only related to PC building and such. Otherwise, say 'I can only answer queries related to PC building'"
        val msg = "What is the capital of France?"
        var actualResponse: String? = null
        chatbotRepo.getResponse(msg, prompt) {
            actualResponse = it
            latch.countDown()
        }
        latch.await(5, TimeUnit.SECONDS)
        assertEquals(
            "I can only answer queries related to PC building.",
            actualResponse
        ) // this line will be executed after getResponse has completed
    }

    @Test
    fun testSpecialCharacterQuery() {
        val latch = CountDownLatch(1)
        val chatbotRepo = ChatbotRepository()
        val prompt =
            "Answer queries only related to PC building and such. Otherwise, say 'I can only answer queries related to PC building'"
        val msg = "@#$%^&*()"
        var actualResponse: String? = null
        chatbotRepo.getResponse(msg, prompt) {
            actualResponse = it
            latch.countDown()
        }
        latch.await(5, TimeUnit.SECONDS)
        assertEquals("I can only answer queries related to PC building", actualResponse)
    }

    @Test
    fun onCreateViewHolder_returnsNonNullViewHolder() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val parent = FrameLayout(context)
        val viewType = 0
        val itemSummary = ItemSummary(
            itemId = "testItemId",
            title = "Test Title",
            price = Price(value = "100.0", currency = "USD"),
            image = Image(imageUrl = "http://test.com"),
            itemWebUrl = "http://test.com"
        )
        val itemSummaries = listOf(itemSummary)
        val productType = "testProductType"
        val adapter = ItemSummaryAdapter(itemSummaries, productType)

        val viewHolder = adapter.onCreateViewHolder(parent, viewType)
        adapter.onBindViewHolder(viewHolder, 0)

        assertNotNull(viewHolder.itemView.findViewById<TextView>(R.id.titleTextView))
        assertNotNull(viewHolder.itemView.findViewById<TextView>(R.id.priceTextView))
        assertNotNull(viewHolder.itemView.findViewById<ImageView>(R.id.imageView))
        assertNotNull(viewHolder.itemView.findViewById<Button>(R.id.itemWebUrlButton))
        assertNotNull(viewHolder.itemView.findViewById<Button>(R.id.moreDetailsButton))
        assertNotNull(viewHolder.itemView.findViewById<Button>(R.id.addToBuildButton))
    }

    @Test
    fun getItemCount_returnsCorrectSize() {
        val itemSummaries =
            listOf<ItemSummary>(mock(ItemSummary::class.java), mock(ItemSummary::class.java))
        val productType = "testProductType"
        val adapter = ItemSummaryAdapter(itemSummaries, productType)

        val itemCount = adapter.itemCount

        assertEquals(2, itemCount)
    }



}