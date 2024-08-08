package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.OK
import org.http4k.core.cookie.*
import org.http4k.lens.*
import org.http4k.template.ViewModel
import ru.ac.uniyar.computations.AuthenticateUserViaLoginQuery
import ru.ac.uniyar.computations.AuthenticationError
import ru.ac.uniyar.domain.admin.Admins
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.domain.vet.Vets
import ru.ac.uniyar.filters.JwtTools
import ru.ac.uniyar.models.ShowLoginAdminFVM
import ru.ac.uniyar.models.ShowLoginFVM
import ru.ac.uniyar.models.ShowLoginVetFVM

private val userLens = Query.string().optional("who")

class ShowLoginFormHandler(
    private val currentUserLens: BiDiLens<Request, User?>,
    private val visitors: Users,
    private val vets: Vets,
    private val admins: Admins,
    private val htmlView: BiDiBodyLens<ViewModel>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val currentUser = currentUserLens(request)
        val who = userLens(request)
        if (who == "vet") {
            return Response(OK).with(htmlView of ShowLoginVetFVM(currentUser, vets.fetchAll()))
        }
        if (who == "admin") {
            return Response(OK).with(htmlView of ShowLoginAdminFVM(currentUser, admins.fetchAll()))
        }
        return Response(OK).with(htmlView of ShowLoginFVM(currentUser, visitors.fetchAllVisitors()))
    }
}

class AuthenticateUser(
    private val currentUserLens: BiDiLens<Request, User?>,
    private val authenticateUserViaLoginQuery: AuthenticateUserViaLoginQuery,
    private val visitors: Users,
    private val vets: Vets,
    private val admins: Admins,
    private val htmlView: BiDiBodyLens<ViewModel>,
    private val jwtTools: JwtTools,
) : HttpHandler {
    companion object {
        private val loginFieldLens = FormField.uuid().required("login")
        private val passwordFieldLens = FormField.nonEmptyString().required("password")
        private val formLens = Body.webForm(
            Validator.Feedback,
            loginFieldLens,
            passwordFieldLens
        ).toLens()
    }

    override fun invoke(request: Request): Response {
        val currentUser = currentUserLens(request)
        val who = userLens(request)
        val form = formLens(request)
        if (form.errors.isNotEmpty()) {
            if (who == "vet")
                return Response(OK).with(htmlView of ShowLoginVetFVM(currentUser, vets.fetchAll(), form))
            if (who == "admin")
                return Response(OK).with(htmlView of ShowLoginAdminFVM(currentUser, admins.fetchAll(), form))
            return Response(OK).with(htmlView of ShowLoginFVM(currentUser, visitors.fetchAllVisitors(), form))
        }
        val userId = try {
            authenticateUserViaLoginQuery(loginFieldLens(form), passwordFieldLens(form))
        } catch (_: AuthenticationError) {
            val newErrors = form.errors + Invalid(
                passwordFieldLens.meta.copy(description = "Login or password should match")
            )
            val newForm = form.copy(errors = newErrors)
            if (who == "vet")
                return Response(OK).with(htmlView of ShowLoginVetFVM(currentUser, vets.fetchAll(), form))
            if (who == "admin")
                return Response(OK).with(htmlView of ShowLoginAdminFVM(currentUser, admins.fetchAll(), form))
            return Response(OK).with(htmlView of ShowLoginFVM(currentUser, visitors.fetchAllVisitors(), newForm))
        }
        val token = jwtTools.create(userId) ?: return Response(INTERNAL_SERVER_ERROR)
        val authCookie = Cookie("token", token, httpOnly = true, sameSite = SameSite.Strict)
        return Response(FOUND)
            .header("Location", "/")
            .cookie(authCookie)
    }
}

class LogOutUser : HttpHandler {
    override fun invoke(request: Request): Response {
        return Response(FOUND)
            .header("Location", "/")
            .invalidateCookie("token")
    }
}