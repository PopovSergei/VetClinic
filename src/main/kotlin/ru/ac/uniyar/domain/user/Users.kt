package ru.ac.uniyar.domain.user

import ru.ac.uniyar.domain.EMPTY_UUID
import java.util.*

class Users {
    private val users = mutableListOf<User>()
    private val visitors = mutableListOf<User>()

    fun toSting() : String {
        return users.joinToString("\n") { rec -> rec.name }
    }

    private fun toUuid() : String {
        return users.joinToString("\n") { rec -> rec.id.toString() }
    }

    fun add(user: User) {
        var newId = user.id
        while (toUuid().contains(user.id.toString()) || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        users.add(User(newId, user.name, user.pass))
    }

    fun addVisitor(user: User) {
        var newId = user.id
        while (toUuid().contains(user.id.toString()) || newId == EMPTY_UUID) {
            newId = UUID.randomUUID()
        }
        visitors.add(User(newId, user.name, user.pass))
    }

    fun removeUser(id: String) {
        users.remove(users.find { it.id.toString() == id })
    }

    fun fetchOne(index: Int): User? {
        return users.getOrNull(index)
    }

    fun fetch(uuid: UUID): User? {
        for(i in 0 until fetchAll().count()) {
            if (users[i].id == uuid) {
                return users.getOrNull(i)
            }
        }
        return null
    }

    fun fetchVisitor(uuid: UUID): User? {
        for(i in 0 until fetchAllVisitors().count()) {
            if (visitors[i].id == uuid) {
                return visitors.getOrNull(i)
            }
        }
        return null
    }

    fun fetchAll() : Iterable<IndexedValue<User>> = users.withIndex()

    fun fetchAllVisitors() : Iterable<IndexedValue<User>> = visitors.withIndex()
}