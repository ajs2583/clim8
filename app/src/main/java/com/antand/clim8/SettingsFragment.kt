package com.antand.clim8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.antand.clim8.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()

        // Load existing settings
        binding.switchTempUnit.isChecked = SettingsManager.isUsingCelsius(context)
        binding.switchNotifications.isChecked = SettingsManager.areNotificationsEnabled(context)
        binding.sliderUpdateFrequency.value = SettingsManager.getUpdateFrequency(context).toFloat()
        updateFrequencyLabel(SettingsManager.getUpdateFrequency(context))

        // When user toggles temperature unit
        binding.switchTempUnit.setOnCheckedChangeListener { _, isChecked ->
            SettingsManager.setUseCelsius(context, isChecked)
        }

        // When user toggles notifications
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            SettingsManager.setNotificationsEnabled(context, isChecked)
        }

        // When user changes slider
        binding.sliderUpdateFrequency.addOnChangeListener { _, value, _ ->
            val hours = value.toInt()
            SettingsManager.setUpdateFrequency(context, hours)
            updateFrequencyLabel(hours)
        }

        // Save button
        binding.btnSaveSettings.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun updateFrequencyLabel(hours: Int) {
        binding.labelFrequency.text = "Updates every $hours hours"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
