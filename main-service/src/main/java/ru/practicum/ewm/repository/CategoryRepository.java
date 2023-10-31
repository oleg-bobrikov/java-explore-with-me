package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
        default Category findCategoryById(long id) {
            return findById(id).orElseThrow(
                    () -> new NotFoundException(
                            String.format("No category found with identifier %s", id)));
        }
}