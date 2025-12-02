import { memo } from "react";
import { DeploymentProgress as DeploymentProgressType } from "../types";

interface Props {
  progress: DeploymentProgressType;
}

export const DeploymentProgress = memo(function DeploymentProgress({ progress }: Props) {
  const {
    status,
    progressPercent,
    totalRecipients,
    successCount,
    failedCount,
    currentPhase,
    estimatedTimeRemaining,
    throughputPerSecond
  } = progress;

  const successRate = totalRecipients > 0 
    ? ((successCount / (successCount + failedCount)) * 100).toFixed(1) 
    : "0";

  return (
    <div className="deployment-container">
      <div className="deployment-header">
        <div className="deployment-status">
          <div className={`deployment-status-indicator status-${status}`}>
            <div className="status-pulse"></div>
          </div>
          <div>
            <div className="deployment-status-title">
              {status === "deploying" ? "🚀 Campaign Deploying" : 
               status === "completed" ? "✅ Deployment Complete" : 
               status === "failed" ? "❌ Deployment Failed" : 
               "⚙️ Initializing"}
            </div>
            <div className="deployment-phase">{currentPhase}</div>
          </div>
        </div>
      </div>

      <div className="deployment-progress-bar-wrapper">
        <div className="deployment-progress-bar">
          <div 
            className="deployment-progress-fill"
            style={{ width: `${progressPercent}%` }}
          >
            <div className="deployment-progress-shimmer"></div>
          </div>
          <div className="deployment-progress-text">
            {progressPercent}%
          </div>
        </div>
      </div>

      <div className="deployment-stats">
        <div className="deployment-stat-card success">
          <div className="stat-icon">✓</div>
          <div className="stat-content">
            <div className="stat-value">{successCount.toLocaleString()}</div>
            <div className="stat-label">Successful</div>
          </div>
          <div className="stat-rate">{successRate}%</div>
        </div>

        <div className="deployment-stat-card failed">
          <div className="stat-icon">✗</div>
          <div className="stat-content">
            <div className="stat-value">{failedCount.toLocaleString()}</div>
            <div className="stat-label">Failed</div>
          </div>
          <div className="stat-rate">{(100 - parseFloat(successRate)).toFixed(1)}%</div>
        </div>

        <div className="deployment-stat-card total">
          <div className="stat-icon">📊</div>
          <div className="stat-content">
            <div className="stat-value">{totalRecipients.toLocaleString()}</div>
            <div className="stat-label">Total Recipients</div>
          </div>
        </div>
      </div>

      <div className="deployment-metrics">
        <div className="deployment-metric">
          <span className="metric-icon">⚡</span>
          <span className="metric-text">Throughput: <strong>{throughputPerSecond} emails/sec</strong></span>
        </div>
        {status === "deploying" && estimatedTimeRemaining && (
          <div className="deployment-metric">
            <span className="metric-icon">⏱️</span>
            <span className="metric-text">ETA: <strong>{estimatedTimeRemaining}</strong></span>
          </div>
        )}
      </div>
    </div>
  );
});

