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
package org.springframework.samples.petclinic.visits.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.Date;

/**
 * JPA entity representing a veterinary visit for a specific pet.
 * <p>
 * Mapped to the {@code visits} table. Each visit records the date, a textual
 * description, and the pet ID it belongs to. Includes a {@link VisitBuilder}
 * for convenient programmatic construction (primarily used in tests).
 *
 * @author Ken Krebs
 * @author Maciej Szarlinski
 * @author Ramazan Sakin
 */
@Entity
@Table(name = "visits")
public class Visit {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** The date of the visit; defaults to the current date/time. Serialized as {@code yyyy-MM-dd}. */
    @Column(name = "visit_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date = new Date();

    /** Free-text description of the visit (up to 8192 characters). */
    @Size(max = 8192)
    @Column(name = "description")
    private String description;

    /** Foreign key reference to the pet this visit belongs to. */
    @Column(name = "pet_id")
    private int petId;

    public Integer getId() {
        return this.id;
    }

    public Date getDate() {
        return this.date;
    }

    public String getDescription() {
        return this.description;
    }

    public int getPetId() {
        return this.petId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }


    /**
     * Builder for constructing {@link Visit} instances with a fluent API.
     * <p>
     * Primarily used in test code to create visit fixtures.
     */
    public static final class VisitBuilder {
        private Integer id;
        private Date date;
        private @Size(max = 8192) String description;
        private int petId;

        private VisitBuilder() {
        }

        /**
         * Creates a new builder instance.
         *
         * @return a fresh {@link VisitBuilder}
         */
        public static VisitBuilder aVisit() {
            return new VisitBuilder();
        }

        public VisitBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public VisitBuilder date(Date date) {
            this.date = date;
            return this;
        }

        public VisitBuilder description(String description) {
            this.description = description;
            return this;
        }

        public VisitBuilder petId(int petId) {
            this.petId = petId;
            return this;
        }

        /**
         * Builds and returns the configured {@link Visit} entity.
         *
         * @return the constructed visit
         */
        public Visit build() {
            Visit visit = new Visit();
            visit.setId(id);
            visit.setDate(date);
            visit.setDescription(description);
            visit.setPetId(petId);
            return visit;
        }
    }
}
