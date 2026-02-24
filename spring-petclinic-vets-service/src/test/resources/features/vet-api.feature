@vets-service @vets
Feature: Vet REST API
  As a PetClinic API consumer
  I want to retrieve veterinarian information via the REST API
  So that I can view vets and their specialties

  # ---------------------------------------------------------------------------
  # GET /vets - List all veterinarians
  # ---------------------------------------------------------------------------

  @positive @get
  Scenario: Retrieve all vets returns a non-empty list
    Given the vets-service is running
    When I send a GET request to "/vets"
    Then the response status should be 200
    And the response should be a JSON array
    And the response should contain at least 6 vet entries

  @positive @get @payload
  Scenario: Vet response payload matches expected field mapping
    Given the vets-service is running
    When I send a GET request to "/vets"
    Then the response status should be 200
    And the response Content-Type should be "application/json"
    And each vet object in the response should contain the following fields:
      | field        | type    |
      | id           | integer |
      | firstName    | string  |
      | lastName     | string  |
      | specialties  | array   |

  @positive @get @payload
  Scenario: Every vet object has a consistent structure across the list
    Given the vets-service is running
    When I send a GET request to "/vets"
    Then the response status should be 200
    And every object in the response array should have the same set of fields:
      | field           |
      | id              |
      | firstName       |
      | lastName        |
      | specialties     |
      | nrOfSpecialties |

  # ---------------------------------------------------------------------------
  # Verify known seed vets are present with correct data
  # ---------------------------------------------------------------------------

  @positive @get @seed-data
  Scenario Outline: Verify seed vet data is returned correctly
    Given the vets-service is running
    When I send a GET request to "/vets"
    Then the response status should be 200
    And the response should contain a vet with firstName "<firstName>" and lastName "<lastName>"

    Examples:
      | firstName | lastName |
      | James     | Carter   |
      | Helen     | Leary    |
      | Linda     | Douglas  |
      | Rafael    | Ortega   |
      | Henry     | Stevens  |
      | Sharon    | Jenkins  |

  # ---------------------------------------------------------------------------
  # Specialty field mapping and structure
  # ---------------------------------------------------------------------------

  @positive @get @specialties @payload
  Scenario: Specialty objects within vets contain correct fields
    Given the vets-service is running
    When I send a GET request to "/vets"
    Then the response status should be 200
    And vets that have specialties should have specialty objects with the following fields:
      | field | type    |
      | id    | integer |
      | name  | string  |

  @positive @get @specialties
  Scenario Outline: Verify vets have the correct number of specialties
    Given the vets-service is running
    When I send a GET request to "/vets"
    Then the response status should be 200
    And the vet with firstName "<firstName>" and lastName "<lastName>" should have <specialtyCount> specialties

    Examples:
      | firstName | lastName | specialtyCount | description                       |
      | James     | Carter   | 0              | James Carter has no specialties   |
      | Helen     | Leary    | 1              | Helen Leary has 1 specialty       |
      | Linda     | Douglas  | 2              | Linda Douglas has 2 specialties   |
      | Rafael    | Ortega   | 1              | Rafael Ortega has 1 specialty     |
      | Henry     | Stevens  | 1              | Henry Stevens has 1 specialty     |
      | Sharon    | Jenkins  | 0              | Sharon Jenkins has no specialties  |

  @positive @get @specialties
  Scenario Outline: Verify specific vet-specialty assignments
    Given the vets-service is running
    When I send a GET request to "/vets"
    Then the response status should be 200
    And the vet with firstName "<firstName>" and lastName "<lastName>" should have specialty "<specialtyName>"

    Examples:
      | firstName | lastName | specialtyName | description                     |
      | Helen     | Leary    | radiology     | Helen specializes in radiology  |
      | Linda     | Douglas  | surgery       | Linda specializes in surgery    |
      | Linda     | Douglas  | dentistry     | Linda also in dentistry         |
      | Rafael    | Ortega   | surgery       | Rafael specializes in surgery   |
      | Henry     | Stevens  | radiology     | Henry specializes in radiology  |

  @positive @get @specialties
  Scenario: Vets without specialties return an empty specialties array
    Given the vets-service is running
    When I send a GET request to "/vets"
    Then the response status should be 200
    And the vet with firstName "James" and lastName "Carter" should have an empty "specialties" array
    And the vet with firstName "Sharon" and lastName "Jenkins" should have an empty "specialties" array

  # ---------------------------------------------------------------------------
  # nrOfSpecialties computed field validation
  # ---------------------------------------------------------------------------

  @positive @get @computed-fields
  Scenario Outline: The nrOfSpecialties field matches the actual specialties array length
    Given the vets-service is running
    When I send a GET request to "/vets"
    Then the response status should be 200
    And for the vet with firstName "<firstName>" and lastName "<lastName>" the "nrOfSpecialties" field should equal the size of the "specialties" array

    Examples:
      | firstName | lastName |
      | James     | Carter   |
      | Helen     | Leary    |
      | Linda     | Douglas  |
      | Rafael    | Ortega   |
      | Henry     | Stevens  |
      | Sharon    | Jenkins  |

  # ---------------------------------------------------------------------------
  # Caching behavior
  # ---------------------------------------------------------------------------

  @positive @get @caching
  Scenario: Consecutive GET requests to /vets return consistent results
    Given the vets-service is running
    When I send a GET request to "/vets"
    Then the response status should be 200
    And I capture the response body as "firstResponse"
    When I send a GET request to "/vets"
    Then the response status should be 200
    And the response body should be identical to "firstResponse"

  # ---------------------------------------------------------------------------
  # Negative / edge-case scenarios
  # ---------------------------------------------------------------------------

  @negative @method-not-allowed
  Scenario Outline: Unsupported HTTP methods on /vets return 405
    Given the vets-service is running
    When I send a <method> request to "/vets"
    Then the response status should be 405

    Examples:
      | method |
      | POST   |
      | PUT    |
      | DELETE |
      | PATCH  |

  @negative @get
  Scenario: GET /vets with an unsupported sub-path returns 404
    Given the vets-service is running
    When I send a GET request to "/vets/99999"
    Then the response status should be 404

  @positive @get @content-type
  Scenario: GET /vets returns application/json content type
    Given the vets-service is running
    When I send a GET request to "/vets"
    Then the response status should be 200
    And the response Content-Type should be "application/json"

  # ---------------------------------------------------------------------------
  # Specialty seed data integrity
  # ---------------------------------------------------------------------------

  @positive @specialties @seed-data
  Scenario: All known specialties are represented across vets
    Given the vets-service is running
    When I send a GET request to "/vets"
    Then the response status should be 200
    And the following specialties should appear across all vets:
      | specialtyName |
      | radiology     |
      | surgery       |
      | dentistry     |
