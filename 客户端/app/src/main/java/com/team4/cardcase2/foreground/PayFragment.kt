package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PayFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_pay, container, false)

        val completeButton: Button = root.findViewById(R.id.complete)
        completeButton.text = "Order on Taobao"
        completeButton.setOnClickListener {
            val taobaoUrl = "https://s.taobao.com/search?q=%E5%90%8D%E7%89%87%E5%8D%B0%E5%88%B7"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(taobaoUrl))
            startActivity(intent)
            findNavController().navigate(R.id.make4Fragment)
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
