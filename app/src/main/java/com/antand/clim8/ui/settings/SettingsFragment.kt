package com.antand.clim8.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.antand.clim8.utils.SettingsManager
import com.antand.clim8.databinding.FragmentSettingsBinding

// fragment that allows the user to manage app settings (temperature unit, notifications, update frequency)
class SettingsFragment : Fragment() {

    // view binding for fragment_settings.xml
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // inflate the layout and return the root view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    // called after the view is created; bind settings values and listeners here
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()

        // load existing saved settings

        // set the temperature unit switch based on saved preference
        binding.switchTempUnit.isChecked = SettingsManager.isUsingCelsius(context)

        // set the notifications switch based on saved preference
        binding.switchNotifications.isChecked = SettingsManager.areNotificationsEnabled(context)

        // set the update frequency slider to match saved preference
        binding.sliderUpdateFrequency.value = SettingsManager.getUpdateFrequency(context).toFloat()

        // update the text label to reflect current update frequency
        updateFrequencyLabel(SettingsManager.getUpdateFrequency(context))

        // event listeners for UI Interactions

        // handle toggling between celsius and fahrenheit
        binding.switchTempUnit.setOnCheckedChangeListener { _, isChecked ->
            SettingsManager.setUseCelsius(context, isChecked)
        }

        // handle enabling/disabling notifications
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            SettingsManager.setNotificationsEnabled(context, isChecked)
        }

        // handle slider changes for weather update frequency
        binding.sliderUpdateFrequency.addOnChangeListener { _, value, _ ->
            val hours = value.toInt()
            SettingsManager.setUpdateFrequency(context, hours)
            updateFrequencyLabel(hours) // update label text with new value
        }

        // handle save button click — just navigates back to previous screen
        binding.btnSaveSettings.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    // updates the label text for the update frequency setting
    private fun updateFrequencyLabel(hours: Int) {
        binding.labelFrequency.text = "Updates every $hours hours"
    }

    // clean up the binding when view is destroyed to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
