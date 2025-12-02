import { memo } from "react";
import { AssistantMessage } from "../types";

interface Props {
  message: AssistantMessage;
}

export const MessageBubble = memo(function MessageBubble({ message }: Props) {
  const { role, content, segmentData, emailData, journeyData, thinkingSteps, timestamp } = message;

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
          {thinkingSteps?.map((step, idx) => (
            <div key={idx} className="thinking-step">
              <span className="thinking-step-icon">✓</span>
              <span className="thinking-step-text">{step}</span>
            </div>
          ))}
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
    </div>
  );
});

