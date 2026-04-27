package com.team4.cardcase2.foreground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.team4.cardcase2.R

class MainWindowFragment : AppCompatActivity() {

    val frasgmentList= mutableListOf<Fragment>(
        WalletFragment()
    )
    private val titleList= mutableListOf<String>("Wallet")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main_window)

        val adapter=DataAdapter(this, frasgmentList, titleList)
        val viewPager2: ViewPager2 =findViewById(R.id.viewPager2)
        viewPager2.adapter=adapter
        val tabs: TabLayout =findViewById(R.id.tabLayout)

        TabLayoutMediator(tabs, viewPager2){
                tab, position->tab.text=adapter.getPageTitle(position)
            tab.setIcon(adapter.getPageIcon(position))
        }.attach()
    }
}