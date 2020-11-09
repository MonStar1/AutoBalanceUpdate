package com.balance.update.autobalanceupdate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.balance.update.autobalanceupdate.sms.seller.Seller
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottomsheet.recyclerView

class BottomSheet(val onSelected: (Seller) -> Unit) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = object : ListAdapter<Seller, ViewHolder>(object : ItemCallback<Seller>() {
            override fun areItemsTheSame(oldItem: Seller, newItem: Seller): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Seller, newItem: Seller): Boolean {
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
                    Seller.Food,
                    Seller.Health,
                    Seller.Sweet,
                    Seller.Transport,
                    Seller.Cafe,
                    Seller.Household,
                    Seller.Clothes,
                    Seller.Child,
                    Seller.Gift,
                    Seller.Fun,
                    Seller.Music,
                    Seller.Unexpected
                )
            )
        }
    }
}