package com.zeeshan.chatapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.model.GroupChat

class GroupListAdapter(
    var context: Context,
    var dataList: ArrayList<GroupChat>,
    var itemClick: (group: GroupChat) -> Unit,
    var itemLongClick: (group: GroupChat) -> Unit
) :
    RecyclerView.Adapter<GroupListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.card_group, null, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        viewHolder.bindUser(dataList[position])
    }


    inner class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val groupName = view.findViewById<TextView>(R.id.groupName)

        fun bindUser(group: GroupChat) {
            groupName.text = group.groupName

            view.setOnClickListener {
                itemClick(group)
            }

            view.setOnLongClickListener {
                itemLongClick(group)
                true
            }

        }
    }
}