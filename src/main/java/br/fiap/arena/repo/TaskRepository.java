package br.fiap.arena.repo;

import br.fiap.arena.domain.Task;
import br.fiap.arena.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
}
