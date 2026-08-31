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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AssistantController.class)
@DisabledInNativeImage
@DisabledInAotMode
class AssistantControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ClinicAssistant clinicAssistant;

	@Test
	void showsAssistantPage() throws Exception {
		this.mockMvc.perform(get("/assistant"))
			.andExpect(status().isOk())
			.andExpect(view().name("assistant"))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Clinic Assistant")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("class=\"conversation-panel\"")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("class=\"evidence-rail\"")));
	}

	@Test
	void submitsOneQuestionAndDisplaysGroundedAnswerAndActivity() throws Exception {
		given(this.clinicAssistant.ask("Find owner Franklin")).willReturn(new AssistantResponse(
				"George Franklin has one pet: Leo (cat).", "find_owners_by_last_name - matches found"));

		this.mockMvc.perform(post("/assistant").param("question", "Find owner Franklin"))
			.andExpect(status().isOk())
			.andExpect(view().name("assistant"))
			.andExpect(model().attribute("answer", "George Franklin has one pet: Leo (cat)."))
			.andExpect(model().attribute("activity", "find_owners_by_last_name - matches found"))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("George Franklin has one pet")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("matches found")));

		verify(this.clinicAssistant).ask("Find owner Franklin");
	}

	@Test
	void rejectsBlankQuestionWithoutCallingAssistant() throws Exception {
		this.mockMvc.perform(post("/assistant").param("question", "   "))
			.andExpect(status().isOk())
			.andExpect(view().name("assistant"))
			.andExpect(model().attribute("error", "Enter a question about an owner, pet, or recorded visit."));
	}

	@Test
	void acceptsBoundedStatelessConversationMessages() throws Exception {
		List<ConversationTurn> turns = List.of(new ConversationTurn("Find owner Coleman",
				"Jean Coleman has 2 pets: Samantha (cat), Max (cat).", "find_owners_by_last_name - matches found"));
		given(this.clinicAssistant.ask(turns, "What visits has Samantha had?"))
			.willReturn(new AssistantResponse("Samantha has one recorded visit: 2013-01-01 - rabies shot.",
					"find_owners_by_last_name - matches found"));

		this.mockMvc
			.perform(post("/assistant/messages").contentType("application/json")
				.content(
						"""
								{"turns":[{"question":"Find owner Coleman","answer":"Jean Coleman has 2 pets: Samantha (cat), Max (cat).","activity":"find_owners_by_last_name - matches found"}],"question":"What visits has Samantha had?"}
								"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.answer").value("Samantha has one recorded visit: 2013-01-01 - rabies shot."))
			.andExpect(jsonPath("$.activity").value("find_owners_by_last_name - matches found"));

		verify(this.clinicAssistant).ask(turns, "What visits has Samantha had?");
	}

	@Test
	void rejectsASeventhTurn() throws Exception {
		this.mockMvc
			.perform(post("/assistant/messages").contentType("application/json")
				.content(
						"""
								{"turns":[
								  {"question":"Find owner Franklin","answer":"George Franklin has one pet: Leo (cat).","activity":"find_owners_by_last_name - matches found"},
								  {"question":"Find owner Franklin","answer":"George Franklin has one pet: Leo (cat).","activity":"find_owners_by_last_name - matches found"},
								  {"question":"Find owner Franklin","answer":"George Franklin has one pet: Leo (cat).","activity":"find_owners_by_last_name - matches found"},
								  {"question":"Find owner Franklin","answer":"George Franklin has one pet: Leo (cat).","activity":"find_owners_by_last_name - matches found"},
								  {"question":"Find owner Franklin","answer":"George Franklin has one pet: Leo (cat).","activity":"find_owners_by_last_name - matches found"},
								  {"question":"Find owner Franklin","answer":"George Franklin has one pet: Leo (cat).","activity":"find_owners_by_last_name - matches found"}
								],"question":"And again?"}
								"""))
			.andExpect(status().isBadRequest());
	}

	@Test
	void rejectsOversizedQuestionAndMalformedConversationHistory() throws Exception {
		this.mockMvc
			.perform(post("/assistant/messages").contentType("application/json")
				.content("{\"turns\":[],\"question\":\"" + "x".repeat(201) + "\"}"))
			.andExpect(status().isBadRequest());

		this.mockMvc.perform(post("/assistant/messages").contentType("application/json")
			.content(
					"{\"turns\":[{\"question\":null,\"answer\":\"answer\",\"activity\":\"activity\"}],\"question\":\"Next\"}"))
			.andExpect(status().isBadRequest());
	}

}
