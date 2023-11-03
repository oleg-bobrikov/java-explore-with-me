package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.User;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByIdIn(Set<Long> ids, Pageable page);

    default User findUserById(long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(
                        String.format("No user found with identifier %s", id)));
    }
}