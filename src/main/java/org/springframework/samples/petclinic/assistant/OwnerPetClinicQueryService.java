/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.assistant;

import java.util.List;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class OwnerPetClinicQueryService implements PetClinicQuery {

	private final OwnerRepository owners;

	OwnerPetClinicQueryService(OwnerRepository owners) {
		this.owners = owners;
	}

	@Override
	@Transactional(readOnly = true)
	public OwnerSearchResult findOwnersByLastName(String lastNamePrefix) {
		String normalizedPrefix = lastNamePrefix == null ? "" : lastNamePrefix.trim();
		if (normalizedPrefix.isEmpty()) {
			return new OwnerSearchResult(List.of());
		}

		List<OwnerSummary> matchingOwners = this.owners
			.findByLastNameStartingWithIgnoreCaseOrderByLastNameAscFirstNameAsc(normalizedPrefix)
			.stream()
			.map(this::toSummary)
			.toList();
		return new OwnerSearchResult(matchingOwners);
	}

	private OwnerSummary toSummary(Owner owner) {
		List<PetSummary> pets = owner.getPets().stream().map(this::toSummary).toList();
		return new OwnerSummary(owner.getFirstName() + " " + owner.getLastName(), pets);
	}

	private PetSummary toSummary(Pet pet) {
		List<VisitSummary> visits = pet.getVisits().stream().map(this::toSummary).toList();
		return new PetSummary(pet.getName(), pet.getType().getName(), visits);
	}

	private VisitSummary toSummary(Visit visit) {
		return new VisitSummary(visit.getDate(), visit.getDescription());
	}

}
