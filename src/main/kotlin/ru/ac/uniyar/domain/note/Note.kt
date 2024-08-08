package ru.ac.uniyar.domain.note

import ru.ac.uniyar.domain.animal.Animal
import ru.ac.uniyar.domain.vet.Vet
import java.time.LocalDateTime

data class Note(
    val addDate: LocalDateTime,
    var animal: Animal,
    val date: LocalDateTime,
    val vet: Vet,
    val conclusion: String,
    val check: String,
)
