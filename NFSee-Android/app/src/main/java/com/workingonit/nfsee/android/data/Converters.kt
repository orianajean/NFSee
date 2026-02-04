package com.workingonit.nfsee.android.data

import androidx.room.TypeConverter
import com.workingonit.nfsee.android.data.entity.ItemStatus

class Converters {
    @TypeConverter
    fun fromItemStatus(value: ItemStatus): String = value.name

    @TypeConverter
    fun toItemStatus(value: String): ItemStatus = enumValueOf(value)
}
