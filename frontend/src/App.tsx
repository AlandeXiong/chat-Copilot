import { ChatPanel } from "./components/ChatPanel";
import { useState } from "react";
import { MarketingState } from "./types";

const initialState: MarketingState = {
  conversationStep: "segment"
};

export function App() {
  const [state, setState] = useState<MarketingState>(initialState);

  return (
    <div className="app-root">
      <header className="app-header">
        <div className="brand">
          <span className="brand-logo">◎</span>
          <div>
            <div className="brand-title">LCMS Copilot</div>
            <div className="brand-subtitle">
              POC – Intelligent campaign and journey builder
            </div>
          </div>
        </div>
      </header>
      <main className="app-main-chat">
        <ChatPanel state={state} onStateChange={setState} />
      </main>
    </div>
  );
}


