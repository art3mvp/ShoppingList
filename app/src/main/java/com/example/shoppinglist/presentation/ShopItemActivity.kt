package com.example.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ShopItemActivity : AppCompatActivity() {


    private lateinit var viewModel: ShopItemViewModel
    private lateinit var textInputLayoutName: TextInputLayout
    private lateinit var textInputLayoutQuantity: TextInputLayout
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextQuantity: TextInputEditText
    private lateinit var buttonSave: Button

    private var screenMode = MODE_UNKNOWN
    private var shopItemId = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shop_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        parseIntent()
        initViews()
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
        viewModel.shopItem.observe(this) {
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
        viewModel.shouldFinishActivity.observe(this) {
            finish()
        }
        viewModel.errorInputCount.observe(this) {
            val message = if (it) {
                "Empty quantity field!"
            } else {
                null
            }
            textInputLayoutQuantity.error = message
        }
        viewModel.errorInputName.observe(this) {
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

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent!")
        }
        val mode = intent.getStringExtra(EXTRA_SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode $mode!")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            Log.d("my_tag", intent.hasExtra(EXTRA_ITEM_ID).toString())
            if (!intent.hasExtra(EXTRA_ITEM_ID)) {
                throw RuntimeException("Param item id is absent")
            }
            shopItemId = intent.getIntExtra(EXTRA_ITEM_ID, ShopItem.UNDEFINED_ID)
        }
        Log.d("my_tag", "screen mode: $screenMode\nitem id: $shopItemId")
    }

    private fun initViews() {
        textInputLayoutName = findViewById(R.id.textInputLayoutName)
        textInputLayoutQuantity = findViewById(R.id.textInputLayoutQuantity)
        editTextName = findViewById(R.id.editTextName)
        editTextQuantity = findViewById(R.id.editTextQuantity)
        buttonSave = findViewById(R.id.buttonSave)
    }

    companion object {

        fun newIntentEditItem(context: Context, shopItemId: Int): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(EXTRA_ITEM_ID, shopItemId)
            return intent
        }

        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_ADD)
            return intent
        }

        private const val EXTRA_SCREEN_MODE = "extra_mode"
        private const val EXTRA_ITEM_ID = "extra_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""
    }

}