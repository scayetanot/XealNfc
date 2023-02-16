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

    private val viewModel: XealViewModel by activityViewModels()
    private val args: HomeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.viewModel = viewModel

        if (viewModel.userData.name.isNullOrBlank()) {
            openDialogBox()
        } else {
            viewModel.setUserData(args.name, args.amout)
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
            viewModel.setUserData(edittext.text.toString())
            binding.name.text = viewModel.userData.name
            binding.availableFund.text = getString(R.string.dollar, viewModel.userData.remainingAmount.toString())
        }

        alert.show()
    }
    private fun subscribeToViewState() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect {
                when (it) {
                    XealViewModel.ViewState.SELECT_10 -> {
                        binding.button10.background = context?.getDrawable(R.drawable.button_selected_bg)
                        binding.button25.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.button50.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.payNowButton.isEnabled = true
                    }

                    XealViewModel.ViewState.SELECT_25 -> {
                        binding.button10.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.button25.background = context?.getDrawable(R.drawable.button_selected_bg)
                        binding.button50.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.payNowButton.isEnabled = true
                    }

                    XealViewModel.ViewState.SELECT_50 -> {
                        binding.button10.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.button25.background = context?.getDrawable(R.drawable.button_default_bg)
                        binding.button50.background = context?.getDrawable(R.drawable.button_selected_bg)
                        binding.payNowButton.isEnabled = true
                    }

                    XealViewModel.ViewState.Loading -> {
                        binding.payNowButton.text = ""
                        binding.payNowButton.isEnabled = false
                        binding.progressBar.isVisible = true
                    }

                    is XealViewModel.ViewState.AddingAmountSuccess -> {
                        val action = HomeFragmentDirections.actionHomeFragmentToSuccessFragment(
                            amount = it.amout
                        )
                        findNavController().navigate(action)
                    }

                    XealViewModel.ViewState.AddingAmountError -> {
                        binding.payNowButton.isEnabled = true
                        binding.payNowButton.text = getString(R.string.pay_now)
                        binding.progressBar.isVisible = false
                        Toast.makeText(requireContext(), getString(R.string.added_amount_error), Toast.LENGTH_LONG)
                            .show()
                    }

                    XealViewModel.ViewState.NFC_TAG_READ_ONLY -> {
                        Toast.makeText(requireContext(), getString(R.string.tag_read_only), Toast.LENGTH_LONG)
                            .show()
                    }

                    XealViewModel.ViewState.NFC_TAG_MESSAGE_ERROR -> {
                        Toast.makeText(requireContext(), getString(R.string.tag_write_error), Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> Unit
                }
            }
        }
    }
}