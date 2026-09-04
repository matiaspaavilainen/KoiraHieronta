package com.example.koirahieronta.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.koirahieronta.database.Dog

data class DogEditState(
    val dog: Dog,
    val name: TextFieldState = TextFieldState(dog.name),
    var birthDate: Long = 0L,
    val breed: TextFieldState = TextFieldState(dog.breed),
    val longTermIllness: TextFieldState = TextFieldState(dog.longTermIllness.orEmpty()),
    val hobbies: TextFieldState = TextFieldState(dog.hobbies.orEmpty()),
    val editing: MutableState<Boolean> = mutableStateOf(false)
)
