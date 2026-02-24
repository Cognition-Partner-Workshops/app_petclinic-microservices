Feature: Visit Management
  As a PetClinic user
  I want to manage visits for pets
  So that I can track veterinary appointments

  Scenario: Create a new visit for a pet
    When I create a visit for pet 7 with date "2024-03-15" and description "annual checkup"
    Then the response status should be 201
    And the visit should have description "annual checkup"
    And the JSON field "$.petId" should be the integer 7

  Scenario: Get visits for a specific pet
    When I get visits for pet 7
    Then the response status should be 200
    And the visits list should have at least 1 entries

  Scenario: Get visits for multiple pets
    When I get visits for pets "7,8"
    Then the response status should be 200
    And the visits items should not be empty

  Scenario: Verify preloaded visit data
    When I get visits for pet 7
    Then the response status should be 200
    And the JSON response should contain "rabies shot"

  Scenario: Create visits from CSV test data
    When I create visits from CSV "testdata/visits.csv" for pet 8
    Then the response status should be 201

  Scenario: Get visits for pet with no visits returns empty list
    When I get visits for pet 999
    Then the response status should be 200
    And the JSON array "$" should be empty
