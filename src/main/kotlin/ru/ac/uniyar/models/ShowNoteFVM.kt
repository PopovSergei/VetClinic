package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import ru.ac.uniyar.domain.animal.Animal
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.vet.Vet

class ShowNoteFVM(
    currentUser: User?,
    val animals: Iterable<IndexedValue<Animal>>,
    val form: WebForm = WebForm()
): AuthenticatedViewModel(currentUser)
