# FitMe
Android application utilizing Google's Firestore Cloud Database to track users' calorie intake and expenditure in order to intelligently suggest workouts via push notifications.

Primary API, Database, and Algorithms Design: https://github.com/brapana


Primary UI Design: https://github.com/MarissaSalcido

Short video of FitMe in action: https://www.youtube.com/watch?v=UXIBZtQbpOw 

## APIs Used
1. Edamam (https://developer.edamam.com/food-database-api) in order to analyze food being added to the food log, and extract the approximate calorie count. 
2. Google Fit (https://developers.google.com/fit) for calculating the number of steps taken and number of minutes spent moving in order to automatically create an exercise item in the user’s history that reflects this data.
3. Google Authentication Services (https://cloud.google.com/docs/authentication/) in order to allow users to log in to their Google account and give the appropriate permissions to access Google Fit services.

## Software Used: 
1. Android Studio to design and program the application.
2. Google’s Firebase NoSQL cloud database in order to handle data interaction (user calorie goals, food history, preferred exercises, etc.).

## User Interactions

### Profile View
Users may enter relevant details about themselves such as their name, age, height, weight, etc (see personal model) and the 
system will keep these records on the cloud database. These details are viewable and editable from the Profile view, able to be navigated 
to by the bottom bar of the app. 

### Schedule View
The next view (from left to right) is the schedule view, which allows users to view their personal history of performed 
exercises including the name, the time when it was entered, amount of time it was performed, and the total calories burned. Users may also 
set a workout time using an interactive clock interface, which uses push notifications to notify them 10 minutes before their scheduled workout is to begin. 

### Home View
The next view is the home view, where users can see and change their current calorie goal, calories consumed, calories burned, and calories remaining 
(calorie goal - [calories consumed - calories burned]). Users may also view and change the duration of their next workout, and view their next workout alarm (if set). 
After selecting the “add food item” button users can add a food item, allowing them to categorize the meal as breakfast, lunch, dinner, or snack and type the name of the food. 
The amount of calories will automatically be filled in from Edamam API data, but users may choose to manually enter the calories as well. Pressing the start workout button presents 
users with a sorted list (see Information Retrieval section for details) of recommended workouts they can choose from to instantly add the workout to their history. 

### Food Diary View
The food diary view allows users to view their food intake history as well add access the add food item view (same as home view). 

### Favorites View
Lastly, the favorites section allows users to view their favorite exercises (exercises that will be recommended when beginning a workout) as well as add more to the list via the add button. 
