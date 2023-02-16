package com.example.xealnfc

import android.content.Context
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.xealnfc.databinding.FragmentSuccessBinding

class SuccessFragment: Fragment() {

    private lateinit var binding: FragmentSuccessBinding

    private val args: SuccessFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuccessBinding.inflate(layoutInflater)

        val v = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        binding.successText.text = getString(R.string.successfully_added, args.amount.toString())
        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                findNavController().navigateUp()
            }
        },2000)

        return binding.root
    }
}