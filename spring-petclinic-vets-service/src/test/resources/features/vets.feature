Feature: Veterinarian Management
  As a PetClinic user
  I want to view veterinarian information
  So that I can find the right vet for my pet

  Scenario: List all veterinarians
    When I request the list of veterinarians
    Then the response status should be 200
    And the response content type should be "application/json"
    And the vets list should have at least 6 entries

  Scenario: Verify first vet in the list
    When I request the list of veterinarians
    Then the response status should be 200
    And vet at index 0 should have firstName "James" and lastName "Carter"

  Scenario: Verify vet with radiology specialty
    When I request the list of veterinarians
    Then the response status should be 200
    And the vets list should contain a vet named "Helen" "Leary"
    And vet "Helen" "Leary" should have specialty "radiology"

  Scenario: Verify vet with multiple specialties
    When I request the list of veterinarians
    Then the response status should be 200
    And the vets list should contain a vet named "Linda" "Douglas"
    And vet "Linda" "Douglas" should have specialty "surgery"
    And vet "Linda" "Douglas" should have specialty "dentistry"

  Scenario: Verify vet with no specialties
    When I request the list of veterinarians
    Then the response status should be 200
    And vet "James" "Carter" should have no specialties

  Scenario: Verify all preloaded vets from CSV
    When I request the list of veterinarians
    Then the response status should be 200
    And all vets from CSV "testdata/vets.csv" should be present
