package br.fiap.arena.api;

import br.fiap.arena.api.dto.TaskRequest;
import br.fiap.arena.domain.Task;
import br.fiap.arena.domain.TaskStatus;
import br.fiap.arena.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TaskRequest req, UriComponentsBuilder uriBuilder) {
        Task t = new Task();
        t.setTitle(req.getTitle());
        t.setPriority(req.getPriority());
        t.setStatus(req.getStatus());
        t.setDueDate(req.getDueDate());
        Task saved = service.create(t);
        URI location = uriBuilder.path("/api/tasks/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(value = "status", required = false) TaskStatus status,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<?> p = (status == null)
                ? service.pageAll(PageRequest.of(page, size))
                : service.pageByStatus(status, PageRequest.of(page, size));
        return ResponseEntity.ok(p);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        return ResponseEntity.ok(service.stats());
    }
}
