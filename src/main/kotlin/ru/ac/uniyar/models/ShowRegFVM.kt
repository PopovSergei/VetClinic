package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.vet.Vet

class ShowRegFVM(currentUser: User?, val vetList: Iterable<IndexedValue<Vet>>, val idString: String, val form: WebForm = WebForm()): AuthenticatedViewModel(currentUser)
