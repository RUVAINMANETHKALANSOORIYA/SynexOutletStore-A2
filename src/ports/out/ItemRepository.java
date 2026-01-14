package ports.out;

import domain.store.Item;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> findAllActive();
    Optional<Item> findByCode(String itemCode);
}
