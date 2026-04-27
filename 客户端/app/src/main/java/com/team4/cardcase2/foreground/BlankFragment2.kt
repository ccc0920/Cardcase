package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.Encoder

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment2.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class BlankFragment2 : Fragment(), ScanFragment.QRCodeScanResultListener {
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

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_blank, container, false)
        val addButton2: Button = root.findViewById(R.id.addButton2)
        addButton2.setOnClickListener {
            val navController=findNavController()
            navController.navigate(R.id.createCardFragment)
        }
//        val recyclerView: RecyclerView =root.findViewById(R.id.groupView)
//        val groupLists = getGids(1)
//        val adapter = GroupAdapter(groupLists)
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val myCardView: RecyclerView = root.findViewById(R.id.myCardView)
        myCardView.adapter = CardAdapter(emptyList())
        myCardView.layoutManager = LinearLayoutManager(requireContext())

        val testButton: Button = root.findViewById(R.id.testButton)
        testButton.setOnClickListener {
            findNavController().navigate(R.id.cardDetailFragment)
        }

        val scanButton: Button = root.findViewById(R.id.scanButton2)
        scanButton.setOnClickListener {
            // 启动QRCodeScannerFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.blankFragment, ScanFragment())
                .addToBackStack(null)
                .commit()
        }
        return root
    }

    override fun onQRCodeScanned(result: String) {
        println("---------------")
        println(result)
        // 使用扫描结果
        val coder: Encoder = Encoder()
        val qrResult = coder.decode(result)
        Log.d("ButtonFragment", "Decoded QR Code Result: $qrResult")

        Toast.makeText(context, "QR Code Result: $qrResult", Toast.LENGTH_LONG).show()
        // 返回到ButtonFragment
        parentFragmentManager.popBackStack()
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}