package com.fahm781.rigcraft

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView;
    var partPickerFragment: PartPickerFragment = PartPickerFragment();
    var buildGuideFragment: BuildGuideFragment = BuildGuideFragment();
    var chatbotFragment: ChatbotFragment = ChatbotFragment();
    var accountFragment: AccountFragment = AccountFragment();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        //when app opens the selected tab should be the part picker
    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, partPickerFragment).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.partpicker -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, partPickerFragment).commit();
                    true
                }
                R.id.buildguide -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, buildGuideFragment).commit();
                    true
                }
                R.id.chatbot -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, chatbotFragment).commit();
                    true
                }
                R.id.useraccount -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, accountFragment).commit();
                    true
                }
                else -> false
            }
        }
    }
}