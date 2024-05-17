package com.example.shoppinglist.domain

import androidx.lifecycle.LiveData

interface ShopListRepository {

    suspend fun deleteShopItem(shopItem: ShopItem)

    suspend fun editShopItem(shopItem: ShopItem)

    suspend fun getShopItem(shopItemID: Int): ShopItem

    suspend fun addShopItem(shopItem: ShopItem)

    fun getShopList(): LiveData<List<ShopItem>>
}