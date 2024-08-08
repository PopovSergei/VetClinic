package ru.ac.uniyar.models

import ru.ac.uniyar.domain.animal.Animal
import ru.ac.uniyar.domain.note.Note
import ru.ac.uniyar.domain.registry.Registry
import ru.ac.uniyar.domain.user.User

class VetVM(
    currentUser: User?,
    val registryNew: Iterable<IndexedValue<Registry>>,
    val registryOld: Iterable<IndexedValue<Registry>>,
    val animals: Iterable<IndexedValue<Animal>>,
    val note: Iterable<IndexedValue<Note>>
): AuthenticatedViewModel(currentUser)