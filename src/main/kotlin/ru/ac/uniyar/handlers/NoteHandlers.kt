package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.BiDiLens
import org.http4k.routing.path
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.animal.RecAnimal
import ru.ac.uniyar.domain.note.RecNote
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.models.FullNoteVM
import ru.ac.uniyar.models.NoteVM

class ShowNoteHandler(
    private val recNote: RecNote,
    private val currentUserLens: BiDiLens<Request, User?>,
    private val currentAccessLens: BiDiLens<Request, String?>,
    private val htmlView: BiDiBodyLens<ViewModel>
): HttpHandler {
    override fun invoke(request: Request): Response {
        val currentUser = currentUserLens(request) ?: return Response(Status.NOT_ACCEPTABLE)
        val currentAccess = currentAccessLens(request)
        if (currentAccess != "vet" && currentAccess != "admin")
            return Response(Status.NOT_ACCEPTABLE)
        return Response(Status.OK)
            .with(htmlView of NoteVM(currentUser, recNote.fetchAll().sortedBy { it.value.date }.reversed()))
    }
}

fun showFullNoteHandler(
    recNote: RecNote,
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = handler@{ request ->
    val numberString = request.path("number").orEmpty()
    val number = numberString.toIntOrNull() ?: return@handler Response(Status.BAD_REQUEST)
    val note = recNote.fetchOne(number) ?: return@handler Response(Status.BAD_REQUEST)
    val animal = note.animal
    val idString = animal.id.toString()
    val currentUser = currentUserLens(request) ?: return@handler Response(Status.NOT_ACCEPTABLE)
    val currentAccess = currentAccessLens(request)
    if (currentAccess != "vet" && currentAccess != "admin")
        Response(Status.NOT_ACCEPTABLE)
    else
        Response(Status.OK).with(htmlView of FullNoteVM(currentUser, note, idString))
}