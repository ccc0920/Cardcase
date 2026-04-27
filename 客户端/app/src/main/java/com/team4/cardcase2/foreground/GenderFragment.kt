package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GenderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GenderFragment : Fragment() {
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
        val view=inflater.inflate(R.layout.fragment_gender, container, false)
        val layout1 = view.findViewById<ConstraintLayout>(R.id.layout1)
        val layout2 = view.findViewById<ConstraintLayout>(R.id.layout2)
        val layout3 = view.findViewById<ConstraintLayout>(R.id.layout3)
        val layout4 = view.findViewById<ConstraintLayout>(R.id.layout4)
        val layout5 = view.findViewById<ConstraintLayout>(R.id.layout5)
        val check1=view.findViewById<ImageView>(R.id.check1)
        val check2=view.findViewById<ImageView>(R.id.check2)
        val check3=view.findViewById<ImageView>(R.id.check3)
        val check4=view.findViewById<ImageView>(R.id.check4)
        val check5=view.findViewById<ImageView>(R.id.check5)


        check1.visibility = View.VISIBLE
        check2.visibility = View.GONE
        check3.visibility = View.GONE
        check4.visibility = View.GONE
        check5.visibility = View.GONE



        // 设置点击事件监听器
        layout1.setOnClickListener {
            check1.visibility = View.VISIBLE
            check2.visibility = View.GONE
            check3.visibility = View.GONE
            check4.visibility = View.GONE
            check5.visibility = View.GONE

        }

        layout2.setOnClickListener {
            check2.visibility = View.VISIBLE
            check1.visibility = View.GONE
            check3.visibility = View.GONE
            check4.visibility = View.GONE
            check5.visibility = View.GONE
        }

        layout3.setOnClickListener {
            // 在这里处理第三个布局的点击事件
            // 显示/隐藏相应的图片等操作
            check3.visibility = View.VISIBLE
            check2.visibility = View.GONE
            check1.visibility = View.GONE
            check4.visibility = View.GONE
            check5.visibility = View.GONE
        }
        layout4.setOnClickListener{
            check4.visibility = View.VISIBLE
            check2.visibility = View.GONE
            check3.visibility = View.GONE
            check1.visibility = View.GONE
            check5.visibility = View.GONE
        }
        layout5.setOnClickListener{
            check5.visibility = View.VISIBLE
            check2.visibility = View.GONE
            check3.visibility = View.GONE
            check1.visibility = View.GONE
            check5.visibility = View.GONE
        }

        val back: ImageButton =view.findViewById(R.id.backLast)
        val goInfo2:Button=view.findViewById(R.id.gonext)
        back.setOnClickListener {
            val navigator = findNavController()
            navigator.navigate(R.id.info1Fragment)
        }

        goInfo2.setOnClickListener {
            val navigator = findNavController()
            navigator.navigate(R.id.info2Fragment)
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GenderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GenderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}