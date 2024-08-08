package ru.ac.uniyar.domain.animal

import ru.ac.uniyar.domain.user.User
import java.time.LocalDateTime
import java.util.UUID

data class Animal(
    val id: UUID,
    val addDate: LocalDateTime,
    val owner: User,
    val kind: String,
    val name: String
)