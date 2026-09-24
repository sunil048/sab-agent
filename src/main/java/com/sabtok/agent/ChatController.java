package com.sabtok.agent;

import io.modelcontextprotocol.client.McpSyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final ChatClient chatClient;
    private final List<McpSyncClient> mcpSyncClients;

    public ChatController(ChatClient.Builder builder, ToolCallbackProvider tools,
                          List<McpSyncClient> mcpSyncClients) {
        this.mcpSyncClients = mcpSyncClients;
        Arrays.stream(tools.getToolCallbacks()).forEach(t -> {
            log.info("Tool Callback found: {}", t.getToolDefinition());
        });

        this.chatClient = builder
                .defaultToolCallbacks(tools)
                .build();
    }

    @GetMapping("/chat")
    public String chat() {
        return chatClient.prompt()
                .user("Get all items currently in the shopping list")
                .call()
                .content();
    }

    @GetMapping("/manual-add")
    public String manualAdd(@RequestParam String name, @RequestParam int quantity) {
        // Manually invoking the remote tool matching the name declared on your server side

        McpSyncClient shoppingServer = mcpSyncClients.get(0);
        var response = shoppingServer.callTool(
                new io.modelcontextprotocol.spec.McpSchema.CallToolRequest(
                        "addItem",
                        java.util.Map.of("name", name, "quantity", quantity)
                )
        );

        return response.content().toString();
    }
}
