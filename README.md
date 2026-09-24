# DVaaS MCP Client

A Spring Boot application demonstrating how to build an MCP (Model Context Protocol) client using Spring AI. This client connects to the [DVaaS MCP Server](https://www.danvega.dev/mcp) to access Dan Vega's content (YouTube videos, blog posts, speaking engagements, newsletters, and podcast episodes) through AI-powered chat interactions.

## Overview

This project showcases how to integrate MCP servers into Spring Boot applications using the [Spring AI MCP Client Boot Starter](https://docs.spring.io/spring-ai/reference/1.1/api/mcp/mcp-client-boot-starter-docs.html). The MCP protocol enables AI assistants to access external tools and data sources, and this example demonstrates connecting to a real-world MCP server to query professional content.

## Features

- **Spring AI MCP Client Integration**: Leverages Spring AI's MCP client starter for seamless server connectivity
- **HTTP/Streamable Transport**: Connects to the DVaaS MCP server using streamable HTTP transport
- **Automatic Tool Discovery**: Automatically discovers and registers 21+ tools from the DVaaS server
- **OpenAI Chat Integration**: Uses OpenAI's chat models with function calling to interact with MCP tools
- **REST API**: Exposes a simple `/chat` endpoint for testing AI-powered content queries

## Prerequisites

- Java 25 or higher
- Maven 3.6+
- OpenAI API key

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd dvaas-client
```

### 2. Configure OpenAI API Key

Set your OpenAI API key as an environment variable:

```bash
export OPENAI_API_KEY=your-api-key-here
```

Or add it to your `application.yaml`:

```yaml
spring:
  ai:
    openai:
      api-key: your-api-key-here
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

### 4. Test the Chat Endpoint

```bash
curl http://localhost:8080/chat
```

This will query Dan Vega's latest YouTube videos using the MCP server's tools.

## Configuration

The application is configured in `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: dvaas-client
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
    mcp:
      client:
        name: dvaas-mcp-client
        version: 0.0.1
        toolcallback:
          enabled: true
        streamable-http:
          connections:
            dvaas:
              url: https://mcp.danvega.dev/mcp
```

### Key Configuration Properties

- `spring.ai.mcp.client.name`: Client identifier
- `spring.ai.mcp.client.toolcallback.enabled`: Enables automatic tool callback registration
- `spring.ai.mcp.client.streamable-http.connections`: Defines MCP server connections
  - `dvaas.url`: The DVaaS MCP server endpoint

## How It Works

### 1. MCP Client Initialization

The Spring AI MCP Client Boot Starter automatically:
- Connects to the DVaaS MCP server at startup
- Discovers available tools (21+ tools across YouTube, blog, speaking, newsletter, and podcast categories)
- Registers these tools as function callbacks for the AI model

### 2. Chat Controller

The `ChatController` (src/main/java/dev/danvega/dvaas/ChatController.java:13) demonstrates:

```java
@RestController
public class ChatController {
    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder, ToolCallbackProvider tools) {
        // Logs all discovered MCP tools
        Arrays.stream(tools.getToolCallbacks()).forEach(t -> {
            log.info("Tool Callback found: {}", t.getToolDefinition());
        });

        // Builds chat client with MCP tools
        this.chatClient = builder
                .defaultToolCallbacks(tools)
                .build();
    }

    @GetMapping("/chat")
    public String chat() {
        return chatClient.prompt()
                .user("What are Dan Vega's latest YouTube videos")
                .call()
                .content();
    }
}
```

### 3. AI Function Calling Flow

1. User sends a query to `/chat` endpoint
2. ChatClient sends the prompt to OpenAI
3. OpenAI determines which MCP tools to call based on the query
4. Spring AI executes the MCP tool calls via the DVaaS server
5. Results are sent back to OpenAI
6. OpenAI generates a natural language response
7. Response is returned to the user

## Available DVaaS Tools

The DVaaS MCP server provides 21 tools across 5 categories:

### YouTube Tools (4)
- Search videos
- Get channel statistics
- Find topic-based tutorials
- Retrieve video metadata

### Blog Tools (4)
- Discover posts
- Search by keywords
- Get post details
- Browse categories

### Speaking Tools (4)
- View upcoming events
- Access event archives
- Get event details
- Search by date range

### Newsletter Tools (4)
- Access newsletter issues
- Search archives
- Get subscriber statistics
- Filter by topic

### Podcast Tools (5)
- Browse episodes
- Search transcripts
- View guest information
- Access show notes
- Get episode details

## Dependencies

Key dependencies used in this project:

```xml
<!-- Spring Boot Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring AI MCP Client -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-client</artifactId>
</dependency>

<!-- Spring AI OpenAI -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

## Extending the Application

### Adding More MCP Servers

You can connect to multiple MCP servers by adding more connections:

```yaml
spring:
  ai:
    mcp:
      client:
        streamable-http:
          connections:
            dvaas:
              url: https://mcp.danvega.dev/mcp
            another-server:
              url: https://another-mcp-server.com/mcp
```

### Customizing Chat Prompts

Modify the `chat()` method in `ChatController` to accept user input:

```java
@GetMapping("/chat")
public String chat(@RequestParam String question) {
    return chatClient.prompt()
            .user(question)
            .call()
            .content();
}
```

### Adding STDIO MCP Servers

For local MCP servers using STDIO transport:

```yaml
spring:
  ai:
    mcp:
      client:
        stdio:
          connections:
            local-server:
              command: /path/to/mcp-server
              args:
                - --config
                - /path/to/config.json
```

## Resources

- [Spring AI MCP Client Documentation](https://docs.spring.io/spring-ai/reference/1.1/api/mcp/mcp-client-boot-starter-docs.html)
- [DVaaS MCP Server](https://www.danvega.dev/mcp)
- [Model Context Protocol Specification](https://modelcontextprotocol.io/)
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)

## Tech Stack

- **Spring Boot**: 3.5.7
- **Spring AI**: 1.1.0-M3
- **Java**: 25
- **OpenAI API**: GPT models with function calling

## License

[Add your license information here]

## Author

[Add your author information here]
