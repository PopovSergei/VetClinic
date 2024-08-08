package ru.ac.uniyar.domain.animal

import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.user.User
import java.util.UUID

class RecAnimal {
    private val recAnimal = mutableListOf<Animal>()

    fun toSting(): String {
        return recAnimal.joinToString("\n") { rec -> rec.addDate.toString() }
    }

    private fun toUuid(): String {
        return recAnimal.joinToString("\n") { rec -> rec.id.toString() }
    }

    fun add(animal: Animal) {
        var newId = animal.id
        while (toUuid().contains(animal.id.toString()) || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        recAnimal.add(animal)
    }

    fun update(animal: Animal, newName: String, newKind: String) {
        for(i in 0 until fetchAll().count()) {
            if (recAnimal[i].id == animal.id) {
                recAnimal[i] = Animal(animal.id, animal.addDate, animal.owner, newKind, newName)
            }
        }
    }

    fun filterAnimals(currentUser: User?): Iterable<IndexedValue<Animal>> {
        return recAnimal.filter { it.owner == currentUser!!}.withIndex()
    }

    fun fetchOne(index: Int): Animal? {
        return recAnimal.getOrNull(index)
    }

    fun fetch(uuid: String): Animal? {
        for(i in 0 until fetchAll().count()) {
            if (recAnimal[i].id.toString() == uuid) {
                return recAnimal.getOrNull(i)
            }
        }
        return null
    }

    fun fetchAll(): Iterable<IndexedValue<Animal>> = recAnimal.withIndex()
}