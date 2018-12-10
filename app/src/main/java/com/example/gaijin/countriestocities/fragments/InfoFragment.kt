package com.example.gaijin.countriestocities.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gaijin.countriestocities.R
import com.example.gaijin.countriestocities.dataclasses.GeonamePart
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_info.*


class InfoFragment : Fragment() {

    var info: GeonamePart? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }


    override fun onStart() {
        super.onStart()
        if (info != null) {
            activity!!.title_text.text = info!!.title
            activity!!.summary_text.text = info!!.summary
            activity!!.coordinates_text.text = "lng: ${info!!.lng} lat: ${info!!.lng}"
            activity!!.link_text.text = "http://" + info!!.wikipediaUrl
            activity!!.link_text.setOnClickListener { goToLinkInBrowcer() }
        }
    }

    private fun goToLinkInBrowcer() {
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(activity!!.link_text.text.toString()))
        val chooser = Intent.createChooser(intent, getString(R.string.CHOOSE))
        // Verify the intent will resolve to at least one activity
        var packageManager = context!!.packageManager
        var programs = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        if (!programs.isEmpty()) {
            startActivity(chooser)
        }
    }


    override fun onDetach() {
        super.onDetach()
    }

}