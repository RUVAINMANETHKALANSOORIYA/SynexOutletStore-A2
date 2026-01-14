package ports.out;

import domain.auth.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    String loadPasswordHash(String username);
}
