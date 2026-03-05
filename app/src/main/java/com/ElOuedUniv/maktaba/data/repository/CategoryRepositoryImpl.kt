package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Category

interface CategoryRepository {
    fun getAllCategories(): List<Category>
    fun getCategoryById(id: String): Category?
}

class CategoryRepositoryImpl : CategoryRepository {

    // Step 2.1: Create Sample Categories
    private val categoriesList = listOf(
        Category(
            id = "1",
            name = "Programming",
            description = "Books about software development and coding"
        ),
        Category(
            id = "2",
            name = "Algorithms",
            description = "Books about algorithms and data structures"
        ),
        Category(
            id = "3",
            name = "Databases",
            description = "Books about database design and management"
        ),
        Category(
            id = "4",
            name = "Mobile Development",
            description = "Books about Android and iOS development"
        ),
        Category(
            id = "5",
            name = "Artificial Intelligence",
            description = "Books about AI and Machine Learning"
        )
    )

    // Step 2.2: Implement getAllCategories
    override fun getAllCategories(): List<Category> {
        return categoriesList
    }

    // Step 2.3: Implement getCategoryById
    override fun getCategoryById(id: String): Category? {
        return categoriesList.find { it.id == id }
    }
}