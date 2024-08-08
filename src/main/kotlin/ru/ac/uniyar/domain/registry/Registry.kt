package ru.ac.uniyar.domain.registry

import ru.ac.uniyar.domain.animal.Animal
import ru.ac.uniyar.domain.vet.Vet
import java.time.LocalDateTime

data class Registry(
    val addDate: LocalDateTime,
    val vet: Vet,
    val date: LocalDateTime,
    var animal: Animal
)
