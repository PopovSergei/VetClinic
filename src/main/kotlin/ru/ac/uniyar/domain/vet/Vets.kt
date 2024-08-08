package ru.ac.uniyar.domain.vet

import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.user.User
import java.util.*

class Vets {
    private val vets = mutableListOf<Vet>()

    private fun toUuid() : String {
        return vets.joinToString("\n") { rec -> rec.id.toString() }
    }

    fun add(vet: Vet) {
        var newId = vet.id
        while (toUuid().contains(vet.id.toString()) || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        vets.add(Vet(newId, vet.name, vet.pass))
    }

    fun removeVet(id: String) {
        vets.remove(vets.find { it.id.toString() == id })
    }

    fun fetchOne(index: Int): Vet? {
        return vets.getOrNull(index)
    }

    fun fetch(uuid: UUID): Vet? {
        for(i in 0 until fetchAll().count()) {
            if (vets[i].id == uuid) {
                return vets.getOrNull(i)
            }
        }
        return null
    }

    fun fetchAll() : Iterable<IndexedValue<Vet>> = vets.withIndex()
}