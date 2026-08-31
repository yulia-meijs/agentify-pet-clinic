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

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class AssistantConfiguration {

	@Bean
	ClinicAssistant clinicAssistant(ObjectProvider<ChatModel> chatModels, PetClinicQuery petClinicQuery) {
		ChatModel chatModel = chatModels.getIfAvailable();
		if (chatModel == null) {
			return (turns, question) -> new AssistantResponse(
					"The Clinic Assistant is unavailable because its AI model is not configured.",
					SpringAiClinicAssistant.TOOL_NAME + " - unsupported");
		}
		return new SpringAiClinicAssistant(chatModel, petClinicQuery);
	}

}
