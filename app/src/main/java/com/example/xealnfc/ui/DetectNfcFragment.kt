package com.example.xealnfc.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.xealnfc.databinding.FragmentDetectNfcBinding

class DetectNfcFragment: Fragment() {

    private lateinit var binding: FragmentDetectNfcBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetectNfcBinding.inflate(layoutInflater)
        return binding.root
    }

}