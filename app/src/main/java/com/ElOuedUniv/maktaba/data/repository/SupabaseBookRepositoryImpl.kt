package com.ElOuedUniv.maktaba.data.repository

import android.content.Context
import android.net.Uri
import com.ElOuedUniv.maktaba.data.model.Book
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class SupabaseBookRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
    @ApplicationContext private val context: Context
) : BookRepository {

    override fun getAllBooks(): Flow<List<Book>> = flow {
        try {
            val books = supabase.postgrest["books"]
                .select()
                .decodeList<Book>()
            android.util.Log.d("SUPABASE", "Books fetched: ${books.size}")
            emit(books)
        } catch (e: Exception) {
            android.util.Log.e("SUPABASE", "Error: ${e.message}")
            emit(emptyList())
        }
    }

    override fun getBookByIsbn(isbn: String): Book? = null

    override suspend fun addBook(book: Book) {
        val imageUrl = book.imageUrl?.let { uriString ->
            uploadImage(Uri.parse(uriString))
        }
        val bookToSave = book.copy(imageUrl = imageUrl)
        supabase.postgrest["books"].insert(bookToSave)
    }

    private suspend fun uploadImage(uri: Uri): String? {
        return try {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes() ?: return null
            val fileName = "covers/${UUID.randomUUID()}.jpg"
            supabase.storage["book_covers"].upload(fileName, bytes)
            supabase.storage["book_covers"].publicUrl(fileName)
        } catch (e: Exception) {
            android.util.Log.e("SUPABASE", "Image upload error: ${e.message}")
            null
        }
    }
}