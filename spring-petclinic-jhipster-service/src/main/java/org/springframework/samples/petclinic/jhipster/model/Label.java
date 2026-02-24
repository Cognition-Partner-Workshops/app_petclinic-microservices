package org.springframework.samples.petclinic.jhipster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Label.
 */
@Entity
@Table(name = "label")
public class Label implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 3)
    @Column(name = "label", nullable = false)
    private String label;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "labels")
    @JsonIgnoreProperties(value = { "bankAccount", "labels" }, allowSetters = true)
    private Set<Operation> operations = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Label id(Long id) {
        this.setId(id);
        return this;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Label label(String label) {
        this.setLabel(label);
        return this;
    }

    public Set<Operation> getOperations() {
        return this.operations;
    }

    public void setOperations(Set<Operation> operations) {
        if (this.operations != null) {
            this.operations.forEach(i -> i.removeLabel(this));
        }
        if (operations != null) {
            operations.forEach(i -> i.addLabel(this));
        }
        this.operations = operations;
    }

    public Label operations(Set<Operation> operations) {
        this.setOperations(operations);
        return this;
    }

    public Label addOperation(Operation operation) {
        this.operations.add(operation);
        operation.getLabels().add(this);
        return this;
    }

    public Label removeOperation(Operation operation) {
        this.operations.remove(operation);
        operation.getLabels().remove(this);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Label)) {
            return false;
        }
        return getId() != null && getId().equals(((Label) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Label{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            "}";
    }
}
