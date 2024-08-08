package ru.ac.uniyar.routes

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.animal.Animal
import ru.ac.uniyar.domain.animal.RecAnimal
import ru.ac.uniyar.domain.note.RecNote
import ru.ac.uniyar.domain.registry.RecRegistry
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.models.ShowAnimalFVM
import java.time.LocalDateTime
import java.util.*

fun animalCreationRoute(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    recAnimal: RecAnimal,
    recRegistry: RecRegistry,
    recNote: RecNote,
    htmlView: BiDiBodyLens<ViewModel>
) = routes(
    "/" bind Method.GET to showAnimalForm(currentUserLens, currentAccessLens, htmlView),
    "/" bind Method.POST to addAnimalWithLens(currentUserLens, currentAccessLens, recAnimal, recRegistry, recNote, htmlView)
)

private val animalIdLens = Query.string().optional("animal")

fun showAnimalForm(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val currentUser = currentUserLens(request)
    val currentAccess = currentAccessLens(request)
    if (currentAccess != "user") {
        Response(Status.NOT_ACCEPTABLE)
    } else {
        Response(Status.OK).with(htmlView of ShowAnimalFVM(currentUser))
    }
}

private val kindFormLens = FormField.nonEmptyString().required("kind")
private val nameFormLens = FormField.nonEmptyString().required("name")
private val animalFormLens = Body.webForm(
    Validator.Feedback,
    kindFormLens,
    nameFormLens,
).toLens()

fun addAnimalWithLens(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    recAnimal: RecAnimal,
    recRegistry: RecRegistry,
    recNote: RecNote,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val webForm = animalFormLens(request)
    val addDate = LocalDateTime.now()
    val currentUser = currentUserLens(request)
    val currentAccess = currentAccessLens(request)
    val animalId = animalIdLens(request)

    if (currentAccess != "user") {
        Response(Status.NOT_ACCEPTABLE)
    } else {
        if(webForm.errors.isEmpty()) {
            if (animalId != null) {
                recAnimal.update(recAnimal.fetch(animalId)!!, nameFormLens(webForm), kindFormLens(webForm))
                recRegistry.updateAnimal(recAnimal.fetch(animalId)!!, nameFormLens(webForm), kindFormLens(webForm))
                recNote.updateAnimal(recAnimal.fetch(animalId)!!, nameFormLens(webForm), kindFormLens(webForm))
                Response(Status.FOUND).header("Location", "/animal/$animalId")
            } else {
                recAnimal.add(Animal(
                    UUID.randomUUID(),
                    addDate,
                    currentUser!!,
                    kindFormLens(webForm),
                    nameFormLens(webForm),
                ))
                Response(Status.FOUND).header("Location", "/user")
            }
        } else {
            Response(Status.OK).with(htmlView of ShowAnimalFVM(currentUser, webForm))
        }
    }
}