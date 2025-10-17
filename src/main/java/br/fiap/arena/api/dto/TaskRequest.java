package br.fiap.arena.api.dto;

import br.fiap.arena.domain.TaskStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class TaskRequest {
    @NotBlank
    private String title;
    @Min(1) @Max(5)
    private Integer priority = 1;
    private TaskStatus status = TaskStatus.OPEN;
    private LocalDate dueDate;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
