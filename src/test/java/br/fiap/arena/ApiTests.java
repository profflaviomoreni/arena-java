package br.fiap.arena;

import br.fiap.arena.domain.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @Test
    void createReturns201AndLocation() throws Exception {
        var body = Map.of(
                "title", "Estudar Spring",
                "priority", 3,
                "status", TaskStatus.OPEN.name(),
                "dueDate", LocalDate.now().minusDays(1).toString()
        );
        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/tasks/")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Estudar Spring")));
    }

    @Test
    void listIsPaged() throws Exception {
        for (int i = 0; i < 5; i++) {
            var body = Map.of("title", "Tarefa " + i, "priority", 1, "status", TaskStatus.OPEN.name());
            mvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsString(body))).andExpect(status().isCreated());
        }
        mvc.perform(get("/api/tasks?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", lessThanOrEqualTo(2)))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(5)));
    }

    @Test
    void statsCountsOverdueProperly() throws Exception {
        var past = Map.of("title","Venceu ontem","priority",2,"status",TaskStatus.OPEN.name(),
                "dueDate", LocalDate.now().minusDays(1).toString());
        var future = Map.of("title","Vence amanhã","priority",2,"status",TaskStatus.OPEN.name(),
                "dueDate", LocalDate.now().plusDays(1).toString());
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(past)))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(future)))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/tasks/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overdueCount", greaterThanOrEqualTo(1)));
    }

    @Test
    void deleteReturns204() throws Exception {
        var body = Map.of("title", "Apagar", "priority", 1, "status", TaskStatus.OPEN.name());
        var res = mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();
        var created = om.readTree(res.getResponse().getContentAsString());
        long id = created.get("id").asLong();
        mvc.perform(delete("/api/tasks/" + id)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/tasks/" + id)).andExpect(status().isNotFound());
    }
}
