package comp.rest.services;

import comp.rest.models.Menu_Iterm;
import comp.rest.repository.MenuRepository;
import java.util.List;
import java.util.Optional;

public class MenuService {

    private final MenuRepository repository;

    public MenuService(MenuRepository repository) {
        this.repository = repository;
    }

    public List<Menu_Iterm> getAllItems() {
        return repository.getAllItems();
    }

    public Optional<Menu_Iterm> findById(int id) {
        return repository.findById(id);
    }

    public Menu_Iterm addItem(String name, double price, String category) {
        return repository.addItem(name, price, category);
    }

    public boolean removeItem(int id) {
        return repository.removeById(id);
    }
}
