## Smart Marketing Copilot POC

This repository contains a minimal proof-of-concept for an intelligent marketing assistant similar to D365 Customer Insights Copilot.
The goal is to keep the implementation simple and fully commented while keeping a professional enterprise-style UI.

### Tech stack

- **Frontend** (`frontend/`): React 18 + Vite, TypeScript, responsive layout (desktop + mobile).
- **Backend** (`backend/`): Spring Boot (JDK 17) Maven project with a plain WebSocket endpoint.

### Features

- **Copilot-style assistant panel** to capture the marketing intent from the user.
- **Segment suggestion** panel that displays AI-style targeting logic and mocked customer list.
- **Email template editor** that allows editing of HTML email content.
- **Email preview** that renders the HTML (including CTA buttons) as the user edits.
- **Customer journey** summary including recommended steps and scheduling hint.

All backend responses are mocked, generated deterministically from the latest user intent.

### Project structure (high level)

- `frontend/` – React + Vite application
  - `package.json`, `vite.config.mts`, `index.html`, `tsconfig.json`
  - `src/`
    - `main.tsx` – React entry
    - `App.tsx` – Main layout
    - `styles.css` – Global responsive styles (dark blue enterprise theme)
    - `types.ts` – Shared TypeScript types
    - `components/` – UI components
    - `hooks/useMarketingSocket.ts` – WebSocket client hook
- `backend/` – Spring Boot Maven project
  - `pom.xml` – Maven configuration
  - `src/main/java/com/example/smartmarketing/SmartMarketingApplication.java`
  - `src/main/java/com/example/smartmarketing/config/WebSocketConfig.java`
  - `src/main/java/com/example/smartmarketing/ws/MarketingAssistantHandler.java`
  - `src/main/resources/application.properties`

### Running the backend (Spring Boot, JDK 17)

From the repository root:

```bash
cd backend
mvn spring-boot:run
```

This starts the backend on `http://localhost:8080` and exposes a WebSocket endpoint at `ws://localhost:8080/ws/assistant`.

### Running the frontend (React + Vite)

From the repository root:

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server will start on `http://localhost:5173`.  
The React app will automatically connect to the backend WebSocket at `ws://localhost:8080/ws/assistant`.

### Notes

- All comments in the source code are written in English to keep the POC easy to read and maintain.
- WebSocket messages are small JSON objects:
  - Frontend → backend:
    - `{ "type": "intent", "intent": "<user free text>" }`
    - `{ "type": "ping" }`
  - Backend → frontend:
    - `{ "type": "assistant_message", "message": "..." }`
    - `{ "type": "state_update", "state": { ... } }`
- The backend uses simple deterministic logic to generate mock segments, HTML email, and journey steps to simulate an AI-powered system without external dependencies.


# chat-Copilot
