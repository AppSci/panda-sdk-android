package com.appsci.panda.sdk.data.subscriptions.local

import androidx.room.*
import io.reactivex.Single

@Dao
abstract class PurchaseDao {

    @Query("SELECT * FROM Purchase")
    abstract fun selectPurchases(): Single<List<PurchaseEntity>>

    @Query("SELECT * FROM Purchase WHERE synced!=1")
    abstract fun selectNotSentPurchases(): Single<List<PurchaseEntity>>

    @Query("SELECT * FROM Purchase where productId=:productId")
    abstract fun selectPurchase(productId: String): PurchaseEntity?

    @Query("DELETE FROM Purchase")
    abstract fun deletePurchases()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(purchaseEntity: PurchaseEntity)

    @Query("UPDATE Purchase SET synced = 1 WHERE productId = :productId")
    abstract fun markSynced(productId: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(purchases: List<PurchaseEntity>)

    @Transaction
    open fun putPurchase(purchaseEntity: PurchaseEntity) {
        insert(purchaseEntity)
    }

    @Transaction
    open fun putPurchases(purchases: List<PurchaseEntity>) {
        insert(purchases)
    }
}
