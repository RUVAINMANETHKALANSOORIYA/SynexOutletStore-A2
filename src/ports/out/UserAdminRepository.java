package ports.out;

import domain.auth.User;
import java.util.List;

public interface UserAdminRepository {
    boolean usernameExists(String username);
    long insertCashier(String username, String passwordHash, String email);

    List<User> listStaffUsers();
}
