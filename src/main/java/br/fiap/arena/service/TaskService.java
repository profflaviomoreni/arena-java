package br.fiap.arena.service;

import br.fiap.arena.domain.Task;
import br.fiap.arena.domain.TaskStatus;
import br.fiap.arena.repo.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) { this.repository = repository; }

    public Task create(Task t) { return repository.save(t); }

    public Page<Task> pageAll(Pageable pageable) { return repository.findAll(pageable); }

    public Page<Task> pageByStatus(TaskStatus status, Pageable pageable) {
        List<Task> all = repository.findAll().stream()
                .filter(t -> t.getStatus() == status)
                .toList();
        int start = Math.min((int) pageable.getOffset(), all.size());
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<Task> content = all.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(content, pageable, all.size());
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Task not found");
        }
        repository.deleteById(id);
    }

    public Map<String, Object> stats() {
        List<Task> all = repository.findAll();
        long overdue = all.stream()
                .filter(t -> t.getDueDate() != null)
                .filter(t -> t.getStatus() != TaskStatus.DONE)
                .filter(t -> t.getDueDate().isBefore(LocalDate.now()))
                .count();

        Map<Integer, Long> hist = all.stream()
                .collect(Collectors.groupingBy(t -> Optional.ofNullable(t.getPriority()).orElse(0), Collectors.counting()));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", all.size());
        result.put("overdueCount", overdue);
        result.put("priorityHistogram", hist);
        return result;
    }
}
