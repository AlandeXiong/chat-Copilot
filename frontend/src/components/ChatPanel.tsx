import { useEffect, useRef, useState } from "react";
import { AssistantMessage, MarketingState } from "../types";
import { useMarketingSocket } from "../hooks/useMarketingSocket";
import { MessageBubble } from "./MessageBubble";

interface Props {
  state: MarketingState;
  onStateChange(next: MarketingState): void;
}

// Helper function to determine if a step is completed
const isStepCompleted = (
  currentStep: string | undefined, 
  stepToCheck: string
): boolean => {
  const stepOrder = ["segment", "email", "journey", "deployment", "analytics"];
  const currentIndex = stepOrder.indexOf(currentStep || "segment");
  const checkIndex = stepOrder.indexOf(stepToCheck);
  return currentIndex > checkIndex;
};

export function ChatPanel({ state, onStateChange }: Props) {
  const [input, setInput] = useState("");
  const [messages, setMessages] = useState<AssistantMessage[]>([]);
  const viewportRef = useRef<HTMLDivElement | null>(null);
  const lastPayloadIdRef = useRef<string>("");

  const { status, sendMessage, lastPayload } = useMarketingSocket();

  const currentStep = state.conversationStep ?? "segment";

  useEffect(() => {
    if (!lastPayload) return;

    // Generate unique ID for payload to avoid duplicate processing
    const payloadId = JSON.stringify(lastPayload) + Date.now();
    if (lastPayloadIdRef.current === payloadId) return;
    lastPayloadIdRef.current = payloadId;

    if (lastPayload.type === "stage_start") {
      // Immediately update the conversation step when stage starts
      onStateChange({ 
        conversationStep: lastPayload.stage as "segment" | "email" | "journey" | "deployment" | "analytics"
      });
    }

    if (lastPayload.type === "thinking") {
      // Update or create thinking message
      setMessages((prev) => {
        const lastMsg = prev[prev.length - 1];
        if (lastMsg && lastMsg.role === "thinking") {
          // Update existing thinking message
          return [
            ...prev.slice(0, -1),
            {
              ...lastMsg,
              thinkingSteps: [...(lastMsg.thinkingSteps || []), lastPayload.step]
            }
          ];
        } else {
          // Create new thinking message
          return [
            ...prev,
            {
              role: "thinking",
              content: "",
              timestamp: new Date().toISOString(),
              thinkingSteps: [lastPayload.step]
            }
          ];
        }
      });
    }

    if (lastPayload.type === "deployment_progress") {
      // Update or create deployment progress message
      setMessages((prev) => {
        const lastMsg = prev[prev.length - 1];
        if (lastMsg && lastMsg.role === "deployment") {
          // Update existing deployment message
          return [
            ...prev.slice(0, -1),
            {
              ...lastMsg,
              deploymentProgress: lastPayload.progress
            }
          ];
        } else {
          // Create new deployment message
          return [
            ...prev,
            {
              role: "deployment",
              content: "",
              timestamp: new Date().toISOString(),
              deploymentProgress: lastPayload.progress
            }
          ];
        }
      });
    }

    if (lastPayload.type === "assistant_message") {
      // Remove thinking message (but keep deployment) and add assistant response
      setMessages((prev) => {
        const filtered = prev.filter(m => m.role !== "thinking");
        return [
          ...filtered,
          {
            role: "assistant",
            content: lastPayload.message,
            timestamp: new Date().toISOString()
          }
        ];
      });
    }

    if (lastPayload.type === "state_update") {
      const stateData = lastPayload.state;
      
      // Create a rich AI message with the state data embedded
      const richMessage: AssistantMessage = {
        role: "assistant",
        content: "", // Content is already sent via assistant_message
        timestamp: new Date().toISOString()
      };

      // Attach rich data based on what's in the state update
      if (stateData.segmentSuggestion || stateData.segmentResult) {
        richMessage.segmentData = {
          suggestion: stateData.segmentSuggestion || "",
          result: stateData.segmentResult || []
        };
      }

      if (stateData.emailHtml) {
        richMessage.emailData = {
          html: stateData.emailHtml
        };
      }

      if (stateData.journeyPlan || stateData.scheduleHint) {
        richMessage.journeyData = {
          plan: stateData.journeyPlan || "",
          scheduleHint: stateData.scheduleHint
        };
      }

      if (stateData.analyticsData) {
        richMessage.analyticsData = stateData.analyticsData;
      }

      // Only add if there's actual rich content
      if (richMessage.segmentData || richMessage.emailData || richMessage.journeyData || richMessage.analyticsData) {
        setMessages((prev) => [...prev, richMessage]);
      }

      // Update conversation step
      if (stateData.conversationStep) {
        onStateChange({ conversationStep: stateData.conversationStep });
      }
    }
  }, [lastPayload, onStateChange]);

  useEffect(() => {
    if (viewportRef.current) {
      // Use smooth scroll and add a small delay to ensure DOM is updated
      requestAnimationFrame(() => {
        if (viewportRef.current) {
          viewportRef.current.scrollTo({
            top: viewportRef.current.scrollHeight,
            behavior: 'smooth'
          });
        }
      });
    }
  }, [messages]);

  const handleSend = () => {
    if (!input.trim()) return;

    const msg: AssistantMessage = {
      role: "user",
      content: input.trim(),
      timestamp: new Date().toISOString()
    };
    setMessages((prev) => [...prev, msg]);

    sendMessage({
      type: "intent",
      intent: input.trim()
    });

    setInput("");
  };

  return (
    <div className="chat-container">
      <div className="chat-header">
        <div className="chat-header-left">
          <div className="chat-title">LCMS Copilot</div>
          <div className="chat-subtitle">
            Multi-turn conversation for campaign creation
          </div>
        </div>
        <div className="chat-header-right">
          <div className="stepper">
            <div
              className={`stepper-pill ${
                currentStep === "segment" ? "stepper-pill-active" : 
                isStepCompleted(currentStep, "segment") ? "stepper-pill-completed" : ""
              }`}
            >
              <span className="stepper-index">1</span>
              <span className="stepper-label">Segment</span>
            </div>
            <div
              className={`stepper-pill ${
                currentStep === "email" ? "stepper-pill-active" : 
                isStepCompleted(currentStep, "email") ? "stepper-pill-completed" : ""
              }`}
            >
              <span className="stepper-index">2</span>
              <span className="stepper-label">Email</span>
            </div>
            <div
              className={`stepper-pill ${
                currentStep === "journey" ? "stepper-pill-active" : 
                isStepCompleted(currentStep, "journey") ? "stepper-pill-completed" : ""
              }`}
            >
              <span className="stepper-index">3</span>
              <span className="stepper-label">Journey</span>
            </div>
            <div
              className={`stepper-pill ${
                currentStep === "deployment" ? "stepper-pill-active" : 
                isStepCompleted(currentStep, "deployment") ? "stepper-pill-completed" : ""
              }`}
            >
              <span className="stepper-index">4</span>
              <span className="stepper-label">Deploy</span>
            </div>
            <div
              className={`stepper-pill ${
                currentStep === "analytics" ? "stepper-pill-active" : 
                isStepCompleted(currentStep, "analytics") ? "stepper-pill-completed" : ""
              }`}
            >
              <span className="stepper-index">5</span>
              <span className="stepper-label">Analytics</span>
            </div>
          </div>
          <div className={`status-pill status-${status}`}>
            <span className="dot" />
            {status === "connected"
              ? "Connected"
              : status === "connecting"
              ? "Connecting..."
              : "Disconnected"}
          </div>
        </div>
      </div>

      <div className="chat-viewport" ref={viewportRef}>
        {messages.length === 0 && (
          <div className="chat-empty">
            <div className="chat-empty-icon">💬</div>
            <p className="chat-empty-title">Start a conversation</p>
            <p className="chat-empty-hint">
              Try: <span className="pill">"Create a re-engagement campaign for inactive VIP users"</span>
            </p>
          </div>
        )}
        {messages.map((msg, idx) => (
          <MessageBubble key={`${msg.timestamp}-${idx}`} message={msg} />
        ))}
      </div>

      <div className="chat-input-row">
        <input
          className="chat-input"
          placeholder="Describe your marketing intent..."
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter" && !e.shiftKey) {
              e.preventDefault();
              handleSend();
            }
          }}
        />
        <button className="btn primary" onClick={handleSend}>
          Send
        </button>
      </div>
    </div>
  );
}

