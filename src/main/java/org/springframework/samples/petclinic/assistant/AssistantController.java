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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Controller
class AssistantController {

	private final ClinicAssistant clinicAssistant;

	AssistantController(ClinicAssistant clinicAssistant) {
		this.clinicAssistant = clinicAssistant;
	}

	@GetMapping("/assistant")
	String showAssistant() {
		return "assistant";
	}

	@PostMapping("/assistant")
	String askAssistant(@RequestParam String question, Model model) {
		model.addAttribute("question", question);
		if (question.isBlank()) {
			model.addAttribute("error", "Enter a question about an owner, pet, or recorded visit.");
			return "assistant";
		}
		if (question.length() > 200) {
			model.addAttribute("error", "Questions cannot exceed 200 characters.");
			return "assistant";
		}

		AssistantResponse response = this.clinicAssistant.ask(question);
		model.addAttribute("answer", response.answer());
		model.addAttribute("activity", response.activity());
		return "assistant";
	}

	@PostMapping(path = "/assistant/messages", consumes = "application/json", produces = "application/json")
	@ResponseBody
	AssistantResponse askAssistant(@RequestBody AssistantRequest request) {
		validate(request);
		return this.clinicAssistant.ask(request.turns(), request.question());
	}

	private static void validate(AssistantRequest request) {
		if (request.question() == null || request.question().isBlank()) {
			throw new ResponseStatusException(BAD_REQUEST, "Enter a question about an owner, pet, or recorded visit.");
		}
		if (request.question().length() > 200) {
			throw new ResponseStatusException(BAD_REQUEST, "Questions cannot exceed 200 characters.");
		}
		if (request.turns().size() >= ClinicAssistant.MAX_TURNS) {
			throw new ResponseStatusException(BAD_REQUEST, "This conversation has reached its "
					+ ClinicAssistant.MAX_TURNS + "-turn limit. Reset to continue.");
		}
		for (ConversationTurn turn : request.turns()) {
			if (turn == null || turn.question() == null || turn.answer() == null || turn.activity() == null
					|| turn.question().length() > 200 || turn.answer().length() > 2000
					|| turn.activity().length() > 200) {
				throw new ResponseStatusException(BAD_REQUEST, "The conversation history is invalid.");
			}
		}
	}

}
