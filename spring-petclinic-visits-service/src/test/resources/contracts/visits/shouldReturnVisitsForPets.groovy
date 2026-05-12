import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return visits for given pet ids"
    request {
        method GET()
        url("/pets/visits") {
            queryParameters {
                parameter "petId": "1,2"
            }
        }
        headers {
            accept(applicationJson())
        }
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body([
            items: [
                [
                    id         : 1,
                    petId      : 1,
                    date       : "2023-01-01",
                    description: "rabies shot"
                ],
                [
                    id         : 2,
                    petId      : 2,
                    date       : "2023-03-15",
                    description: "neutered"
                ]
            ]
        ])
    }
}
