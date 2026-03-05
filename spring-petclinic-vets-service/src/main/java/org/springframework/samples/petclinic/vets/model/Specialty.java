/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vets.model;

import jakarta.persistence.*;

/**
 * JPA entity representing a veterinarian's area of expertise (e.g. radiology, surgery, dentistry).
 * <p>
 * Mapped to the {@code specialties} table. Associated with {@link Vet} entities
 * through a many-to-many join table.
 *
 * @author Juergen Hoeller
 * @author Ramazan Sakin
 */
@Entity
@Table(name = "specialties")
public class Specialty {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** The display name of this specialty. */
    @Column(name = "name")
    private String name;

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
