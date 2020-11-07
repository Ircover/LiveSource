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

class LiveDataSource<T>(selectionManager: InterceptableSelectionManager) {
    private val dataSource = InterceptableSelectableDataSource<T>(selectionManager)
    val allItems = ListItemsLiveData { dataSource.dataSource }
    val selectedItems = ListItemsLiveData { dataSource.getSelectedItems() }
    fun setDataSource(dataSource: ArrayList<T>,
                      changeMode: ChangeDataSourceMode = ChangeDataSourceMode.ClearAllSelection) {
        this.dataSource.setDataSource(dataSource, changeMode)
        allItems.notifyChange()
        selectedItems.notifyChange()
    }

    fun observeSelectionChange(lifecycleOwner: LifecycleOwner, observer: (position: Int, isSelected: Boolean) -> Unit) {
        dataSource.registerSelectionChangeListener(observer).let { disposable ->
            lifecycleOwner.lifecycle.addObserver(DataSourceLifecycleObserver(disposable))
        }
    }

    fun observeItemSelectionChange(lifecycleOwner: LifecycleOwner, observer: (item: T, isSelected: Boolean) -> Unit) {
        dataSource.registerItemSelectionChangeListener(observer).let { disposable ->
            lifecycleOwner.lifecycle.addObserver(DataSourceLifecycleObserver(disposable))
        }
    }

    fun isItemSelected(position: Int) = dataSource.isPositionSelected(position)
    fun clickPosition(position: Int) = dataSource.clickPosition(position)
}