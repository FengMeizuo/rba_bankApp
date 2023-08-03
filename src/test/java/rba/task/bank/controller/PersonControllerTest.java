package rba.task.bank.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import rba.task.bank.model.Person;
import rba.task.bank.model.Status;
import rba.task.bank.service.CsvService;
import rba.task.bank.service.PersonService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @MockBean
    private CsvService csvService;

    @Test
    public void addPersonTest() throws Exception {

        mockMvc.perform(post("/addPerson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Ivan\",\"lastName\":\"Horvat\",\"oib\":\"12345678900\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void suspendPersonTest() throws Exception {

        Person person = new Person("Ivan", "Horvat", "12345678900");
        when(personService.findPersonByOib(person.getOib())).thenReturn(person);

        mockMvc.perform(patch("/suspendPerson/"+person.getOib()))
                .andExpect(status().isOk());
    }

    @Test
    public void reactivatePersonTest() throws Exception {

        Person person = new Person("Ivan", "Horvat", "12345678900", Status.INACTIVE);
        when(personService.findPersonByOib(person.getOib())).thenReturn(person);

        mockMvc.perform(patch("/reactivatePerson/" + person.getOib()))
                .andExpect(status().isOk());
    }

    @Test
    public void deletePersonTest() throws Exception {

        Person person = new Person("Ivan", "Horvat", "12345678900");
        when(personService.findPersonByOib(person.getOib())).thenReturn(person);
        mockMvc.perform(delete("/deletePerson/" + person.getOib()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void searchByOIBTest() throws Exception {

        Person person = new Person("Ivan", "Horvat", "12345678900");
        when(personService.findPersonByOib(person.getOib())).thenReturn(person);
        mockMvc.perform(get("/searchByOibs")
                        .param("oibs", person.getOib()))
                .andExpect(status().isOk());
    }

    @Test
    public void searchByStatusTest() throws Exception {

        Person person = new Person("Ivan", "Horvat", "12345678900");
        when(personService.findByStatus(Status.ACTIVE)).thenReturn(Collections.singletonList(person));
        mockMvc.perform(get("/searchByStatus")
                        .param("statusStr", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    // More tests to cover edge cases, such as not found errors, invalid inputs, etc.
}
