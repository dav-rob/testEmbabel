# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Embabel agent project built on the Embabel framework for developing LLM-powered agents. It uses Spring Boot 3.5.6, Java 21, and Embabel Agent version 0.1.4. The project demonstrates how to build agents that orchestrate multiple LLM calls with different personas, temperatures, and objectives.

### Differences from Base Template

This project was initialized from [embabel/java-agent-template](https://github.com/embabel/java-agent-template) and has been enhanced with the following additions:

**Configuration & Documentation:**
- ✅ **CLAUDE.md** - This comprehensive guide for Claude Code usage
- ✅ **.env.example** - Template for API key configuration (security best practice)
- ✅ **.gitignore** - Modified to include `.env` (prevent API key commits)
- ✅ **src/test/resources/application.properties** - Test configuration (base template only has .gitkeep)

**Custom Configuration Pattern:**
- ✅ **src/main/java/.../config/StoryGenerationConfig.java** - Configuration property record for bundling model, temperature, and parameters
- ✅ **src/main/java/.../config/StoryReviewConfig.java** - Configuration property record pattern example
- ✅ **src/main/java/.../config/package-info.java** - Documentation for configuration patterns
- ✅ **TestEmbabelApplication.java** - Added `@ConfigurationPropertiesScan` annotation

**Enhanced Configuration:**
- ✅ **application.properties** - Added example custom agent configuration properties:
  - `story.generation.*` (model, temperature, word-count)
  - `story.review.*` (model, temperature, word-count)

**GitHub Actions:**
- ✅ **.github/workflows/maven.yml** - Modified to comment out SonarCloud (optional, free for public repos)

**Project Artifacts:**
- `.sdkmanrc` - SDK manager configuration
- `TestEmbabel.iml` - IntelliJ IDEA module file
- `agent-api.log` - Generated log file (should be in .gitignore)

**Key Enhancement:** The configuration property record pattern demonstrates how to create reusable configuration bundles (like Embabel's `embabel.agent.shell.chat.*`) for your own agents, allowing externalized configuration of model, temperature, and other parameters without code changes.

## Build and Run Commands

**Maven Wrapper**: The project includes a Maven wrapper (`mvnw` / `mvnw.cmd`) configured for Maven 3.9.6. You can use `./mvnw` instead of `mvn` to ensure consistent Maven version across environments.

### Build
```bash
mvn clean install
# Or with API keys for integration tests
source .env && mvn clean install
```

### Run Tests
```bash
# Unit tests (no API key required)
mvn test -Dtest=WriteAndReviewAgentTest

# Integration tests (requires API keys)
source .env && mvn test

# Run a specific test class
mvn test -Dtest=WriteAndReviewAgentTest

# Run a specific test method
mvn test -Dtest=WriteAndReviewAgentTest#testWriteAndReviewAgent
```

### Run Application
```bash
# Start the interactive Spring Shell (requires API keys)
source .env && ./scripts/shell.sh
```

### Shell Commands
Once in the Spring Shell:
- `x "Tell me a story about..."` - Invoke the WriteAndReviewAgent dynamically
- `demo` - Run the WriteAndReviewAgent programmatically with a fixed prompt
- `animal` - Demonstrate simple AI injection to invent a fictional animal

## CI/CD

GitHub Actions automatically runs on every push and pull request via `.github/workflows/maven.yml`:
- Sets up Java 21 (Temurin distribution)
- Configures Testcontainers for integration tests
- Executes `mvn -U -B test verify` to build and run all tests
- Uses Maven dependency caching for faster builds
- **SonarCloud is optional**: Currently commented out in the workflow. Can be enabled for code quality analysis (free for public repos, requires setup at sonarcloud.io and adding `SONAR_TOKEN` to GitHub secrets).

The DCO (Developer Certificate of Origin) configuration in `.github/dco.yml` requires commit sign-offs from non-members.

## Architecture

### Core Concepts

**Agents**: The central abstraction in Embabel. Agents are Spring beans annotated with `@Agent` that orchestrate LLM operations to achieve goals. Each agent contains multiple `@Action` methods that represent steps in the workflow, with one method marked `@AchievesGoal` as the final output.

**Action Methods**: Methods within an agent annotated with `@Action`. Embabel automatically provides method parameters through dependency injection, including:
- `UserInput` - User's request
- `OperationContext` - Provides access to AI operations via `context.ai()`
- Results from previous action methods (chaining)

**OperationContext**: Injected into action methods, provides `ai()` to configure and invoke LLMs:
- `withAutoLlm()` - Use default model
- `withDefaultLlm()` - Use default model
- `withLlm(LlmOptions)` - Configure specific model and parameters
- `withPromptContributor(Persona)` - Add persona/role to prompt
- `generateText()` - Generate text response
- `createObject()` - Generate structured object matching Java record/class

**Personas**: Define LLM behavior through role, goal, and backstory. Can use `RoleGoalBackstory` or custom `Persona` classes. Applied via `withPromptContributor()`.

**Dependency Injection**: Embabel agents are Spring beans. You can inject `Ai` into any Spring component (not just agents) to perform LLM operations. See `InjectedDemo` for standalone AI usage.

### Project Structure

```
src/main/java/net/davidroberts/testEmbabel/
├── TestEmbabelApplication.java          # Spring Boot entry point
├── DemoShell.java                       # Spring Shell commands
├── agent/
│   └── WriteAndReviewAgent.java         # Example multi-step agent
└── injected/
    └── InjectedDemo.java                # Example of AI injection into component

src/test/java/net/davidroberts/testEmbabel/agent/
├── WriteAndReviewAgentTest.java         # Unit tests with FakeOperationContext
└── WriteAndReviewAgentIntegrationTest.java
```

### Key Files

**WriteAndReviewAgent.java**: Demonstrates multi-step agent workflow:
1. `craftStory()` - Action method using creative persona with high temperature (0.7)
2. `reviewStory()` - Action method (goal-achieving) using reviewer persona with default temperature
3. Returns `ReviewedStory` record combining both outputs

**DemoShell.java**: Shows two invocation patterns:
- Programmatic agent invocation via `AgentInvocation.create()`
- Direct AI component usage via injected `Ai` instance

**InjectedDemo.java**: Demonstrates injecting `Ai` directly into a non-agent Spring component for simple LLM operations.

### Testing Strategy

**Unit Tests**: Use `FakeOperationContext` from `embabel-agent-test`:
- Mock LLM responses with `context.expectResponse()`
- Inspect generated prompts via `context.getLlmInvocations()`
- Verify prompts contain expected keywords
- Test agent logic without actual LLM calls

**Integration Tests**: Use `@Profile("!test")` on agents to prevent instantiation during tests, or create test-specific profiles for end-to-end testing with real LLMs.

## Configuration

### API Key Setup

Embabel requires API keys via environment variables. **Never commit API keys to source control.**

1. Copy `.env.example` to `.env`
2. Add your API keys to `.env`
3. Source before running: `source .env && ./mvnw test` or `source .env && ./scripts/shell.sh`

Required variables:
- `OPENAI_API_KEY` - For OpenAI models (default provider)
- `ANTHROPIC_API_KEY` - For Claude models (if using Anthropic dependency)

### LLM Model Configuration

Embabel uses `application.properties` to configure which models to use in different contexts. All configuration is optional with sensible defaults.

**Model Selection Hierarchy:**

In your agent code, you select models using these methods (from most specific to most general):

1. **Explicit model**: `context.ai().withLlm(LlmOptions.withModel("gpt-4o").withTemperature(0.7))`
2. **Role-based**: `context.ai().withLlm(LlmOptions.byRole("best"))` → uses `embabel.models.llms.best` from config
3. **Auto/Default**: `context.ai().withAutoLlm()` or `.withDefaultLlm()` → uses `embabel.models.default-llm`

**Key Configuration Properties:**

```properties
# Default model for all operations (used by withDefaultLlm/withAutoLlm)
embabel.models.default-llm=gpt-4o-mini

# Role-based models (use with byRole("role-name"))
embabel.models.llms.best=gpt-4o          # High-quality, expensive
embabel.models.llms.cheapest=gpt-4o-mini # Fast, economical

# Agent platform ranking (selects which agent/goal to use)
# Set to null or omit to use default-llm
embabel.agent-platform.ranking.llm=gpt-4o-mini

# Shell chat interface model
embabel.agent.shell.chat.model=gpt-4o-mini
embabel.agent.shell.chat.temperature=0.3
```

**When Each Model Is Used:**

- **default-llm**: Fallback for all `withDefaultLlm()` and `withAutoLlm()` calls
- **llms.{role}**: Selected when code uses `LlmOptions.byRole("role-name")`
- **ranking.llm**: Used by Embabel platform to decide which agent handles a user request (when multiple agents match)
- **Explicit models**: When code specifies a particular model directly

**Pattern**: Start with `default-llm` for everything, then add role-based models (`best`, `cheapest`) for cost/quality optimization, and finally use explicit models for specific tasks requiring particular capabilities.

### Provider Setup

By default, uses OpenAI. To switch providers:

1. Add dependency to `pom.xml` (e.g., `embabel-agent-starter-anthropic`, `embabel-agent-starter-bedrock`)
2. Configure models in `application.properties` using provider-specific model names
3. Set provider API keys via environment variables

See `docs/llm-docs.md` for detailed provider-specific configuration (Bedrock, Ollama, etc.).

### Custom Configuration Records Pattern

You can create configuration property records that bundle model, temperature, and other settings together, similar to how Embabel's `embabel.agent.shell.chat.*` works.

**Example - Create a configuration record:**

```java
@ConfigurationProperties(prefix = "story.generation")
public record StoryGenerationConfig(
        String model,
        Double temperature,
        Integer wordCount
) {
    public StoryGenerationConfig {
        // Compact constructor with defaults
        if (model == null) model = "gpt-4o-mini";
        if (temperature == null) temperature = 0.7;
        if (wordCount == null) wordCount = 100;
    }
}
```

**Configure in application.properties:**

```properties
story.generation.model=gpt-4o
story.generation.temperature=0.8
story.generation.word-count=150
```

**Use in your agent:**

```java
@Agent
public class MyAgent {
    private final StoryGenerationConfig config;

    MyAgent(StoryGenerationConfig config) {
        this.config = config;
    }

    @Action
    Story generate(OperationContext context) {
        return context.ai()
            .withLlm(LlmOptions
                .withModel(config.model())
                .withTemperature(config.temperature()))
            .createObject(prompt, Story.class);
    }
}
```

**Enable in main application:** Add `@ConfigurationPropertiesScan` to your `@SpringBootApplication` class.

This pattern allows you to:
- Configure model + temperature + other params as a bundle
- Change configuration without recompiling
- Use different configs per environment (dev/prod)
- Keep related settings together

See `src/main/java/net/davidroberts/testEmbabel/config/` for working examples: `StoryGenerationConfig` and `StoryReviewConfig`.

## Development Patterns

### Creating a New Agent

1. Create class annotated with `@Agent(description = "...")`
2. Add `@Action` methods for each step in the workflow
3. Mark final method with `@AchievesGoal(description = "...", export = @Export(...))`
4. Use `OperationContext` parameter to access `ai()` for LLM operations
5. Chain actions by having methods return records that become inputs to subsequent actions
6. Configure LLM behavior via `withLlm()`, personas via `withPromptContributor()`

### Working with Personas

Create personas to define LLM behavior:
```java
RoleGoalBackstory.withRole("...")
    .andGoal("...")
    .andBackstory("...");
```

Apply with `context.ai().withPromptContributor(persona)` before generating text.

### Structuring Outputs

Use Java records for structured data. Embabel can:
- Parse LLM responses into records via `createObject(prompt, RecordClass.class)`
- Implement interfaces like `HasContent` and `Timestamped` for framework integration
- Chain records between action methods for multi-step workflows

## Dependencies

Custom Embabel repository at `repo.embabel.com/artifactory/` provides:
- `embabel-agent-starter` - Core framework
- `embabel-agent-starter-openai` - OpenAI integration (default)
- `embabel-agent-starter-anthropic` - Anthropic integration (commented in pom.xml)
- `embabel-agent-starter-shell` - Interactive shell
- `embabel-agent-test` - Testing utilities
