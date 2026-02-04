package com.workingonit.nfsee.android

import com.workingonit.nfsee.android.data.NFSeeDatabase

class NFSeeApplication : android.app.Application() {
    val database: NFSeeDatabase by lazy { NFSeeDatabase.create(this) }
}
