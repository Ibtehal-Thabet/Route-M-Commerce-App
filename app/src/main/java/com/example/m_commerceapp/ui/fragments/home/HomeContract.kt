package com.example.m_commerceapp.ui.fragments.home

import androidx.lifecycle.LiveData
import com.example.domain.model.Brand
import com.example.domain.model.Category
import kotlinx.coroutines.flow.StateFlow

class HomeContract {

    interface ViewModel {
        val state: StateFlow<State>
        val event: LiveData<Event>
        fun invokeAction(action: Action)
    }
    sealed class State{
        object Loading: State()
        class Error(val message: String): State()
        class SuccessCategory(val categories: List<Category?>?): State()
        class SuccessBrands(val brands: List<Brand?>?): State()
    }
    sealed class Event{
        class NavigateToProductsCategory(val category: Category) : Event()

    }
    sealed class Action{
        object LoadCategories:Action()
        object LoadBrands:Action()

        class CategoryClicked(val category: Category) : Action()
    }
}