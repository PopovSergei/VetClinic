package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.BiDiLens
import org.http4k.routing.path
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.animal.RecAnimal
import ru.ac.uniyar.domain.note.RecNote
import ru.ac.uniyar.domain.registry.RecRegistry
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.models.AnimalVM
import java.time.LocalDateTime

fun showAnimalHandler(
    recAnimal: RecAnimal,
    recRegistry: RecRegistry,
    recNote: RecNote,
    currentUserLens: BiDiLens<Request, User?>,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = handler@{ request ->
    val idString = request.path("id").orEmpty()
    val animal = recAnimal.fetch(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val registry = recRegistry.filterRegistryAnimal(animal)
    val date = LocalDateTime.now()
    val registryOld = registry.filter { it.value.date <= date }
    val registryNew = registry.filter { it.value.date > date }
    val note = recNote.filterNoteAnimal(animal)
    val currentUser = currentUserLens(request) ?: return@handler Response(Status.NOT_ACCEPTABLE)
    Response(Status.OK).with(
        htmlView of AnimalVM(
            currentUser,
            animal,
            idString,
            registryNew.sortedBy { it.value.date },
            registryOld.sortedBy { it.value.date }.reversed(),
            note
        ))
}