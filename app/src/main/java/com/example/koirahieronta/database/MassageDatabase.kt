package com.example.koirahieronta.database

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    entities = [Dog::class, Massage::class, Client::class], version = 1, exportSchema = false
)
@ColumnTypeConverters(Converters::class)
abstract class MassageDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDAO
    abstract fun dogDao(): DogDAO
    abstract fun massageDao(): MassageDAO
}