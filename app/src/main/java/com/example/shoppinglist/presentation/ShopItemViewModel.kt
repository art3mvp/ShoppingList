package com.example.shoppinglist.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.AddShopItemUseCase
import com.example.shoppinglist.domain.EditShopItemUseCase
import com.example.shoppinglist.domain.GetShopItemUseCase
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputLayout.LengthCounter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ShopItemViewModel(application: Application): AndroidViewModel(application) {

    private val repo = ShopListRepositoryImpl(application)

    private val getShopItemUseCase = GetShopItemUseCase(repo)
    private val addShopItemUseCase = AddShopItemUseCase(repo)
    private val editShopItemUseCase = EditShopItemUseCase(repo)
    private val _shopItem = MutableLiveData<ShopItem>()
    private val _shouldFinishActivity = MutableLiveData<Unit>()
    val shouldFinishActivity: LiveData<Unit>
        get () = _shouldFinishActivity

    val shopItem: LiveData<ShopItem>
        get() = _shopItem

    private val _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    fun getShopItem(shopItemId: Int) {
        viewModelScope.launch {
            _shopItem.value = getShopItemUseCase.getShopItem(shopItemId)
        }
    }

    fun addShopItem(inputName: String?, inputCount: String?) {
        viewModelScope.launch {
            val name = parseName(inputName)
            val count = parseCount(inputCount)
            if (validateInput(name, count)) {
                val shopItem = ShopItem(name, count, true)
                addShopItemUseCase.addShopItem(shopItem)
                finishWork()
            }
        }
    }

    fun editShopItem(inputName: String?, inputCount: String?) {
        viewModelScope.launch {
            val name = parseName(inputName)
            val count = parseCount(inputCount)
            if (validateInput(name, count)) {
                _shopItem.value?.let {
                    val item = it.copy(name = name, count = count)
                    editShopItemUseCase.editShopItem(item)
                    finishWork()
                }
            }
        }
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim()?:""
    }
    private fun parseCount(inputCount: String?): Int {
        return inputCount?.trim()?.toIntOrNull()?:0
    }

    private fun finishWork() {
        _shouldFinishActivity.value = Unit
    }

    private fun validateInput(name: String, count: Int): Boolean {
        if (name.isNotBlank() && count > 0) {
            return true
        }

        if (name.isBlank()) {
             _errorInputName.value = true
        }
        if (count <= 0) {
             _errorInputCount.value = true
        }
        return false
    }

    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun resetErrorInputCount() {
        _errorInputCount.value = false
    }
}