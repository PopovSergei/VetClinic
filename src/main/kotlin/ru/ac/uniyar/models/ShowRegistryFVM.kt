package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import ru.ac.uniyar.domain.animal.Animal
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.vet.Vet

class ShowRegistryFVM(currentUser: User?, val vetList: Iterable<IndexedValue<Vet>>, val animals: Iterable<IndexedValue<Animal>>, val form: WebForm = WebForm()): AuthenticatedViewModel(currentUser)
