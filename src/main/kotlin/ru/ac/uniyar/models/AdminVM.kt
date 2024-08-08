package ru.ac.uniyar.models

import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.vet.Vet

class AdminVM(currentUser: User?, val vets: Iterable<IndexedValue<Vet>>): AuthenticatedViewModel(currentUser)