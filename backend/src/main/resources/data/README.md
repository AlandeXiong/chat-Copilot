# Mock Data Configuration

This folder contains all the mock response data used by the Smart Marketing Copilot POC. All data can be modified without recompiling the Java backend.

## 📂 File Structure

### `segment-data.json`
Contains segment strategy and top leads preview.

**Fields:**
- `suggestion` (string): Segment filter description
- `totalMatched` (number): Total number of users matching the segment criteria
- `topLeads` (array): Top 10 leads with highest engagement scores
  - `id` (string): Unique lead identifier
  - `name` (string): Lead full name
  - `email` (string): Lead email address
  - `score` (number): Engagement score (0-100)

**Example:**
```json
{
  "suggestion": "Segment: Inactive VIP customers\nFilters: ...",
  "totalMatched": 3847,
  "topLeads": [
    {"id": "1", "name": "Alice Chen", "email": "alice.chen@example.com", "score": 89},
    ...
  ]
}
```

---

### `email-template.html`
HTML email template with inline styles.

**Dynamic placeholders:**
- `{{firstName}}` - Recipient's first name
- `{{deadline}}` - Campaign deadline date (auto-calculated as current date + 10 days)

**Components:**
- Hero banner image (Unsplash URL - replace with your CDN)
- Personalized greeting
- Limited time offer box
- Benefits list with emojis
- Primary CTA button
- Social proof section
- Footer with links

**Customization:**
- Change image URL in `<img src="...">` tag
- Modify colors (primary: `#123B8D`)
- Update benefits list
- Adjust CTA text and URL

---

### `journey-plan.json`
Customer journey orchestration configuration.

**Fields:**
- `plan` (string): Multi-line journey flow description
- `scheduleHint` (string): Recommended send time window

**Journey Stages:**
1. Entry conditions
2. Initial action
3. Wait period
4. Branching logic
5. Exit criteria

---

### `analytics-data.json`
Conversion funnel and performance metrics.

**Fields:**
- `totalImpressions` (number): Campaign reach
- `totalLeads` (number): Total leads generated
- `overallConversionRate` (number): Percentage conversion
- `funnelStages` (array): 7-stage conversion funnel
  - `stage` (string): Stage name
  - `count` (number): User count at this stage
  - `rate` (number): Conversion rate (%)
  - `change` (number|null): Percentage change vs baseline
- `bottleneck` (object): AI-detected bottleneck analysis
  - `stage` (string): Problematic stage
  - `dropoffRate` (number): Drop-off percentage
  - `reasons` (array): Root causes
  - `recommendations` (array): AI-generated action items

**Funnel Stages:**
1. Impressions
2. Clicks
3. Landing Page Views
4. Form Starts
5. Leads Generated
6. Qualified Leads
7. Conversions

---

### `deployment-config.json`
Campaign deployment simulation settings.

**Fields:**
- `totalRecipients` (number): Number of users to deploy to
- `successRate` (number): Success rate (0.0-1.0, default: 0.97)
- `failureRate` (number): Failure rate (0.0-1.0, default: 0.03)
- `throughputPerSecond` (number): Emails sent per second
- `progressSteps` (array): Progress percentage checkpoints
- `delayPerStepMs` (object): Random delay range per step
  - `min` (number): Minimum delay (ms)
  - `max` (number): Maximum delay (ms)
- `phases` (array): Deployment phase descriptions
  - `progressThreshold` (number): Progress % when this phase starts
  - `description` (string): Phase description text

**Timing:**
- 16 progress steps × ~3 seconds = ~48 seconds total
- Adjust `delayPerStepMs` to change deployment duration

---

### `thinking-steps.json`
AI Chain-of-Thought (COT) visualization steps for each stage.

**Structure:**
```json
{
  "segment": [ ... ],
  "email": [ ... ],
  "journey": [ ... ],
  "deployment": [ ... ],
  "analytics": [ ... ]
}
```

Each stage contains an array of step objects:
- `step` (string): Thinking step description text
- `delayMs` (number): Delay in milliseconds before next step

**Step Types:**
- `🧠` - Intent analysis / reasoning
- `🔧 Tool Call X/Y` - Tool invocation
- `→` (indented) - Sub-task within a tool call
- `✅` - Final synthesis

**Customization:**
- Add/remove steps
- Adjust delays for faster/slower COT
- Change tool names and descriptions
- Modify emoji icons

---

## 🔧 How to Customize

### Changing Segment Criteria
Edit `segment-data.json`:
```json
{
  "suggestion": "Segment: Your custom segment\nFilters:\n  - Your custom filters...",
  "totalMatched": 5000,
  "topLeads": [ ... ]
}
```

### Modifying Email Design
Edit `email-template.html`:
- Replace hero image URL
- Update heading text
- Add/remove benefits
- Change CTA button text
- Modify colors and styling

### Adjusting Deployment Speed
Edit `deployment-config.json`:
```json
{
  "delayPerStepMs": {
    "min": 1000,  // Faster: 16 steps × 1.5s = ~24s
    "max": 2000
  }
}
```

### Adding More Thinking Steps
Edit `thinking-steps.json`:
```json
{
  "segment": [
    {"step": "🔧 Tool Call 4/4: NewTool", "delayMs": 800},
    {"step": "   → Doing something new", "delayMs": 900}
  ]
}
```

---

## 📊 Data Flow

```
User Input
    ↓
MarketingAssistantHandler
    ↓
DataLoader.loadSegmentData()
    ↓
segment-data.json → Parse → Return to frontend
```

All files are loaded from the classpath at runtime, so you can modify them and restart the backend to see changes immediately without recompiling.

---

## 🎯 Benefits of Configuration Files

✅ **No recompilation needed** - Just edit JSON/HTML and restart  
✅ **Easy A/B testing** - Swap different data files  
✅ **Non-developer friendly** - Marketing team can modify content  
✅ **Version control** - Track changes to mock data separately  
✅ **Quick iterations** - Faster POC demonstrations  

---

## 💡 Tips

- Keep realistic data (similar to production volumes)
- Test with different audience sizes
- Adjust timing to match your demo flow
- Use high-quality images for email templates
- Validate JSON before saving (use jsonlint.com)

