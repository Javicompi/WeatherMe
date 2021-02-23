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

![List]( "List")
