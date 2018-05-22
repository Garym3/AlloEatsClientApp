package fr.esgi.alloeatsclientapp.business.adapters

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import fr.esgi.alloeatsclientapp.R

class CarouselAdapter(private val mContext: Context) :
        RecyclerView.Adapter<CarouselAdapter.MyViewHolder>() {

    private var mPhotoUris: ArrayList<Uri>? = ArrayList()

    constructor(mContext: Context, photoUris: ArrayList<Uri>?) : this(mContext){
        mPhotoUris = photoUris
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var photo: ImageView = view.findViewById<View>(R.id.thumbnail) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.restaurant_page_carousel_layout, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(mPhotoUris == null || itemCount <= 0) return

        Picasso.with(mContext)
                .load(mPhotoUris!![position])
                .resize(512, 512)
                .centerInside()
                .into(holder.photo)
    }

    override fun getItemCount(): Int { return mPhotoUris!!.size }

    fun add(photoUri: Uri){
        mPhotoUris!!.add(photoUri)
        notifyDataSetChanged()
    }
}
