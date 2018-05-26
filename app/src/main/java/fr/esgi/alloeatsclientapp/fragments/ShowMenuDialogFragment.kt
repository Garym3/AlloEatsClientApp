package fr.esgi.alloeatsclientapp.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import fr.esgi.alloeatsclientapp.R

/**
 * A simple [Fragment] subclass.
 *
 */
class ShowMenuDialogFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_show_menu_dialog, container, false)
    }
}
