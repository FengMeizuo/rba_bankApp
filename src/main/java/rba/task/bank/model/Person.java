package rba.task.bank.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Person {

    public Person() {}

    public Person(String firstName, String lastName, String oib) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.oib = oib;
    }

    public Person(String firstName, String lastName, String oib, Status status) {
        this(firstName, lastName, oib);
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Ime")
    private String firstName;
    @Column(name = "Prezime")
    private String lastName;
    @Column(name = "OIB")
    private String oib;
    @Column(name = "Status")
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
}
