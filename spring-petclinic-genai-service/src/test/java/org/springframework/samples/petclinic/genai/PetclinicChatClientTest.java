package org.springframework.samples.petclinic.genai;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetclinicChatClientTest {

    @Test
    void exchangeReturnsResponseOnSuccess() {
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.ChatClientRequestSpec userSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(userSpec);
        when(userSpec.call()).thenReturn(callSpec);
        when(callSpec.content()).thenReturn("Hello! How can I help?");

        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        ChatMemory chatMemory = mock(ChatMemory.class);
        PetclinicTools tools = mock(PetclinicTools.class);

        when(builder.defaultSystem(anyString())).thenReturn(builder);
        when(builder.defaultAdvisors(any(Advisor[].class))).thenReturn(builder);
        when(builder.defaultTools(any(Object.class))).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);

        PetclinicChatClient client = new PetclinicChatClient(builder, chatMemory, tools);
        String result = client.exchange("Hello");

        assertThat(result).isEqualTo("Hello! How can I help?");
    }

    @Test
    void exchangeReturnsErrorMessageOnException() {
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenThrow(new RuntimeException("LLM error"));

        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        ChatMemory chatMemory = mock(ChatMemory.class);
        PetclinicTools tools = mock(PetclinicTools.class);

        when(builder.defaultSystem(anyString())).thenReturn(builder);
        when(builder.defaultAdvisors(any(Advisor[].class))).thenReturn(builder);
        when(builder.defaultTools(any(Object.class))).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);

        PetclinicChatClient client = new PetclinicChatClient(builder, chatMemory, tools);
        String result = client.exchange("Hello");

        assertThat(result).isEqualTo("Chat is currently unavailable. Please try again later.");
    }
}
