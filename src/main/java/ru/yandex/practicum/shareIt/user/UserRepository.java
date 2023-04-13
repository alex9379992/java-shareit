package ru.yandex.practicum.shareIt.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.shareIt.user.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
