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
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ShopItemFragment: Fragment() {


    private lateinit var viewModel: ShopItemViewModel

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener


    private lateinit var textInputLayoutName: TextInputLayout
    private lateinit var textInputLayoutQuantity: TextInputLayout
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextQuantity: TextInputEditText
    private lateinit var buttonSave: Button

    private var screenMode: String = MODE_UNKNOWN
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

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
    ): View? {

        return inflater.inflate(R.layout.fragment_shop_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
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
        buttonSave.setOnClickListener {
            viewModel.addShopItem(
                editTextName.text.toString(),
                editTextQuantity.text.toString()
            )
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        viewModel.shopItem.observe(viewLifecycleOwner) {
            editTextName.setText(it.name)
            editTextQuantity.setText(it.count.toString())
        }
        buttonSave.setOnClickListener {
            viewModel.editShopItem(
                editTextName.text.toString(),
                editTextQuantity.text.toString()
            )
        }
    }



    private fun viewModelObservers() {
        viewModel.shouldFinishActivity.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished()
        }
        viewModel.errorInputCount.observe(viewLifecycleOwner) {
            val message = if (it) {
                "Empty quantity field!"
            } else {
                null
            }
            textInputLayoutQuantity.error = message
        }
        viewModel.errorInputName.observe(viewLifecycleOwner) {
            val message = if (it) {
                "Empty name field!"
            } else {
                null
            }
            textInputLayoutName.error = message
        }
    }

    private fun onTextChangedListeners() {
        editTextName.doOnTextChanged { _, _, _, _ ->
            viewModel.resetErrorInputName()
        }
        editTextQuantity.doOnTextChanged { _, _, _, _ ->
            viewModel.resetErrorInputName()
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


    private fun initViews(view: View) {
        textInputLayoutName = view.findViewById(R.id.textInputLayoutName)
        textInputLayoutQuantity = view.findViewById(R.id.textInputLayoutQuantity)
        editTextName = view.findViewById(R.id.editTextName)
        editTextQuantity = view.findViewById(R.id.editTextQuantity)
        buttonSave = view.findViewById(R.id.buttonSave)
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