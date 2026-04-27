package com.team4.cardcase2.foreground

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.ServerCard
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GroupAdapter(private val groups: MutableList<String>,
                   private val listener:ButtonClickListener?
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_member, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val groupName = groups[position]
        holder.showGroup.text = groupName

        // 设置长按事件监听器
        holder.itemView.setOnLongClickListener {
            removeItem(position)
            true
        }
        holder.itemView.setOnClickListener {
            MainScope().launch {
                listener!!.rowClick(position, groups[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showGroup: TextView = itemView.findViewById(R.id.nameText)
        // 其他布局中的视图组件...
    }

    fun addItem(item: String) {
        groups.add(item)
        notifyItemInserted(groups.size - 1)
    }

    private fun removeItem(position: Int) {
        groups.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, groups.size)
    }
}