package rba.task.bank.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import rba.task.bank.model.Person;
import rba.task.bank.model.Status;
import rba.task.bank.service.PersonService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PersonService personService;

    @Test
    public void addPersonIntegrationTest() {
        String requestBody = "{\"firstName\":\"Ivan\",\"lastName\":\"Horvat\",\"oib\":\"12345678901\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Person> response = restTemplate.exchange(
                "/addPerson", HttpMethod.POST, entity, Person.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Ivan", response.getBody().getFirstName());
        assertEquals("Horvat", response.getBody().getLastName());
        assertEquals("12345678901", response.getBody().getOib());
        assertEquals(Status.ACTIVE, response.getBody().getStatus());

        boolean personDeleted = personService.deletePersonByOib(response.getBody().getOib());
        assertEquals(true, personDeleted);
    }
}