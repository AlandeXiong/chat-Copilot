package com.example.smartmarketing.ws;

import com.example.smartmarketing.util.DataLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket handler that orchestrates multi-turn conversation flow.
 * All mock data is loaded from JSON/HTML files in src/main/resources/data/
 * for easy configuration without recompiling.
 */
@Component
public class MarketingAssistantHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DataLoader dataLoader;

    private final MockStateFactory stateFactory;

    public MarketingAssistantHandler(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
        this.stateFactory = new MockStateFactory();
    }

    private enum ConversationStage {
        SEGMENT,
        EMAIL,
        JOURNEY,
        DEPLOYMENT,
        ANALYTICS
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        JsonNode root = objectMapper.readTree(message.getPayload());
        String type = root.path("type").asText();

        if ("ping".equals(type)) {
            session.sendMessage(new TextMessage("{\"type\":\"assistant_message\",\"message\":\"Backend is ready.\"}"));
            return;
        }

        if (!"intent".equals(type)) {
            return;
        }

        String intent = root.path("intent").asText();
        if (intent.isEmpty()) {
            return;
        }

        ConversationStage stage = (ConversationStage) session.getAttributes()
                .getOrDefault("stage", ConversationStage.SEGMENT);

        // Process in a separate thread to avoid blocking WebSocket
        new Thread(() -> {
            try {
                processWithThinkingSteps(session, intent, stage);
            } catch (Exception e) {
                // In production, log the error properly
                e.printStackTrace();
            }
        }).start();
    }

    private void processWithThinkingSteps(WebSocketSession session, String intent, ConversationStage stage) throws IOException, InterruptedException {
        switch (stage) {
            case SEGMENT -> {
                // Immediately signal that we're starting segment stage
                sendStageStartSignal(session, "segment");
                
                // Execute thinking steps from JSON configuration
                executeThinkingSteps(session, "segment");

                // Step 4: Final result
                String assistantMessage = """
                        Step 1 · Segment design
                        I generated a suggested audience segment based on your intent. Review filters and tell me if you want to refine it.
                        """;
                sendPayload(session, new OutgoingMessage("assistant_message", assistantMessage, null));

                MockStateUpdate segmentUpdate = stateFactory.segmentFromIntent(intent);
                sendPayload(session, new OutgoingMessage("state_update", null, segmentUpdate.toStateNode(objectMapper)));

                session.getAttributes().put("stage", ConversationStage.EMAIL);
            }
            case EMAIL -> {
                // Signal email stage start
                sendStageStartSignal(session, "email");
                
                // Execute thinking steps from JSON configuration
                executeThinkingSteps(session, "email");

                // Step 4: Final result
                String assistantMessage = """
                        Step 2 · Email template
                        I drafted an HTML email using the same intent. Adjust tone, images and call-to-action if needed.
                        """;
                sendPayload(session, new OutgoingMessage("assistant_message", assistantMessage, null));

                MockStateUpdate emailUpdate = stateFactory.emailFromIntent(intent);
                sendPayload(session, new OutgoingMessage("state_update", null, emailUpdate.toStateNode(objectMapper)));

                session.getAttributes().put("stage", ConversationStage.JOURNEY);
            }
            case JOURNEY -> {
                // Signal journey stage start
                sendStageStartSignal(session, "journey");
                
                // Execute thinking steps from JSON configuration
                executeThinkingSteps(session, "journey");

                // Step 4: Final result
                String assistantMessage = """
                        Step 3 · Customer journey
                        I assembled a simple journey including entry criteria, wait steps and branches. Tell me how to evolve this flow.
                        """;
                sendPayload(session, new OutgoingMessage("assistant_message", assistantMessage, null));

                MockStateUpdate journeyUpdate = stateFactory.journeyFromIntent(intent);
                sendPayload(session, new OutgoingMessage("state_update", null, journeyUpdate.toStateNode(objectMapper)));

                session.getAttributes().put("stage", ConversationStage.DEPLOYMENT);
            }
            case DEPLOYMENT -> {
                // Signal deployment stage start
                sendStageStartSignal(session, "deployment");
                
                // Execute thinking steps from JSON
                executeThinkingSteps(session, "deployment");

                // Send initial deployment message
                String deployMessage = """
                        Step 4 · Intelligent Campaign Deployment
                        Starting smart deployment system. Real-time progress tracking enabled.
                        """;
                sendPayload(session, new OutgoingMessage("assistant_message", deployMessage, null));

                // Load deployment configuration
                JsonNode deployConfig = dataLoader.loadDeploymentConfig();
                int totalRecipients = deployConfig.path("totalRecipients").asInt(3847);
                double successRate = deployConfig.path("successRate").asDouble(0.97);
                int throughput = deployConfig.path("throughputPerSecond").asInt(850);
                
                // Load progress steps from config
                List<Integer> progressStepsList = new ArrayList<>();
                JsonNode stepsArray = deployConfig.path("progressSteps");
                if (stepsArray.isArray()) {
                    stepsArray.forEach(node -> progressStepsList.add(node.asInt()));
                }
                int[] progressSteps = progressStepsList.stream().mapToInt(Integer::intValue).toArray();
                
                int minDelay = deployConfig.path("delayPerStepMs").path("min").asInt(2500);
                int maxDelay = deployConfig.path("delayPerStepMs").path("max").asInt(3500);
                
                for (int progress : progressSteps) {
                    Thread.sleep(minDelay + (int)(Math.random() * (maxDelay - minDelay)));
                    
                    int successCount = (int)(totalRecipients * progress / 100.0 * successRate);
                    int failedCount = (int)(totalRecipients * progress / 100.0 * (1 - successRate));
                    int currentTotal = successCount + failedCount;
                    
                    String status = progress < 100 ? "deploying" : "completed";
                    String phase = getDeploymentPhase(progress);
                    int remainingRecipients = totalRecipients - currentTotal;
                    int remainingSteps = 0;
                    for (int p : progressSteps) {
                        if (p > progress) remainingSteps++;
                    }
                    String timeRemaining = progress < 100 ? 
                        String.format("%d seconds", remainingSteps * 3) : "Completed";
                    
                    sendDeploymentProgress(session, status, progress, totalRecipients, 
                        successCount, failedCount, phase, timeRemaining, throughput);
                }

                // Final completion message
                String completionMessage = """
                        ✅ Campaign deployment completed successfully!
                        Ready to analyze performance data and conversion metrics.
                        """;
                sendPayload(session, new OutgoingMessage("assistant_message", completionMessage, null));

                // Send state update to mark deployment as complete
                MockStateUpdate deploymentComplete = new MockStateUpdate(null, null, null, null, null, null, null, "deployment");
                sendPayload(session, new OutgoingMessage("state_update", null, deploymentComplete.toStateNode(objectMapper)));

                session.getAttributes().put("stage", ConversationStage.ANALYTICS);
            }
            case ANALYTICS -> {
                // Signal analytics stage start
                sendStageStartSignal(session, "analytics");
                
                // Execute thinking steps from JSON configuration
                executeThinkingSteps(session, "analytics");

                // Final result
                String assistantMessage = """
                        Step 4 · Performance Analytics & Diagnostics
                        I analyzed your campaign data and identified key conversion bottlenecks. Review the funnel breakdown and AI-powered recommendations below.
                        """;
                sendPayload(session, new OutgoingMessage("assistant_message", assistantMessage, null));

                MockStateUpdate analyticsUpdate = stateFactory.analyticsFromIntent(intent);
                sendPayload(session, new OutgoingMessage("state_update", null, analyticsUpdate.toStateNode(objectMapper)));

                // Loop back to segment so that the flow can be repeated in this POC.
                session.getAttributes().put("stage", ConversationStage.SEGMENT);
            }
        }
    }

    private void sendThinkingStep(WebSocketSession session, String step) throws IOException {
        var thinkingMessage = objectMapper.createObjectNode();
        thinkingMessage.put("type", "thinking");
        thinkingMessage.put("step", step);
        session.sendMessage(new TextMessage(thinkingMessage.toString()));
    }

    private void sendStageStartSignal(WebSocketSession session, String stage) throws IOException {
        var stageMessage = objectMapper.createObjectNode();
        stageMessage.put("type", "stage_start");
        stageMessage.put("stage", stage);
        session.sendMessage(new TextMessage(stageMessage.toString()));
    }

    /**
     * Execute thinking steps loaded from JSON configuration file
     */
    private void executeThinkingSteps(WebSocketSession session, String stage) throws IOException, InterruptedException {
        JsonNode stepsArray = dataLoader.loadThinkingSteps(stage);
        if (stepsArray.isArray()) {
            for (JsonNode stepNode : stepsArray) {
                String stepText = stepNode.path("step").asText();
                int delayMs = stepNode.path("delayMs").asInt(800);
                sendThinkingStep(session, stepText);
                Thread.sleep(delayMs);
            }
        }
    }

    private void sendDeploymentProgress(WebSocketSession session, String status, int progressPercent,
                                       int totalRecipients, int successCount, int failedCount,
                                       String currentPhase, String timeRemaining, int throughput) throws IOException {
        var progressMessage = objectMapper.createObjectNode();
        progressMessage.put("type", "deployment_progress");
        
        var progress = objectMapper.createObjectNode();
        progress.put("status", status);
        progress.put("progressPercent", progressPercent);
        progress.put("totalRecipients", totalRecipients);
        progress.put("successCount", successCount);
        progress.put("failedCount", failedCount);
        progress.put("currentPhase", currentPhase);
        progress.put("estimatedTimeRemaining", timeRemaining);
        progress.put("throughputPerSecond", throughput);
        
        progressMessage.set("progress", progress);
        session.sendMessage(new TextMessage(progressMessage.toString()));
    }

    private String getDeploymentPhase(int progress) {
        JsonNode deployConfig = dataLoader.loadDeploymentConfig();
        JsonNode phases = deployConfig.path("phases");
        
        if (phases.isArray()) {
            for (JsonNode phaseNode : phases) {
                int threshold = phaseNode.path("progressThreshold").asInt();
                if (progress < threshold) {
                    return phaseNode.path("description").asText();
                }
            }
            // Return last phase if progress >= all thresholds
            if (phases.size() > 0) {
                return phases.get(phases.size() - 1).path("description").asText();
            }
        }
        
        return "Processing...";
    }

    private void sendPayload(WebSocketSession session, OutgoingMessage outgoing) throws IOException {
        JsonNode node = outgoing.toJsonNode(objectMapper);
        session.sendMessage(new TextMessage(node.toString()));
    }

    private record OutgoingMessage(String type, String message, JsonNode state) {
        JsonNode toJsonNode(ObjectMapper mapper) {
            var root = mapper.createObjectNode();
            root.put("type", type);
            if (message != null) {
                root.put("message", message);
            }
            if (state != null) {
                root.set("state", state);
            }
            return root;
        }
    }

    private record MockSegmentUser(String id, String name, String email, int score) {
    }

    private record MockFunnelStage(String stage, int count, double rate, Double change) {}
    
    private record MockBottleneck(String stage, double dropoffRate, List<String> reasons, List<String> recommendations) {}
    
    private record MockAnalyticsData(
            List<MockFunnelStage> funnelStages,
            int totalImpressions,
            int totalLeads,
            double overallConversionRate,
            MockBottleneck bottleneck
    ) {}

    private record MockStateUpdate(
            String segmentSuggestion,
            List<MockSegmentUser> segmentResult,
            Integer segmentTotalMatched,
            String emailHtml,
            String journeyPlan,
            String scheduleHint,
            MockAnalyticsData analyticsData,
            String conversationStep
    ) {
        JsonNode toStateNode(ObjectMapper mapper) {
            var root = mapper.createObjectNode();
            if (segmentSuggestion != null) {
                root.put("segmentSuggestion", segmentSuggestion);
            }

            if (segmentResult != null) {
                var array = mapper.createArrayNode();
                for (MockSegmentUser u : segmentResult) {
                    var node = mapper.createObjectNode();
                    node.put("id", u.id());
                    node.put("name", u.name());
                    node.put("email", u.email());
                    node.put("score", u.score());
                    array.add(node);
                }
                root.set("segmentResult", array);
            }

            if (segmentTotalMatched != null) {
                root.put("segmentTotalMatched", segmentTotalMatched);
            }

            if (emailHtml != null) {
                root.put("emailHtml", emailHtml);
            }
            if (journeyPlan != null) {
                root.put("journeyPlan", journeyPlan);
            }
            if (scheduleHint != null) {
                root.put("scheduleHint", scheduleHint);
            }
            
            if (analyticsData != null) {
                var analyticsNode = mapper.createObjectNode();
                
                // Funnel stages
                var stagesArray = mapper.createArrayNode();
                for (MockFunnelStage stage : analyticsData.funnelStages()) {
                    var stageNode = mapper.createObjectNode();
                    stageNode.put("stage", stage.stage());
                    stageNode.put("count", stage.count());
                    stageNode.put("rate", stage.rate());
                    if (stage.change() != null) {
                        stageNode.put("change", stage.change());
                    }
                    stagesArray.add(stageNode);
                }
                analyticsNode.set("funnelStages", stagesArray);
                
                analyticsNode.put("totalImpressions", analyticsData.totalImpressions());
                analyticsNode.put("totalLeads", analyticsData.totalLeads());
                analyticsNode.put("overallConversionRate", analyticsData.overallConversionRate());
                
                // Bottleneck
                if (analyticsData.bottleneck() != null) {
                    var bottleneckNode = mapper.createObjectNode();
                    bottleneckNode.put("stage", analyticsData.bottleneck().stage());
                    bottleneckNode.put("dropoffRate", analyticsData.bottleneck().dropoffRate());
                    
                    var reasonsArray = mapper.createArrayNode();
                    for (String reason : analyticsData.bottleneck().reasons()) {
                        reasonsArray.add(reason);
                    }
                    bottleneckNode.set("reasons", reasonsArray);
                    
                    var recsArray = mapper.createArrayNode();
                    for (String rec : analyticsData.bottleneck().recommendations()) {
                        recsArray.add(rec);
                    }
                    bottleneckNode.set("recommendations", recsArray);
                    
                    analyticsNode.set("bottleneck", bottleneckNode);
                }
                
                root.set("analyticsData", analyticsNode);
            }
            
            if (conversationStep != null) {
                root.put("conversationStep", conversationStep);
            }
            return root;
        }
    }

    /**
     * Mock data factory that loads data from JSON/HTML files.
     * This allows easy modification without recompiling Java code.
     */
    private final class MockStateFactory {

        MockStateUpdate segmentFromIntent(String intent) {
            JsonNode segmentData = dataLoader.loadSegmentData();
            
            String baseSuggestion = segmentData.path("suggestion").asText();
            String dynamicLine = "\nReasoning: Derived from intent \"" + intent + "\".";
            String segmentSuggestion = baseSuggestion + dynamicLine;

            // Load top leads from JSON
            List<MockSegmentUser> users = new ArrayList<>();
            JsonNode topLeads = segmentData.path("topLeads");
            if (topLeads.isArray()) {
                for (JsonNode leadNode : topLeads) {
                    users.add(new MockSegmentUser(
                        leadNode.path("id").asText(),
                        leadNode.path("name").asText(),
                        leadNode.path("email").asText(),
                        leadNode.path("score").asInt()
                    ));
                }
            }

            int totalMatched = segmentData.path("totalMatched").asInt(3847);
            return new MockStateUpdate(segmentSuggestion, users, totalMatched, null, null, null, null, "segment");
        }

        MockStateUpdate emailFromIntent(String intent) {
            // Load email template from HTML file
            String emailHtml = dataLoader.loadEmailTemplate();
            
            // Replace dynamic placeholders
            String deadline = LocalDateTime.now().plusDays(10).toLocalDate().toString();
            emailHtml = emailHtml.replace("{{deadline}}", deadline);

            // In this POC we do not change the segment at this step, we only enrich the email.
            return new MockStateUpdate(null, null, null, emailHtml, null, null, null, "email");
        }

        MockStateUpdate journeyFromIntent(String intent) {
            // Load journey data from JSON file
            JsonNode journeyData = dataLoader.loadJourneyData();
            String journeyPlan = journeyData.path("plan").asText();
            String scheduleHint = journeyData.path("scheduleHint").asText();

            return new MockStateUpdate(null, null, null, null, journeyPlan, scheduleHint, null, "journey");
        }
        
        MockStateUpdate analyticsFromIntent(String intent) {
            // Load analytics data from JSON file
            JsonNode analyticsJson = dataLoader.loadAnalyticsData();
            
            // Parse funnel stages
            List<MockFunnelStage> funnelStages = new ArrayList<>();
            JsonNode stagesArray = analyticsJson.path("funnelStages");
            if (stagesArray.isArray()) {
                for (JsonNode stageNode : stagesArray) {
                    Double change = stageNode.has("change") && !stageNode.path("change").isNull() 
                        ? stageNode.path("change").asDouble() 
                        : null;
                    funnelStages.add(new MockFunnelStage(
                        stageNode.path("stage").asText(),
                        stageNode.path("count").asInt(),
                        stageNode.path("rate").asDouble(),
                        change
                    ));
                }
            }
            
            // Parse bottleneck
            JsonNode bottleneckNode = analyticsJson.path("bottleneck");
            List<String> reasons = new ArrayList<>();
            JsonNode reasonsArray = bottleneckNode.path("reasons");
            if (reasonsArray.isArray()) {
                reasonsArray.forEach(node -> reasons.add(node.asText()));
            }
            
            List<String> recommendations = new ArrayList<>();
            JsonNode recsArray = bottleneckNode.path("recommendations");
            if (recsArray.isArray()) {
                recsArray.forEach(node -> recommendations.add(node.asText()));
            }
            
            MockBottleneck bottleneck = new MockBottleneck(
                bottleneckNode.path("stage").asText(),
                bottleneckNode.path("dropoffRate").asDouble(),
                reasons,
                recommendations
            );
            
            MockAnalyticsData analyticsData = new MockAnalyticsData(
                funnelStages,
                analyticsJson.path("totalImpressions").asInt(),
                analyticsJson.path("totalLeads").asInt(),
                analyticsJson.path("overallConversionRate").asDouble(),
                bottleneck
            );
            
            return new MockStateUpdate(null, null, null, null, null, null, analyticsData, "analytics");
        }
    }
}


