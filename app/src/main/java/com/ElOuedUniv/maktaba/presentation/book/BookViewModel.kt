package com.ElOuedUniv.maktaba.presentation.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.data.model.Book
import com.ElOuedUniv.maktaba.domain.usecase.AddBookUseCase
import com.ElOuedUniv.maktaba.domain.usecase.GetBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val addBookUseCase: AddBookUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<BookUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            getBooksUseCase()
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message
                        )
                    }
                }
                .collect { books ->
                    _uiState.update {
                        it.copy(
                            books = books,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onAction(action: BookUiAction) {
        when (action) {

            BookUiAction.RefreshBooks -> loadBooks()

            BookUiAction.OnAddBookClick -> {
                _uiState.update { it.copy(isAddingBook = true) }
            }

            BookUiAction.OnDismissAddBook -> {
                _uiState.update { it.copy(isAddingBook = false) }
            }

            is BookUiAction.OnAddBookConfirm -> {
                viewModelScope.launch {
                    addBookUseCase(
                        Book(
                            title = action.title,
                            isbn = action.isbn,
                            nbPages = action.nbPages
                        )
                    )

                    _uiState.update { it.copy(isAddingBook = false) }

                    _uiEvent.emit(
                        BookUiEvent.ShowSnackbar("Book added successfully")
                    )
                }
            }
        }
    }
}
