package org.springframework.samples.petclinic.carddemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.carddemo.model.DisclosureGroup;
import org.springframework.samples.petclinic.carddemo.model.DisclosureGroupId;

public interface DisclosureGroupRepository extends JpaRepository<DisclosureGroup, DisclosureGroupId> {
}
