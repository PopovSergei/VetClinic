package ru.ac.uniyar.routes

import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.ac.uniyar.computations.AuthenticateUserViaLoginQuery
import ru.ac.uniyar.computations.AuthenticationError
import ru.ac.uniyar.computations.hashPassword
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.domain.vet.Vet
import ru.ac.uniyar.domain.vet.Vets
import ru.ac.uniyar.filters.JwtTools
import ru.ac.uniyar.handlers.AuthenticateUser
import ru.ac.uniyar.handlers.lensOrNull
import ru.ac.uniyar.models.ShowLoginFVM
import ru.ac.uniyar.models.ShowUserFVM
import ru.ac.uniyar.store.Settings
import java.util.*

fun userCreationRoute(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    users: Users,
    visitors: Users,
    vets: Vets,
    settings: Settings,
    who: String,
    htmlView: BiDiBodyLens<ViewModel>
) = routes(
    "/" bind Method.GET to showUserForm(currentUserLens, htmlView),
    "/" bind Method.POST to addUserWithLens(currentUserLens, currentAccessLens, users, visitors, vets, settings, who, htmlView)
)

fun showUserForm(currentUserLens: BiDiLens<Request, User?>, htmlView: BiDiBodyLens<ViewModel>): HttpHandler = { request->
    val currentUser = currentUserLens(request)
    Response(Status.OK).with(htmlView of ShowUserFVM(currentUser))
}

private val nameFormLens = FormField.nonEmptyString().required("name")
private val passOneFormLens = FormField.nonEmptyString().required("pass_one")
private val passTwoFormLens = FormField.nonEmptyString().required("pass_two")
private val userFormLens = Body.webForm(
    Validator.Feedback,
    nameFormLens,
    passOneFormLens,
    passTwoFormLens,
).toLens()

fun addUserWithLens(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    users: Users,
    visitors: Users,
    vets: Vets,
    settings: Settings,
    who: String,
    htmlView: BiDiBodyLens<ViewModel>,
): HttpHandler = { request ->
    var form = userFormLens(request)
    val firstPassword = lensOrNull(passOneFormLens, form)
    val secondPassword = lensOrNull(passTwoFormLens, form)

    val currentUser = currentUserLens(request)
    val currentAccess = currentAccessLens(request)

    if (firstPassword != null && firstPassword != secondPassword) {
        val newErrors = form.errors + Invalid(passOneFormLens.meta.copy(description = "Passwords should match"))
        form = form.copy(errors = newErrors)
    }

    if (form.errors.isEmpty()) {
        val hashedPass = hashPassword(firstPassword!!, settings.salt)
        val user = User(UUID.randomUUID(), nameFormLens(form), hashedPass)
        users.add(user)
        if (who == "vet") {
            if (currentAccess == "admin") {
                vets.add(Vet(user.id, user.name, hashedPass))
                //Response(Status.FOUND).header("Location", "/")
            } else {
                Response(Status.NOT_ACCEPTABLE)
            }
        } else {
            visitors.addVisitor(user)
//            val userId = authenticateUserViaLoginQuery(user.id, firstPassword)
//            val token = jwtTools.create(userId) ?: Response(Status.INTERNAL_SERVER_ERROR)
//            val authCookie = Cookie("token", token as String, httpOnly = true, sameSite = SameSite.Strict)
            //Response(Status.FOUND).header("Location", "/")//.cookie(authCookie)
        }
        Response(Status.FOUND).header("Location", "/")
    } else {
        Response(Status.OK).with(htmlView of ShowUserFVM(currentUser, form))
    }
}