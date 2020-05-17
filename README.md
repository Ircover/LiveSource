# LiveSource

Android extension for [Selection Manager](https://github.com/Ircover/SelectionManager) library to work with items selection in `LiveData` style. To use it just create `LiveDataSource` instance and place in its constructor any `InterceptableSelectionManager` you want to be used.

    val users = LiveDataSource<User>(MultipleSelection())

## Change data
To set items source use `setDataSource` method.

    val newValues: ArrayList<User>
    users.setDataSource(newValues)
In some cases you will need to use specific selection processing after data source changing. For that you can place `ChangeDataSourceMode` as second parameter. `ClearAllSelection` mode is default one.

    users.setDataSource(newValues, ChangeDataSourceMode.HoldSelectedItems)
## Observe changes
To observe data source change you can use `allItems` property - it's also `LiveData`, which can be observed with `LifeCycle`.

    viewModel.users.allItems.observe(this, Observer { items: ArrayList<User> -> ... })
To observe selection change you can use `selectedItems` property (it's `LiveData` too).

    viewModel.users.selectedItems.observe(this, Observer { selectedItems: ArrayList<User> -> ... })
In case you want to listen selection changing for items one by one, you can use one of following methods: 

    viewModel.users.observeSelectionChange(this) { position: Int, isSelected: Boolean -> ... } //listen for positions
    viewModel.users.observeItemSelectionChange(this) { user: User, isSelected: Boolean -> ... } //listen for items
