package com.ven.proj.ui.notifications.bdwater

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_table")
class Water()
{
@PrimaryKey(autoGenerate = true)
@ColumnInfo(name = "id")
var waterInfo: Long = 0L

@ColumnInfo(name = "data")
var day: String = ""

@ColumnInfo(name = "amount")
var amount: String = ""}