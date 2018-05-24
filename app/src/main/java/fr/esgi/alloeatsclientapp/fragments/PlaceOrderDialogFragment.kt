package fr.esgi.alloeatsclientapp.fragments

import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.utils.Global.Companion.codeAddRestaurantToFavorite
import fr.esgi.alloeatsclientapp.utils.Global.Companion.codeShowRestaurantPage

class PlaceOrderDialogFragment : DialogFragment() {
    private var codePasserListener: IOnCodePassListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView =
                inflater.inflate(R.layout.selected_restaurant_item_dialog_layout, container, false)

        setOnClickListeners(rootView)

        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        codePasserListener = context as IOnCodePassListener
    }

    private fun setOnClickListeners(rootView: View) {
        rootView.findViewById<Button>(R.id.addToFavorites_button)
                .setOnClickListener({ addRestaurantToFavorites() })

        rootView.findViewById<Button>(R.id.openRestaurantCard_button)
                .setOnClickListener({ goToRestaurantPage() })

        rootView.findViewById<Button>(R.id.dismissRestaurantItemFragmentDialog_button)
                .setOnClickListener({ dismiss() })
    }

    private fun addRestaurantToFavorites(){
        codePasserListener?.onCodePass(codeAddRestaurantToFavorite)
        dismiss()
    }

    private fun goToRestaurantPage(){
        codePasserListener?.onCodePass(codeShowRestaurantPage)
        dismiss()
    }
}