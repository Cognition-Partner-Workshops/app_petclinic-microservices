Feature: Owner Management
  As a PetClinic user
  I want to manage pet owners
  So that I can track customer information

  Scenario: List all owners
    When I send a GET request to "/owners"
    Then the response status should be 200
    And the response content type should be "application/json"
    And the owners list should have at least 10 entries

  Scenario: Get a specific owner by ID
    When I send a GET request to "/owners/1"
    Then the response status should be 200
    And the JSON field "$.firstName" should be "George"
    And the JSON field "$.lastName" should be "Franklin"
    And the JSON field "$.city" should be "Madison"

  Scenario: Create a new owner
    When I create an owner with firstName "Jane" lastName "Doe" address "123 Main St" city "Springfield" telephone "5551234567"
    Then the response status should be 201
    And the JSON field "$.firstName" should be "Jane"
    And the JSON field "$.lastName" should be "Doe"
    And the JSON field "$.address" should be "123 Main St"

  Scenario: Update an existing owner
    When I update owner 1 with firstName "George" lastName "Franklin" address "999 New Ave" city "Madison" telephone "6085551023"
    Then the response status should be 204

  Scenario: Create owners from CSV test data
    When I create owners from CSV "testdata/owners.csv"
    Then the response status should be 201

  Scenario: Verify preloaded owner data
    When I send a GET request to "/owners/2"
    Then the response status should be 200
    And the JSON field "$.firstName" should be "Betty"
    And the JSON field "$.lastName" should be "Davis"
    And the JSON field "$.city" should be "Sun Prairie"
    And the JSON field "$.telephone" should be "6085551749"

  Scenario: Verify owner has pets
    When I send a GET request to "/owners/6"
    Then the response status should be 200
    And the JSON field "$.firstName" should be "Jean"
    And the JSON array "$.pets" should not be empty
