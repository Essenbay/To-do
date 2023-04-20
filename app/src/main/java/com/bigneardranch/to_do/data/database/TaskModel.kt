package com.bigneardranch.to_do.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "isSolved") val isSolved: Boolean,
    @ColumnInfo(name = "isInProgress") val isInProgress: Boolean
)