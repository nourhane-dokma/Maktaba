package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Book
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor() : BookRepository {

    private val _booksList = mutableListOf(
        Book("11111", "Clean Code", 10),
        Book("22222", "The Pragmatic Programmer", 0),
        Book("33333", "Design Patterns", 0),
        Book("44444", "Refactoring", 0),
        Book("55555", "Head First Design Patterns", 0)
    )

    private val booksFlow = MutableSharedFlow<List<Book>>(replay = 1).apply {
        tryEmit(_booksList.toList())
    }

    override fun getAllBooks(): Flow<List<Book>> = flow {
        delay(2000)
        emitAll(booksFlow)
    }

    override fun getBookByIsbn(isbn: String): Book? {
        return _booksList.find { it.isbn == isbn }
    }

    override fun addBook(book: Book) {
        _booksList.add(book)
        booksFlow.tryEmit(_booksList.toList())
    }
}