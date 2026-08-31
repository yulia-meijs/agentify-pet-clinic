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

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(OwnerPetClinicQueryService.class)
class OwnerPetClinicQueryTests {

	@Autowired
	private PetClinicQuery petClinicQuery;

	@Test
	void findsOneOwnerAndOnlyApprovedPetFields() {
		OwnerSearchResult result = this.petClinicQuery.findOwnersByLastName(" frank ");

		assertThat(result.owners())
			.containsExactly(new OwnerSummary("George Franklin", java.util.List.of(new PetSummary("Leo", "cat"))));
	}

	@Test
	void findsOwnersByCaseInsensitivePrefix() {
		OwnerSearchResult result = this.petClinicQuery.findOwnersByLastName("  dAv ");

		assertThat(result.owners()).extracting(OwnerSummary::fullName)
			.containsExactlyInAnyOrder("Betty Davis", "Harold Davis");
	}

	@Test
	void returnsOnlyApprovedRecordedVisitFields() {
		OwnerSearchResult result = this.petClinicQuery.findOwnersByLastName("Coleman");

		assertThat(result.owners()).singleElement().satisfies(owner -> {
			assertThat(owner.fullName()).isEqualTo("Jean Coleman");
			assertThat(owner.pets()).containsExactlyInAnyOrder(
					new PetSummary("Samantha", "cat",
							java.util.List.of(new VisitSummary(LocalDate.of(2013, 1, 1), "rabies shot"),
									new VisitSummary(LocalDate.of(2013, 1, 4), "spayed"))),
					new PetSummary("Max", "cat",
							java.util.List.of(new VisitSummary(LocalDate.of(2013, 1, 2), "rabies shot"),
									new VisitSummary(LocalDate.of(2013, 1, 3), "neutered"))));
		});
	}

	@Test
	void returnsNoOwnersWhenNoneMatch() {
		OwnerSearchResult result = this.petClinicQuery.findOwnersByLastName("missing");

		assertThat(result.owners()).isEmpty();
	}

}
