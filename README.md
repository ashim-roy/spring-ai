# Spring AI — RAG & Tool Calling

Learning and experimentation project for **Spring AI** with Java and Spring Boot.

This project is part of my learning journey through the **Coding Shuttle Spring AI, RAG and Tool Calling** module, where I am incrementally exploring how modern AI applications can be built using Spring Boot.

The project covers LLM integration, prompts, structured AI responses, embeddings, vector databases, similarity search, RAG, Advisors, and Tool Calling.

---

## 📚 Topics

### 9.1 — The Mechanics of Generative AI

- Generative AI fundamentals
- Large Language Models (LLMs)
- Tokens
- Local vs cloud AI models
- How an AI application communicates with an LLM

### 9.2 — Introduction to Spring AI and ChatClient

- Spring AI fundamentals
- Spring Boot auto-configuration
- Spring AI starters
- `ChatClient`
- `ChatModel` abstraction
- Prompting
- Ollama
- Qwen
- Google Gemini
- OpenAI
- Model configuration
- Structured output
- Mapping LLM responses to Java objects
- `JokeDto` using Java Records

### 9.3 — Embeddings and Vector Search

- Embeddings
- Vector representations
- Embedding models
- Vector databases
- PostgreSQL + pgvector
- Document ingestion
- Similarity search
- `SearchRequest`
- Top-K results
- Similarity threshold
- Distance and similarity scores

### 9.4 — RAG (Retrieval Augmented Generation)

- Retrieval
- Vector search
- Context augmentation
- RAG pipeline
- Grounding LLM responses with application data

### 9.5 — Mastering Advisors

- Spring AI Advisors
- Request/response interception
- Prompt processing
- Logging
- `SimpleLoggerAdvisor`
- Cross-cutting AI concerns

### 9.6 — Tool Calling in Spring AI

- Tools
- Function/tool calling
- LLM → Tool → Result → LLM
- Connecting LLMs with application logic
- Database/API interactions through tools


---

## 🏗️ Current Architecture

The application currently follows the basic Spring AI flow:

```mermaid
Spring Boot
     ↓
Spring AI
     ↓
ChatClient
     ↓
ChatModel
     ↓
AI Provider
     ↓
    LLM
```


For vector search:

```mermaid
User Query
    ↓
Embedding Model
    ↓
Vector
    ↓
pgvector
    ↓
Similarity Search
    ↓
Relevant Documents
```

# 🤖 AI Models

This project experiments with both local and cloud-based AI models.

- **Ollama + Qwen** → local chat/LLM
- **Gemini** → cloud chat/LLM
- **OpenAI** → embeddings
- **PostgreSQL + pgvector** → vector storage/search

Local AI : Ollama - Used to run AI models locally.

Qwen : Currently used through Ollama for local LLM experimentation.

### Cloud AI

Google Gemini : Used for cloud-based chat generation.

OpenAI : OpenAI is currently used for embeddings.

The application is designed around Spring AI abstractions, making it possible to switch between supported providers through configuration.

## Structured AI Output

Instead of always receiving plain text from an LLM, Spring AI can map structured responses into Java objects.

For example:
```java
public record JokeDto(
    String text,
    String category,
    Double laughScore,
    Boolean isNSFW
) {
}
```

The application can request a structured response and Spring AI handles the conversion into the Java object.

Conceptually:
```mermaid
JokeDto.class
    ↓
Spring AI
    ↓
Output instructions
    ↓
    LLM
    ↓
Structured JSON
    ↓
JokeDto
```
This makes AI responses easier to work with inside a Java application.

# 🔢 Embeddings

Embeddings convert text into a numerical vector representation.

For example:

"Interstellar is a space movie" -->  [0.12, -0.44, 0.98, ...]

The vector represents the semantic meaning of the text. The project currently uses an OpenAI embedding model:

text-embedding-3-small

Example:
```java
public float[] getEmbedding(String inputText) {
    return embeddingModel.embed(inputText);
}
```

## 🗄️ Vector Store — PostgreSQL + pgvector

