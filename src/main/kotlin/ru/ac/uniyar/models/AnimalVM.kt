package ru.ac.uniyar.models

import ru.ac.uniyar.domain.animal.Animal
import ru.ac.uniyar.domain.note.Note
import ru.ac.uniyar.domain.registry.Registry
import ru.ac.uniyar.domain.user.User

class AnimalVM(
    currentUser: User?,
    val animal: Animal,
    val idString: String,
    val registryNew: Iterable<IndexedValue<Registry>>,
    val registryOld: Iterable<IndexedValue<Registry>>,
    val matchNote: Iterable<IndexedValue<Note>>,
    ): AuthenticatedViewModel(currentUser)