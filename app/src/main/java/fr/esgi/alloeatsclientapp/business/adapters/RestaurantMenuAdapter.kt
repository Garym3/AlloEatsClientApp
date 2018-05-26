package fr.esgi.alloeatsclientapp.business.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.models.Menu

class RestaurantMenuAdapter(context: Context, private val menus: ArrayList<Menu>) : BaseAdapter() {
    override fun getItem(position: Int) = menus[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = menus.size

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, savedView: View?, parent: ViewGroup?): View {
        var view = savedView
        val holder: ViewHolder

        if (view == null) {
            view = inflater.inflate(R.layout.restaurant_menu_row, parent, false)
            holder = ViewHolder(view)
            view!!.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val menu = getItem(position)

        holder.name.text = menu.name

        Picasso.with(view.context)
                .load(menu.backgroundImageUri)
                .placeholder(R.drawable.default_restaurant_icon)
                .error(R.drawable.default_restaurant_icon)
                .resize(256, 256)
                .centerCrop()
                .into(holder.backgroundImage)

        return view
    }

    internal class ViewHolder(view: View) {
        @BindView(R.id.menu_name_TextView)
        lateinit var name: TextView

        @BindView(R.id.menu_background_ImageView)
        lateinit var backgroundImage: ImageView

        init {
            ButterKnife.bind(this, view)
        }
    }
}
