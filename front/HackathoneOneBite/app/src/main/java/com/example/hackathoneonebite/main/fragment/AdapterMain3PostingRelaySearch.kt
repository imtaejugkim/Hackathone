package com.example.hackathoneonebite.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathoneonebite.databinding.ItemRelayNameListBinding

class AdapterMain3PostingRelaySearch
    : RecyclerView.Adapter<AdapterMain3PostingRelaySearch.NameViewHolder>(), Filterable {

    private var nameIdList = mutableListOf<String>()
    private var filteredNameIdList = mutableListOf<String>()

    fun setData(data: List<String>) {
        nameIdList.clear()
        nameIdList.addAll(data)
        filteredNameIdList.clear()
        filteredNameIdList.addAll(data)
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
        holder.bind(filteredNameIdList[position])
    }

    override fun getItemCount(): Int {
        return filteredNameIdList.size
    }

    inner class NameViewHolder(val binding: ItemRelayNameListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(nameId: String) {
            val (name, userId) = extractNameAndUserId(nameId)
            binding.nameTextView.text = name
            binding.idTextView.text = userId

            binding.root.setOnClickListener {
                nameClickListener?.onNameClick(nameId)
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
                    filterResults.values = nameIdList
                } else {
                    val filteredNames = nameIdList.filter { it.contains(constraint, true) }
                    filterResults.values = filteredNames
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredNameIdList.clear()
                filteredNameIdList.addAll(results?.values as List<String>)
                notifyDataSetChanged()
            }
        }
    }

    fun clearData() {
        filteredNameIdList.clear()
        notifyDataSetChanged()
    }

}
