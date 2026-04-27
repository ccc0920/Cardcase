package com.team4.cardcase2.foreground

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.team4.cardcase2.R

class DataAdapter (
    fa: FragmentActivity,
    val fragmentlist:List<Fragment>,
    val titlelist:List<String>
): FragmentStateAdapter(fa){
    interface ClickListener{
        fun onClickItem(position: Int)
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentlist[position]
    }

    override fun getItemCount(): Int {
        return fragmentlist.size
    }

    fun getPageTitle(position: Int):CharSequence?{
        return titlelist[position]
    }

    fun getPageIcon(position: Int): Int {
        return when(position){
            0-> R.drawable.wallet_02
            1->R.drawable.user_profile_group
            2->R.drawable.wrench
            3->R.drawable.settings
            else -> {0}
        }
    }
}