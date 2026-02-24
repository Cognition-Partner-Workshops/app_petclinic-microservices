@cross-service @integration
Feature: Cross-Service Data Consistency
  As a PetClinic system administrator
  I want to verify data consistency across microservices
  So that visits reference valid pets which reference valid owners

  # ---------------------------------------------------------------------------
  # Visit-to-Pet referential consistency
  # ---------------------------------------------------------------------------

  @positive @visit-pet
  Scenario Outline: Each seed visit references a pet that exists in the customers-service
    Given the visits-service is running
    And the customers-service is running
    When I send a GET request to the visits-service at "/owners/*/pets/<petId>/visits"
    Then the response status should be 200
    And the response should be a JSON array with at least <expectedVisits> entries
    When I send a GET request to the customers-service at "/owners/*/pets/<petId>"
    Then the response status should be 200
    And the response body field "id" should be <petId>
    And the response body field "name" should be "<petName>"

    Examples:
      | petId | expectedVisits | petName  | description                           |
      | 7     | 2              | Samantha | pet 7 (Samantha) has visits           |
      | 8     | 2              | Max      | pet 8 (Max) has visits                |

  @positive @visit-pet
  Scenario: All visits returned by batch query reference pets that exist
    Given the visits-service is running
    And the customers-service is running
    When I send a GET request to the visits-service at "/pets/visits?petId=7&petId=8"
    Then the response status should be 200
    And I collect all unique "petId" values from the "items" array
    Then for each collected petId, a GET request to the customers-service at "/owners/*/pets/{petId}" should return 200

  # ---------------------------------------------------------------------------
  # Pet-to-Owner referential consistency
  # ---------------------------------------------------------------------------

  @positive @pet-owner
  Scenario Outline: Each seed pet references a valid owner in the customers-service
    Given the customers-service is running
    When I send a GET request to "/owners/*/pets/<petId>"
    Then the response status should be 200
    And the response body field "owner" should be "<ownerFullName>"
    When I send a GET request to "/owners/<ownerId>"
    Then the response status should be 200
    And the response body field "firstName" should be "<ownerFirstName>"
    And the response body field "lastName" should be "<ownerLastName>"

    Examples:
      | petId | ownerId | ownerFirstName | ownerLastName | ownerFullName     |
      | 1     | 1       | George         | Franklin      | George Franklin   |
      | 2     | 2       | Betty          | Davis         | Betty Davis       |
      | 3     | 3       | Eduardo        | Rodriquez     | Eduardo Rodriquez |
      | 5     | 4       | Harold         | Davis         | Harold Davis      |
      | 7     | 6       | Jean           | Coleman       | Jean Coleman      |
      | 9     | 7       | Jeff           | Black         | Jeff Black        |
      | 12    | 10      | Carlos         | Estaban       | Carlos Estaban    |

  @positive @pet-owner
  Scenario Outline: Owner's pet list includes the expected pet
    Given the customers-service is running
    When I send a GET request to "/owners/<ownerId>"
    Then the response status should be 200
    And the response body field "pets" should contain a pet with name "<petName>" and id <petId>

    Examples:
      | ownerId | petId | petName   | description                      |
      | 1       | 1     | Leo       | George Franklin owns Leo         |
      | 2       | 2     | Basil     | Betty Davis owns Basil           |
      | 3       | 3     | Rosy      | Eduardo Rodriquez owns Rosy      |
      | 3       | 4     | Jewel     | Eduardo Rodriquez owns Jewel     |
      | 6       | 7     | Samantha  | Jean Coleman owns Samantha       |
      | 6       | 8     | Max       | Jean Coleman owns Max            |
      | 10      | 12    | Lucky     | Carlos Estaban owns Lucky        |
      | 10      | 13    | Sly       | Carlos Estaban owns Sly          |

  # ---------------------------------------------------------------------------
  # Visit-to-Pet-to-Owner full chain consistency
  # ---------------------------------------------------------------------------

  @positive @full-chain
  Scenario Outline: Full referential chain - visit references pet which references owner
    Given the visits-service is running
    And the customers-service is running
    When I send a GET request to the visits-service at "/owners/*/pets/<petId>/visits"
    Then the response status should be 200
    And the response should contain a visit with description "<visitDescription>"
    When I send a GET request to the customers-service at "/owners/*/pets/<petId>"
    Then the response status should be 200
    And the response body field "name" should be "<petName>"
    And the response body field "owner" should be "<ownerFullName>"
    When I send a GET request to the customers-service at "/owners/<ownerId>"
    Then the response status should be 200
    And the response body field "firstName" should be "<ownerFirstName>"

    Examples:
      | petId | visitDescription | petName  | ownerId | ownerFullName | ownerFirstName |
      | 7     | rabies shot      | Samantha | 6       | Jean Coleman  | Jean           |
      | 7     | spayed           | Samantha | 6       | Jean Coleman  | Jean           |
      | 8     | rabies shot      | Max      | 6       | Jean Coleman  | Jean           |
      | 8     | neutered         | Max      | 6       | Jean Coleman  | Jean           |

  # ---------------------------------------------------------------------------
  # Creating a visit for a newly created pet and owner
  # ---------------------------------------------------------------------------

  @positive @end-to-end
  Scenario: End-to-end flow - create owner, create pet, create visit, verify consistency
    Given the customers-service is running
    And the visits-service is running

    # Step 1: Create a new owner
    When I send a POST request to the customers-service at "/owners" with the following JSON body:
      | firstName | lastName    | address       | city      | telephone  |
      | EndToEnd  | TestOwner   | 999 Test Rd.  | TestCity  | 5550001111 |
    Then the response status should be 201
    And I capture the "id" from the response as "newOwnerId"

    # Step 2: Create a pet for the new owner
    When I send a POST request to the customers-service at "/owners/{newOwnerId}/pets" with the following JSON body:
      | id | birthDate  | name       | typeId |
      | 0  | 2024-01-01 | E2EPet     | 2      |
    Then the response status should be 201
    And I capture the "id" from the response as "newPetId"

    # Step 3: Create a visit for the new pet
    When I send a POST request to the visits-service at "/owners/*/pets/{newPetId}/visits" with the following JSON body:
      | date       | description        |
      | 2024-06-01 | end-to-end visit   |
    Then the response status should be 201
    And the response body field "petId" should equal the captured "newPetId"

    # Step 4: Verify the visit is retrievable
    When I send a GET request to the visits-service at "/owners/*/pets/{newPetId}/visits"
    Then the response status should be 200
    And the response should contain a visit with description "end-to-end visit"

    # Step 5: Verify the pet is retrievable and belongs to the owner
    When I send a GET request to the customers-service at "/owners/*/pets/{newPetId}"
    Then the response status should be 200
    And the response body field "name" should be "E2EPet"
    And the response body field "owner" should be "EndToEnd TestOwner"

    # Step 6: Verify the owner has the pet
    When I send a GET request to the customers-service at "/owners/{newOwnerId}"
    Then the response status should be 200
    And the response body field "pets" should contain a pet with name "E2EPet"

  # ---------------------------------------------------------------------------
  # Pet type consistency across pet creation and retrieval
  # ---------------------------------------------------------------------------

  @positive @pet-type
  Scenario Outline: Pet type is consistent between creation and retrieval
    Given the customers-service is running
    When I send a GET request to "/petTypes"
    Then the response status should be 200
    And the response should contain a pet type with id <typeId> and name "<typeName>"
    When I send a GET request to "/owners/*/pets/<petId>"
    Then the response status should be 200
    And the nested field "type.id" should be <typeId>
    And the nested field "type.name" should be "<typeName>"

    Examples:
      | petId | typeId | typeName | description              |
      | 1     | 1      | cat      | Leo is a cat             |
      | 3     | 2      | dog      | Rosy is a dog            |
      | 5     | 3      | lizard   | Iggy is a lizard         |
      | 6     | 4      | snake    | George pet is a snake    |
      | 9     | 5      | bird     | Lucky is a bird          |
      | 2     | 6      | hamster  | Basil is a hamster       |

  # ---------------------------------------------------------------------------
  # Batch visit query consistency with individual pet queries
  # ---------------------------------------------------------------------------

  @positive @batch-consistency
  Scenario: Batch visit query results match individual pet visit queries
    Given the visits-service is running
    When I send a GET request to "/pets/visits?petId=7&petId=8"
    Then the response status should be 200
    And I capture the "items" array size as "batchTotal"
    When I send a GET request to "/owners/*/pets/7/visits"
    Then the response status should be 200
    And I capture the response array size as "pet7Count"
    When I send a GET request to "/owners/*/pets/8/visits"
    Then the response status should be 200
    And I capture the response array size as "pet8Count"
    Then the sum of "pet7Count" and "pet8Count" should equal "batchTotal"

  # ---------------------------------------------------------------------------
  # Negative cross-service scenarios
  # ---------------------------------------------------------------------------

  @negative @orphan-visit
  Scenario: Creating a visit for a non-existent pet in visits-service still succeeds but pet is not in customers-service
    Given the visits-service is running
    And the customers-service is running
    When I send a POST request to the visits-service at "/owners/*/pets/99999/visits" with the following JSON body:
      | date       | description    |
      | 2024-01-01 | orphan visit   |
    Then the response status should be 201
    When I send a GET request to the customers-service at "/owners/*/pets/99999"
    Then the response status should be 404

  @negative @cross-service
  Scenario Outline: Visits-service accepts any petId but customers-service validates pet existence
    Given the visits-service is running
    And the customers-service is running
    When I send a POST request to the visits-service at "/owners/*/pets/<petId>/visits" with the following JSON body:
      | date       | description         |
      | 2024-01-01 | consistency check   |
    Then the response status should be <visitStatus>
    When I send a GET request to the customers-service at "/owners/*/pets/<petId>"
    Then the response status should be <customerStatus>

    Examples:
      | petId | visitStatus | customerStatus | description                              |
      | 7     | 201         | 200            | existing pet - both services agree       |
      | 50000 | 201         | 404            | non-existent pet - visits accepts anyway  |

  # ---------------------------------------------------------------------------
  # Owner count and pet count consistency
  # ---------------------------------------------------------------------------

  @positive @count-consistency
  Scenario: Total pets across all owners matches individual owner pet counts
    Given the customers-service is running
    When I send a GET request to "/owners"
    Then the response status should be 200
    And for each owner in the response, the "pets" array size should match the number of pets for that owner
    And the sum of all owners' pet counts should equal the total number of unique pets in the system
