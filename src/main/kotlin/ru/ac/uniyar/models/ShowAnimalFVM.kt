package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import ru.ac.uniyar.domain.user.User

class ShowAnimalFVM(currentUser: User?, val form: WebForm = WebForm()): AuthenticatedViewModel(currentUser)
