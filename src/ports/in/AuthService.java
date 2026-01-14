package ports.in;

import domain.auth.PasswordHash;
import domain.auth.User;
import ports.out.UserRepository;

import java.util.Optional;

public final class AuthService {

    private final UserRepository users;

    public AuthService(UserRepository users) {
        this.users = users;
    }

    /**
     * Attempts login and returns authenticated User if successful.
     */
    public Optional<User> login(String username, String password) {

        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }

        // Keep your semester-1 validation rule
        if (containsNumbers(username)) {
            throw new IllegalArgumentException(
                    "Invalid login credentials. You can't add numbers to login."
            );
        }

        String stored = users.loadPasswordHash(username.trim());
        if (stored == null) return Optional.empty();

        boolean ok = PasswordHash.looksLikeSha256Hex(stored)
                ? PasswordHash.sha256(password).equalsIgnoreCase(stored)
                : password.equals(stored); // dev fallback

        if (!ok) return Optional.empty();

        Optional<User> user = users.findByUsername(username.trim());

        if (user.isEmpty()) return Optional.empty();

        if ("DISABLED".equalsIgnoreCase(user.get().status())) {
            return Optional.empty();
        }

        return user;
    }

    private boolean containsNumbers(String text) {
        return text != null && text.matches(".*\\d.*");
    }
}
