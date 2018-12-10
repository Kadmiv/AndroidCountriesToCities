package com.example.gaijin.countriestocities.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gaijin.countriestocities.R
import kotlinx.android.synthetic.main.fragment_error.*


class ErrorFragment : Fragment() {

    var errorText: String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_error, container, false)
    }

    override fun onStart() {
        super.onStart()
        error_text.setText(errorText.toString())
        try_again_btn.setOnClickListener { onTryButtonClick() }
    }

    private fun onTryButtonClick() {
        val broadcastIntent = Intent(getString(R.string.BROADCAST_ACTION))
        broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_RELOAD))
        context!!.sendBroadcast(broadcastIntent)
    }


    override fun onDetach() {
        super.onDetach()
    }
}// Required empty public constructor
