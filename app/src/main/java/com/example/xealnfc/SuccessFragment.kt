package com.example.xealnfc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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

        if (args.amout.isNotEmpty()) {
            binding.successText.text = getString(R.string.successfully_added, args.amout)
        } else {
            binding.successText.isVisible = false
        }

        return binding.root
    }
}