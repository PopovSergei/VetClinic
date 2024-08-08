package ru.ac.uniyar

import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.filter.ServerFilters
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.BiDiLens
import org.http4k.lens.RequestContextKey
import org.http4k.routing.*
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.PebbleTemplates
import org.http4k.template.ViewModel
import org.http4k.template.viewModel
import ru.ac.uniyar.computations.AuthenticateUserViaLoginQuery
import ru.ac.uniyar.computations.FetchAccessViaToken
import ru.ac.uniyar.computations.FetchUserViaToken
import ru.ac.uniyar.domain.InitSome
import ru.ac.uniyar.domain.admin.Admins
import ru.ac.uniyar.domain.animal.RecAnimal
import ru.ac.uniyar.domain.note.RecNote
import ru.ac.uniyar.domain.registry.RecRegistry
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.domain.vet.Vets
import ru.ac.uniyar.filters.JwtTools
import ru.ac.uniyar.filters.accessFilter
import ru.ac.uniyar.filters.authenticationFilter
import ru.ac.uniyar.filters.showErrorMessageFilter
import ru.ac.uniyar.handlers.*
import ru.ac.uniyar.routes.*
import ru.ac.uniyar.store.Settings
import ru.ac.uniyar.store.SettingsFileError
import kotlin.io.path.Path

fun app(recRegistry: RecRegistry,
        recNote: RecNote,
        recAnimal: RecAnimal,
        vets: Vets,
        users: Users,
        visitors: Users,
        admins: Admins,
        settings: Settings,
        currentUserLens: BiDiLens<Request, User?>,
        currentAccessLens: BiDiLens<Request, String?>,
        authenticateUserViaLoginQuery: AuthenticateUserViaLoginQuery,
        html: BiDiBodyLens<ViewModel>,
        jwtTools: JwtTools
): HttpHandler = routes(
    static(ResourceLoader.Classpath("/ru/ac/uniyar/public/")),
    "/" bind GET to RedirectToMainHandler(),
    "/main" bind GET to ShowMainHandler(currentUserLens, html),

    "/registry" bind GET to ShowRegistryHandler(recRegistry, currentUserLens, html),
    "/registry/new" bind registryCreationRoute(currentUserLens, recRegistry, recAnimal, vets, html),
    "/registry/{number}" bind GET to showFullRegistryHandler(recRegistry, currentUserLens, html),

    "/note" bind GET to ShowNoteHandler(recNote, currentUserLens, currentAccessLens, html),
    "/note/new" bind noteCreationRoute(currentUserLens, currentAccessLens, recNote, recAnimal, vets, html),
    "/note/{number}" bind GET to showFullNoteHandler(recNote, currentUserLens, currentAccessLens, html),

    "/animal/new" bind animalCreationRoute(currentUserLens, currentAccessLens, recAnimal, recRegistry, recNote, html),
    "/animal/{id}" bind GET to showAnimalHandler(recAnimal, recRegistry, recNote, currentUserLens, html),
    "/updateAnimal" bind animalCreationRoute(currentUserLens, currentAccessLens, recAnimal, recRegistry, recNote, html),

    "/vets" bind GET to ShowVetsHandler(currentUserLens, vets, html),
    "/vet/{id}" bind GET to showVetHandler(currentUserLens, vets, recRegistry, recNote, html),
    "/addVet" bind userCreationRoute(currentUserLens, currentAccessLens, users, visitors, vets, settings,"vet", html),
    "/removeVet" bind vetRemoveRoute(currentUserLens, currentAccessLens, vets, users, html),

    "/user" bind GET to showUserHandler(currentUserLens, currentAccessLens, vets, recRegistry, recNote, recAnimal, html),
    "/user/new" bind userCreationRoute(currentUserLens, currentAccessLens, users, visitors, vets, settings, "", html),

    "/login" bind GET to ShowLoginFormHandler(currentUserLens, visitors, vets, admins, html),
    "/login" bind Method.POST to AuthenticateUser(currentUserLens, authenticateUserViaLoginQuery, visitors, vets, admins, html, jwtTools),
    "/logout" bind GET to LogOutUser(),
)

fun main() {
    val settings = try {
        Settings(Path("settings.json"))
    } catch (error: SettingsFileError) {
        println(error.message)
        return
    }

    val recRegistry = RecRegistry()
    val recNote = RecNote()
    val vets = Vets()
    val recAnimal = RecAnimal()
    val users = Users()
    val visitors = Users()
    val admins = Admins()
    InitSome().initSome(recRegistry, recNote, vets, recAnimal, users, visitors, admins, settings)

    /** Пароль от всех аккаунтов - 123 **/

    val contexts = RequestContexts()
    val currentUserLens = RequestContextKey.optional<User>(contexts)
    val currentAccessLens = RequestContextKey.optional<String>(contexts)

    val authenticateUserViaLoginQuery = AuthenticateUserViaLoginQuery(users, settings)
    val fetchUserViaToken = FetchUserViaToken(users)
    val fetchAccessViaToken = FetchAccessViaToken(visitors, vets, admins)

    val jwtTools = JwtTools(settings.salt, "ru.ac.uniyar.WebApplication")

    val renderer = PebbleTemplates().HotReload("src/main/resources")
    val htmlView = Body.viewModel(renderer, ContentType.TEXT_HTML).toLens()
    val printingApp: HttpHandler =
        ServerFilters.InitialiseRequestContext(contexts)
            .then(showErrorMessageFilter(currentUserLens, renderer))
            .then(authenticationFilter(currentUserLens, fetchUserViaToken, jwtTools))
            .then(accessFilter(currentAccessLens, fetchAccessViaToken, jwtTools))
            .then(app(recRegistry, recNote, recAnimal, vets, users, visitors, admins, settings, currentUserLens, currentAccessLens, authenticateUserViaLoginQuery, htmlView, jwtTools))
    val server = printingApp.asServer(Undertow(9000)).start()
    println("Server started on http://localhost:" + server.port())
}