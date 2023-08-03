package rba.task.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rba.task.bank.repository.PersonRepository;
import rba.task.bank.model.Person;
import rba.task.bank.model.Status;

import java.util.Collections;
import java.util.List;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    @Transactional
    public boolean deletePersonByOib(String oib) {
        long deletedCount = personRepository.deleteByOib(oib);
        return deletedCount > 0;
    }

    public boolean existsByOib(String oib) {
        return personRepository.findByOibIn(Collections.singletonList(oib)).size() > 0;
    }

    public Person findPersonByOib(String oib) { return personRepository.findPersonByOib(oib); }

    public List<Person> findByOibIn(List<String> oibs) { return personRepository.findByOibIn(oibs); }

    public List<Person> findByStatus(Status status) { return personRepository.findByStatus(status); }
}
