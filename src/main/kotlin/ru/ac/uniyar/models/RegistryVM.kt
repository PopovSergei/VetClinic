package ru.ac.uniyar.models

import ru.ac.uniyar.domain.registry.Registry
import ru.ac.uniyar.domain.user.User

class RegistryVM(currentUser: User?, val recList: Iterable<IndexedValue<Registry>>): AuthenticatedViewModel(currentUser)
