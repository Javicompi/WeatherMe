# Udacity Android Nanodegree capstone project

The app uses the MVVM architecture pattern. The data is retrieved from the repository using Room 
to cache the data, and Retrofit to retrieve and update its values. The app updates the data every 
hour (server does every couple of hours) with the constraints of device connected to internet, idle, 
and not low battery. So that when the user opens the app, data should be updated to at least the 
last hour. And the weather information is retrieved from [OpenWeatherMap](https://openweathermap.org/ "OpenWeatherMap").

**Navigation:**
