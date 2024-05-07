package com.example.shoppinglist.domain

interface ShopListRepository {

    fun deleteShopItem(shopItem: ShopItem)

    fun editShopItem(shopItem: ShopItem)

    fun getShopItem(shopItemID: Int)

    fun addShopItem(shopItem: ShopItem)

    fun getShopList(): List<ShopItem>
}