package com.example.shoppinglist.domain

interface ShopListRepository {

    fun deleteShopItem(shopItem: ShopItem)

    fun editShopItem(shopItem: ShopItem)
}