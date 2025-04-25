package com.antand.clim8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val slider = view.findViewById<Slider>(R.id.sliderUpdateFrequency)
        val label = view.findViewById<TextView>(R.id.labelFrequency)

        slider.addOnChangeListener { _, value, _ ->
            label.text = "Updates every ${value.toInt()} hours"
        }

        view.findViewById<View>(R.id.btnSaveSettings).setOnClickListener {
            Toast.makeText(requireContext(), "Settings saved (not really)", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
