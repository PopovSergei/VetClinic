package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.BiDiLens
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.animal.RecAnimal
import ru.ac.uniyar.domain.note.RecNote
import ru.ac.uniyar.domain.registry.RecRegistry
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.vet.Vets
import ru.ac.uniyar.models.AdminVM
import ru.ac.uniyar.models.UserVM
import ru.ac.uniyar.models.VetVM
import java.time.LocalDateTime

fun showUserHandler(
    currentUserLens: BiDiLens<Request, User?>,
    currentAccessLens: BiDiLens<Request, String?>,
    vets: Vets,
    recRegistry: RecRegistry,
    recNote: RecNote,
    recAnimal: RecAnimal,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = handler@{ request ->
    val currentUser = currentUserLens(request) ?: return@handler Response(Status.NOT_ACCEPTABLE)
    val currentAccess = currentAccessLens(request)
    if (currentAccess == "vet") {
        val vet = vets.fetch(currentUser.id) ?: return@handler Response(Status.BAD_REQUEST)
        val registry = recRegistry.filterRegistryVet(vet)
        val date = LocalDateTime.now()
        val registryOld = registry.filter { it.value.date <= date }
        val registryNew = registry.filter { it.value.date > date }
        val vetAnimal = recRegistry.filterRegistryVetAnimal(vet)
        val note = recNote.filterNoteVet(vet)
        Response(Status.OK).with(
            htmlView of VetVM(
                currentUser,
                registryNew.sortedBy { it.value.date },
                registryOld.sortedBy { it.value.date }.reversed(),
                vetAnimal,
                note
            ))
    } else {
        if (currentAccess == "admin") {
            Response(Status.OK).with(htmlView of AdminVM(currentUser, vets.fetchAll()))
        } else {
            val date = LocalDateTime.now()
            val animals = recAnimal.filterAnimals(currentUser)
            val registry = recRegistry.filterRegistry(currentUser)
            val note = recNote.filterNote(currentUser)
            val registryOld = registry.filter { it.value.date <= date }
            val registryNew = registry.filter { it.value.date > date }
            Response(Status.OK).with(
                htmlView of UserVM(
                    currentUser,
                    animals,
                    note,
                    registryNew.sortedBy { it.value.date },
                    registryOld.sortedBy { it.value.date }.reversed(),
                ))
        }
    }
}