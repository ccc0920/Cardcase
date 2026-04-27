package com.team4.cardcase2.foreground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team4.cardcase2.R


/**
 * A simple [Fragment] subclass.
 * Use the [CreateCardFragment2.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class CreateCardFragment2 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_create_card, container, false)

        val startButton: Button = root.findViewById(R.id.button_create_new)
        startButton.setOnClickListener {
            findNavController().navigate(R.id.createNewFragment)
        }

        val myCardView: RecyclerView = root.findViewById(R.id.myCardView)
        myCardView.adapter = CardAdapter(emptyList())
        myCardView.layoutManager = LinearLayoutManager(requireContext())

        val backButton: TextView = root.findViewById(R.id.backButton2)
        backButton.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.blankFragment2)
        }

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateCardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateCardFragment2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        @JvmStatic
        fun newInstance() =
            CreateCardFragment2()
    }
}