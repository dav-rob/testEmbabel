/**
 * Configuration records for agent behavior.
 *
 * <p>These records demonstrate the pattern for creating custom configuration bundles
 * that bind model selection, temperature, and other parameters from application.properties.
 *
 * <h2>Example Usage in an Agent:</h2>
 * <pre>
 * {@code
 * @Agent(description = "Example agent using configuration records")
 * public class MyAgent {
 *
 *     private final StoryGenerationConfig genConfig;
 *
 *     MyAgent(StoryGenerationConfig genConfig) {
 *         this.genConfig = genConfig;
 *     }
 *
 *     @Action
 *     Story generate(UserInput input, OperationContext context) {
 *         return context.ai()
 *             .withLlm(LlmOptions
 *                 .withModel(genConfig.model())
 *                 .withTemperature(genConfig.temperature())
 *             )
 *             .createObject(prompt, Story.class);
 *     }
 * }
 * }
 * </pre>
 *
 * <h2>Configuration in application.properties:</h2>
 * <pre>
 * story.generation.model=gpt-4o
 * story.generation.temperature=0.7
 * story.generation.word-count=150
 * </pre>
 *
 * <p>This pattern mirrors Embabel's built-in shell.chat configuration:
 * <pre>
 * embabel.agent.shell.chat.model=gpt-4o-mini
 * embabel.agent.shell.chat.temperature=0.3
 * </pre>
 */
package net.davidroberts.testEmbabel.config;
