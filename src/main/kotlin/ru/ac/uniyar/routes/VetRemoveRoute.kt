package ru.ac.uniyar.routes

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.domain.vet.Vets
import ru.ac.uniyar.models.RemoveVetFVM

private val vetId = Query.string().optional("id")

fun vetRemoveRoute(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    vets: Vets,
    users: Users,
    htmlView: BiDiBodyLens<ViewModel>
) = routes(
    "/" bind Method.GET to showRemoveVetForm(currentUserLens, currentAccessLens, vets, users, htmlView),
    "/" bind Method.POST to removeVetWithLens(currentUserLens, currentAccessLens, vets, users, htmlView)
)

fun showRemoveVetForm(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    vets: Vets,
    users: Users,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val currentUser = currentUserLens(request)
    val currentAccess = currentAccessLens(request)
    val id = vetId(request)
    if (id != null) {
        if (currentAccess == "admin") {
            users.removeUser(id)
            vets.removeVet(id)
        }
        Response(Status.FOUND).header("Location", "/user")
    } else {
        Response(Status.OK).with(htmlView of RemoveVetFVM(currentUser, vets.fetchAll()))
    }
}

private val vetIdFormLens = FormField.uuid().required("vet")
private val vetFormLens = Body.webForm(
    Validator.Feedback,
    vetIdFormLens
).toLens()

fun removeVetWithLens(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    vets: Vets,
    users: Users,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val webForm = vetFormLens(request)
    val currentUser = currentUserLens(request)
    val currentAccess = currentAccessLens(request)
    if(currentAccess != "admin") {
        Response(Status.NOT_ACCEPTABLE)
    } else {
        if(webForm.errors.isEmpty()) {
            users.removeUser(vetIdFormLens(webForm).toString())
            vets.removeVet(vetIdFormLens(webForm).toString())
            Response(Status.FOUND).header("Location", "/user")
        } else {
            Response(Status.OK).with(htmlView of RemoveVetFVM(currentUser, vets.fetchAll(), webForm))
        }
    }
}