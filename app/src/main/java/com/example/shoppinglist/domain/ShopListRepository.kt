package com.example.shoppinglist.domain

interface ShopListRepository {

    fun deleteShopItem(shopItem: ShopItem)

    fun editShopItem(shopItem: ShopItem)

<<<<<<< HEAD
    fun getShopItem(shopItemID: Int): ShopItem
=======
    fun getShopItem(shopItemID: Int)
>>>>>>> 86d4456 (data layer work)

    fun addShopItem(shopItem: ShopItem)

    fun getShopList(): List<ShopItem>
}