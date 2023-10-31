package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    default Compilation findCompilationById(long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format("No compilation found with identifier %s", id)));
    }

}
