package comp.rest.services;

import comp.rest.models.Table;
import comp.rest.repository.TableRepository;
import java.util.List;
import java.util.Optional;

public class TableService {

    private final TableRepository repository;

    public TableService(TableRepository repository) {
        this.repository = repository;
    }

    public List<Table> getAllTables() {
        return repository.getTables();
    }

    public Optional<Table> findById(int id) {
        return repository.findById(id);
    }

    public Table addTable(int capacity) {
        return repository.addTable(capacity);
    }

    public boolean removeTable(int id) {
        return repository.removeById(id);
    }

    public boolean updateAvailability(int id, Table.Availability availability) {
        return repository.updateAvailability(id, availability);
    }

    public boolean markUnavailable(int id) {
        return repository.updateAvailability(id, Table.Availability.UNAVAILABLE);
    }

    public boolean markAvailable(int id) {
        return repository.updateAvailability(id, Table.Availability.AVAILABLE);
    }
}