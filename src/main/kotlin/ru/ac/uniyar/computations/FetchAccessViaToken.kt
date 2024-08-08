package ru.ac.uniyar.computations

import ru.ac.uniyar.domain.admin.Admins
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.domain.vet.Vets
import java.util.*

class FetchAccessViaToken(
    private val visitors: Users,
    private val vets: Vets,
    private val admins: Admins
) {
    operator fun invoke(token: String): String? {
        val uuid = try {
            UUID.fromString(token)
        } catch (_: IllegalArgumentException) {
            return null
        }
        if (vets.fetch(uuid) != null) {
            return "vet"
        }
        if (admins.fetch(uuid) != null) {
            return "admin"
        }
        if (visitors.fetchVisitor(uuid) != null) {
            return "user"
        }
        return "anon"
    }
}