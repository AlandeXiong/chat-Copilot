import { memo } from "react";
import { FunnelStage } from "../types";

interface Props {
  funnelStages: FunnelStage[];
}

export const FunnelChart = memo(function FunnelChart({ funnelStages }: Props) {
  if (!funnelStages || funnelStages.length === 0) return null;

  const maxCount = funnelStages[0]?.count || 1;

  return (
    <div className="funnel-chart">
      <div className="funnel-chart-title">Conversion Funnel Visualization</div>
      <div className="funnel-stages">
        {funnelStages.map((stage, idx) => {
          const widthPercent = (stage.count / maxCount) * 100;
          const hasNegativeChange = stage.change !== undefined && stage.change < 0;
          // Show count inside bar only if bar is wide enough (>15%), otherwise show outside
          const showCountInside = widthPercent > 15;
          
          return (
            <div key={idx} className="funnel-stage-row">
              <div className="funnel-stage-label">
                <span className="funnel-stage-name">{stage.stage}</span>
                {stage.change !== undefined && (
                  <span className={`funnel-stage-change ${hasNegativeChange ? 'negative' : 'positive'}`}>
                    {stage.change > 0 ? '+' : ''}{stage.change.toFixed(1)}%
                  </span>
                )}
              </div>
              <div className="funnel-stage-bar-container">
                <div 
                  className="funnel-stage-bar"
                  style={{ 
                    width: `${Math.max(widthPercent, 2)}%`, // Minimum 2% width for visibility
                    background: `linear-gradient(135deg, 
                      hsl(${200 + idx * 15}, 70%, ${60 - idx * 5}%), 
                      hsl(${220 + idx * 15}, 80%, ${50 - idx * 5}%))`
                  }}
                >
                  {showCountInside && (
                    <span className="funnel-stage-count funnel-count-inside">
                      {stage.count.toLocaleString()}
                    </span>
                  )}
                </div>
                {!showCountInside && (
                  <span className="funnel-stage-count funnel-count-outside">
                    {stage.count.toLocaleString()}
                  </span>
                )}
              </div>
              <div className="funnel-stage-rate">{stage.rate.toFixed(1)}%</div>
            </div>
          );
        })}
      </div>
    </div>
  );
});

