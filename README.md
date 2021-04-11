# Udacity Android Nanodegree capstone project

The app uses the MVVM architecture pattern. The data is retrieved from the repository using Room 
to cache the data, and Retrofit to retrieve and update its values. The app updates the data every 
hour (server does every couple of hours) with the constraints of device connected to internet, idle, 
and not low battery. So that when the user opens the app, data should be updated to at least the 
last hour. And the weather information is retrieved from [OpenWeatherMap](https://openweathermap.org/ "OpenWeatherMap").

## Navigation: ##

![Navigation](https://github.com/Javicompi/WeatherMe/blob/master/Navigation.png?raw=true "Navigation")

Application contains 3 main screens, and one of them has a secondary to show the search result. 
Screens are Fragment based, and single Activity for the whole app. Navigation is done via Navigation 
Component, with a NavHostFragment on the Activity, and navigation between the Fragments via 
BottomNavigationView. Using actions when needed and SafeArgs to pass data between fragments.

## Screens: ##

**List**

![List](https://github.com/Javicompi/WeatherMe/blob/master/List.png?raw=true "List")

This is the first screen shown when the app opens. When there’s no data previously saved in the 
database, it’ll show a message indicating so. And a ProgressBar when the repository is loading the 
data. In case there’s data already saved, will show a RecyclerView with the list of items retrieved. 

The RecyclerView adapter uses DiffUtil so that when a new item is added, or an existing one deleted, 
it’ll animate the list to update it’s new state and values. And when the user clicks an item, app 
navigates to details Fragment with an Action and the item id (Primary Key) via SafeArgs. 

The Fragment uses a ViewModel and observes it’s values to update the adapter and show messages. 

**Details**

![Details](https://github.com/Javicompi/WeatherMe/blob/master/Details%20Collapsed.png?raw=true "Details collapsed")
![Details](https://github.com/Javicompi/WeatherMe/blob/master/Details%20Expanded.png?raw=true "Details expanded")

This screen shows the details from a previously saved element in the database, or an empty message 
when there’s no selected item. Users can navigate to this screen from the list Fragment, when a 
new item has been added to the database, or manually via BottomNavigationView. 

The Fragment has a layout at the bottom showing quick info of the selected element. When collapsed, 
like in the picture on the left, shows location and small description of the item selected. This 
layout can be expanded to show more details, like in the picture in the right. This layout uses 
MotionLayout to animate. And FloatingActionButton, which can be clicked to delete the current 
element, animates along the bottom layout. So that it disappears when it’s completely expanded.

The Fragment uses a ViewModel to observe the LiveData. All the logic is handled by the ViewModel.

**Search**

![Search](https://github.com/Javicompi/WeatherMe/blob/master/Search.png?raw=true "Search")
![Search](https://github.com/Javicompi/WeatherMe/blob/master/Search%20Result.png?raw=true "Search result")

This screen is where the users can search new entries using Retrofit for the API calls. In the 
first screen, in the left, users can choose to search a new location by text, or clicking the Fab 
to get the current location. In case the location permission is not guaranteed, they’ll be asked 
to do so. And in any case, internet connectivity is required too. In both cases, if it’s not 
possible to do the request, it will be informed by a SnackBar. 

The second screen, in the right, shows the results of the query. It uses the same layout as the 
Details screen (included in the xml). In this case, the Fab shows a “plus” icon instead. Since 
this item can be added to the database. And if it’s already cached, will replace the previous one. 
If the users decide not to save it, they can navigate back via the ToolBar or with the back button. 
And when saved, they’ll navigate to the details screen via NavController with an action and with 
the id of the entry sent in the SafeArgs. 

Both Fragments use ViewModel for the logic.

## Extras: ##

**BindingAdapters**

Functions to set xml values programmatically like colors, icons, and things like the icon rotation 
for the wind direction, sunrise and sunset in local times, and degrees as strings (N, NE, SW, etc).

**Extensions**

Extension functions for things like hide the keyboard from the fragment, check if there’s internet 
connectivity and animations.

**Constants**

Small file with a few constant values like the base url for the API service, or the API key 
(removed from the Github repository).

**SingleEvent**

Class used for single fire triggering events like showing a SnackBar, navigation, etc.
