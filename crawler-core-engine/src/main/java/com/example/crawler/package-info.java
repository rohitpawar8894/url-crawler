/**
 * Core services follow SOLID and KISS:
 * - Single Responsibility: Fetcher (HTTP), RobotsService (robots), ExtractorService (HTML/metadata),
 *   ClassificationService (page type), TopicsService (keyphrases).
 * - Open/Closed: Classifiers/strategies swappable without modifying consumers.
 * - Liskov: Services expose simple, substitutable contracts.
 * - Interface Segregation: Small focused services.
 * - Dependency Inversion: Controller depends on higher-level services.
 *
 * Patterns:
 * - Strategy for classification and topic extraction.
 * - Builder-like response assembly in controller.
 */
package com.example.crawler;
