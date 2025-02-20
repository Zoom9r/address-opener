package com.example.addressopener

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        // Створюємо адаптер для ViewPager2
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2 // Дві вкладки
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> MainFragment()
                    1 -> AOFragment()
                    else -> throw IllegalStateException("Unexpected position $position")
                }
            }
        }

        viewPager.adapter = adapter

        // З'єднуємо TabLayout з ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Основна"
                1 -> "АО"
                else -> null
            }
        }.attach()
    }
}
