package com.tom.deezergame.spotifive

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable

// https://www.truiton.com/2018/06/android-autocompletetextview-suggestions-from-webservice-call/
class AutoSuggestAdapter(
    context: Context,
    resource: Int,
    private val listData: MutableList<String> = mutableListOf()
) : ArrayAdapter<String>(context, resource), Filterable {

    fun setData(list: MutableList<String>) {
        listData.clear()
        listData.addAll(list)
    }

    override fun getCount(): Int {
        return listData.size
    }

    override fun getItem(position: Int): String? {
        return listData.get(position)
    }

    fun getObject(position: Int): String {
        return listData.get(position)
    }

    override fun getFilter(): Filter {
        val dataFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    filterResults.values = listData
                    filterResults.count = listData.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
        return dataFilter
    }
}