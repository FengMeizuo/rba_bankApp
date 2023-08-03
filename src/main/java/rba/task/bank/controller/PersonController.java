package rba.task.bank.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rba.task.bank.model.Person;
import rba.task.bank.model.Status;
import rba.task.bank.service.CsvService;
import rba.task.bank.service.PersonService;

import java.util.List;

@RestController
public class PersonController {

    @Autowired
    private PersonService personService;
    @Autowired
    private CsvService csvService;

    private static final Logger log = LoggerFactory.getLogger(CsvService.class);

    @PostMapping("/addPerson")
    public ResponseEntity<?> addPerson(@RequestBody Person person) {

        if (personService.existsByOib(person.getOib())) {
            return new ResponseEntity<>("Person with OIB " + person.getOib() + " already exists.", HttpStatus.CONFLICT);
        }

        Person savedPerson = personService.savePerson(person);
        log.info("Added new person with OIB: {}", person.getOib());
        return new ResponseEntity<>(savedPerson, HttpStatus.CREATED);
    }

    @PatchMapping("/suspendPerson/{oib}")
    public ResponseEntity<?> suspendPerson(@PathVariable String oib) {

        Person person = personService.findPersonByOib(oib);

        if (person != null && person.getStatus() == Status.ACTIVE) {
            person.setStatus(Status.SUSPENDED);
            personService.savePerson(person);
            log.info("Suspended person with OIB: {}", oib);
            if (csvService.csvFileExistsForOib(oib)) {
                try {
                    csvService.markCsvAsSuspended(oib);
                } catch(Exception e) {
                    log.error("Failed to mark CSV as suspended for OIB: {}", oib, e);
                }
            }
            return new ResponseEntity<>(person, HttpStatus.OK);
        } else if (person != null && person.getStatus() == Status.SUSPENDED) {
            return new ResponseEntity<>("Person with OIB " + oib + " is already suspended.", HttpStatus.BAD_REQUEST);
        } else if (person != null && person.getStatus() == Status.INACTIVE) {
            return new ResponseEntity<>("Person with OIB " + oib + " is inactive.", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>("Person with OIB " + oib + " not found.", HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/reactivatePerson/{oib}")
    public ResponseEntity<?> reactivatePerson(@PathVariable String oib) {
        Person person = personService.findPersonByOib(oib);

        if (person != null && (person.getStatus() == Status.SUSPENDED || person.getStatus() == Status.INACTIVE)) {
            person.setStatus(Status.ACTIVE);
            personService.savePerson(person);
            log.info("Reactivated person with OIB: {}", oib);
            if (csvService.csvFileExistsForOib(oib)) {
                try {
                    csvService.markCsvAsActive(oib);
                } catch(Exception e) {
                    log.error("Failed to mark CSV as active for OIB: {}", oib, e);
                }
            }
            return new ResponseEntity<>(person, HttpStatus.OK);
        } else if (person != null && person.getStatus() == Status.ACTIVE) {
            return new ResponseEntity<>("Person with OIB " + oib + " is already active.", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>("Person with OIB " + oib + " not found.", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deletePerson/{oib}")
    public ResponseEntity<?> deletePerson(@PathVariable String oib) {

        Person person = personService.findPersonByOib(oib);

        if (person != null) {
            person.setStatus(Status.INACTIVE);
            personService.savePerson(person);
            log.info("Marked person as inactive with OIB: {}", oib);
            if (csvService.csvFileExistsForOib(oib)) {
                try {
                    csvService.markCsvAsInactive(oib);
                } catch(Exception e) {
                    log.error("Failed to mark CSV as inactive for OIB: {}", oib, e);
                }
            }

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("Person with OIB " + oib + " not found.", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/searchByOibs")
    public ResponseEntity<List<Person>> searchByOIB(@RequestParam List<String> oibs) {
        log.debug("Searching for persons with OIBs: {}", oibs);
        List<Person> persons = personService.findByOibIn(oibs);
        try {
            csvService.writePersonsToCsv(persons);
        } catch(Exception e) {
            log.error("Failed to write persons to CSV", e);
        }
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/searchByStatus")
    public ResponseEntity<?> searchByStatus(@RequestParam String statusStr) {
        log.debug("Searching for persons with status: {}", statusStr);
        Status status;
        try {
            status = Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid status: " + statusStr, e);
            return new ResponseEntity<>("Invalid status: " + statusStr, HttpStatus.BAD_REQUEST);
        }

        List<Person> persons = personService.findByStatus(status);
        if (persons.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(persons);
    }
}
