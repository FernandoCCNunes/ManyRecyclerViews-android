package com.nando.manyrecyclerviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViews()
    }

    private fun setViews() {
        val arrayOfNames: ArrayList<String> = arrayListOf(
            "Fernando",
            "Francisco",
            "Joana",
            "Mama",
            "Papa",
            "Filipe",
            "Alexandra",
            "Frederico",
            "Neuza",
            "Rita",
            "Vitor",
            "André",
            "Gabriela",
            "Pilar",
            "GueGue"
        )
        val adapter = TestingAdapter(arrayOfNames)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        my_recycler_view.layoutManager = layoutManager
        my_recycler_view.adapter = adapter
    }


    class TestingAdapter(items: ArrayList<String>): MyAdapter<String, TestingAdapter.ViewHolder>(items) {

        class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val card: CardView = view.findViewById(R.id.card)
            val name: TextView = view.findViewById(R.id.card_name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ViewHolder(inflater.inflate(R.layout.my_recycler_card, parent, false))
        }

        override fun getItemCount(): Int = items.size

        /*
        * To NOTE!
        *
        * To remove items by their position you should use holder.getAdapterPosition()[JAVA] or holder.adapterPosition [Kotlin]
        * the reason being that the recycler view does not REDRAW views after using any notify with the exception of
        * notifyDataSetChanged, only in this case does the recycler view completely redraws every child and provides the correct
        * position in the onBindViewHolder function
        */
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]

            holder.name.text = item

            holder.card.setOnClickListener {
                removeItems(2, 2)
            }
        }
    }

    abstract class MyAdapter<T, K : RecyclerView.ViewHolder>(var items: ArrayList<T>) :
        RecyclerView.Adapter<K>() {

        protected var recyclerView: RecyclerView? = null

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            this.recyclerView = recyclerView
            super.onAttachedToRecyclerView(recyclerView)
        }



        /*
        * To NOTE!!!!
        *
        * If you add an item to the position 0 and you are at the top of the scrollview
        * you won't see the item until you scroll top or scroll programmatically to the item after added
        */

        /*
        * Adds an item to the start or end of the list
        */
        fun addItem(item: T, toEnd: Boolean = true) {
            if (toEnd) {
                addItemAt(items.size, item)
            } else {
                addItemAt(0, item)
            }
        }

        /*
        * Adds an item to the specified position
        */
        fun addItemAt(position: Int, item: T) {
            items.add(position, item)
            notifyItemInserted(position)
        }

        /*
        * Adds an array of items tto the start or end of the list
        */
        fun addItems(items: ArrayList<T>, toEnd: Boolean = true) {
            if (toEnd) {
                this.items.addAll(items)
                val positionStart = this.items.size - (items.size)
                val positionEnd = items.size
                notifyItemRangeInserted(positionStart, positionEnd)
            } else {
                this.items.addAll(0, items)
                notifyItemRangeInserted(0, items.size)
            }
        }

        /*
        * Adds an array of items to the specified positions
        *
        * @param 'relative'
        * true -> sets the items position relative to the position of the old items before any was added
        * false -> sets the items to the position after the list calculated new positions every time each item is added
        */
        fun addItems(items: ArrayList<T>, positions: ArrayList<Int>, relative: Boolean = true) {
            for ((index, item) in items.withIndex()) {
                if (relative) addItemAt(positions[index] + index, item)
                else addItemAt(positions[index], item)

            }
        }

        /*
        * Removes a specific item
        */
        fun removeItem(item: T) {
            if (this.items.contains(item)) {
                val position = items.indexOf(item)
                removeItemAt(position)
            }
        }

        /*
        * Removes an item at a specific position
        */
        fun removeItemAt(position: Int) {
            if (this.items.getOrNull(position) != null) {
                notifyItemRemoved(position)
                items.removeAt(position)
            }
        }

        /*
        * Removes an item at a specific position
        */
        fun removeItems(items: ArrayList<T>) {
            for (item in items) {
                if (this.items.contains(item)) {
                    removeItemAt(this.items.indexOf(item))
                }
            }
        }

        /*
        * Removes items at the specified positions
        *
        * @param 'relative'
        * true -> removes the items position relative to the position of the old items before any was removed
        * false -> removed the items from the position after the list calculated new positions every time each item is removed
        */
        fun removeItemsAt(positions: ArrayList<Int>, relative: Boolean = true) {
            for ((index, position) in positions.withIndex()) {
                if (this.items.getOrNull(position) != null) {
                    if (relative) removeItemAt(position - index)
                    else removeItemAt(position)
                }
            }
        }

        /*
        * Removes a number of items from the start or end of the list
        */
        fun removeItems(count: Int, fromEnd: Boolean = true) {
            if (fromEnd) removeItems(this.items.size - count, count)
            else removeItems(0, count)
        }

        /*
        * Removes a number of items starting from the specified position
        */
        fun removeItems(position: Int, count: Int) {
            if (this.items.getOrNull(position) != null) {
                if (this.items.size >= position + count) {
                    this.items.removeAll(this.items.subList(position, position + count))
                    notifyItemRangeRemoved(position, count)
                } else {
                    removeItems(position, this.items.size - position)
                }
            }
        }

        /*
        * Scrolls to specified item
        */
        fun requestScrollToItem(item: T, smoothScroll: Boolean = false) {
            if (recyclerView != null && this.items.contains(item)) {
                if (smoothScroll) recyclerView!!.smoothScrollToPosition(items.indexOf(item))
                else recyclerView!!.scrollToPosition(items.indexOf(item))
            }
        }

        /*
        * Scrolls to specified position
        */
        fun requestScrollToItem(position: Int, smoothScroll: Boolean = false) {
            if (recyclerView != null && this.items.getOrNull(position) != null) {
                if (smoothScroll) recyclerView!!.smoothScrollToPosition(position)
                else recyclerView!!.scrollToPosition(position)
            }
        }
    }
}
