package fr.esgi.alloeatsclientapp.business.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.models.Order
import fr.esgi.alloeatsclientapp.models.google.details.Result
import fr.esgi.alloeatsclientapp.utils.Google
import java.util.*


class OrderAdapter(context: Context, private val orders: ArrayList<Order>) : BaseAdapter() {
    private val sb = StringBuilder()

    override fun getItem(position: Int) = orders[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = orders.size

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, savedView: View?, parent: ViewGroup?): View {
        var view = savedView
        val holder: ViewHolder

        if (view == null) {
            view = inflater.inflate(R.layout.order_row, parent, false)
            holder = ViewHolder(view)
            view!!.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        // Get the data item for this position
        val order = getItem(position)

        // Populate the data into the template view using the data object
        holder.restaurantName.text = sb.append("Restaurant: ").append(order.restaurantName)
        sb.setLength(0)
        holder.id.text = sb.append("Order ID: ").append(order.orderId)
        sb.setLength(0)
        holder.date.text = sb.append("Ordered At: ").append(order.orderDate)
        sb.setLength(0)

        return view
    }

    internal class ViewHolder(view: View) {
        @BindView(R.id.orderRestaurant_TextView)
        lateinit var restaurantName: TextView

        @BindView(R.id.orderId_TextView)
        lateinit var id: TextView

        @BindView(R.id.orderDate_TextView)
        lateinit var date: TextView

        init {
            ButterKnife.bind(this, view)
        }
    }
}
