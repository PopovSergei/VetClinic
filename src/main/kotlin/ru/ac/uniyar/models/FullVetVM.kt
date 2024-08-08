package ru.ac.uniyar.models

import ru.ac.uniyar.domain.animal.Animal
import ru.ac.uniyar.domain.note.Note
import ru.ac.uniyar.domain.registry.Registry
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.vet.Vet

class FullVetVM(
    currentUser: User?,
    val vet: Vet,
    val registryNew: Iterable<IndexedValue<Registry>>,
    val registryOld: Iterable<IndexedValue<Registry>>,
    val note: Iterable<IndexedValue<Note>>,
    val animals: Iterable<IndexedValue<Animal>>,
): AuthenticatedViewModel(currentUser)