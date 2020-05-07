# Popular Movies - Stage 2

## Overview
This project is apart of the Udacity Android Developer Nanodegree. 

The purpose of this project was to built an app, optimized for tablets also, to help users discover popular and highly rated movies on the web. It displays a scrolling grid of movie trailers, launches a details screen whenever a particular movie is selected. This app utilizes core Android user interface components and fetches movie information using themoviedb.org web API.

This app improves the functionality of the app built in Stage 1.

## Functionality
1. Users are able to view list of movies.
2. Movies can also be sorted by Most popular, Top Rated and Favorite.
3. Users are able to view details of a selected movie.
4. Play trailers (in the youtube app or a web browser).
5. Read reviews if available.
6. Mark a movie as a favorite in here, by tapping the "Favorite" button. This is for a local movies collection stored on the phone.
7. This application utilizes the Room persistence library with LiveData for the "Favorited" movies.

## Running the app
This app utilizes the Movie Database API. To run this app, you must obtain a free API key from the [Movie Database API](https://developers.themoviedb.org/) and add this following line of code to your MainActivity and DetailedActivity files:
API_KEY = "";

## Screenshots
<img src="https://github.com/akash-starvin/PopularMovies/blob/master/Screenshots/Screenshot_main_list.jpg" width="285"/> <img src="https://github.com/akash-starvin/PopularMovies/blob/master/Screenshots/Screenshot_sort.jpg" width="285"/> <img src="https://github.com/akash-starvin/PopularMovies/blob/master/Screenshots/Screenshot_deatil.jpg" width="285"/>
