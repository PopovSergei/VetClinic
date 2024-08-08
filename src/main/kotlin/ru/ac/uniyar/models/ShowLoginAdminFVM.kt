package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import ru.ac.uniyar.domain.admin.Admin
import ru.ac.uniyar.domain.user.User

class ShowLoginAdminFVM(currentUser: User?, val admins: Iterable<IndexedValue<Admin>>, val form: WebForm = WebForm()) : AuthenticatedViewModel(currentUser)