
package com.cinema_package.cinema_project.Repository;

import com.cinema_package.cinema_project.Model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
}