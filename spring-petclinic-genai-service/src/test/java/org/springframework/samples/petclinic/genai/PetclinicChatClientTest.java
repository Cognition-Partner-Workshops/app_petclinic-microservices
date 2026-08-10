package org.springframework.samples.petclinic.genai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class PetclinicChatClientTest {

    private final ChatClient.CallResponseSpec callResponse = mock(ChatClient.CallResponseSpec.class);

    private PetclinicChatClient client;

    @BeforeEach
    void setUp() {
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        given(requestSpec.user(anyString())).willReturn(requestSpec);
        given(requestSpec.call()).willReturn(callResponse);

        ChatClient chatClient = mock(ChatClient.class);
        given(chatClient.prompt()).willReturn(requestSpec);

        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        given(builder.defaultSystem(anyString())).willReturn(builder);
        given(builder.defaultAdvisors(any(), any())).willReturn(builder);
        given(builder.defaultTools(any(Object.class))).willReturn(builder);
        given(builder.build()).willReturn(chatClient);

        client = new PetclinicChatClient(builder, mock(ChatMemory.class), mock(PetclinicTools.class));
    }

    @Test
    void shouldReturnAnswerFromTheModel() {
        given(callResponse.content()).willReturn("There are 6 vets.");

        assertThat(client.exchange("How many vets?")).isEqualTo("There are 6 vets.");
    }

    @Test
    void shouldReturnFallbackMessageWhenTheModelFails() {
        given(callResponse.content()).willThrow(new IllegalStateException("model unavailable"));

        assertThat(client.exchange("How many vets?"))
            .isEqualTo("Chat is currently unavailable. Please try again later.");
    }
}
