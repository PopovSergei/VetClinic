package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.BiDiLens
import org.http4k.routing.path
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.note.RecNote
import ru.ac.uniyar.domain.registry.RecRegistry
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.vet.Vets
import ru.ac.uniyar.models.FullVetVM
import ru.ac.uniyar.models.VetsVM
import java.time.LocalDateTime
import java.util.*

class ShowVetsHandler(
    private val currentUserLens: BiDiLens<Request, User?>,
    private val vets: Vets,
    private val htmlView: BiDiBodyLens<ViewModel>
): HttpHandler {
    override fun invoke(request: Request): Response {
        val currentUser = currentUserLens(request)
        return Response(Status.OK)
            .with(htmlView of VetsVM(currentUser, vets.fetchAll()))
    }
}

fun showVetHandler(
    currentUserLens: BiDiLens<Request, User?>,
    vets: Vets,
    recRegistry: RecRegistry,
    recNote: RecNote,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = handler@{ request ->
    val idString = request.path("id").orEmpty()
    val currentUser = currentUserLens(request)
    val vet = vets.fetch(UUID.fromString(idString)) ?: return@handler Response(Status.BAD_REQUEST)
    val registry = recRegistry.filterRegistryVet(vet)
    val date = LocalDateTime.now()
    val registryOld = registry.filter { it.value.date <= date }
    val registryNew = registry.filter { it.value.date > date }
    val note = recNote.filterNoteVet(vet)
    val vetAnimal = recRegistry.filterRegistryVetAnimal(vet)
    Response(Status.OK).with(
        htmlView of FullVetVM(
            currentUser,
            vet,
            registryNew.sortedBy { it.value.date },
            registryOld.sortedBy { it.value.date }.reversed(),
            note,
            vetAnimal
        ))
}