package ru.ircover.selectionmanager.livesource

import androidx.lifecycle.*
import ru.ircover.selectionmanager.ChangeDataSourceMode
import ru.ircover.selectionmanager.Disposable
import ru.ircover.selectionmanager.InterceptableSelectableDataSource
import ru.ircover.selectionmanager.InterceptableSelectionManager

private class DataSourceLifecycleObserver(private val disposable: Disposable) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        disposable.dispose()
    }
}

class LiveDataSource<T>(selectionManager: InterceptableSelectionManager) : LiveData<InterceptableSelectableDataSource<T>>() {
    val allItems = ListItemsLiveData { value?.dataSource ?: arrayListOf() }
    val selectedItems = ListItemsLiveData { value?.getSelectedItems() ?: arrayListOf() }
    private var selectedItemsDisposable: Disposable? = null
    init {
        value = InterceptableSelectableDataSource(selectionManager)
    }
    fun setDataSource(dataSource: ArrayList<T>,
                      changeMode: ChangeDataSourceMode = ChangeDataSourceMode.ClearAllSelection) {
        value?.setDataSource(dataSource, changeMode)
        allItems.notifyChange()
        selectedItems.notifyChange()
    }

    fun observeSelectionChange(lifecycleOwner: LifecycleOwner, observer: (position: Int, isSelected: Boolean) -> Unit) {
        value?.registerSelectionChangeListener(observer)?.let { disposable ->
            lifecycleOwner.lifecycle.addObserver(DataSourceLifecycleObserver(disposable))
        }
    }

    fun observeItemSelectionChange(lifecycleOwner: LifecycleOwner, observer: (item: T, isSelected: Boolean) -> Unit) {
        value?.registerItemSelectionChangeListener(observer)?.let { disposable ->
            lifecycleOwner.lifecycle.addObserver(DataSourceLifecycleObserver(disposable))
        }
    }

    override fun onActive() {
        selectedItemsDisposable = value?.registerSelectionChangeListener { _, _ ->
            selectedItems.notifyChange()
        }
    }

    override fun onInactive() {
        selectedItemsDisposable?.dispose()
        selectedItemsDisposable = null
    }
}