package com.cinema_package.cinema_project.Service;

import com.cinema_package.cinema_project.CinemaProjectApplication;
import com.cinema_package.cinema_project.Model.BookingHistory;
import com.cinema_package.cinema_project.Model.Movie;
import com.cinema_package.cinema_project.Model.NewMovieRequest;
import com.cinema_package.cinema_project.Repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> filterMovies(String title, LocalDate date, String location, String genre)
    {
        List<Movie> movies = movieRepository.findAll();
        List<Movie> filteredMovies = new ArrayList<>();

        for (Movie movie : movies) {
            boolean match = true;

            if (title != null && !movie.getTitle().toLowerCase().contains(title.toLowerCase())) {
                match = false;
            }
            if (date != null && !movie.getDate().isEqual(date)) {
                match = false;
            }
            if (location != null && !movie.getLocation().toLowerCase().contains(location.toLowerCase())) {
                match = false;
            }
            if (genre != null && !movie.getGenre().toLowerCase().contains(genre.toLowerCase())) {
                match = false;
            }
            if (match) {
                filteredMovies.add(movie);
            }
        }

        return filteredMovies;
    }



//    Fetching Movies *****************************************************

    @GetMapping
    public List<Movie> getAllMovies(String title, LocalDate date, String location, String genre) {
        if (title == null && date == null && location == null && genre == null) {
            return movieRepository.findAll();
        } else {
            return filterMovies(title, date, location, genre);
        }
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable("id") Integer id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie ID: " + id));
    }



//    Fetching Movie Booking History************************************************************************
    @GetMapping("/booking/history")
    public List<BookingHistory> getBookingHistory() {
        List<Movie> movies = movieRepository.findAll();
        List<BookingHistory> bookingHistory = new ArrayList<>();

        for (Movie movie : movies) {
            int bookedTickets = movie.getTotalSeats() - movie.getAvailableSeats();

            if (bookedTickets > 0) {
                int totalPrice = bookedTickets * movie.getPrice();

                BookingHistory booking = new BookingHistory();
                booking.setId(movie.getId());
                booking.setTitle(movie.getTitle());
                booking.setDirector(movie.getDirector());
                booking.setDescription(movie.getDescription());
                booking.setGenre(movie.getGenre());
                booking.setDate(movie.getDate());
                booking.setLocation(movie.getLocation());
                booking.setBookedTickets(bookedTickets);
                booking.setTotalPrice(totalPrice);
                bookingHistory.add(booking);
            }
        }
        return bookingHistory;
    }


//  Booking tickets via Portal***************************************************************************************




    @PostMapping("/booking/{movieId}/{tickets}/{payment}")
    public void bookTickets(
            @PathVariable("movieId") Integer id,
            @PathVariable("tickets") Integer tickets,
            @PathVariable("payment") Integer payment
    ) {
        Movie movie = getMovieById(id);

        int availableSeats = movie.getAvailableSeats();
        if (tickets > availableSeats) {
            throw new IllegalArgumentException("No seats available at this time.");
        }

        int calculatedTotalPrice = tickets * movie.getPrice();

        if (!payment.equals(calculatedTotalPrice)) {
            throw new IllegalArgumentException("Invalid total price.");
        }

        availableSeats -= tickets;
        movie.setAvailableSeats(availableSeats);

        movieRepository.save(movie);
    }



//    Adding Movies to multiplexes - Owner Perspective*****************************************************************

    @PostMapping
    public void addMovie(@RequestBody NewMovieRequest request) {
        Movie movie = new Movie();
        movie.setDescription(request.getDescription());
        movie.setDirector(request.getDirector());
        movie.setGenre(request.getGenre());
        movie.setTitle(request.getTitle());
        movie.setDate(request.getDate());
        movie.setLocation(request.getLocation());
        movie.setTotalSeats(request.getTotalSeats());
        movie.setAvailableSeats(request.getAvailableSeats());
        movie.setPrice(request.getPrice());
        movieRepository.save(movie);
    }



//    Remove a Movie from theaters **************************************************************************************

        @DeleteMapping("{movieId}")
    public void deleteMovie(@PathVariable("movieId") Integer id){
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie ID: " + id));

        movieRepository.delete(movie);
    }


//    Updating Movie Details ******************************************************************************************

    @PutMapping("{movieId}")
    public void updateMovie(@PathVariable("movieId") Integer id,
                            @RequestBody NewMovieRequest request){

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie ID: " + id));

        movie.setDescription(request.getDescription());
        movie.setDirector(request.getDirector());
        movie.setGenre(request.getGenre());
        movie.setTitle(request.getTitle());
        movie.setDate(request.getDate());
        movie.setLocation(request.getLocation());
        movie.setTotalSeats(request.getTotalSeats());
        movie.setAvailableSeats(request.getAvailableSeats());
        movie.setPrice(request.getPrice());
        movieRepository.save(movie);
    }




//    Loading the Current Movie Timings into the database **************************************************************
    


    @GetMapping("load")
    public String loadData()
    {
        List<Movie> movie_list = new ArrayList<>(List.of(
                new Movie(1, "Inception", "Christopher Nolan", "A mind-bending thriller", "Sci-Fi", LocalDate.of(2010, 7, 16), "IMAX Theater 1", 200, 150, 300),
                new Movie(2, "The Dark Knight", "Christopher Nolan", "Gotham's hero faces a new foe", "Action", LocalDate.of(2008, 7, 18), "IMAX Theater 2", 250, 200, 350),
                new Movie(3, "Parasite", "Bong Joon-ho", "A commentary on class and inequality", "Thriller", LocalDate.of(2019, 5, 30), "Cineplex 5", 180, 120, 280),
                new Movie(4, "Interstellar", "Christopher Nolan", "A journey beyond our galaxy", "Sci-Fi", LocalDate.of(2014, 11, 7), "IMAX Theater 3", 220, 180, 320),
                new Movie(5, "The Godfather", "Francis Ford Coppola", "The story of an Italian-American crime family", "Crime", LocalDate.of(1972, 3, 24), "Classic Cinema 1", 150, 100, 250),
                new Movie(6, "Schindler's List", "Steven Spielberg", "A historical drama on the Holocaust", "Drama", LocalDate.of(1993, 12, 15), "Historic Theater", 170, 150, 200),
                new Movie(7, "Avengers: Endgame", "Anthony and Joe Russo", "The Avengers assemble for a final battle", "Action", LocalDate.of(2019, 4, 26), "IMAX Theater 4", 300, 250, 400),
                new Movie(8, "Joker", "Todd Phillips", "A gritty look into the Joker's origins", "Crime", LocalDate.of(2019, 10, 4), "Cineplex 3", 200, 180, 300),
                new Movie(9, "Forrest Gump", "Robert Zemeckis", "The story of a man's extraordinary life journey", "Drama", LocalDate.of(1994, 7, 6), "Classic Cinema 2", 160, 140, 220),
                new Movie(10, "The Matrix", "Lana and Lilly Wachowski", "A hacker discovers the truth about reality", "Sci-Fi", LocalDate.of(1999, 3, 31), "Cineplex 6", 210, 190, 310)
        ));

        movieRepository.saveAll(movie_list);

        return "Successful.";
    }
}
