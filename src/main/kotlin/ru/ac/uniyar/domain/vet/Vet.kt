package ru.ac.uniyar.domain.vet

import java.util.UUID

data class Vet(
    val id: UUID,
    val name: String,
    val pass: String
)