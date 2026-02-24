@owners
Feature: Owner Management
  As a veterinary clinic receptionist
  I want to manage pet owner records
  So that I can keep track of our clients and their contact information

  Background:
    Given the PetClinic system is running

  Scenario: Register a new owner
    Given there are no owners in the system
    When I create an owner with first name "George", last name "Franklin", address "110 W. Liberty St.", city "Madison", and telephone "6085551023"
    Then the response status should be 201
    And the response should contain "George"
    And the response should contain "Franklin"

  Scenario: Retrieve a single owner by ID
    Given there are no owners in the system
    And the following owner exists:
      | firstName | lastName | address          | city     | telephone  |
      | Betty     | Davis    | 638 Cardinal Ave | Sun Prairie | 6085551749 |
    When I request that owner by ID
    Then the response status should be 200
    And the response should contain "Betty"
    And the response should contain "Davis"

  Scenario: List all owners
    Given there are no owners in the system
    And the following owner exists:
      | firstName | lastName | address          | city     | telephone  |
      | Harold    | Davis    | 563 Friendly St  | Windsor  | 6085553198 |
    When I create an owner with first name "Peter", last name "McTavish", address "2387 S. Fair Way", city "Madison", and telephone "6085552765"
    And I request the owner list
    Then the response status should be 200
    And the owner list should have 2 entries

  Scenario: Update an existing owner's city
    Given there are no owners in the system
    And the following owner exists:
      | firstName | lastName  | address           | city    | telephone  |
      | Eduardo   | Rodriquez | 2693 Commerce St. | McFarland | 6085558763 |
    When I update the owner's city to "Sun Prairie"
    Then the response status should be 204
    And the owner's city should be "Sun Prairie"
