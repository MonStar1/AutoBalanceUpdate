package com.balance.update.autobalanceupdate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.balance.update.autobalanceupdate.sms.category.Category
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottomsheet.recyclerView

class BottomSheet(val onSelected: (Category) -> Unit) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = object : ListAdapter<Category, ViewHolder>(object : ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem == newItem
            }
        }) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return object :
                    ViewHolder(
                        LayoutInflater.from(parent.context)
                            .inflate(android.R.layout.simple_list_item_1, parent, false)
                    ) {

                }
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.itemView.findViewById<TextView>(android.R.id.text1).text = getItem(position).name
                holder.itemView.setOnClickListener {
                    onSelected(getItem(position))
                    dismiss()
                }
            }
        }.apply {
            submitList(
                listOf(
                    Category.Food,
                    Category.Health,
                    Category.Sweet,
                    Category.Transport,
                    Category.Cafe,
                    Category.Household,
                    Category.Clothes,
                    Category.Child,
                    Category.Gift,
                    Category.Fun,
                    Category.Music,
                    Category.Unexpected
                )
            )
        }
    }
}