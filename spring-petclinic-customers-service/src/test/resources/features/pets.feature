Feature: Pet Management
  As a PetClinic user
  I want to manage pets for owners
  So that I can track pet information

  Scenario: List all pet types
    When I send a GET request to "/petTypes"
    Then the response status should be 200
    And the response content type should be "application/json"
    And the JSON array "$" should have size 6

  Scenario: Get a specific pet by ID
    When I send a GET request to "/owners/2/pets/2"
    Then the response status should be 200
    And the JSON field "$.name" should be "Basil"
    And the JSON field "$.type.id" should be the integer 6

  Scenario: Create a new pet for an owner
    When I create a pet for owner 1 with name "Buddy" birthDate "2020-05-15" typeId 2
    Then the response status should be 201
    And the JSON field "$.name" should be "Buddy"

  Scenario: Get pet not found returns 404
    When I send a GET request to "/owners/1/pets/9999"
    Then the response status should be 404

  Scenario: Verify preloaded pet data for owner 3
    When I send a GET request to "/owners/3/pets/3"
    Then the response status should be 200
    And the JSON field "$.name" should be "Rosy"
