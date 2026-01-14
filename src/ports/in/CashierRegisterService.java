package ports.in;

import domain.auth.PasswordHash;
import ports.out.UserAdminRepository;

public final class CashierRegisterService {

    private final UserAdminRepository adminRepo;

    public CashierRegisterService(UserAdminRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    public long registerCashier(String username, String password, String email) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username required");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }

        String u = username.trim();

        if (adminRepo.usernameExists(u)) {
            throw new IllegalArgumentException("Username already exists");
        }

        String hash = PasswordHash.sha256(password);
        return adminRepo.insertCashier(u, hash, email);
    }
}
