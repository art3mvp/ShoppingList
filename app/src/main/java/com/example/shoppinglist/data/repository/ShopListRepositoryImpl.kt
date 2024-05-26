package com.example.shoppinglist.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.shoppinglist.data.database.ShopListDao
import com.example.shoppinglist.data.mapper.ShopListMapper
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.ShopListRepository
import javax.inject.Inject

class ShopListRepositoryImpl @Inject constructor(
    private val shopListDao: ShopListDao,
    private val mapper: ShopListMapper
) : ShopListRepository {

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        shopListDao.removeShopItem(shopItem.id)
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        addShopItem(shopItem)
    }

    override suspend fun getShopItem(shopItemID: Int): ShopItem {
        return mapper.mapDbModelToEntity(
            shopListDao.getShopItem(shopItemID)
        )
    }

    override suspend fun addShopItem(shopItem: ShopItem) {
        shopListDao.addShopItem(
            mapper.mapEntityToDbModel(shopItem)
        )
    }

    override fun getShopList(): LiveData<List<ShopItem>> = shopListDao.getShopList().map {
        mapper.mapListDbModelToListEntity(it)
    }
}