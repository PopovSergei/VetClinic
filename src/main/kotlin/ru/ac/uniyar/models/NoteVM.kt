package ru.ac.uniyar.models

import ru.ac.uniyar.domain.note.Note
import ru.ac.uniyar.domain.user.User

class NoteVM(currentUser: User?, val recList: Iterable<IndexedValue<Note>>): AuthenticatedViewModel(currentUser)
