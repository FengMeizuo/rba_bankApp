package rba.task.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rba.task.bank.model.Person;
import rba.task.bank.model.Status;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    long deleteByOib(String oib);
    Person findPersonByOib(String oib);
    List<Person> findByOibIn(List<String> oibs);
    List<Person> findByStatus(Status status);
}
