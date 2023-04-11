package ru.yandex.practicum.shareIt.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.shareIt.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
