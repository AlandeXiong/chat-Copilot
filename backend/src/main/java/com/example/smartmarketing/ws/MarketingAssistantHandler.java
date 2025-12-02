package com.example.smartmarketing.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Simple WebSocket handler that accepts a high-level intent
 * and returns mocked AI-like suggestions for segments, email templates and journeys.
 */
@Component
public class MarketingAssistantHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private enum ConversationStage {
        SEGMENT,
        EMAIL,
        JOURNEY
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
                // Step 1: Analyze intent
                sendThinkingStep(session, "Analyzing your intent...");
                Thread.sleep(1200);

                // Step 2: Call segment tool
                sendThinkingStep(session, "Calling segment builder tool...");
                Thread.sleep(1500);

                // Step 3: Query database
                sendThinkingStep(session, "Querying user database for matching criteria...");
                Thread.sleep(1800);

                // Step 4: Final result
                String assistantMessage = """
                        Step 1 · Segment design
                        I generated a suggested audience segment based on your intent. Review filters and tell me if you want to refine it.
                        """;
                sendPayload(session, new OutgoingMessage("assistant_message", assistantMessage, null));

                MockStateUpdate segmentUpdate = MockStateFactory.segmentFromIntent(intent);
                sendPayload(session, new OutgoingMessage("state_update", null, segmentUpdate.toStateNode(objectMapper)));

                session.getAttributes().put("stage", ConversationStage.EMAIL);
            }
            case EMAIL -> {
                // Step 1: Analyze tone
                sendThinkingStep(session, "Analyzing desired tone and style...");
                Thread.sleep(1300);

                // Step 2: Call email generator
                sendThinkingStep(session, "Generating HTML email template...");
                Thread.sleep(1600);

                // Step 3: Personalization
                sendThinkingStep(session, "Adding personalization and CTA optimization...");
                Thread.sleep(1400);

                // Step 4: Final result
                String assistantMessage = """
                        Step 2 · Email template
                        I drafted an HTML email using the same intent. Adjust tone, images and call-to-action if needed.
                        """;
                sendPayload(session, new OutgoingMessage("assistant_message", assistantMessage, null));

                MockStateUpdate emailUpdate = MockStateFactory.emailFromIntent(intent);
                sendPayload(session, new OutgoingMessage("state_update", null, emailUpdate.toStateNode(objectMapper)));

                session.getAttributes().put("stage", ConversationStage.JOURNEY);
            }
            case JOURNEY -> {
                // Step 1: Map flow
                sendThinkingStep(session, "Mapping customer journey flow...");
                Thread.sleep(1400);

                // Step 2: Branching logic
                sendThinkingStep(session, "Designing branch conditions and wait steps...");
                Thread.sleep(1500);

                // Step 3: Scheduling
                sendThinkingStep(session, "Optimizing send time based on historical data...");
                Thread.sleep(1300);

                // Step 4: Final result
                String assistantMessage = """
                        Step 3 · Customer journey
                        I assembled a simple journey including entry criteria, wait steps and branches. Tell me how to evolve this flow.
                        """;
                sendPayload(session, new OutgoingMessage("assistant_message", assistantMessage, null));

                MockStateUpdate journeyUpdate = MockStateFactory.journeyFromIntent(intent);
                sendPayload(session, new OutgoingMessage("state_update", null, journeyUpdate.toStateNode(objectMapper)));

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

    private record MockStateUpdate(
            String segmentSuggestion,
            List<MockSegmentUser> segmentResult,
            String emailHtml,
            String journeyPlan,
            String scheduleHint,
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

            if (emailHtml != null) {
                root.put("emailHtml", emailHtml);
            }
            if (journeyPlan != null) {
                root.put("journeyPlan", journeyPlan);
            }
            if (scheduleHint != null) {
                root.put("scheduleHint", scheduleHint);
            }
            if (conversationStep != null) {
                root.put("conversationStep", conversationStep);
            }
            return root;
        }
    }

    /**
     * Purely deterministic mock data factory. In real life this would call an LLM and internal services.
     */
    private static final class MockStateFactory {

        static MockStateUpdate segmentFromIntent(String intent) {
            String baseSegment = """
                Segment: Inactive VIP customers
                Filters:
                  - Loyalty tier equals \"Gold\" or \"Platinum\"
                  - Last activity greater than 60 days
                  - Email channel opted-in
                  - Engagement score between 30 and 65
                """;

            String dynamicLine = "Reasoning: Derived from intent \"" + intent + "\".";
            String segmentSuggestion = baseSegment + "\n" + dynamicLine;

            List<MockSegmentUser> users = List.of(
                    new MockSegmentUser("1", "Alice Chen", "alice.chen@example.com", 72),
                    new MockSegmentUser("2", "Leo Wang", "leo.wang@example.com", 68),
                    new MockSegmentUser("3", "Maria Gomez", "maria.gomez@example.com", 64)
            );

            return new MockStateUpdate(segmentSuggestion, users, null, null, null, "segment");
        }

        static MockStateUpdate emailFromIntent(String intent) {
            String emailHtml = """
                <div style="font-family: system-ui, -apple-system, 'Segoe UI', sans-serif;">
                  <h2 style="color:#123B8D;margin-bottom:4px;">We miss you at Aurora Club</h2>
                  <p style="font-size:14px;line-height:1.6;">
                    Hi {{firstName}},<br/>
                    we noticed you have not visited us in a while. Because you are one of our most valued members,
                    we prepared an exclusive offer just for you.
                  </p>
                  <p style="font-size:14px;line-height:1.6;">
                    Reactivate your benefits before <strong>""" + LocalDateTime.now().plusDays(10).toLocalDate() + """
                    </strong> and enjoy:
                  </p>
                  <ul style="font-size:14px;line-height:1.6;">
                    <li>Extra 20% points booster on your next purchase</li>
                    <li>Early access to the upcoming product launch</li>
                    <li>Priority support from our concierge team</li>
                  </ul>
                  <p style="margin-top:16px;">
                    <a href="https://example.com/reactivate" class="button">
                      Reactivate my benefits
                    </a>
                  </p>
                  <p style="font-size:12px;color:#666;margin-top:18px;">
                    If you do not want to receive similar campaigns, you can <a href="#" style="color:#123B8D;">manage your preferences</a>.
                  </p>
                </div>
                """;

            // In this POC we do not change the segment at this step, we only enrich the email.
            return new MockStateUpdate(null, null, emailHtml, null, null, "email");
        }

        static MockStateUpdate journeyFromIntent(String intent) {
            String journeyPlan = """
                1. Entry: Segment \"Inactive VIP customers\" (evaluated daily).
                2. Step: Send re-engagement email within 2 hours after entry.
                3. Wait: 3 days.
                4. Branch:
                   - If email opened but no purchase: send reminder with smaller incentive.
                   - If email not opened: resend with different subject line and send time.
                5. Exit: Mark member as \"re-engaged\" if purchase happens within 14 days.
                """;

            String scheduleHint = "Best time window: Tue–Thu, 9:00–11:30 in recipient local time (historical uplift +18%).";

            return new MockStateUpdate(null, null, null, journeyPlan, scheduleHint, "journey");
        }
    }
}


