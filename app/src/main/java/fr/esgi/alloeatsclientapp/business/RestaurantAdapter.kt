package fr.esgi.alloeatsclientapp.business

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import com.squareup.picasso.Picasso

import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.models.nearbySearch.Restaurant


class RestaurantAdapter(context: Context, dataSet: ArrayList<Restaurant>) : BaseAdapter() {
    private val restaurants: ArrayList<Restaurant> = dataSet

    override fun getItem(position: Int) = restaurants[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = restaurants.size

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, savedView: View?, parent: ViewGroup?): View {
        var view = savedView
        val holder: ViewHolder

        if (view == null) {
            view = inflater.inflate(R.layout.restaurant_row, parent, false)
            holder = ViewHolder(view)
            view!!.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        // Get the data item for this position
        val restaurant = getItem(position)
        val sb = StringBuilder()

        // Populate the data into the template view using the data object
        holder.name.text = sb.append("Name: ").append(restaurant.name)
        holder.isOpen.text = toReadableOpenNow(restaurant.openingHours?.openNow!!)
        holder.rating.text = sb.append("rating: ").append(restaurant.rating.toString())
        holder.address.text = sb.append("Address: ").append(restaurant.vicinity)
        if(restaurant.icon == null){
            Picasso.with(view.context)
                    .load("")
                    .placeholder(R.drawable.default_restaurant_icon)
                    .error(R.drawable.default_restaurant_icon)
                    .resize(256, 256)
                    .centerCrop()
                    .into(holder.mainPhoto)
        }
        //Default
        //holder.mainPhoto.setImageResource(R.drawable.default_restaurant_icon)

        return view
    }

    private fun toReadableOpenNow(isOpenNow: Boolean): String{
        return if(isOpenNow) "Opened" else "Closed"
    }

    internal class ViewHolder(view: View) {
        @BindView(R.id.name)
        lateinit var name: TextView

        @BindView(R.id.isOpen)
        lateinit var isOpen: TextView

        @BindView(R.id.rating)
        lateinit var rating: TextView

        @BindView(R.id.address)
        lateinit var address: TextView

        @BindView(R.id.mainPhoto)
        lateinit var mainPhoto: ImageView

        init {
            ButterKnife.bind(this, view)
        }
    }
}
