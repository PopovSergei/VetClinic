package ru.ac.uniyar.domain.registry

import ru.ac.uniyar.domain.animal.Animal
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.vet.Vet

class RecRegistry {
    val recRegistry = mutableListOf<Registry>()
    val vetAnimals = mutableListOf<Animal>()

    fun toSting() : String {
        return recRegistry.joinToString("\n") { rec -> rec.addDate.toString() }
    }

    fun add(rec: Registry) {
        recRegistry.add(rec)
    }

    fun updateAnimal(animal: Animal, newName: String, newKind: String) {
        for(i in 0 until fetchAll().count()) {
            if (recRegistry[i].animal.id == animal.id) {
                recRegistry[i].animal = Animal(animal.id, animal.addDate, animal.owner, newKind, newName)
            }
        }
    }

    fun filterRegistry(currentUser: User?): Iterable<IndexedValue<Registry>> {
        return recRegistry.filter { it.animal.owner == currentUser!!}.withIndex()
    }

    fun filterRegistryVet(vet: Vet): Iterable<IndexedValue<Registry>> {
        return recRegistry.filter { it.vet == vet}.withIndex()
    }

    fun filterRegistryAnimal(animal: Animal): Iterable<IndexedValue<Registry>> {
        return recRegistry.filter { it.animal == animal}.withIndex()
    }

    fun filterRegistryVetAnimal(vet: Vet): Iterable<IndexedValue<Animal>> {
        val rec = recRegistry.filter { it.vet == vet}
        for(i in 0 until rec.count()) {
            if (!vetAnimals.contains(rec[i].animal))
                vetAnimals.add(rec[i].animal)
        }
        return vetAnimals.withIndex()
    }

    fun fetchOne(index: Int): Registry? {
        return recRegistry.getOrNull(index)
    }

    fun fetchAll() : Iterable<IndexedValue<Registry>> = recRegistry.withIndex()
}