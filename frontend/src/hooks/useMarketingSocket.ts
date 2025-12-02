import { useEffect, useRef, useState } from "react";
import { SegmentUser, AnalyticsData, DeploymentProgress } from "../types";

type Payload =
  | { type: "assistant_message"; message: string }
  | { type: "thinking"; step: string }
  | { type: "stage_start"; stage: string }
  | { type: "deployment_progress"; progress: DeploymentProgress }
  | {
      type: "state_update";
      state: {
        segmentSuggestion?: string;
        segmentResult?: SegmentUser[];
        segmentTotalMatched?: number;
        emailHtml?: string;
        journeyPlan?: string;
        scheduleHint?: string;
        analyticsData?: AnalyticsData;
        conversationStep?: "segment" | "email" | "journey" | "deployment" | "analytics";
      };
    };

type Outgoing =
  | {
      type: "intent";
      intent: string;
    }
  | {
      type: "ping";
    };

type Status = "connecting" | "connected" | "disconnected";

export function useMarketingSocket() {
  const [status, setStatus] = useState<Status>("connecting");
  const [lastPayload, setLastPayload] = useState<Payload | null>(null);
  const socketRef = useRef<WebSocket | null>(null);

  useEffect(() => {
    const wsUrl =
      (import.meta.env.VITE_WS_URL as string | undefined) ??
      "ws://localhost:8080/ws/assistant";

    const socket = new WebSocket(wsUrl);
    socketRef.current = socket;
    setStatus("connecting");

    socket.onopen = () => {
      setStatus("connected");
      socket.send(JSON.stringify({ type: "ping" }));
    };

    socket.onclose = () => {
      setStatus("disconnected");
    };

    socket.onerror = () => {
      setStatus("disconnected");
    };

    socket.onmessage = (event) => {
      try {
        const payload = JSON.parse(event.data) as Payload;
        setLastPayload(payload);
      } catch {
        // For POC we simply ignore malformed data.
      }
    };

    return () => {
      socket.close();
    };
  }, []);

  const sendMessage = (msg: Outgoing) => {
    if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
      socketRef.current.send(JSON.stringify(msg));
    }
  };

  return { status, lastPayload, sendMessage };
}


