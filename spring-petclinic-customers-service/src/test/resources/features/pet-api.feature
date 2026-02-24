@customers-service @pets
Feature: Pet REST API
  As a PetClinic API consumer
  I want to manage pets via the REST API
  So that I can create, retrieve, and update pet information for owners

  # ---------------------------------------------------------------------------
  # GET /petTypes - List all pet types
  # ---------------------------------------------------------------------------

  @positive @get @pet-types
  Scenario: Retrieve all pet types returns a non-empty list
    Given the customers-service is running
    When I send a GET request to "/petTypes"
    Then the response status should be 200
    And the response should be a JSON array
    And the response should contain the following pet types:
      | id | name    |
      | 1  | cat     |
      | 2  | dog     |
      | 3  | lizard  |
      | 4  | snake   |
      | 5  | bird    |
      | 6  | hamster |

  @positive @get @pet-types
  Scenario: Pet type response payload matches expected field mapping
    Given the customers-service is running
    When I send a GET request to "/petTypes"
    Then the response status should be 200
    And each pet type object should contain the following fields:
      | field | type    |
      | id    | integer |
      | name  | string  |

  # ---------------------------------------------------------------------------
  # POST /owners/{ownerId}/pets - Create a new pet for an owner
  # ---------------------------------------------------------------------------

  @positive @post
  Scenario Outline: Create a new pet for an existing owner returns 201
    Given the customers-service is running
    And an owner with ID <ownerId> exists
    When I send a POST request to "/owners/<ownerId>/pets" with the following JSON body:
      | id | birthDate   | name   | typeId   |
      | 0  | <birthDate> | <name> | <typeId> |
    Then the response status should be 201
    And the response body field "id" should be a positive integer
    And the response body field "name" should be "<name>"

    Examples:
      | ownerId | birthDate  | name     | typeId | description           |
      | 1       | 2023-01-15 | Whiskers | 1      | cat for George        |
      | 2       | 2022-06-20 | Buddy    | 2      | dog for Betty         |
      | 3       | 2021-11-01 | Scales   | 3      | lizard for Eduardo    |
      | 4       | 2020-03-10 | Slinky   | 4      | snake for Harold      |
      | 5       | 2023-07-04 | Tweety   | 5      | bird for Peter        |
      | 6       | 2022-12-25 | Hammy    | 6      | hamster for Jean      |

  @positive @post
  Scenario: Created pet appears in owner's pet list
    Given the customers-service is running
    And an owner with ID 7 exists
    When I send a POST request to "/owners/7/pets" with the following JSON body:
      | id | birthDate  | name    | typeId |
      | 0  | 2023-05-10 | NewPet  | 2      |
    Then the response status should be 201
    And I capture the "id" from the response
    When I send a GET request to "/owners/7"
    Then the response status should be 200
    And the response body field "pets" should contain a pet with name "NewPet"

  @negative @post
  Scenario: Create a pet for a non-existent owner returns 404
    Given the customers-service is running
    When I send a POST request to "/owners/99999/pets" with the following JSON body:
      | id | birthDate  | name   | typeId |
      | 0  | 2023-01-15 | Ghost  | 1      |
    Then the response status should be 404

  @negative @post
  Scenario: Create a pet with invalid owner ID returns 400
    Given the customers-service is running
    When I send a POST request to "/owners/0/pets" with the following JSON body:
      | id | birthDate  | name   | typeId |
      | 0  | 2023-01-15 | BadPet | 1      |
    Then the response status should be 400

  @negative @post @validation
  Scenario Outline: Create a pet with invalid or missing data
    Given the customers-service is running
    And an owner with ID 1 exists
    When I send a POST request to "/owners/1/pets" with the following JSON body:
      | id | birthDate   | name   | typeId   |
      | 0  | <birthDate> | <name> | <typeId> |
    Then the response status should be <expectedStatus>

    Examples:
      | birthDate  | name | typeId | expectedStatus | description                     |
      | 2023-01-15 |      | 1      | 400            | empty pet name violates @Size   |
      | invalid    | Rex  | 1      | 400            | invalid date format             |

  # ---------------------------------------------------------------------------
  # GET /owners/*/pets/{petId} - Get a specific pet by ID
  # ---------------------------------------------------------------------------

  @positive @get
  Scenario Outline: Retrieve a specific pet by ID returns correct data
    Given the customers-service is running
    When I send a GET request to "/owners/*/pets/<petId>"
    Then the response status should be 200
    And the response body field "id" should be <petId>
    And the response body field "name" should be "<name>"
    And the response body field "owner" should be "<ownerFullName>"

    Examples:
      | petId | name      | ownerFullName     |
      | 1     | Leo       | George Franklin   |
      | 2     | Basil     | Betty Davis       |
      | 3     | Rosy      | Eduardo Rodriquez |
      | 7     | Samantha  | Jean Coleman      |
      | 13    | Sly       | Carlos Estaban    |

  @positive @get @payload
  Scenario: Pet details response payload matches expected field mapping
    Given the customers-service is running
    When I send a GET request to "/owners/*/pets/1"
    Then the response status should be 200
    And the response Content-Type should be "application/json"
    And the JSON response should conform to the following schema:
      | field     | type    | nullable |
      | id        | integer | false    |
      | name      | string  | false    |
      | owner     | string  | false    |
      | birthDate | string  | false    |
      | type      | object  | false    |
    And the "type" object should contain the following fields:
      | field | type    |
      | id    | integer |
      | name  | string  |

  @negative @get
  Scenario Outline: Retrieve a non-existent pet returns 404
    Given the customers-service is running
    When I send a GET request to "/owners/*/pets/<petId>"
    Then the response status should be <expectedStatus>

    Examples:
      | petId | expectedStatus | description        |
      | 99999 | 404            | non-existent pet   |
      | 0     | 404            | zero pet ID        |

  # ---------------------------------------------------------------------------
  # PUT /owners/*/pets/{petId} - Update an existing pet
  # ---------------------------------------------------------------------------

  @positive @put
  Scenario Outline: Update an existing pet with valid data returns 204
    Given the customers-service is running
    And a pet with ID <petId> exists
    When I send a PUT request to "/owners/*/pets/<petId>" with the following JSON body:
      | id      | birthDate   | name   | typeId   |
      | <petId> | <birthDate> | <name> | <typeId> |
    Then the response status should be 204

    Examples:
      | petId | birthDate  | name       | typeId | description                |
      | 1     | 2010-09-07 | LeoUpdated | 1      | update pet name            |
      | 2     | 2022-01-01 | Basil      | 6      | update birth date and keep |
      | 3     | 2011-04-17 | Rosy       | 1      | change pet type            |

  @positive @put
  Scenario: Updated pet data is persisted and retrievable
    Given the customers-service is running
    And a pet with ID 5 exists
    When I send a PUT request to "/owners/*/pets/5" with the following JSON body:
      | id | birthDate  | name        | typeId |
      | 5  | 2010-11-30 | IggyUpdated | 3      |
    Then the response status should be 204
    When I send a GET request to "/owners/*/pets/5"
    Then the response status should be 200
    And the response body field "name" should be "IggyUpdated"

  @negative @put
  Scenario: Update a non-existent pet returns 404
    Given the customers-service is running
    When I send a PUT request to "/owners/*/pets/99999" with the following JSON body:
      | id    | birthDate  | name    | typeId |
      | 99999 | 2023-01-15 | Phantom | 1      |
    Then the response status should be 404

  # ---------------------------------------------------------------------------
  # Pet-Owner relationship integrity
  # ---------------------------------------------------------------------------

  @positive @relationship
  Scenario Outline: Each seed pet belongs to the correct owner
    Given the customers-service is running
    When I send a GET request to "/owners/*/pets/<petId>"
    Then the response status should be 200
    And the response body field "owner" should be "<ownerFullName>"

    Examples:
      | petId | ownerFullName      | description             |
      | 1     | George Franklin    | Leo belongs to George   |
      | 4     | Eduardo Rodriquez  | Jewel belongs to Eduardo|
      | 6     | Peter McTavish     | George pet of Peter     |
      | 9     | Jeff Black         | Lucky belongs to Jeff   |
      | 12    | Carlos Estaban     | Lucky belongs to Carlos |

  @positive @relationship
  Scenario Outline: Each seed pet has the correct pet type
    Given the customers-service is running
    When I send a GET request to "/owners/*/pets/<petId>"
    Then the response status should be 200
    And the nested field "type.name" should be "<typeName>"

    Examples:
      | petId | typeName | description      |
      | 1     | cat      | Leo is a cat     |
      | 3     | dog      | Rosy is a dog    |
      | 5     | lizard   | Iggy is a lizard |
      | 6     | snake    | George is a snake|
      | 9     | bird     | Lucky is a bird  |
      | 2     | hamster  | Basil is hamster |
