package com.example.xealnfc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.xealnfc.databinding.FragmentHomeBinding

class HomeFragment: Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: ViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToViewState()
    }

    private fun subscribeToViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                when (it) {
                    ViewModel.ViewState.SELECT_10 -> {
                        binding.button10.background = context?.getDrawable(R.drawable.button_selected_bg)
                        binding.button25.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.button50.background = context?.getDrawable(R.drawable.button_default_bg)
                    }

                    ViewModel.ViewState.SELECT_25 -> {
                        binding.button10.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.button25.background = context?.getDrawable(R.drawable.button_selected_bg)
                        binding.button50.background = context?.getDrawable(R.drawable.button_default_bg)
                    }

                    ViewModel.ViewState.SELECT_50 -> {
                        binding.button10.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.button25.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.button50.background = context?.getDrawable(R.drawable.button_selected_bg)
                    }

                    else -> Unit
                }
            }
        }
    }
}