package com.unisoc.engineermode.core.impl.nonpublic

import android.content.Context
import android.os.storage.StorageManager
import com.unisoc.engineermode.core.common.appCtx

object StorageManagerProxy {

    private var storageManagerObject: StorageManager =
        appCtx.getSystemService(Context.STORAGE_SERVICE) as StorageManager


    @JvmStatic
    fun getPrimaryStorageSize(): Long {
        return StorageManager::class.java.getMethod("getPrimaryStorageSize")
            .invoke(storageManagerObject) as Long
    }
}