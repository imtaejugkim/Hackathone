package com.example.hackathoneonebite.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.databinding.ItemRelayNameListBinding

class AdapterMain2SearchFragment
    : RecyclerView.Adapter<AdapterMain2SearchFragment.NameViewHolder>(), Filterable {

    private var nameList = mutableListOf<String>()
    private var filteredNameList = mutableListOf<String>()

    fun setData(data: List<String>) {
        nameList.clear()
        nameList.addAll(data)
        filteredNameList.clear()
        filteredNameList.addAll(data)
        notifyDataSetChanged()
    }

    interface OnNameClickListener {
        fun onNameClick(name: String)
    }

    private var nameClickListener: OnNameClickListener? = null

    fun setOnNameClickListener(listener: OnNameClickListener) {
        nameClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameViewHolder {
        val view =
            ItemRelayNameListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NameViewHolder(view)
    }

    override fun onBindViewHolder(holder: NameViewHolder, position: Int) {
        holder.bind(filteredNameList[position])
    }

    override fun getItemCount(): Int {
        return filteredNameList.size
    }

    inner class NameViewHolder(val binding: ItemRelayNameListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String) {
            binding.nameTextView.text = name

            binding.root.setOnClickListener {
                nameClickListener?.onNameClick(name)
            }
        }

        private fun extractNameAndUserId(nameId: String): Pair<String, String> {
            val parts = nameId.split("(", ")")
            val name = parts[0].trim()
            val userId = parts[1].trim()
            return Pair(name, userId)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint.isNullOrEmpty()) {
                    filterResults.values = nameList
                } else {
                    val filteredNames = nameList.filter { it.contains(constraint, true) }
                    filterResults.values = filteredNames
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredNameList.clear()
                filteredNameList.addAll(results?.values as List<String>)
                notifyDataSetChanged()
            }
        }
    }

    fun clearData() {
        filteredNameList.clear()
        notifyDataSetChanged()
    }

}
