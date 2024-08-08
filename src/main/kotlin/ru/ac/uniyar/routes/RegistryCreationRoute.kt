package ru.ac.uniyar.routes

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.animal.RecAnimal
import ru.ac.uniyar.domain.registry.RecRegistry
import ru.ac.uniyar.domain.registry.Registry
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.vet.Vets
import ru.ac.uniyar.models.ShowRegFVM
import ru.ac.uniyar.models.ShowRegistryFVM
import java.time.LocalDateTime

fun registryCreationRoute(
    currentUserLens: BiDiLens<Request, User?>,
    recRegistry: RecRegistry,
    recAnimal: RecAnimal,
    vet: Vets,
    htmlView: BiDiBodyLens<ViewModel>
) = routes(
    "/" bind Method.GET to showRegistryForm(currentUserLens, vet, recAnimal, htmlView),
    "/" bind Method.POST to addRegistryWithLens(currentUserLens, recRegistry, recAnimal, vet, htmlView),
)

private val animalRegIdLens = Query.string().optional("animal")

fun showRegistryForm(
    currentUserLens: BiDiLens<Request, User?>,
    vets: Vets,
    recAnimal: RecAnimal,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = handler@{ request ->
    val animalId = animalRegIdLens(request)
    val currentUser = currentUserLens(request) ?: return@handler Response(Status.NOT_ACCEPTABLE)
    val animals = recAnimal.filterAnimals(currentUser)
    if (animalId == null) {
        Response(Status.OK).with(htmlView of ShowRegistryFVM(currentUser, vets.fetchAll(), animals))
    }
    else {
        Response(Status.OK).with(htmlView of ShowRegFVM(currentUser, vets.fetchAll(), animalId))
    }
}

private val dateTimeFormLens = FormField.dateTime().required("date")
private val vetFormLens = FormField.uuid().required("vet")
private val animalFormLens = FormField.uuid().required("animal")

private val registryFormLens = Body.webForm(
    Validator.Feedback,
    dateTimeFormLens,
    vetFormLens,
    animalFormLens,
).toLens()
private val regFormLens = Body.webForm(
    Validator.Feedback,
    dateTimeFormLens,
    vetFormLens,
).toLens()

fun addRegistryWithLens(
    currentUserLens: BiDiLens<Request, User?>,
    recRegistry: RecRegistry,
    recAnimal: RecAnimal,
    vets: Vets,
    htmlView: BiDiBodyLens<ViewModel>
): HttpHandler = { request ->
    val addDate = LocalDateTime.now()
    val idString = animalRegIdLens(request)
    val currentUser = currentUserLens(request)

    if (idString == null) {
        val webForm = registryFormLens(request)
        if(webForm.errors.isEmpty()) {
            val animal = recAnimal.fetch(animalFormLens(webForm).toString())
            recRegistry.add(Registry(
                    addDate,
                    vets.fetch(vetFormLens(webForm))!!,
                    dateTimeFormLens(webForm),
                    animal!!,
            ))
            Response(Status.FOUND).header("Location", "/user")
        } else {
            val animals = recAnimal.filterAnimals(currentUser)
            Response(Status.OK).with(htmlView of ShowRegistryFVM(currentUser, vets.fetchAll(), animals, webForm))
        }
    }
    else {
        val webForm = regFormLens(request)
        val animal1 = recAnimal.fetch(idString)!!
        if (currentUser!!.id != animal1.owner.id) {
            Response(Status.NOT_ACCEPTABLE)
        } else {
            if(webForm.errors.isEmpty()) {
                recRegistry.add(Registry(
                    addDate,
                    vets.fetch(vetFormLens(webForm))!!,
                    dateTimeFormLens(webForm),
                    animal1,
                ))
                Response(Status.FOUND).header("Location", "/animal/$idString")
            } else {
                Response(Status.OK).with(htmlView of ShowRegFVM(currentUser, vets.fetchAll(), idString, webForm))
            }
        }
    }
}