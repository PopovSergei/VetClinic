package ru.ac.uniyar.models

import ru.ac.uniyar.domain.registry.Registry
import ru.ac.uniyar.domain.user.User

class FullRegistryVM(currentUser: User?, val registry: Registry, val idString: String): AuthenticatedViewModel(currentUser)
