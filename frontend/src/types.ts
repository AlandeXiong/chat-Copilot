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
  totalMatched: number; // Total number of matched users in the segment
}

export interface EmailData {
  html: string;
}

export interface JourneyData {
  plan: string;
  scheduleHint?: string;
}

// Conversion funnel and analytics data
export interface FunnelStage {
  stage: string;
  count: number;
  rate: number;
  change?: number; // percentage change from baseline
}

export interface AnalyticsData {
  funnelStages: FunnelStage[];
  totalImpressions: number;
  totalLeads: number;
  overallConversionRate: number;
  bottleneck?: {
    stage: string;
    dropoffRate: number;
    reasons: string[];
    recommendations: string[];
  };
}

// Campaign deployment progress data
export interface DeploymentProgress {
  status: "initializing" | "deploying" | "completed" | "failed";
  progressPercent: number;
  totalRecipients: number;
  successCount: number;
  failedCount: number;
  currentPhase: string;
  estimatedTimeRemaining?: string;
  throughputPerSecond?: number;
}

// Extended message type with optional rich data
export interface AssistantMessage {
  role: "user" | "assistant" | "system" | "thinking" | "deployment";
  content: string;
  timestamp: string;
  // Optional rich content
  segmentData?: SegmentData;
  emailData?: EmailData;
  journeyData?: JourneyData;
  analyticsData?: AnalyticsData;
  deploymentProgress?: DeploymentProgress;
  // Thinking steps for COT visualization
  thinkingSteps?: string[];
}

export interface MarketingState {
  conversationStep?: "segment" | "email" | "journey" | "deployment" | "analytics";
}


