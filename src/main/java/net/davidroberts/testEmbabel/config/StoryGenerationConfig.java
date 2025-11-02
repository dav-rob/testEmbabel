/*
 * Copyright 2024-2025 Embabel Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.davidroberts.testEmbabel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for story generation behavior.
 * Binds properties from application.properties with prefix "story.generation"
 */
@ConfigurationProperties(prefix = "story.generation")
public record StoryGenerationConfig(
        String model,
        Double temperature,
        Integer wordCount
) {
    public StoryGenerationConfig {
        // Compact constructor - provides defaults if null
        if (model == null) {
            model = "gpt-4o-mini";
        }
        if (temperature == null) {
            temperature = 0.7;
        }
        if (wordCount == null) {
            wordCount = 100;
        }
    }
}
