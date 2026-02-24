@visits-service @visits
Feature: Visit REST API
  As a PetClinic API consumer
  I want to manage veterinary visits via the REST API
  So that I can create and retrieve visit records for pets

  # ---------------------------------------------------------------------------
  # POST /owners/*/pets/{petId}/visits - Create a new visit
  # ---------------------------------------------------------------------------

  @positive @post
  Scenario Outline: Create a new visit for a pet returns 201
    Given the visits-service is running
    When I send a POST request to "/owners/*/pets/<petId>/visits" with the following JSON body:
      | date       | description   |
      | <date>     | <description> |
    Then the response status should be 201
    And the response body field "id" should be a positive integer
    And the response body field "petId" should be <petId>
    And the response body field "description" should be "<description>"

    Examples:
      | petId | date       | description        |
      | 1     | 2024-01-15 | annual checkup     |
      | 2     | 2024-02-20 | vaccination        |
      | 3     | 2024-03-10 | dental cleaning    |
      | 7     | 2024-04-05 | follow-up visit    |
      | 10    | 2024-05-12 | heartworm test     |

  @positive @post
  Scenario: Created visit includes the date field in response
    Given the visits-service is running
    When I send a POST request to "/owners/*/pets/1/visits" with the following JSON body:
      | date       | description       |
      | 2024-06-15 | date check visit  |
    Then the response status should be 201
    And the response body field "date" should be "2024-06-15"

  @positive @post
  Scenario: Created visit is retrievable via GET
    Given the visits-service is running
    When I send a POST request to "/owners/*/pets/1/visits" with the following JSON body:
      | date       | description         |
      | 2024-07-01 | retrievable visit   |
    Then the response status should be 201
    And I capture the "id" from the response
    When I send a GET request to "/owners/*/pets/1/visits"
    Then the response status should be 200
    And the response should contain a visit with description "retrievable visit"

  @positive @post @payload
  Scenario: Visit creation response payload matches expected field mapping
    Given the visits-service is running
    When I send a POST request to "/owners/*/pets/1/visits" with the following JSON body:
      | date       | description       |
      | 2024-08-01 | payload check     |
    Then the response status should be 201
    And the response Content-Type should be "application/json"
    And the JSON response should conform to the following schema:
      | field       | type    | nullable |
      | id          | integer | false    |
      | date        | string  | false    |
      | description | string  | false    |
      | petId       | integer | false    |

  @negative @post @validation
  Scenario: Create a visit with description exceeding 8192 characters returns 400
    Given the visits-service is running
    When I send a POST request to "/owners/*/pets/1/visits" with a description of 8193 characters
    Then the response status should be 400

  @negative @post @validation
  Scenario: Create a visit with invalid date format returns 400
    Given the visits-service is running
    When I send a POST request to "/owners/*/pets/1/visits" with the following JSON body:
      | date        | description     |
      | not-a-date  | bad date visit  |
    Then the response status should be 400

  @negative @post
  Scenario: Create a visit with invalid petId path parameter returns 400
    Given the visits-service is running
    When I send a POST request to "/owners/*/pets/0/visits" with the following JSON body:
      | date       | description     |
      | 2024-01-15 | invalid pet id  |
    Then the response status should be 400

  @positive @post
  Scenario: Create a visit with empty description is accepted
    Given the visits-service is running
    When I send a POST request to "/owners/*/pets/1/visits" with the following JSON body:
      | date       | description |
      | 2024-09-01 |             |
    Then the response status should be 201
    And the response body field "petId" should be 1

  @positive @post
  Scenario: Create a visit without explicit date uses server default date
    Given the visits-service is running
    When I send a POST request to "/owners/*/pets/1/visits" with the following JSON body:
      | description       |
      | no date provided  |
    Then the response status should be 201
    And the response body field "date" should not be null

  # ---------------------------------------------------------------------------
  # GET /owners/*/pets/{petId}/visits - Get visits for a pet
  # ---------------------------------------------------------------------------

  @positive @get
  Scenario: Retrieve visits for a pet with existing visits returns a list
    Given the visits-service is running
    And the following seed visits exist:
      | id | petId | date       | description  |
      | 1  | 7     | 2013-01-01 | rabies shot  |
      | 4  | 7     | 2013-01-04 | spayed       |
    When I send a GET request to "/owners/*/pets/7/visits"
    Then the response status should be 200
    And the response should be a JSON array
    And the response should contain at least 2 visit entries

  @positive @get
  Scenario Outline: Retrieve visits for known seed pets
    Given the visits-service is running
    When I send a GET request to "/owners/*/pets/<petId>/visits"
    Then the response status should be 200
    And the response should be a JSON array
    And the response should contain at least <minVisits> visit entries

    Examples:
      | petId | minVisits | description                  |
      | 7     | 2         | pet 7 has at least 2 visits  |
      | 8     | 2         | pet 8 has at least 2 visits  |

  @positive @get
  Scenario: Retrieve visits for a pet with no visits returns an empty list
    Given the visits-service is running
    When I send a GET request to "/owners/*/pets/99/visits"
    Then the response status should be 200
    And the response should be an empty JSON array

  @positive @get @payload
  Scenario: Visit list response payload matches expected field mapping
    Given the visits-service is running
    When I send a GET request to "/owners/*/pets/7/visits"
    Then the response status should be 200
    And each visit object in the response should contain the following fields:
      | field       | type    |
      | id          | integer |
      | date        | string  |
      | description | string  |
      | petId       | integer |

  @positive @get
  Scenario: All visits for a specific pet have the correct petId
    Given the visits-service is running
    When I send a GET request to "/owners/*/pets/7/visits"
    Then the response status should be 200
    And every visit in the response should have "petId" equal to 7

  @positive @get
  Scenario Outline: Verify seed visit data is returned correctly
    Given the visits-service is running
    When I send a GET request to "/owners/*/pets/<petId>/visits"
    Then the response status should be 200
    And the response should contain a visit with description "<description>"

    Examples:
      | petId | description  |
      | 7     | rabies shot  |
      | 7     | spayed       |
      | 8     | rabies shot  |
      | 8     | neutered     |

  # ---------------------------------------------------------------------------
  # GET /pets/visits?petId= - Batch query visits by multiple pet IDs
  # ---------------------------------------------------------------------------

  @positive @get @batch
  Scenario: Retrieve visits for multiple pets in a single batch request
    Given the visits-service is running
    When I send a GET request to "/pets/visits?petId=7&petId=8"
    Then the response status should be 200
    And the response body field "items" should be a non-empty array

  @positive @get @batch @payload
  Scenario: Batch visit response payload matches expected structure
    Given the visits-service is running
    When I send a GET request to "/pets/visits?petId=7&petId=8"
    Then the response status should be 200
    And the response Content-Type should be "application/json"
    And the JSON response should have a top-level "items" field of type array
    And each item in the "items" array should contain the following fields:
      | field       | type    |
      | id          | integer |
      | date        | string  |
      | description | string  |
      | petId       | integer |

  @positive @get @batch
  Scenario: Batch query returns visits from all requested pets
    Given the visits-service is running
    When I send a GET request to "/pets/visits?petId=7&petId=8"
    Then the response status should be 200
    And the "items" array should contain visits for petId 7
    And the "items" array should contain visits for petId 8

  @positive @get @batch
  Scenario Outline: Batch query with varying pet ID combinations
    Given the visits-service is running
    When I send a GET request to "/pets/visits?<queryString>"
    Then the response status should be 200
    And the response body field "items" should be a JSON array

    Examples:
      | queryString                     | description                    |
      | petId=7                         | single pet with visits         |
      | petId=7&petId=8                 | two pets with visits           |
      | petId=7&petId=8&petId=1         | three pets mixed               |
      | petId=99                        | single pet with no visits      |

  @positive @get @batch
  Scenario: Batch query for pet with no visits returns empty items
    Given the visits-service is running
    When I send a GET request to "/pets/visits?petId=99"
    Then the response status should be 200
    And the response body field "items" should be an empty array

  @negative @get @batch
  Scenario: Batch query without petId parameter returns 400
    Given the visits-service is running
    When I send a GET request to "/pets/visits"
    Then the response status should be 400

  # ---------------------------------------------------------------------------
  # Negative / edge-case scenarios
  # ---------------------------------------------------------------------------

  @negative @method-not-allowed
  Scenario Outline: Unsupported HTTP methods on visit endpoints return 405
    Given the visits-service is running
    When I send a <method> request to "/owners/*/pets/7/visits"
    Then the response status should be 405

    Examples:
      | method |
      | PUT    |
      | DELETE |
      | PATCH  |

  @positive @get @content-type
  Scenario: GET visits returns application/json content type
    Given the visits-service is running
    When I send a GET request to "/owners/*/pets/7/visits"
    Then the response status should be 200
    And the response Content-Type should be "application/json"
