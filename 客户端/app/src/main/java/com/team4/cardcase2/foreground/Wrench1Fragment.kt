package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.OrderResponse
import com.team4.cardcase2.interfaces.HttpRequest

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Wrench1Fragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var orderAdapter: OrderAdapter
    private var orders = mutableListOf<OrderResponse.OrderItem>()

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
        val root = inflater.inflate(R.layout.fragment_wrench1, container, false)
        val recyclerView = root.findViewById<RecyclerView>(R.id.myCardView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        orderAdapter = OrderAdapter(orders) { order ->
            showOrderDetails(order)
        }
        recyclerView.adapter = orderAdapter
        loadOrders()
        return root
    }

    private fun loadOrders() {
        val ctx = requireContext()
        val userId = AppSession.getUserId(ctx)
        val token = AppSession.getToken(ctx)
        if (userId > 0 && token.isNotEmpty()) {
            HttpRequest().sendGetRequest(
                "http://10.0.2.2:8080/api/orders/user/$userId", token
            ) { response, exception ->
                activity?.runOnUiThread {
                    if (exception == null && response != null) {
                        try {
                            val result = OrderResponse.fromJson(response)
                            if (result.success) {
                                orders.clear()
                                orders.addAll(result.orders)
                                orderAdapter.notifyDataSetChanged()
                            }
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                }
            }
        }
    }

    private fun showOrderDetails(order: OrderResponse.OrderItem) {
        val ctx = context ?: return
        AlertDialog.Builder(ctx)
            .setTitle("订单详情")
            .setMessage(
                "订单号: ${order.orderId}\n" +
                "材料: ${order.materials}\n" +
                "数量: ${order.quantity}\n" +
                "状态: ${order.state}"
            )
            .setPositiveButton("关闭", null)
            .setNegativeButton("取消订单") { _, _ ->
                cancelOrder(order.orderId)
            }
            .show()
    }

    private fun cancelOrder(orderId: Long) {
        val ctx = requireContext()
        val token = AppSession.getToken(ctx)
        HttpRequest().sendPutRequest(
            "http://10.0.2.2:8080/api/orders/$orderId/cancel", token, ""
        ) { response, exception ->
            activity?.runOnUiThread {
                if (exception == null) {
                    Toast.makeText(ctx, "订单已取消", Toast.LENGTH_SHORT).show()
                    loadOrders()
                } else {
                    Toast.makeText(ctx, "取消失败: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Wrench1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}