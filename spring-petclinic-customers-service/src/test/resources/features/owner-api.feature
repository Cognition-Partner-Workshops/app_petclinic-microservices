@customers-service @owners
Feature: Owner REST API
  As a PetClinic API consumer
  I want to manage pet owners via the REST API
  So that I can create, retrieve, and update owner information

  # ---------------------------------------------------------------------------
  # GET /owners - List all owners
  # ---------------------------------------------------------------------------

  @positive @get
  Scenario: Retrieve all owners returns a non-empty list
    Given the customers-service is running
    When I send a GET request to "/owners"
    Then the response status should be 200
    And the response should be a JSON array
    And each owner object in the response should contain the following fields:
      | field     | type    |
      | id        | integer |
      | firstName | string  |
      | lastName  | string  |
      | address   | string  |
      | city      | string  |
      | telephone | string  |
      | pets      | array   |

  @positive @get
  Scenario: Verify known seed owners are present in the list
    Given the customers-service is running
    And the following owners exist in the database:
      | firstName | lastName  | address              | city        | telephone  |
      | George    | Franklin  | 110 W. Liberty St.   | Madison     | 6085551023 |
      | Betty     | Davis     | 638 Cardinal Ave.    | Sun Prairie | 6085551749 |
      | Eduardo   | Rodriquez | 2693 Commerce St.    | McFarland   | 6085558763 |
      | Harold    | Davis     | 563 Friendly St.     | Windsor     | 6085553198 |
      | Peter     | McTavish  | 2387 S. Fair Way     | Madison     | 6085552765 |
    When I send a GET request to "/owners"
    Then the response status should be 200
    And the response should contain at least 5 owner entries

  # ---------------------------------------------------------------------------
  # GET /owners/{ownerId} - Get a single owner
  # ---------------------------------------------------------------------------

  @positive @get
  Scenario Outline: Retrieve a specific owner by ID returns correct data
    Given the customers-service is running
    When I send a GET request to "/owners/<ownerId>"
    Then the response status should be 200
    And the response body field "firstName" should be "<firstName>"
    And the response body field "lastName" should be "<lastName>"
    And the response body field "address" should be "<address>"
    And the response body field "city" should be "<city>"
    And the response body field "telephone" should be "<telephone>"

    Examples:
      | ownerId | firstName | lastName  | address              | city        | telephone  |
      | 1       | George    | Franklin  | 110 W. Liberty St.   | Madison     | 6085551023 |
      | 2       | Betty     | Davis     | 638 Cardinal Ave.    | Sun Prairie | 6085551749 |
      | 3       | Eduardo   | Rodriquez | 2693 Commerce St.    | McFarland   | 6085558763 |
      | 6       | Jean      | Coleman   | 105 N. Lake St.      | Monona      | 6085552654 |
      | 10      | Carlos    | Estaban   | 2335 Independence La.| Waunakee    | 6085555487 |

  @positive @get
  Scenario: Retrieve an owner that has pets includes pet details
    Given the customers-service is running
    When I send a GET request to "/owners/1"
    Then the response status should be 200
    And the response body field "pets" should be a non-empty array
    And each pet in the "pets" array should contain the following fields:
      | field     | type    |
      | id        | integer |
      | name      | string  |
      | birthDate | string  |
      | type      | object  |

  @negative @get
  Scenario Outline: Retrieve a non-existent owner returns empty or 404
    Given the customers-service is running
    When I send a GET request to "/owners/<ownerId>"
    Then the response status should be <expectedStatus>

    Examples:
      | ownerId | expectedStatus |
      | 99999   | 404            |
      | 0       | 400            |
      | -1      | 400            |

  # ---------------------------------------------------------------------------
  # POST /owners - Create a new owner
  # ---------------------------------------------------------------------------

  @positive @post
  Scenario Outline: Create a new owner with valid data returns 201
    Given the customers-service is running
    When I send a POST request to "/owners" with the following JSON body:
      | firstName   | lastName   | address   | city   | telephone   |
      | <firstName> | <lastName> | <address> | <city> | <telephone> |
    Then the response status should be 201
    And the response body field "id" should be a positive integer
    And the response body field "firstName" should be "<firstName>"
    And the response body field "lastName" should be "<lastName>"
    And the response body field "address" should be "<address>"
    And the response body field "city" should be "<city>"
    And the response body field "telephone" should be "<telephone>"

    Examples:
      | firstName | lastName  | address           | city       | telephone  |
      | Alice     | Smith     | 123 Main St.      | Springfield| 5551234567 |
      | Bob       | Johnson   | 456 Oak Ave.      | Riverside  | 5559876543 |
      | Charlie   | Williams  | 789 Pine Blvd.    | Lakeview   | 5555551234 |

  @positive @post
  Scenario: Created owner is retrievable by ID
    Given the customers-service is running
    When I send a POST request to "/owners" with the following JSON body:
      | firstName | lastName | address       | city     | telephone  |
      | Retrieve  | Test     | 100 Test Lane | TestCity | 1234567890 |
    Then the response status should be 201
    And I capture the "id" from the response
    When I send a GET request to "/owners/{capturedId}"
    Then the response status should be 200
    And the response body field "firstName" should be "Retrieve"
    And the response body field "lastName" should be "Test"

  @negative @post @validation
  Scenario Outline: Create an owner with missing required fields returns 400
    Given the customers-service is running
    When I send a POST request to "/owners" with the following JSON body:
      | firstName   | lastName   | address   | city   | telephone   |
      | <firstName> | <lastName> | <address> | <city> | <telephone> |
    Then the response status should be 400

    Examples:
      | firstName | lastName | address        | city      | telephone  | description                |
      |           | Smith    | 123 Main St.   | Madison   | 5551234567 | missing firstName          |
      | Alice     |          | 123 Main St.   | Madison   | 5551234567 | missing lastName           |
      | Alice     | Smith    |                | Madison   | 5551234567 | missing address            |
      | Alice     | Smith    | 123 Main St.   |           | 5551234567 | missing city               |
      | Alice     | Smith    | 123 Main St.   | Madison   |            | missing telephone          |

  @negative @post @validation
  Scenario: Create an owner with empty JSON body returns 400
    Given the customers-service is running
    When I send a POST request to "/owners" with an empty JSON body
    Then the response status should be 400

  @negative @post @validation
  Scenario Outline: Create an owner with invalid telephone format returns 400
    Given the customers-service is running
    When I send a POST request to "/owners" with the following JSON body:
      | firstName | lastName | address      | city    | telephone   |
      | Alice     | Smith    | 123 Main St. | Madison | <telephone> |
    Then the response status should be 400

    Examples:
      | telephone       | description                           |
      | abc-def-ghij    | alphabetic characters                 |
      | 123.456.7890    | contains decimal points               |
      | 1234567890123   | exceeds 12-digit maximum              |
      | +1-555-123-4567 | contains special characters            |

  # ---------------------------------------------------------------------------
  # PUT /owners/{ownerId} - Update an existing owner
  # ---------------------------------------------------------------------------

  @positive @put
  Scenario Outline: Update an existing owner with valid data returns 204
    Given the customers-service is running
    And an owner with ID <ownerId> exists
    When I send a PUT request to "/owners/<ownerId>" with the following JSON body:
      | firstName   | lastName   | address   | city   | telephone   |
      | <firstName> | <lastName> | <address> | <city> | <telephone> |
    Then the response status should be 204

    Examples:
      | ownerId | firstName | lastName  | address              | city       | telephone  |
      | 1       | George    | Franklin  | 999 Updated Blvd.    | NewCity    | 6085551023 |
      | 2       | Betty     | DavisUpd  | 638 Cardinal Ave.    | Sun Prairie| 6085551749 |

  @positive @put
  Scenario: Updated owner data is persisted and retrievable
    Given the customers-service is running
    And an owner with ID 3 exists
    When I send a PUT request to "/owners/3" with the following JSON body:
      | firstName | lastName     | address          | city       | telephone  |
      | Eduardo   | UpdatedName  | 2693 Commerce St.| McFarland  | 6085558763 |
    Then the response status should be 204
    When I send a GET request to "/owners/3"
    Then the response status should be 200
    And the response body field "lastName" should be "UpdatedName"

  @negative @put
  Scenario: Update a non-existent owner returns 404
    Given the customers-service is running
    When I send a PUT request to "/owners/99999" with the following JSON body:
      | firstName | lastName | address      | city    | telephone  |
      | Ghost     | Owner    | 000 Nowhere  | Void    | 0000000000 |
    Then the response status should be 404

  @negative @put @validation
  Scenario Outline: Update an owner with invalid data returns 400
    Given the customers-service is running
    And an owner with ID 1 exists
    When I send a PUT request to "/owners/1" with the following JSON body:
      | firstName   | lastName   | address   | city   | telephone   |
      | <firstName> | <lastName> | <address> | <city> | <telephone> |
    Then the response status should be 400

    Examples:
      | firstName | lastName | address        | city    | telephone  | description         |
      |           | Franklin | 110 W. Liberty | Madison | 6085551023 | blank firstName     |
      | George    |          | 110 W. Liberty | Madison | 6085551023 | blank lastName      |
      | George    | Franklin |                | Madison | 6085551023 | blank address       |
      | George    | Franklin | 110 W. Liberty |         | 6085551023 | blank city          |
      | George    | Franklin | 110 W. Liberty | Madison |            | blank telephone     |
      | George    | Franklin | 110 W. Liberty | Madison | not-a-phone| invalid telephone   |

  # ---------------------------------------------------------------------------
  # Response payload structure validation
  # ---------------------------------------------------------------------------

  @positive @payload
  Scenario: Owner response payload matches expected field mapping
    Given the customers-service is running
    When I send a GET request to "/owners/1"
    Then the response status should be 200
    And the response Content-Type should be "application/json"
    And the JSON response should conform to the following schema:
      | field     | type    | nullable |
      | id        | integer | false    |
      | firstName | string  | false    |
      | lastName  | string  | false    |
      | address   | string  | false    |
      | city      | string  | false    |
      | telephone | string  | false    |
      | pets      | array   | false    |

  @positive @payload
  Scenario: Owner list response contains consistent structure for all entries
    Given the customers-service is running
    When I send a GET request to "/owners"
    Then the response status should be 200
    And every object in the response array should have the same set of fields:
      | field     |
      | id        |
      | firstName |
      | lastName  |
      | address   |
      | city      |
      | telephone |
      | pets      |
