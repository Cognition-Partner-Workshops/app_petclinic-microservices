Feature: Data Validation Service
  As a modernization engineer
  I want to validate legacy data against modern systems
  So that I can ensure data migration accuracy

  Scenario: Reject unknown comparison type
    When I send a validation request of type "UNKNOWN_TYPE" with body:
      """
      {
        "sourceQuery": "SELECT 1",
        "destinationQuery": "SELECT 1",
        "compareFields": ["col1"]
      }
      """
    Then the response status should be 400
    And the validation report should have failed
    And the validation report should have an error message containing "Unknown comparison type"

  Scenario: DB_TO_DB validation request with missing datasource returns error
    When I send a validation request of type "db-to-db" with body:
      """
      {
        "sourceQuery": "SELECT id, first_name FROM owners",
        "destinationQuery": "SELECT id, first_name FROM owners",
        "compareFields": ["id", "first_name"]
      }
      """
    Then the response status should be 500
    And the validation report status should be "ERROR"

  Scenario: FILE_TO_API validation request with missing file returns error
    When I send a validation request of type "file-to-api" with body:
      """
      {
        "filePath": "nonexistent-file.dat",
        "fieldDefinitions": [
          {"name": "id", "startPosition": 0, "length": 5, "targetFieldName": "id"}
        ],
        "apiEndpoint": "http://localhost:8080/owners",
        "responseJsonPath": "$",
        "compareFields": ["id"]
      }
      """
    Then the response status should be 500
    And the validation report status should be "ERROR"

  Scenario: FILE_TO_DB validation request with missing file returns error
    When I send a validation request of type "file-to-db" with body:
      """
      {
        "filePath": "nonexistent-file.dat",
        "fieldDefinitions": [
          {"name": "id", "startPosition": 0, "length": 5, "targetFieldName": "id"}
        ],
        "destinationQuery": "SELECT id FROM owners",
        "compareFields": ["id"]
      }
      """
    Then the response status should be 500
    And the validation report status should be "ERROR"

  Scenario: Validate comparison type parsing is case-insensitive
    When I send a validation request of type "DB-TO-DB" with body:
      """
      {
        "sourceQuery": "SELECT 1",
        "destinationQuery": "SELECT 1",
        "compareFields": ["col1"]
      }
      """
    Then the response status should be 500
    And the validation report status should be "ERROR"
