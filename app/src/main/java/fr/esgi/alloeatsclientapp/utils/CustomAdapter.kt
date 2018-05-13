package fr.esgi.alloeatsclientapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import java.util.ArrayList

import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.models.Restaurant


class CustomAdapter(dataSet: ArrayList<Restaurant>, mContext: Context) :
        ArrayAdapter<Restaurant>(mContext, R.layout.restaurant_row, dataSet), View.OnClickListener {

    private var lastPosition = -1

    // View lookup cache
    private class ViewHolder {
        internal var txtName: TextView? = null
        internal var txtRating: TextView? = null
        internal var txtIsOpen: TextView? = null
        internal var txtAddress: TextView? = null
        internal var imgMainPhoto: ImageView? = null
    }

    override fun onClick(v: View) {
        val position = v.tag as Int
        val `object` = getItem(position)
        val dataModel = `object` as Restaurant

        when (v.id) {
            //TODO : Show main photo in fullscreen
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var newConvertView = convertView
        // Get the data item for this position
        val restaurant = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag

        if (newConvertView == null) {

            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            newConvertView = inflater.inflate(R.layout.restaurant_row, parent, false)
            viewHolder.txtName = newConvertView!!.findViewById(R.id.name)
            viewHolder.txtRating = newConvertView.findViewById(R.id.rating)
            viewHolder.txtIsOpen = newConvertView.findViewById(R.id.isOpen)
            viewHolder.txtAddress = newConvertView.findViewById(R.id.address)
            viewHolder.imgMainPhoto = newConvertView.findViewById(R.id.mainPhoto)

            newConvertView.tag = viewHolder
        } else {
            viewHolder = newConvertView.tag as ViewHolder
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        lastPosition = position

        viewHolder.txtName!!.text = restaurant!!.name
        viewHolder.txtRating!!.setText(restaurant.rating)
        viewHolder.txtIsOpen!!.text = formatIsOpenedNow(restaurant)
        viewHolder.txtAddress!!.text = restaurant.address
        viewHolder.imgMainPhoto!!.setOnClickListener(this)
        viewHolder.imgMainPhoto!!.tag = position

        // Return the completed view to render on screen
        return newConvertView
    }

    private fun formatIsOpenedNow(restaurant: Restaurant): String {
        return if (restaurant.isOpenedNow) "Yes" else "No"
    }
}
