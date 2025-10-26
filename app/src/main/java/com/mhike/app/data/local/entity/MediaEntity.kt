package com.mhike.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@Entity(
    tableName = "media",
    foreignKeys = [
        ForeignKey(
            entity = HikeEntity::class,
            parentColumns = ["id"],
            childColumns = ["hikeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("hikeId")]
)
data class MediaEntity @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hikeId: Long,
    val uri: String,                   // content://… or file:// as string
    val mimeType: String? = null,
    val addedAt: Instant = Instant.fromEpochMilliseconds(System.currentTimeMillis())
)
