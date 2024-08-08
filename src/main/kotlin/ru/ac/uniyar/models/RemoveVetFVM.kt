package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.vet.Vet

class RemoveVetFVM(currentUser: User?, val vets: Iterable<IndexedValue<Vet>>, val form: WebForm = WebForm()): AuthenticatedViewModel(currentUser)