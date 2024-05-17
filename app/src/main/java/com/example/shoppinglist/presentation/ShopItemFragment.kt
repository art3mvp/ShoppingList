package com.example.shoppinglist.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Display.Mode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.FragmentShopItemBinding
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ShopItemFragment: Fragment() {


    private lateinit var viewModel: ShopItemViewModel



    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var screenMode: String = MODE_UNKNOWN
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    private var _binding: FragmentShopItemBinding? = null
    private val binding: FragmentShopItemBinding
        get () = _binding ?: throw RuntimeException("FragmentShopItemBinding is null")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            throw RuntimeException("Activity must implement listener")
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =  FragmentShopItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModelObservers()
        onTextChangedListeners()
        launchRightVersion()
    }

    private fun launchRightVersion() {
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
            MODE_ADD -> launchAddMode()
        }
    }

    private fun launchAddMode() {
        binding.buttonSave.setOnClickListener {
            viewModel.addShopItem(
                binding.editTextName.text.toString(),
                binding.editTextQuantity.text.toString()
            )
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        binding.buttonSave.setOnClickListener {
            viewModel.editShopItem(
                binding.editTextName.text.toString(),
                binding.editTextQuantity.text.toString()
            )
        }
    }



    private fun viewModelObservers() {
        viewModel.shouldFinishActivity.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished()
        }
    }

    private fun onTextChangedListeners() {
        binding.editTextName.doOnTextChanged { _, _, _, _ ->
            viewModel.resetErrorInputName()
        }
        binding.editTextQuantity.doOnTextChanged { _, _, _, _ ->
            viewModel.resetErrorInputCount()
        }
    }

    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent!")
        }
        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode $mode!")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(ITEM_ID)) {
                throw RuntimeException("Param item id is absent")
            }
            shopItemId = args.getInt(ITEM_ID, ShopItem.UNDEFINED_ID)
        }
    }

    interface OnEditingFinishedListener {
        fun onEditingFinished()
    }

    companion object {


        fun newInstanceAddItem(): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(ITEM_ID, shopItemId)
                }
            }
        }

        private const val SCREEN_MODE = "extra_mode"
        private const val ITEM_ID = "extra_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""
    }
}