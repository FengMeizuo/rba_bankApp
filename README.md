# **Bank App**

## **Description**

The Bank App handles the process of issuing credit cards to persons. Persons apply to the bank for a credit card, and their details are recorded in a MySQL database.

The application allows the enrollment of a person with attributes such as **Name**, **Surname**, **OIB** (Personal Identification Number), and **Status** (ACTIVE, SUSPENDED, INACTIVE). It supports searching for persons by OIB, creating text files with person details for card production, and managing person statuses. It also handles the deletion of persons, marking associated files as inactive.

This application serves as a back-end service, developed in Java using the **Spring Boot** framework. There is no front-end part of the application.

## **Requirements**

- Java 17
- Apache Maven 3.8.6
- MySQL Community Server 8.0.34

## **Endpoints**

1. **/addPerson (POST)**: Takes a JSON representation of a person object in the request body and saves them in the database. Returns HTTP 409 if a person with the same OIB already exists.
2. **/suspendPerson (PATCH)**: Takes an OIB from the person from the path variable and changes the status to 'suspended'. Returns HTTP 400 if the person's status is inactive, or HTTP 404 if the person does not exist.
3. **/reactivatePerson (PATCH)**: Works the same way as /suspendPerson but changes the person's status to ACTIVE.
4. **/deletePerson (DELETE)**: Works the same way as /suspendPerson but changes the person's status to INACTIVE.
5. **/searchByOibs (GET)**: Returns persons from the database with a given OIB from the oibs list. Also writes the corresponding CSV file for every person that is found.
6. **/searchByStatus (GET)**: Takes a request parameter statusStr and returns all persons with the given status.