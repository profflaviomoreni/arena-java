package br.fiap.arena.api;

import br.fiap.arena.domain.Task;
import br.fiap.arena.domain.TaskStatus;
import br.fiap.arena.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) { this.service = service; }

    @GetMapping("/create")
    public ResponseEntity<?> createViaGet(@RequestParam String title,
                                          @RequestParam(required = false) Integer priority,
                                          @RequestParam(required = false) TaskStatus status) {
        Task t = new Task();
        t.setTitle(title);
        t.setPriority(priority == null ? 1 : priority);
        t.setStatus(status == null ? TaskStatus.OPEN : status);
        Task saved = service.create(t);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) TaskStatus status,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        List<Task> data = status == null ? service.listAll() : service.listByStatus(status);
        int from = 0;
        int to = Math.min(size, data.size());
        List<Task> slice = data.subList(from, to);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("content", slice);
        resp.put("totalElements", data.size());
        resp.put("page", page);
        resp.put("size", size);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deleteViaGet(@PathVariable Long id) {
        boolean removed = service.delete(id);
        return ResponseEntity.ok(removed);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        return ResponseEntity.ok(service.stats());
    }
}
