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
                // Phase 1: Intent Analysis
                sendThinkingStep(session, "🧠 Analyzing marketing intent and extracting key parameters...");
                Thread.sleep(1100);

                // Phase 2: Tool Call 1 - Customer Segmentation Engine
                sendThinkingStep(session, "🔧 Tool Call 1/3: CustomerSegmentationEngine");
                Thread.sleep(800);
                sendThinkingStep(session, "   → Parsing targeting criteria from intent");
                Thread.sleep(700);
                sendThinkingStep(session, "   → Building filter conditions (loyalty tier, activity, engagement)");
                Thread.sleep(900);

                // Phase 3: Tool Call 2 - User Database Query
                sendThinkingStep(session, "🔧 Tool Call 2/3: UserDatabaseQuery");
                Thread.sleep(800);
                sendThinkingStep(session, "   → Executing query against 2.4M user records");
                Thread.sleep(1000);
                sendThinkingStep(session, "   → Found 3,847 matching users");
                Thread.sleep(600);

                // Phase 4: Tool Call 3 - Segment Scorer
                sendThinkingStep(session, "🔧 Tool Call 3/3: SegmentScoringEngine");
                Thread.sleep(800);
                sendThinkingStep(session, "   → Calculating engagement scores and ranking");
                Thread.sleep(900);
                sendThinkingStep(session, "   → Top 3 candidates selected for preview");
                Thread.sleep(700);

                // Final synthesis
                sendThinkingStep(session, "✅ Synthesizing segment strategy and results...");
                Thread.sleep(600);

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
                // Signal email stage start
                sendStageStartSignal(session, "email");
                // Phase 1: Content Analysis
                sendThinkingStep(session, "🧠 Analyzing desired tone, style and messaging strategy...");
                Thread.sleep(1000);

                // Phase 2: Tool Call 1 - Content Generator
                sendThinkingStep(session, "🔧 Tool Call 1/3: EmailContentGenerator");
                Thread.sleep(800);
                sendThinkingStep(session, "   → Generating subject line and preview text");
                Thread.sleep(900);
                sendThinkingStep(session, "   → Creating personalized body content with dynamic fields");
                Thread.sleep(1000);

                // Phase 3: Tool Call 2 - Design Engine
                sendThinkingStep(session, "🔧 Tool Call 2/3: EmailDesignEngine");
                Thread.sleep(800);
                sendThinkingStep(session, "   → Applying responsive HTML/CSS template");
                Thread.sleep(900);
                sendThinkingStep(session, "   → Optimizing for mobile and desktop rendering");
                Thread.sleep(800);

                // Phase 4: Tool Call 3 - CTA Optimizer
                sendThinkingStep(session, "🔧 Tool Call 3/3: CTAOptimizer");
                Thread.sleep(800);
                sendThinkingStep(session, "   → Positioning primary CTA button for max conversion");
                Thread.sleep(900);
                sendThinkingStep(session, "   → Adding secondary action links");
                Thread.sleep(700);

                // Final synthesis
                sendThinkingStep(session, "✅ Assembling final HTML template with inline styles...");
                Thread.sleep(600);

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
                // Signal journey stage start
                sendStageStartSignal(session, "journey");
                // Phase 1: Journey Planning
                sendThinkingStep(session, "🧠 Analyzing customer journey requirements and touchpoints...");
                Thread.sleep(1000);

                // Phase 2: Tool Call 1 - Journey Orchestrator
                sendThinkingStep(session, "🔧 Tool Call 1/3: JourneyOrchestrator");
                Thread.sleep(800);
                sendThinkingStep(session, "   → Mapping entry conditions and exit criteria");
                Thread.sleep(900);
                sendThinkingStep(session, "   → Designing multi-step flow with wait periods");
                Thread.sleep(1000);

                // Phase 3: Tool Call 2 - Branch Logic Engine
                sendThinkingStep(session, "🔧 Tool Call 2/3: BranchLogicEngine");
                Thread.sleep(800);
                sendThinkingStep(session, "   → Creating conditional branches (opened/not opened)");
                Thread.sleep(900);
                sendThinkingStep(session, "   → Defining fallback actions and retry logic");
                Thread.sleep(800);

                // Phase 4: Tool Call 3 - Send Time Optimizer
                sendThinkingStep(session, "🔧 Tool Call 3/3: SendTimeOptimizer");
                Thread.sleep(800);
                sendThinkingStep(session, "   → Analyzing historical engagement patterns");
                Thread.sleep(1000);
                sendThinkingStep(session, "   → Recommending optimal send windows by timezone");
                Thread.sleep(900);

                // Final synthesis
                sendThinkingStep(session, "✅ Building complete journey orchestration plan...");
                Thread.sleep(600);

                // Step 4: Final result
                String assistantMessage = """
                        Step 3 · Customer journey
                        I assembled a simple journey including entry criteria, wait steps and branches. Tell me how to evolve this flow.
                        """;
                sendPayload(session, new OutgoingMessage("assistant_message", assistantMessage, null));

                MockStateUpdate journeyUpdate = MockStateFactory.journeyFromIntent(intent);
                sendPayload(session, new OutgoingMessage("state_update", null, journeyUpdate.toStateNode(objectMapper)));

                session.getAttributes().put("stage", ConversationStage.DEPLOYMENT);
            }
            case DEPLOYMENT -> {
                // Signal deployment stage start
                sendStageStartSignal(session, "deployment");
                // Intelligent campaign deployment simulation
                sendThinkingStep(session, "🚀 Initializing intelligent deployment system...");
                Thread.sleep(800);

                sendThinkingStep(session, "🔧 Tool Call 1/2: CampaignOrchestrator");
                Thread.sleep(600);
                sendThinkingStep(session, "   → Validating campaign configuration and audience segment");
                Thread.sleep(700);
                sendThinkingStep(session, "   → Allocating distributed processing nodes (12 workers)");
                Thread.sleep(800);

                sendThinkingStep(session, "🔧 Tool Call 2/2: DeliveryOptimizer");
                Thread.sleep(600);
                sendThinkingStep(session, "   → Calculating optimal send rate (850 emails/sec)");
                Thread.sleep(700);
                sendThinkingStep(session, "   → Applying throttling rules and reputation management");
                Thread.sleep(800);

                sendThinkingStep(session, "✅ Starting campaign deployment...");
                Thread.sleep(500);

                // Send initial deployment message
                String deployMessage = """
                        Step 4 · Intelligent Campaign Deployment
                        Starting smart deployment system. Real-time progress tracking enabled.
                        """;
                sendPayload(session, new OutgoingMessage("assistant_message", deployMessage, null));

                // Simulate deployment progress with realistic updates (total ~45 seconds)
                int totalRecipients = 3847;
                int[] progressSteps = {3, 8, 15, 22, 30, 38, 46, 55, 63, 71, 78, 84, 90, 95, 98, 100};
                
                for (int progress : progressSteps) {
                    Thread.sleep(2500 + (int)(Math.random() * 1000)); // Random 2500-3500ms per step, ~45s total
                    
                    int successCount = (int)(totalRecipients * progress / 100.0 * 0.97); // 97% success rate
                    int failedCount = (int)(totalRecipients * progress / 100.0 * 0.03); // 3% failure rate
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
                        successCount, failedCount, phase, timeRemaining, 850);
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
                // Phase 1: Data Collection
                sendThinkingStep(session, "🧠 Analyzing campaign performance and conversion metrics...");
                Thread.sleep(1000);

                // Phase 2: Tool Call 1 - Campaign Data Aggregator
                sendThinkingStep(session, "🔧 Tool Call 1/3: CampaignDataAggregator");
                Thread.sleep(800);
                sendThinkingStep(session, "   → Collecting impression data from ad platforms");
                Thread.sleep(900);
                sendThinkingStep(session, "   → Aggregating click-through and engagement metrics");
                Thread.sleep(1000);

                // Phase 3: Tool Call 2 - Funnel Analyzer
                sendThinkingStep(session, "🔧 Tool Call 2/3: ConversionFunnelAnalyzer");
                Thread.sleep(800);
                sendThinkingStep(session, "   → Building funnel stages: Impression → Click → Lead → Conversion");
                Thread.sleep(1000);
                sendThinkingStep(session, "   → Calculating drop-off rates at each stage");
                Thread.sleep(900);
                sendThinkingStep(session, "   → Identified bottleneck at Lead → Conversion (68% drop-off)");
                Thread.sleep(1100);

                // Phase 4: Tool Call 3 - AI Diagnostic Engine
                sendThinkingStep(session, "🔧 Tool Call 3/3: AIDiagnosticEngine");
                Thread.sleep(800);
                sendThinkingStep(session, "   → Running root cause analysis on conversion bottleneck");
                Thread.sleep(1000);
                sendThinkingStep(session, "   → Analyzing historical patterns and A/B test data");
                Thread.sleep(900);
                sendThinkingStep(session, "   → Generating actionable recommendations");
                Thread.sleep(800);

                // Final synthesis
                sendThinkingStep(session, "✅ Synthesizing diagnostic report with visualizations...");
                Thread.sleep(600);

                // Final result
                String assistantMessage = """
                        Step 4 · Performance Analytics & Diagnostics
                        I analyzed your campaign data and identified key conversion bottlenecks. Review the funnel breakdown and AI-powered recommendations below.
                        """;
                sendPayload(session, new OutgoingMessage("assistant_message", assistantMessage, null));

                MockStateUpdate analyticsUpdate = MockStateFactory.analyticsFromIntent(intent);
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
        if (progress < 5) return "🔄 Initializing deployment infrastructure";
        if (progress < 15) return "📨 Batch 1/6 - VIP and high-value customers";
        if (progress < 30) return "📨 Batch 2/6 - Engaged users (last 30 days)";
        if (progress < 50) return "📨 Batch 3/6 - Active segment (last 60 days)";
        if (progress < 70) return "📨 Batch 4/6 - Warm leads";
        if (progress < 85) return "📨 Batch 5/6 - Re-engagement targets";
        if (progress < 98) return "📨 Batch 6/6 - Final recipients";
        return "✅ Finalizing delivery logs and analytics";
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

            // Generate top 10 leads for preview
            List<MockSegmentUser> users = List.of(
                    new MockSegmentUser("1", "Alice Chen", "alice.chen@example.com", 89),
                    new MockSegmentUser("2", "Leo Wang", "leo.wang@example.com", 87),
                    new MockSegmentUser("3", "Maria Gomez", "maria.gomez@example.com", 84),
                    new MockSegmentUser("4", "David Kim", "david.kim@example.com", 82),
                    new MockSegmentUser("5", "Sarah Johnson", "sarah.j@example.com", 79),
                    new MockSegmentUser("6", "Michael Zhang", "m.zhang@example.com", 76),
                    new MockSegmentUser("7", "Emily Rodriguez", "emily.r@example.com", 74),
                    new MockSegmentUser("8", "James Williams", "j.williams@example.com", 72),
                    new MockSegmentUser("9", "Sophie Martinez", "sophie.m@example.com", 70),
                    new MockSegmentUser("10", "Thomas Anderson", "t.anderson@example.com", 68)
            );

            // Total matched: simulate a realistic segment size (3,847 total users)
            return new MockStateUpdate(segmentSuggestion, users, 3847, null, null, null, null, "segment");
        }

        static MockStateUpdate emailFromIntent(String intent) {
            String emailHtml = """
                <div style="font-family: system-ui, -apple-system, 'Segoe UI', sans-serif; max-width: 600px; margin: 0 auto;">
                  <!-- Hero Banner Image -->
                  <div style="margin-bottom: 20px; border-radius: 12px; overflow: hidden;">
                    <img 
                      src="https://images.unsplash.com/photo-1607083206869-4c7672e72a8a?w=1200&h=400&fit=crop&q=80" 
                      alt="VIP Exclusive Offer" 
                      style="width: 100%; height: auto; display: block; border-radius: 12px;"
                    />
                  </div>
                  
                  <h2 style="color:#123B8D; margin-bottom: 8px; font-size: 24px;">We miss you at Aurora Club ✨</h2>
                  
                  <p style="font-size:14px; line-height:1.6; color: #333;">
                    Hi <strong>{{firstName}}</strong>,<br/>
                    We noticed you haven't visited us in a while. Because you are one of our most valued members,
                    we've prepared an <strong style="color:#123B8D;">exclusive VIP offer</strong> just for you.
                  </p>
                  
                  <div style="background: linear-gradient(135deg, #f0f7ff, #e6f2ff); padding: 16px; border-radius: 10px; margin: 16px 0; border-left: 4px solid #123B8D;">
                    <p style="font-size:14px; line-height:1.6; margin: 0; color: #123B8D; font-weight: 600;">
                      ⏰ Limited Time: Reactivate before <strong>""" + LocalDateTime.now().plusDays(10).toLocalDate() + """
                      </strong>
                    </p>
                  </div>
                  
                  <h3 style="color:#123B8D; font-size: 18px; margin-top: 20px;">Your Exclusive Benefits:</h3>
                  <ul style="font-size:14px; line-height:1.8; color: #333;">
                    <li>🎁 <strong>Extra 20% points booster</strong> on your next purchase</li>
                    <li>🚀 <strong>Early access</strong> to the upcoming product launch</li>
                    <li>💎 <strong>Priority support</strong> from our concierge team</li>
                    <li>🎟️ <strong>Complimentary upgrade</strong> to Platinum tier for 3 months</li>
                  </ul>
                  
                  <!-- CTA Button -->
                  <div style="text-align: center; margin: 28px 0;">
                    <a href="https://example.com/reactivate" 
                       class="button"
                       style="display: inline-block; padding: 14px 32px; background: linear-gradient(135deg, #123B8D, #2e5bff); color: #fff; text-decoration: none; border-radius: 999px; font-weight: 600; font-size: 16px; box-shadow: 0 4px 12px rgba(18, 59, 141, 0.4);">
                      ✨ Reactivate My VIP Benefits
                    </a>
                  </div>
                  
                  <!-- Social Proof -->
                  <div style="background: #f9f9f9; padding: 14px; border-radius: 8px; margin-top: 20px; text-align: center;">
                    <p style="font-size: 13px; color: #666; margin: 0;">
                      <strong style="color: #123B8D;">2,847</strong> VIP members have already reactivated this month
                    </p>
                  </div>
                  
                  <p style="font-size:12px; color:#999; margin-top: 24px; text-align: center; line-height: 1.5;">
                    Questions? Reply to this email or visit our <a href="#" style="color:#123B8D; text-decoration: none;">Help Center</a>.<br/>
                    Don't want to receive these offers? <a href="#" style="color:#123B8D; text-decoration: none;">Manage your preferences</a>
                  </p>
                </div>
                """;

            // In this POC we do not change the segment at this step, we only enrich the email.
            return new MockStateUpdate(null, null, null, emailHtml, null, null, null, "email");
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

            return new MockStateUpdate(null, null, null, null, journeyPlan, scheduleHint, null, "journey");
        }
        
        static MockStateUpdate analyticsFromIntent(String intent) {
            // Simulate realistic conversion funnel data
            List<MockFunnelStage> funnelStages = List.of(
                new MockFunnelStage("Impressions", 124500, 100.0, null),
                new MockFunnelStage("Clicks", 8970, 7.2, -2.3),
                new MockFunnelStage("Landing Page Views", 7845, 87.5, -5.1),
                new MockFunnelStage("Form Starts", 3920, 50.0, -8.7),
                new MockFunnelStage("Leads Generated", 2548, 65.0, -12.4),
                new MockFunnelStage("Qualified Leads", 1274, 50.0, -15.2),
                new MockFunnelStage("Conversions", 408, 32.0, -18.6)
            );
            
            MockBottleneck bottleneck = new MockBottleneck(
                "Leads → Qualified Leads",
                50.0,
                List.of(
                    "Lead scoring model showing 45% false positives in current criteria",
                    "Average lead response time of 18 hours (industry benchmark: 5 hours)",
                    "Missing qualification questions in lead capture form",
                    "No automated lead nurturing sequence for warm leads"
                ),
                List.of(
                    "🎯 Refine lead scoring: Add engagement recency weight (last 7 days activity +20 pts)",
                    "⚡ Implement instant lead routing to sales team based on lead score >70",
                    "📝 Add 2-3 qualification questions: Budget range, Decision timeline, Current solution",
                    "🤖 Set up 3-touch automated nurture sequence for leads scoring 50-70",
                    "📊 A/B test shorter form (5 fields vs current 8 fields) to improve completion rate"
                )
            );
            
            MockAnalyticsData analyticsData = new MockAnalyticsData(
                funnelStages,
                124500,
                2548,
                2.05,
                bottleneck
            );
            
            return new MockStateUpdate(null, null, null, null, null, null, analyticsData, "analytics");
        }
    }
}


