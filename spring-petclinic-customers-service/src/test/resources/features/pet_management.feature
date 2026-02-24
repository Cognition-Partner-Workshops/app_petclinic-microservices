@pets
Feature: Pet Management
  As a veterinary clinic receptionist
  I want to manage pets for each owner
  So that I can maintain accurate records of animals we care for

  Background:
    Given the PetClinic system is running

  Scenario: Add a new pet to an owner
    Given an owner "George" "Franklin" exists in the system
    And pet types are available in the database
    When I add a pet named "Leo" with birth date "2020-09-07" and type ID 1 to that owner
    Then the pet response status should be 201
    And the pet response should contain "Leo"

  Scenario: Retrieve available pet types
    Given pet types are available in the database
    When I request the list of pet types
    Then the pet response status should be 200
    And the pet types list should not be empty
    And the pet types list should contain "cat"
    And the pet types list should contain "dog"

  Scenario: Add multiple pets to the same owner
    Given an owner "Betty" "Davis" exists in the system
    And pet types are available in the database
    When I add a pet named "Basil" with birth date "2021-01-15" and type ID 2 to that owner
    Then the pet response status should be 201
    When I add a pet named "Jewel" with birth date "2022-03-20" and type ID 3 to that owner
    Then the pet response status should be 201
