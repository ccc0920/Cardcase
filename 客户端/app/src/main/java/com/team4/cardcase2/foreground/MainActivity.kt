package com.team4.cardcase2.foreground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R

class MainActivity : AppCompatActivity() {

    private val fragmentList = mutableListOf<Fragment>(
        WalletFragment(),
        GroupFragment(),
        WrenchFragment(),
        NavFragment()
    )
    private val titleList = mutableListOf<String>("Wallet", "Group", "Wrench", "Settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main_window)

        val adapter = DataAdapter(this, fragmentList, titleList)
        val viewPager2: ViewPager2 = findViewById(R.id.viewPager2)
        viewPager2.adapter = adapter
        val tabs: TabLayout = findViewById(R.id.tabLayout)

        TabLayoutMediator(tabs, viewPager2) { tab, position ->
            tab.text = adapter.getPageTitle(position)
            tab.setIcon(adapter.getPageIcon(position))
        }.attach()

        val userId = AppSession.getUserId(this)
        val db = openOrCreateDatabase("sqlite.db", MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS SQLTable(uid INTEGER, cid INTEGER, gid TEXT)")
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM SQLTable WHERE uid = ? AND gid = ?",
            arrayOf(userId.toString(), "All Contacts")
        )
        cursor.moveToFirst()
        if (cursor.getInt(0) == 0) {
            db.execSQL("INSERT INTO SQLTable VALUES (?, -1, 'All Contacts')", arrayOf(userId))
        }
        cursor.close()
        db.close()
    }
}
