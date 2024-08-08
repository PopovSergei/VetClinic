package ru.ac.uniyar.domain

import ru.ac.uniyar.computations.hashPassword
import ru.ac.uniyar.domain.admin.Admins
import ru.ac.uniyar.domain.animal.Animal
import ru.ac.uniyar.domain.animal.RecAnimal
import ru.ac.uniyar.domain.note.Note
import ru.ac.uniyar.domain.note.RecNote
import ru.ac.uniyar.domain.registry.RecRegistry
import ru.ac.uniyar.domain.registry.Registry
import ru.ac.uniyar.domain.user.User
import ru.ac.uniyar.domain.user.Users
import ru.ac.uniyar.domain.vet.Vets
import ru.ac.uniyar.domain.vet.Vet
import ru.ac.uniyar.store.Settings
import java.time.LocalDateTime
import java.util.*

class InitSome {
    fun initSome(
        recRegistry: RecRegistry,
        recNote: RecNote,
        vets: Vets,
        recAnimal: RecAnimal,
        users: Users,
        visitors: Users,
        admins: Admins,
        settings: Settings,
    ) {
        val user1 = User(UUID.randomUUID(), "Петя", hashPassword("123", settings.salt))
        val user2 = User(UUID.randomUUID(), "Вася", hashPassword("123", settings.salt))
        val user3 = User(UUID.randomUUID(), "Админ", hashPassword("123", settings.salt))
        users.add(user1)
        users.add(user2)
        users.add(user3)
        visitors.addVisitor(user1)
        visitors.addVisitor(user2)
        admins.addUser(user3)


        recAnimal.add(Animal(UUID.randomUUID(), LocalDateTime.now(), users.fetchOne(0)!!, "Собака", "Мухтар"))
        recAnimal.add(Animal(UUID.randomUUID(), LocalDateTime.now(),users.fetchOne(1)!!,"Собака","Шарик"))

        vets.add(Vet(EMPTY_UUID,"Анновна Анна", hashPassword("123", settings.salt)))
        vets.add(Vet(EMPTY_UUID, "Сергеев Сергей", hashPassword("123", settings.salt)))
        vets.add(Vet(EMPTY_UUID,"Ольговна Ольга", hashPassword("123", settings.salt)))
        vets.add(Vet(EMPTY_UUID,"Андреев Андрей", hashPassword("123", settings.salt)))
        vets.add(Vet(EMPTY_UUID,"Машина Маша", hashPassword("123", settings.salt)))
        vets.add(Vet(EMPTY_UUID,"Владов Влад", hashPassword("123", settings.salt)))
        vets.add(Vet(EMPTY_UUID,"Екатеринина Екатерина", hashPassword("123", settings.salt)))
        vets.add(Vet(EMPTY_UUID,"Кириллов Кирилл", hashPassword("123", settings.salt)))
        vets.add(Vet(EMPTY_UUID,"Александровна Александра", hashPassword("123", settings.salt)))
        vets.add(Vet(EMPTY_UUID,"Дмитров Дмитрий", hashPassword("123", settings.salt)))

        for (i in 0 until vets.fetchAll().count()) {
            users.add(User(vets.fetchOne(i)!!.id, vets.fetchOne(i)!!.name, vets.fetchOne(i)!!.pass))
        }

        recRegistry.add(
            Registry(
            LocalDateTime.now(),
            vets.fetchOne(0)!!,
            LocalDateTime.of(2020, 4, 11, 10, 30),
            recAnimal.fetchOne(0)!!)
        )

        recNote.add(
            Note(
            LocalDateTime.now(),
            recAnimal.fetchOne(0)!!,
            LocalDateTime.of(2020, 4, 11, 10, 30),
            vets.fetchOne(0)!!,
            "Здоров",
            "1000")
        )
        recNote.add(
            Note(
            LocalDateTime.now(),
            recAnimal.fetchOne(1)!!,
            LocalDateTime.of(2022, 4, 11, 11, 35),
            vets.fetchOne(1)!!,
            "Здоров",
            "1000")
        )
    }
}