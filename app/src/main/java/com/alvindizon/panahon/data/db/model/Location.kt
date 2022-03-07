package com.alvindizon.panahon.data.db.model

import androidx.room.Entity

@Entity(tableName = "savedLocations", primaryKeys = ["name"])
data class Location(val name: String, val latitude: String, val longitude: String)