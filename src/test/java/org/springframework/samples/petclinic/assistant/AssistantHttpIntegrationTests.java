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
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.ai.model.chat=none")
@AutoConfigureMockMvc
@Import(AssistantHttpIntegrationTests.DeterministicModelConfiguration.class)
class AssistantHttpIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void knownLookupIsGroundedThroughTheHttpSeam() throws Exception {
		this.mockMvc.perform(post("/assistant").param("question", "Find owner Franklin"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("George Franklin has one pet: Leo (cat).")))
			.andExpect(content().string(containsString("find_owners_by_last_name - matches found")));
	}

	@Test
	void ambiguousAndAbsentLookupsHaveExplicitOutcomes() throws Exception {
		this.mockMvc.perform(post("/assistant").param("question", "Find owner Davis"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Betty Davis")))
			.andExpect(content().string(containsString("Harold Davis")))
			.andExpect(content().string(containsString("Please clarify")));

		this.mockMvc.perform(post("/assistant").param("question", "Find owner Missing"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("No matching owner record was found in PetClinic.")))
			.andExpect(content().string(containsString("find_owners_by_last_name - no matches")));
	}

	@Test
	void mutationAndMedicalRequestsAreRefusedThroughTheHttpSeam() throws Exception {
		this.mockMvc.perform(post("/assistant").param("question", "Delete owner Franklin"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("read-only")))
			.andExpect(content().string(containsString("No change was made")))
			.andExpect(content().string(containsString("find_owners_by_last_name - unsupported")));

		this.mockMvc.perform(post("/assistant").param("question", "What medicine should I give this sick pet?"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("cannot provide veterinary diagnosis or treatment advice")))
			.andExpect(content().string(containsString("find_owners_by_last_name - unsupported")));
	}

	@Test
	void visitFollowUpIsGroundedThroughTheStatelessConversationHttpSeam() throws Exception {
		this.mockMvc
			.perform(post("/assistant/messages").contentType("application/json")
				.content(
						"""
								{"turns":[{"question":"Find owner Coleman","answer":"Jean Coleman has 2 pets: Max (cat), Samantha (cat).","activity":"find_owners_by_last_name - matches found"}],"question":"What visits has Samantha had?"}
								"""))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Samantha has 2 recorded visits")))
			.andExpect(content().string(containsString("2013-01-01 - rabies shot")))
			.andExpect(content().string(containsString("2013-01-04 - spayed")))
			.andExpect(content().string(containsString("find_owners_by_last_name - matches found")));
	}

	@Test
	void multiTurnMessageSeamStillRefusesSchedulingAndMedicalAdvice() throws Exception {
		String earlierTurn = """
				{"question":"Find owner Coleman","answer":"Jean Coleman has 2 pets: Max (cat), Samantha (cat).","activity":"find_owners_by_last_name - matches found"}
				""";
		this.mockMvc
			.perform(post("/assistant/messages").contentType("application/json")
				.content("{\"turns\":[" + earlierTurn + "],\"question\":\"Reschedule Samantha's appointment\"}"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("read-only")))
			.andExpect(content().string(containsString("No change was made")));

		this.mockMvc
			.perform(post("/assistant/messages").contentType("application/json")
				.content("{\"turns\":[" + earlierTurn + "],\"question\":\"What medicine should Samantha take?\"}"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("cannot provide veterinary diagnosis or treatment advice")));
	}

	@TestConfiguration(proxyBeanMethods = false)
	static class DeterministicModelConfiguration {

		@Bean
		ChatModel deterministicToolCallingModel() {
			return new DeterministicToolCallingModel();
		}

	}

	private static final class DeterministicToolCallingModel implements ChatModel {

		@Override
		public ChatResponse call(Prompt prompt) {
			if (prompt.getInstructions().stream().anyMatch(ToolResponseMessage.class::isInstance)) {
				return new ChatResponse(List.of(new Generation(new AssistantMessage("Tool result received."))));
			}

			String question = prompt.getUserMessage().getText();
			String prefix = question.contains("Franklin") ? "Frank" : question.contains("Davis") ? "Dav"
					: question.contains("Coleman") ? "Coleman" : question.contains("Missing") ? "Missing" : null;
			if (prefix == null) {
				return new ChatResponse(List.of(new Generation(new AssistantMessage("Unsupported."))));
			}

			ToolCall call = new ToolCall("lookup-1", "function", SpringAiClinicAssistant.TOOL_NAME,
					"{\"lastNamePrefix\":\"" + prefix + "\"}");
			ChatGenerationMetadata metadata = ChatGenerationMetadata.builder().finishReason("tool_calls").build();
			return new ChatResponse(List
				.of(new Generation(AssistantMessage.builder().content("").toolCalls(List.of(call)).build(), metadata)));
		}

		@Override
		public ChatOptions getOptions() {
			return ToolCallingChatOptions.builder().build();
		}

	}

}
