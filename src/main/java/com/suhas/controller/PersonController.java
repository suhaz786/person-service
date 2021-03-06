package com.suhas.controller;

import com.suhas.dto.PersonRequest;
import com.suhas.exception.PersonAlreadyExistsException;
import com.suhas.exception.PersonNotFoundException;
import com.suhas.exception.PersonServiceException;
import com.suhas.model.Person;
import com.suhas.service.IPersonService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(value = "/person", description = "REST Controller for handling all CRUD operations for Person entity")
@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private IPersonService personService;

    @ApiOperation(value = "Get All Persons", notes = "API to retrieve all persons", nickname = "getPersons")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server error"),
            @ApiResponse(code = 404, message = "No Content"),
            @ApiResponse(code = 405, message = "Method not allowed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 200, message = "Success", response = List.class, responseContainer = "List")})
    @GetMapping("/")
    public ResponseEntity<List<Person>> getAllPersons() {
        List<Person> persons = personService.getAll();
        if (persons.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }


    @ApiOperation(value = "create", notes = "Create Person", nickname = "createPerson")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server error"),
            @ApiResponse(code = 404, message = "No Content"),
            @ApiResponse(code = 405, message = "Method not allowed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 200, message = "Success", response = Person.class)})
    @PostMapping("/")
    public ResponseEntity<?> createPerson(@RequestBody PersonRequest request) throws PersonServiceException {
        if (personService.checkIfPersonExists(request)) {
            throw new PersonAlreadyExistsException("Unable to update. Person with name " +
                    request.getFirstName() + " " + request.getLastName() + " already exists.");
        }
        Person person = personService.create(request);
        return new ResponseEntity<>(person, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update Person By Id", notes = "API to update a Person", nickname = "updatePerson")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server error"),
            @ApiResponse(code = 404, message = "Document not found"),
            @ApiResponse(code = 200, message = "Success",
                    response = List.class, responseContainer = "List")})
    @PutMapping("/{personId}")
    public ResponseEntity<?> updateDocument(@ApiParam(value = "personId", required = true)
                                            @PathVariable(value = "personId") Long personId,
                                            @Valid @RequestBody PersonRequest request) throws PersonServiceException {
        if (personService.findById(personId) == null) {
            throw new PersonNotFoundException("Unable to update. Person with name " +
                    request.getFirstName() + " " + request.getLastName() + " doesn't exist.");
        }
        Person updatedPerson = personService.update(personId, request);
        return new ResponseEntity<>(updatedPerson, HttpStatus.OK);
    }

    @ApiOperation(value = "delete", notes = "Delete Person By id", nickname = "deletePerson")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server error"),
            @ApiResponse(code = 404, message = "No Content"),
            @ApiResponse(code = 405, message = "Method not allowed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 200, message = "Success", response = String.class)})
    @DeleteMapping("/{personId}")
    public ResponseEntity<?> deletePerson(@PathVariable("personId") Long personId) throws PersonServiceException {
        if (personService.findById(personId) == null) {
            throw new PersonNotFoundException("Unable to update. Person with id " + personId + " doesn't exist.");
        }
        personService.delete(personId);
        return new ResponseEntity<Person>(HttpStatus.OK);
    }

    @ApiOperation(value = "deleteAll", notes = "Delete All Person", nickname = "deleteAll")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server error"),
            @ApiResponse(code = 404, message = "No Content"),
            @ApiResponse(code = 405, message = "Method not allowed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 200, message = "Success", response = String.class)})
    @DeleteMapping(value = "/")
    public ResponseEntity<Person> deleteAllUsers() {
        personService.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}