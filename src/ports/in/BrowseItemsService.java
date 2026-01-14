package ports.in;

import domain.store.Item;
import ports.out.ItemRepository;

import java.util.List;

public final class BrowseItemsService {

    private final ItemRepository items;

    public BrowseItemsService(ItemRepository items) {
        this.items = items;
    }

    public List<Item> browseActiveItems() {
        return items.findAllActive();
    }
}
