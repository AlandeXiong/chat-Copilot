# Smart Marketing Copilot POC

> An AI-powered intelligent marketing campaign builder with multi-turn conversation, real-time deployment simulation, and advanced analytics diagnostics.

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![React](https://img.shields.io/badge/React-18.x-61DAFB?logo=react)](https://reactjs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-007396?logo=java)](https://openjdk.org/)

## 🎯 Project Overview

An enterprise-grade marketing automation POC featuring AI-driven multi-turn conversation flow with real-time progress visualization, deployment simulation, and performance analytics. Inspired by D365 Customer Insights Copilot design.

### Key Features

- 🎯 **Smart Segment Creation**: AI-powered audience targeting with real-time user matching and scoring
- ✉️ **Email Template Generation**: Dynamic HTML email creation with personalization tokens and CTA optimization  
- 🗺️ **Customer Journey Design**: Multi-step automation with branching logic and wait conditions
- 🚀 **Intelligent Deployment**: Real-time campaign deployment simulation with 45-second progress tracking
- 📊 **Performance Analytics**: Conversion funnel visualization with AI-powered bottleneck diagnostics
- 🤖 **Chain-of-Thought (COT) Visualization**: Real-time display of AI reasoning steps with tool invocations

---

## 🏗️ Architecture

### Technology Stack

**Frontend:**
- React 18 + TypeScript + Vite
- Responsive design (mobile & desktop)
- WebSocket client for real-time bi-directional communication
- Dark blue enterprise theme with tech-inspired animations

**Backend:**
- Spring Boot 3.3.5 + JDK 17
- WebSocket server with multi-stage orchestration
- Maven build system
- Mock AI engine with realistic delay simulation (~9-48 seconds per stage)

**Communication Protocol:**
- WebSocket for real-time messaging
- JSON payload format
- Multiple message types: `thinking`, `stage_start`, `deployment_progress`, `state_update`

---

## 📁 Project Structure

```
chatbot/
├── frontend/                    # React + Vite application
│   ├── package.json
│   ├── vite.config.mts
│   ├── tsconfig.json
│   ├── index.html
│   └── src/
│       ├── main.tsx            # React entry point
│       ├── App.tsx             # Main layout with chat interface
│       ├── styles.css          # Global styles (enterprise dark blue theme)
│       ├── types.ts            # TypeScript interfaces
│       ├── hooks/
│       │   └── useMarketingSocket.ts  # WebSocket client hook
│       └── components/
│           ├── ChatPanel.tsx          # Main chat container
│           ├── MessageBubble.tsx      # Message rendering (user/AI/deployment)
│           ├── FunnelChart.tsx        # Conversion funnel visualization
│           └── DeploymentProgress.tsx # Real-time deployment tracker
│
└── backend/                     # Spring Boot Maven project
    ├── pom.xml                  # Maven dependencies
    └── src/main/
        ├── java/com/example/smartmarketing/
        │   ├── SmartMarketingApplication.java      # Boot entry
        │   ├── config/WebSocketConfig.java         # WebSocket setup
        │   └── ws/MarketingAssistantHandler.java   # Main handler with COT simulation
        └── resources/
            └── application.properties               # Server config (port 8080)
```

---

## 🚀 Getting Started

### Prerequisites

- **Node.js** 16+ and npm
- **Java JDK** 17+
- **Maven** 3.6+

### Running the Backend

From the repository root:

```bash
cd backend
mvn spring-boot:run
```

The backend starts on **`http://localhost:8080`** with WebSocket endpoint at **`ws://localhost:8080/ws/assistant`**.

### Running the Frontend

From the repository root:

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server starts on **`http://localhost:5173`**. The React app automatically connects to the backend WebSocket.

---

## 🎬 Multi-Turn Conversation Flow

The POC implements a **5-stage progressive workflow** with real-time AI thinking visualization:

### Stage 1: Segment Creation
- **User Input**: `"Create a re-engagement campaign for inactive VIP users"`
- **AI Process**: ~9 seconds with 10 thinking steps (3 tool calls)
- **Output**: 
  - Segment strategy (filters, criteria)
  - 3 matched users with engagement scores
  - Rich table visualization

### Stage 2: Email Template
- **User Input**: `"Make the email warm and professional"`
- **AI Process**: ~9 seconds (3 tools: ContentGenerator, DesignEngine, CTAOptimizer)
- **Output**: 
  - HTML email with personalization tokens
  - Live preview with CTA buttons
  - Inline editable template

### Stage 3: Customer Journey
- **User Input**: `"Create a 3-step automated journey"`
- **AI Process**: ~9 seconds (3 tools: JourneyOrchestrator, BranchLogicEngine, SendTimeOptimizer)
- **Output**: 
  - Multi-step flow with branches
  - Wait conditions
  - Optimal send time recommendations

### Stage 4: Intelligent Deployment 🚀
- **User Input**: `"Start the campaign deployment"`
- **AI Process**: ~45-48 seconds real-time simulation
- **Features**:
  - **16-step progress bar** (3% → 100%) with gradient animation
  - **6 batches**: VIP → Engaged → Active → Warm Leads → Re-engagement → Final
  - **Success/failure tracking**: 97% success rate, 3% failure rate
  - **Throughput metrics**: 850 emails/sec
  - **ETA countdown**: Dynamic time remaining
  - **Pulsing status indicator**: Blue (deploying) → Green (completed)

### Stage 5: Performance Analytics 📊
- **User Input**: `"Analyze campaign performance"`
- **AI Process**: ~10 seconds (3 tools: DataAggregator, FunnelAnalyzer, DiagnosticEngine)
- **Output**:
  - **7-stage conversion funnel**: Impressions → Conversions
  - **Interactive visualization**: Color-coded bars with shimmer effect
  - **AI bottleneck detection**: Identifies "Leads → Qualified Leads" (50% drop-off)
  - **Root cause analysis**: 4 reasons (lead scoring, response time, form issues, nurturing)
  - **Actionable recommendations**: 5 AI-generated optimization strategies

---

## 🎨 UI/UX Highlights

### Design Philosophy
- **Enterprise-grade dark blue theme** with radial gradients
- **Copilot-inspired chat interface** (inspired by industry product)
- **Real-time progress indicators** with pulsing animations
- **Responsive layout**: Adapts to mobile, tablet, and desktop

### Visual Effects
- **Thinking bubbles**: Spinning loader + step-by-step COT visualization
- **Progress stepper**: 5-stage indicator with active (pulsing blue) and completed (green ✓) states
- **Deployment animation**: 
  - Gradient flow animation (3s loop)
  - Shimmer effect on progress bar (2s sweep)
  - Pulsing status icon (2s heartbeat)
- **Funnel chart**: 
  - Horizontal bars with color gradient (blue → purple → pink)
  - Smart number positioning (inside/outside based on bar width)
  - Negative change indicators in red

### Accessibility
- Smooth scrolling with custom scrollbar
- Auto-scroll to latest message using `requestAnimationFrame`
- Color-blind friendly palette
- Keyboard navigation support

---

## 🔧 WebSocket Message Protocol

### Frontend → Backend

```json
// User intent
{
  "type": "intent",
  "intent": "Create a re-engagement campaign for VIP users"
}

// Ping
{
  "type": "ping"
}
```

### Backend → Frontend

```json
// Stage start (lights up progress indicator)
{
  "type": "stage_start",
  "stage": "segment"
}

// Thinking step (COT visualization)
{
  "type": "thinking",
  "step": "🔧 Tool Call 1/3: CustomerSegmentationEngine"
}

// Deployment progress update
{
  "type": "deployment_progress",
  "progress": {
    "status": "deploying",
    "progressPercent": 45,
    "totalRecipients": 3847,
    "successCount": 1683,
    "failedCount": 52,
    "currentPhase": "📨 Batch 3/6 - Active segment",
    "estimatedTimeRemaining": "18 seconds",
    "throughputPerSecond": 850
  }
}

// State update (rich data)
{
  "type": "state_update",
  "state": {
    "conversationStep": "segment",
    "segmentSuggestion": "...",
    "segmentResult": [...],
    "emailHtml": "...",
    "journeyPlan": "...",
    "analyticsData": {...}
  }
}

// Assistant message (text reply)
{
  "type": "assistant_message",
  "message": "Step 1 · Segment design\nI generated a suggested audience segment..."
}
```

---

## 🧪 Mock AI Engine

The backend simulates realistic AI behavior with:

- **COT (Chain-of-Thought) simulation**: 10-12 steps per stage with varied delays (600-1000ms)
- **Tool invocation simulation**: 2-3 tools per stage (e.g., SegmentationEngine, CTAOptimizer, DiagnosticEngine)
- **Progress realism**: Non-linear progress steps (faster start, slower finish)
- **Success/failure rates**: 97% success, 3% failure (industry-standard)
- **Deterministic mock data**: Based on user intent string for reproducibility

### Timing Breakdown
- Stage 1-3: ~9 seconds each (segment, email, journey)
- Stage 4: ~45-48 seconds (deployment with 16 progress updates)
- Stage 5: ~10 seconds (analytics)
- **Total end-to-end experience**: ~85 seconds

---

## 📝 Code Style & Best Practices

- ✅ **All comments in English** for international collaboration
- ✅ **TypeScript strict mode** enabled
- ✅ **React functional components** with hooks
- ✅ **Memoization** (`React.memo`) for performance
- ✅ **CSS animations** with GPU acceleration (`will-change`, `transform`)
- ✅ **Responsive design** with CSS Grid and Flexbox
- ✅ **WebSocket reconnection** handling
- ✅ **Error boundary** for graceful degradation
- ✅ **`.gitignore`** for both frontend and backend (excludes `node_modules/`, `target/`, IDE configs)

---

## 🛠️ Development

### Build for Production

**Frontend:**
```bash
cd frontend
npm run build
# Output: frontend/dist/
```

**Backend:**
```bash
cd backend
mvn clean package
# Output: backend/target/smart-marketing-backend-0.0.1-SNAPSHOT.jar
```

### Run Production Build

```bash
java -jar backend/target/smart-marketing-backend-0.0.1-SNAPSHOT.jar
# Serve frontend/dist/ with nginx or similar
```

---

## 🎯 Future Enhancements

While this is a POC, potential production improvements include:

- [ ] Real LLM integration (OpenAI, Azure OpenAI, etc.)
- [ ] Database persistence (PostgreSQL, MongoDB)
- [ ] User authentication & authorization
- [ ] A/B testing framework
- [ ] Real-time collaboration (multi-user)
- [ ] Export to PDF/Excel
- [ ] Email deliverability tracking
- [ ] Advanced analytics dashboard
- [ ] Campaign scheduling
- [ ] Template library

---

## 📄 License

MIT License - feel free to use this POC for learning and experimentation.

---

## 🙏 Acknowledgments

Inspired by:
- Microsoft Dynamics 365 Customer Insights
- GitHub Copilot interface design
- Modern AI assistant patterns

Built with ❤️ using React, Spring Boot, and WebSocket technology.
