package com.example.xealnfc

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.xealnfc.databinding.FragmentHomeBinding


class HomeFragment: Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: ViewModel by activityViewModels()
    private val args: HomeFragmentArgs by navArgs()

    private var userData: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.viewModel = viewModel

        if (args.name.isBlank()) {
            openDialogBox()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToViewState()
    }


    private fun openDialogBox() {
        val alert: AlertDialog.Builder = AlertDialog.Builder(context)
        val edittext = EditText(context)
        alert.setMessage("Enter Your Name")
        alert.setTitle("Empty Tag")

        alert.setView(edittext)

        alert.setPositiveButton(
            "Continue"
        ) { dialog, whichButton -> //What ever you want to do with the value
            userData = User(edittext.text.toString(), 0)
            binding.name.text = userData?.name
            binding.availableFund.text = getString(R.string.dollar, userData?.remainingAmount.toString())
        }

        alert.show()
    }
    private fun subscribeToViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                when (it) {
                    ViewModel.ViewState.SELECT_10 -> {
                        binding.button10.background = context?.getDrawable(R.drawable.button_selected_bg)
                        binding.button25.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.button50.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.payNowButton.isEnabled = true
                    }

                    ViewModel.ViewState.SELECT_25 -> {
                        binding.button10.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.button25.background = context?.getDrawable(R.drawable.button_selected_bg)
                        binding.button50.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.payNowButton.isEnabled = true
                    }

                    ViewModel.ViewState.SELECT_50 -> {
                        binding.button10.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.button25.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.button50.background = context?.getDrawable(R.drawable.button_selected_bg)
                        binding.payNowButton.isEnabled = true
                    }

                    ViewModel.ViewState.NFC_TAG_INITIALIZED -> {
                        Toast.makeText(requireContext(), getString(R.string.init_nfc_success), Toast.LENGTH_LONG)
                            .show()
                    }

                    ViewModel.ViewState.NFC_TAG_INIT_FAILED -> {
                    Toast.makeText(requireContext(), getString(R.string.init_nfc_failed), Toast.LENGTH_LONG)
                        .show()
                    }

                    ViewModel.ViewState.Loading -> {
                        binding.payNowButton.text = ""
                        binding.payNowButton.isEnabled = false
                        binding.progressBar.isVisible = true
                    }

                    is ViewModel.ViewState.AddingAmountSuccess -> {
                        val action = HomeFragmentDirections.actionHomeFragmentToSuccessFragment(
                            amout = it.amout
                        )
                        findNavController().navigate(action)
                    }

                    ViewModel.ViewState.AddingAmountError -> {
                        binding.payNowButton.isEnabled = true
                        binding.payNowButton.text = getString(R.string.pay_now)
                        binding.progressBar.isVisible = false
                        Toast.makeText(requireContext(), getString(R.string.added_amount_error), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> Unit
                }
            }
        }
    }
}