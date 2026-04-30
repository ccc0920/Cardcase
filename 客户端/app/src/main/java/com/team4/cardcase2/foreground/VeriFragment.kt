package com.team4.cardcase2.foreground

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.R

class VeriFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Email verification removed — navigate back immediately
        findNavController().navigateUp()
        return inflater.inflate(R.layout.fragment_veri, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = VeriFragment()
    }
}