The project uses PostgreSQL with pgvector as the vector database.

Movie descriptions are stored as Spring AI Document objects along with metadata such as:

- Title
- Genre
- Year
- Embedding vector

Example:

```java
new Document(
"A team of explorers travel through a wormhole in space...",
Map.of(
        "title", "Interstellar",
        "genre", "Sci-Fi",
        "year", 2014
        )
);
```

The documents are then added to the vector store:

vectorStore.add(movies);

# 🔎 Similarity Search

The application supports semantic similarity search.  For example, the query:

"a team of people travel through a blackhole"

does not need to exactly match the stored movie description. The query is converted into an embedding and compared with the stored document embeddings.

The closest documents are returned.

Example:
```java
vectorStore.similaritySearch(
SearchRequest.builder()
.query(inputText)
.topK(4)
.similarityThreshold(0.3)
.build()
);
```
Top-K : topK(4) means we want up to the 4 most relevant documents.

Similarity Threshold : similarityThreshold(0.3) filters out results below the configured similarity level.

Distance : The vector distance represents how far apart two vectors are.

Generally:  Smaller distance =  More similar meaning

This is one of the key concepts behind semantic search.

# 🔌 Advisors

Spring AI Advisors allow us to intercept and work with AI requests and responses.

The project currently uses: new SimpleLoggerAdvisor()

Example:
```java
chatClient.prompt()
    .user(renderedText)
    .advisors(
    new SimpleLoggerAdvisor()
    )
    .call()
    .entity(JokeDto.class);
```


This helps inspect what is being sent to and received from the LLM.

Advisors can also be used for other cross-cutting concerns such as:

- Logging
- Prompt modification
- Adding context
- Memory
- Request/response processing


# ⚙️ Configuration

The application uses Spring Boot configuration to select the AI provider and models.

Example:
```yml
spring:
ai:
ollama:
base-url: http://localhost:11434
chat:
options:
model: qwen2.5:latest

    google:
      genai:
        api-key: ${GEMINI_API_KEY}

    openai:
      api-key: ${OPENAI_API_KEY}
      embedding:
        options:
          model: text-embedding-3-small

    model:
      chat: ollama
```
API keys are provided through environment variables and are not stored in the repository.

# 🧪 Testing

The project contains Spring Boot integration tests for:

- Chat/LLM responses
- Embedding generation
- Vector store ingestion
- Similarity search

Example:
```java
@Test
public void testSimilaritySearch() {
var response = aiService.similaritySearch(
                    "a team of people travel through a blackhole"
                    );

    System.out.println(response);
}
```

# 🛠️ Technology Stack
- Java 21
- Spring Boot
- Spring AI
- Maven
- Ollama
- Qwen
- Google Gemini
- OpenAI
- PostgreSQL
- pgvector
- JUnit
- Mockito

- # 🚧 Project Status

The project is being implemented incrementally as I progress through the Coding Shuttle course.

✅ Currently Implemented
- Spring AI setup
- ChatClient
- ChatModel abstraction
- Ollama + Qwen
- Google Gemini
- OpenAI embeddings
- Prompt templates
- Structured output
- Java Record DTOs
- Spring AI Advisors
- SimpleLoggerAdvisor
- Embedding generation
- PostgreSQL + pgvector
- Document ingestion
- Vector similarity search
- Top-K search
- Similarity threshold

# 🔜 Coming Next
- RAG pipeline
- Retrieval + generation
- Context injection
- Advanced Advisors
- Tool Calling
- LLM-driven application tools
- Database/API tool integration
- Practical RAG and Tool Calling projects

# 🎯 Learning Goal

The goal of this project is not just to call an LLM API, but to understand how AI applications are designed and implemented using Java, Spring Boot and Spring AI.

The project gradually moves from:
```mermaid
LLM Integration 
    ↓
ChatClient
    ↓
Structured Output
    ↓
Embeddings
    ↓
Vector Search
    ↓
    RAG
    ↓
Advisors
    ↓
Tool Calling
```

and builds toward a practical understanding of how production-style AI applications can be developed with the Spring ecosystem.
