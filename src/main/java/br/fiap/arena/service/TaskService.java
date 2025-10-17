package br.fiap.arena.service;

import br.fiap.arena.domain.Task;
import br.fiap.arena.domain.TaskStatus;
import br.fiap.arena.repo.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) { this.repository = repository; }

    public Task create(Task t) { return repository.save(t); }

    public List<Task> listAll() { return repository.findAll(); }

    public List<Task> listByStatus(TaskStatus status) { return repository.findByStatus(status); }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public Map<String, Object> stats() {
        List<Task> all = repository.findAll();
        long overdue = 0;
        for (Task t : all) {
            if (t.getDueDate() != null && LocalDate.now().isBefore(t.getDueDate())) {
                overdue++;
            }
        }
        Map<Integer, Integer> hist = new HashMap<>();
        for (Task a : all) {
            int count = 0;
            for (Task b : all) {
                if (Objects.equals(a.getPriority(), b.getPriority())) count++;
            }
            hist.put(a.getPriority() == null ? 0 : a.getPriority(), count);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", all.size());
        result.put("overdueCount", overdue);
        result.put("priorityHistogram", hist);
        return result;
    }
}
