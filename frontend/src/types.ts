export interface SegmentUser {
  id: string;
  name: string;
  email: string;
  score: number;
}

// Rich content types that can be embedded in AI messages
export interface SegmentData {
  suggestion: string;
  result: SegmentUser[];
}

export interface EmailData {
  html: string;
}

export interface JourneyData {
  plan: string;
  scheduleHint?: string;
}

// Extended message type with optional rich data
export interface AssistantMessage {
  role: "user" | "assistant" | "system" | "thinking";
  content: string;
  timestamp: string;
  // Optional rich content
  segmentData?: SegmentData;
  emailData?: EmailData;
  journeyData?: JourneyData;
  // Thinking steps for COT visualization
  thinkingSteps?: string[];
}

export interface MarketingState {
  conversationStep?: "segment" | "email" | "journey";
}


