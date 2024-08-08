package ru.ac.uniyar.models

import ru.ac.uniyar.domain.note.Note
import ru.ac.uniyar.domain.user.User

class FullNoteVM(currentUser: User?, val note: Note, val idString: String): AuthenticatedViewModel(currentUser)
