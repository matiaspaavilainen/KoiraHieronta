package com.example.koirahieronta.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.koirahieronta.data.DogRepository
import com.example.koirahieronta.database.Dog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogViewModel @Inject constructor(
    private val dogRepository: DogRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DogState())
    private val _dogs = dogRepository.getAllDogs()

    val dogNameState = TextFieldState()
    val selectedDogName = TextFieldState(dogNameState.text.toString())
    val dogBreedState = TextFieldState()
    val longTermIllnessState = TextFieldState()
    val hobbiesState = TextFieldState()

    val state = combine(_state, _dogs) { state, dogs ->
        state.copy(dogs = dogs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DogState())

    suspend fun saveDog(ownerId: Long): LongArray {
        val dogName = dogNameState.text.toString()
        val dateOfBirth = _state.value.dateOfBirth
        val dogBreed = dogBreedState.text.toString()
        val longTermIllness = longTermIllnessState.text.toString()
        val hobbies = hobbiesState.text.toString()

        val dogId = dogRepository.createDog(
            name = dogName,
            dateOfBirth = dateOfBirth,
            breed = dogBreed,
            ownerId = ownerId,
            longTermIllness = longTermIllness,
            hobbies = hobbies,
        )

        return longArrayOf(dogId, ownerId)
    }

    fun deleteDog(dog: Dog) {
        viewModelScope.launch { dogRepository.deleteDog(dog) }
    }

    fun updateDog(dog: Dog) {
        viewModelScope.launch { dogRepository.updateDog(dog) }
        clearAllStates()
    }

    fun setDateOfBirth(date: Long) {
        _state.update { it.copy(dateOfBirth = date) }
    }

    fun setSelectedDogId(id: Long) {
        _state.update { it.copy(selectedDogId = id) }
    }

    fun clearAllStates() {
        dogNameState.clearText()
        selectedDogName.clearText()
        dogBreedState.clearText()
        longTermIllnessState.clearText()
        hobbiesState.clearText()
        _state.update {
            it.copy(
                dateOfBirth = 0L,
                selectedDogId = null,
            )
        }
    }
}