package com.appsci.panda.sdk.data.subscriptions.local

import io.reactivex.Single

interface PurchasesLocalStore {

    fun getPurchases(): Single<List<PurchaseEntity>>

    fun getNotSentPurchases(): Single<List<PurchaseEntity>>

    fun markSynced(id: String)

    fun savePurchases(purchases: List<PurchaseEntity>)
}

class PurchasesLocalStoreImpl(private val purchaseDao: PurchaseDao) : PurchasesLocalStore {

    override fun getPurchases(): Single<List<PurchaseEntity>> =
            purchaseDao.selectPurchases()

    override fun getNotSentPurchases(): Single<List<PurchaseEntity>> =
            purchaseDao.selectNotSentPurchases()

    override fun markSynced(id: String) {
        purchaseDao.markSynced(id)
    }

    override fun savePurchases(purchases: List<PurchaseEntity>) {
        purchaseDao.putPurchases(purchases)
    }

}
