package ru.ac.uniyar.domain.note

import ru.ac.uniyar.domain.animal.Animal
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.vet.Vet

class RecNote {
    private val recNote = mutableListOf<Note>()

    fun toSting() : String {
        return recNote.joinToString("\n") { rec -> rec.addDate.toString() }
    }

    fun add(rec: Note) {
        recNote.add(rec)
    }

    fun updateAnimal(animal: Animal, newName: String, newKind: String) {
        for(i in 0 until fetchAll().count()) {
            if (recNote[i].animal.id == animal.id) {
                recNote[i].animal = Animal(animal.id, animal.addDate, animal.owner, newKind, newName)
            }
        }
    }

    fun filterNote(currentUser: User?): Iterable<IndexedValue<Note>> {
        return recNote.filter { it.animal.owner == currentUser!!}.withIndex()
    }

    fun filterNoteVet(vet: Vet): Iterable<IndexedValue<Note>> {
        return recNote.filter { it.vet == vet}.withIndex()
    }

    fun filterNoteAnimal(animal: Animal): Iterable<IndexedValue<Note>> {
        return recNote.filter { it.animal == animal}.withIndex()
    }

    fun fetchOne(index: Int): Note? {
        return recNote.getOrNull(index)
    }

    fun fetchAll() : Iterable<IndexedValue<Note>> = recNote.withIndex()
}