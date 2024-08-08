package ru.ac.uniyar.routes

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.animal.RecAnimal
import ru.ac.uniyar.domain.note.Note
import ru.ac.uniyar.domain.note.RecNote
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.vet.Vets
import ru.ac.uniyar.models.ShowNoteFVM
import ru.ac.uniyar.models.ShowNoteNewFVM
import java.time.LocalDateTime

fun noteCreationRoute(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    recNote: RecNote,
    recAnimal: RecAnimal,
    vets: Vets,
    htmlView: BiDiBodyLens<ViewModel>
) = routes(
    "/" bind Method.GET to showNoteForm(currentUserLens, currentAccessLens, recAnimal, htmlView),
    "/" bind Method.POST to addNoteWithLens(currentUserLens, currentAccessLens, recNote, recAnimal, vets, htmlView)
)

private val animalNoteIdLens = Query.string().optional("animal")

fun showNoteForm(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    recAnimal: RecAnimal,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = handler@{ request ->
    val animalId = animalNoteIdLens(request)
    val currentUser = currentUserLens(request) ?: return@handler Response(Status.NOT_ACCEPTABLE)
    val currentAccess = currentAccessLens(request)
    if (currentAccess != "vet")
        Response(Status.NOT_ACCEPTABLE)
    else {
        if (animalId == null) {
            Response(Status.OK).with(htmlView of ShowNoteFVM(currentUser,recAnimal.fetchAll()))
        }
        else {
            Response(Status.OK).with(htmlView of ShowNoteNewFVM(currentUser, animalId))
        }
    }
}

private val dateTimeFormLens = FormField.dateTime().required("date")
private val animalFormLens = FormField.uuid().required("animal")
private val conclusionFormLens = FormField.nonEmptyString().required("conclusion")
private val checkFormLens = FormField.nonEmptyString().required("check")

private val noteFormLens = Body.webForm(
    Validator.Feedback,
    dateTimeFormLens,
    animalFormLens,
    conclusionFormLens,
    checkFormLens,
).toLens()
private val noteNewFormLens = Body.webForm(
    Validator.Feedback,
    dateTimeFormLens,
    conclusionFormLens,
    checkFormLens,
).toLens()

fun addNoteWithLens(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    recNote: RecNote,
    recAnimal: RecAnimal,
    vets: Vets,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val addDate = LocalDateTime.now()
    val idString =  animalNoteIdLens(request)
    val currentUser = currentUserLens(request)
    val currentAccess = currentAccessLens(request)

    if (currentAccess != "vet") {
        Response(Status.NOT_ACCEPTABLE)
    } else {
        if (idString == null) {
            val webForm = noteFormLens(request)
            if(webForm.errors.isEmpty()) {
                val animal = recAnimal.fetch(animalFormLens(webForm).toString())

                recNote.add(Note(
                    addDate,
                    animal!!,
                    dateTimeFormLens(webForm),
                    vets.fetch(currentUser!!.id)!!,
                    conclusionFormLens(webForm),
                    checkFormLens(webForm),
                ))
                Response(Status.FOUND).header("Location", "/note")
            } else {
                Response(Status.OK).with(htmlView of ShowNoteFVM(currentUser, recAnimal.fetchAll(), webForm))
            }
        }
        else {
            val webForm = noteNewFormLens(request)
            val animal = recAnimal.fetch(idString)!!

            if(webForm.errors.isEmpty()) {
                recNote.add(Note(
                    addDate,
                    animal,
                    dateTimeFormLens(webForm),
                    vets.fetch(currentUser!!.id)!!,
                    conclusionFormLens(webForm),
                    checkFormLens(webForm),
                ))
                Response(Status.FOUND).header("Location", "/animal/$idString")
            } else {
                Response(Status.OK).with(htmlView of ShowNoteNewFVM(currentUser, idString, webForm))
            }
        }
    }
}