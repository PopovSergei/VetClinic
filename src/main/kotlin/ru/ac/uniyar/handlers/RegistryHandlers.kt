package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.BiDiLens
import org.http4k.routing.path
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.animal.RecAnimal
import ru.ac.uniyar.domain.registry.RecRegistry
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.models.FullRegistryVM
import ru.ac.uniyar.models.RegistryVM

class ShowRegistryHandler(
    private val recRegistry: RecRegistry,
    private val currentUserLens: BiDiLens<Request, User?>,
    private val htmlView: BiDiBodyLens<ViewModel>
    ): HttpHandler {
    override fun invoke(request: Request): Response {
        val currentUser = currentUserLens(request) ?: return Response(Status.NOT_ACCEPTABLE)
        return Response(Status.OK)
            .with(htmlView of RegistryVM(currentUser, recRegistry.fetchAll()))
    }
}

fun showFullRegistryHandler(
    recRegistry: RecRegistry,
    currentUserLens: BiDiLens<Request, User?>,
    htmlView: BiDiBodyLens<ViewModel>
    ): HttpHandler = handler@{ request ->
    val numberString = request.path("number").orEmpty()
    val number = numberString.toIntOrNull() ?: return@handler Response(Status.BAD_REQUEST)
    val registry = recRegistry.fetchOne(number) ?: return@handler Response(Status.BAD_REQUEST)
    val animal = registry.animal
    val idString = animal.id.toString()
    val currentUser = currentUserLens(request) ?: return@handler Response(Status.NOT_ACCEPTABLE)
    Response(Status.OK).with(htmlView of FullRegistryVM(currentUser, registry, idString))
}