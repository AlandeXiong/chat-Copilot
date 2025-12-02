import { memo } from "react";
import { AssistantMessage } from "../types";
import { FunnelChart } from "./FunnelChart";
import { DeploymentProgress } from "./DeploymentProgress";

interface Props {
  message: AssistantMessage;
}

export const MessageBubble = memo(function MessageBubble({ message }: Props) {
  const { role, content, segmentData, emailData, journeyData, analyticsData, deploymentProgress, thinkingSteps, timestamp } = message;

  // Special rendering for deployment state
  if (role === "deployment" && deploymentProgress) {
    return (
      <div className="message-bubble message-bubble-deployment">
        <DeploymentProgress progress={deploymentProgress} />
      </div>
    );
  }

  // Special rendering for thinking state
  if (role === "thinking") {
    return (
      <div className="message-bubble message-bubble-thinking">
        <div className="thinking-header">
          <div className="thinking-icon">
            <div className="thinking-spinner"></div>
          </div>
          <span className="thinking-title">AI is thinking...</span>
        </div>
        <div className="thinking-steps">
          {thinkingSteps?.map((step, idx) => {
            // Check if this is a sub-step (starts with spaces or →)
            const isSubStep = step.trim().startsWith("→") || step.startsWith("   ");
            const displayText = step.trim();
            
            return (
              <div 
                key={idx} 
                className="thinking-step"
                style={isSubStep ? { paddingLeft: '24px', fontSize: '12px' } : {}}
              >
                <span className="thinking-step-icon">
                  {isSubStep ? "·" : "✓"}
                </span>
                <span className="thinking-step-text">{displayText}</span>
              </div>
            );
          })}
        </div>
      </div>
    );
  }

  return (
    <div className={`message-bubble message-bubble-${role}`}>
      <div className="message-meta">
        <span className="message-role">
          {role === "user" ? "You" : role === "assistant" ? "LCMS Copilot" : "System"}
        </span>
        <span className="message-time">
          {new Date(timestamp).toLocaleTimeString()}
        </span>
      </div>

      {content && <div className="message-content">{content}</div>}

      {segmentData && (
        <div className="message-rich-segment">
          <div className="rich-section-title">
            <span className="rich-icon">👥</span>
            Segment Strategy
          </div>
          <pre className="segment-suggestion">{segmentData.suggestion}</pre>
          {segmentData.result && segmentData.result.length > 0 && (
            <div className="segment-crowd">
              <div className="segment-crowd-header">
                <span className="rich-icon">📊</span>
                Matched Users ({segmentData.result.length})
              </div>
              <div className="segment-table">
                <div className="segment-table-header">
                  <span>Name</span>
                  <span>Email</span>
                  <span>Score</span>
                </div>
                {segmentData.result.map((user) => (
                  <div className="segment-table-row" key={user.id}>
                    <span>{user.name}</span>
                    <span>{user.email}</span>
                    <span className="segment-score">{user.score}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {emailData && (
        <div className="message-rich-email">
          <div className="rich-section-title">
            <span className="rich-icon">✉️</span>
            Email Template
          </div>
          <div className="email-preview-wrapper">
            <div
              className="email-preview"
              dangerouslySetInnerHTML={{ __html: emailData.html }}
            />
          </div>
        </div>
      )}

      {journeyData && (
        <div className="message-rich-journey">
          <div className="rich-section-title">
            <span className="rich-icon">🗺️</span>
            Customer Journey
          </div>
          <pre className="journey-plan">{journeyData.plan}</pre>
          {journeyData.scheduleHint && (
            <div className="schedule-hint">
              <span className="badge">⏰ Scheduling</span>
              <span>{journeyData.scheduleHint}</span>
            </div>
          )}
        </div>
      )}

      {analyticsData && (
        <div className="message-rich-analytics">
          <div className="rich-section-title">
            <span className="rich-icon">📊</span>
            Conversion Analytics & AI Diagnostics
          </div>
          
          <div className="analytics-summary">
            <div className="analytics-metric">
              <div className="metric-label">Total Impressions</div>
              <div className="metric-value">{analyticsData.totalImpressions.toLocaleString()}</div>
            </div>
            <div className="analytics-metric">
              <div className="metric-label">Leads Generated</div>
              <div className="metric-value">{analyticsData.totalLeads.toLocaleString()}</div>
            </div>
            <div className="analytics-metric">
              <div className="metric-label">Conversion Rate</div>
              <div className="metric-value metric-highlight">{analyticsData.overallConversionRate.toFixed(2)}%</div>
            </div>
          </div>

          <FunnelChart funnelStages={analyticsData.funnelStages} />

          {analyticsData.bottleneck && (
            <div className="bottleneck-analysis">
              <div className="bottleneck-header">
                <span className="bottleneck-icon">🔍</span>
                <div>
                  <div className="bottleneck-title">AI-Detected Bottleneck</div>
                  <div className="bottleneck-stage">{analyticsData.bottleneck.stage}</div>
                </div>
                <div className="bottleneck-rate">
                  {analyticsData.bottleneck.dropoffRate.toFixed(0)}% drop-off
                </div>
              </div>

              <div className="bottleneck-section">
                <div className="bottleneck-section-title">🔴 Root Causes Identified</div>
                <ul className="bottleneck-list">
                  {analyticsData.bottleneck.reasons.map((reason, idx) => (
                    <li key={idx}>{reason}</li>
                  ))}
                </ul>
              </div>

              <div className="bottleneck-section">
                <div className="bottleneck-section-title">💡 AI Recommendations</div>
                <ul className="bottleneck-list recommendations-list">
                  {analyticsData.bottleneck.recommendations.map((rec, idx) => (
                    <li key={idx}>{rec}</li>
                  ))}
                </ul>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
});

