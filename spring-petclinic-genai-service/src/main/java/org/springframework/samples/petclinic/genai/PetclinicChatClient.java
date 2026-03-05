package org.springframework.samples.petclinic.genai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes the AI chat endpoint for the PetClinic front-end.
 * <p>
 * Receives natural-language queries from users and forwards them to the configured
 * LLM via Spring AI's {@link ChatClient}. The chat client is pre-configured with:
 * <ul>
 *   <li>A system prompt defining the assistant's persona and behavioral rules</li>
 *   <li>Chat memory (up to 10 previous messages) for conversational context</li>
 *   <li>Tool bindings to {@link PetclinicTools} for executing domain actions</li>
 *   <li>A logging advisor for request/response observability</li>
 * </ul>
 *
 * @author Oded Shopen
 */
@RestController
@RequestMapping("/")
public class PetclinicChatClient {

    private static final Logger LOG = LoggerFactory.getLogger(PetclinicChatClient.class);

	/** The configured Spring AI chat client used to interact with the LLM. */
	private final ChatClient chatClient;

	/**
	 * Constructs the chat controller, building a {@link ChatClient} with a system prompt,
	 * memory advisor, logging advisor, and tool bindings.
	 *
	 * @param builder         the auto-configured {@link ChatClient.Builder} from Spring AI
	 * @param chatMemory      the chat memory store for maintaining conversation context
	 * @param petclinicTools  the tool beans the LLM can invoke for domain operations
	 */
	public PetclinicChatClient(ChatClient.Builder builder, ChatMemory chatMemory,
                               PetclinicTools petclinicTools) {
        // @formatter:off
		this.chatClient = builder
				.defaultSystem("""
                          You are a friendly AI assistant designed to help with the management of a veterinarian pet clinic called Spring Petclinic.
                          Your job is to answer questions about and to perform actions on the user's behalf, mainly around
                          veterinarians, owners, owners' pets and owners' visits.
                          You are required to answer an a professional manner. If you don't know the answer, politely tell the user
                          you don't know the answer, then ask the user a followup question to try and clarify the question they are asking.
                          If you do know the answer, provide the answer but do not provide any additional followup questions.
                          When dealing with vets, if the user is unsure about the returned results, explain that there may be additional data that was not returned.
                          Only if the user is asking about the total number of all vets, answer that there are a lot and ask for some additional criteria.
                          For owners, pets or visits - provide the correct data.
                          """)
				.defaultAdvisors(
						// Chat memory helps us keep context when using the chatbot for up to 10 previous messages.
                        MessageChatMemoryAdvisor.builder(chatMemory)
                            .order(10)
                            .build(),
						new SimpleLoggerAdvisor()
						)
                .defaultTools(petclinicTools)
				.build();
  }

  /**
   * Processes a chat message by forwarding it to the LLM and returning its response.
   * <p>
   * All chatbot interactions flow through this single endpoint. If the LLM call
   * fails for any reason, a user-friendly error message is returned instead.
   *
   * @param query the natural-language query from the user
   * @return the LLM's response text, or an error message if processing fails
   */
  @PostMapping("/chatclient")
  public String exchange(@RequestBody String query) {
	  try {
		  return this.chatClient
              .prompt()
              .user(query)
              .call()
              .content();
	  } catch (Exception exception) {
          LOG.error("Error processing chat message", exception);
 	      return "Chat is currently unavailable. Please try again later.";
	  }
  }
}
